package com.winhou.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.winhou.auth.service.SysMenuService;
import com.winhou.auth.service.SysUserService;
import com.winhou.common.config.exception.MyException;
import com.winhou.common.jwt.JwtHelper;
import com.winhou.common.result.Result;
import com.winhou.common.utils.MD5;
import com.winhou.model.system.SysUser;
import com.winhou.vo.system.LoginVo;
import com.winhou.vo.system.RouterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author wiho
 * @Date 2023/5/2 17:23
 * @Description 后台登录管理类
 * @Since version-1.0
 */
@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    // login
    @ApiOperation(value = "登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("token", "admin-token");
//        return Result.ok(map);
        // 查询数据库 从vo对象获取用户名
        String username = loginVo.getUsername();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserService.getOne(wrapper);
        // 是否存在用户信息
        if (sysUser == null) {
            throw new MyException(201, "用户不存在");
        }
        // 验证密码
        String password_db = sysUser.getPassword();
        String password_input = MD5.encrypt(loginVo.getPassword());
        if (!password_input.equals(password_db)) {
            throw new MyException(201, "密码错误");
        }
        // 验证用户状态
        if (sysUser.getStatus().intValue() == 0) {
            throw new MyException(201, "用户被禁用");
        }
        // 使用jwt根据用户id和用户名生成token
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        return Result.ok(map);

    }

    // info
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        // 从请求头获取token
        String token = request.getHeader("token");
        // 从token获取用户id
        Long userId = JwtHelper.getUserId(token);
        // 根据用户id查询用户信息
        SysUser sysUser = sysUserService.getById(userId);
        // 根据用户id获取用户的菜单权限
        List<RouterVo> routerList = sysMenuService.findUserMenuListByUserId(userId);
        // 根据用户id获取用户的按钮权限
        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("roles", "[admin]");
        map.put("name", sysUser.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        // 添加当前用户的菜单权限
        map.put("routers", routerList);
        // 添加当前用户的按钮权限
        map.put("buttons", permsList);
        return Result.ok(map);
    }

    // logout
    @PostMapping("logout")
    public Result logout() {
        return Result.ok();
    }
}
