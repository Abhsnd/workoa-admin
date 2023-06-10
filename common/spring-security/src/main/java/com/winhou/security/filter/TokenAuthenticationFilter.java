package com.winhou.security.filter;

import com.alibaba.fastjson.JSON;
import com.winhou.common.jwt.JwtHelper;
import com.winhou.common.result.Result;
import com.winhou.common.result.ResultCodeEnum;
import com.winhou.common.utils.ResponseUtil;
import com.winhou.security.custom.LoginUserInfoHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author wiho
 * @Date 2023/5/13 11:20
 * @Description 自定义token解析器
 * @Since version-1.0
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 如果是登录接口，则直接放行
        if ("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }

    // 获取认证信息
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (!StringUtils.isEmpty(token)) {
            String username = JwtHelper.getUsername(token);
            if (!StringUtils.isEmpty(username)) {
                // 通过ThreadLocal记录当前登录人信息
                LoginUserInfoHelper.setUserId(JwtHelper.getUserId(token));
                LoginUserInfoHelper.setUsername(username);
                // 通过username从Redis中获取权限数据
                String authString = (String) redisTemplate.opsForValue().get(username);
                // 封装权限数据到集合
                if (!StringUtils.isEmpty(authString)) {
                    List<Map> mapList = JSON.parseArray(authString, Map.class);
                    System.out.println(mapList);
                    List<SimpleGrantedAuthority> authList = new ArrayList<>();
                    for (Map map : mapList) {
                        authList.add(new SimpleGrantedAuthority((String) map.get("authority")));
                    }
                    return new UsernamePasswordAuthenticationToken(username, null, authList);
                } else {
                    return new UsernamePasswordAuthenticationToken(username, null, null);
                }
            }
        }
        return null;
    }
}
