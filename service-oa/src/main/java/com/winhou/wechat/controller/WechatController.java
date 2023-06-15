package com.winhou.wechat.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.winhou.auth.service.SysUserService;
import com.winhou.common.jwt.JwtHelper;
import com.winhou.common.result.Result;
import com.winhou.model.system.SysUser;
import com.winhou.vo.wechat.BindPhoneVo;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/admin/wechat")
@CrossOrigin
public class WechatController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private WxMpService wxMpService;

    @Value("${wechat.userInfoUrl}")
    private String userInfoUrl;

    // 微信授权方法
    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl,
                            HttpServletRequest request) {
        // 1. 授权路径
        // 2. 固定值，授权类型 WxConsts.OAuth2Scope.SNSAPI_USERINFO
        // 3.授权成功后跳转路径
        String redirectURL = null;
        try {
            redirectURL = wxMpService.getOAuth2Service().buildAuthorizationUrl(userInfoUrl,
                    WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                    URLEncoder.encode(returnUrl.replace("guiguoa", "#"), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return "redirect:" + redirectURL;
    }

    // 获取用户信息-授权
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws Exception {
        // 获取accessToken
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);

        // 获取openId
        String openId = accessToken.getOpenId();
        System.out.println("openId: " + openId);

        // 获取微信用户信息
        WxOAuth2UserInfo wxUserInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        System.out.println("微信用户信息: " + JSON.toJSONString(wxUserInfo));

        // 根据openId查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getOpenId, openId);
        SysUser user = sysUserService.getOne(wrapper);

        String token = "";
        //user != null 说明已经绑定，反之为建立账号绑定，去页面建立账号绑定
        if (user != null) {
            token = JwtHelper.createToken(user.getId(), user.getUsername());
        }

        if(returnUrl.indexOf("?") == -1) {
            return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
        } else {
            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
        }
    }

    @ApiOperation(value = "微信账号绑定手机")
    @PostMapping("bindPhone")
    @ResponseBody
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        // 根据手机号查询数据库
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, bindPhoneVo.getPhone());
        SysUser user = sysUserService.getOne(wrapper);

        // 如果存在，更新openId
        if (user != null) {
            user.setOpenId(bindPhoneVo.getOpenId());
            sysUserService.updateById(user);

            String token = JwtHelper.createToken(user.getId(), user.getUsername());
            return Result.ok(token);
        } else {
            return Result.fail("手机号码不存在，请联系管理员");
        }
    }

}
