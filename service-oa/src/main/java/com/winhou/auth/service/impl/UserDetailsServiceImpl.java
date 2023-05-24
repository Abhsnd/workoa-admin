package com.winhou.auth.service.impl;

import com.winhou.auth.service.SysMenuService;
import com.winhou.auth.service.SysUserService;
import com.winhou.model.system.SysUser;
import com.winhou.security.custom.CustomUser;
import com.winhou.security.custom.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getUserByUserName(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        if (sysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }
        // 根据userid查询用户的按钮权限
        List<String> userPermsList = sysMenuService.findUserPermsByUserId(sysUser.getId());
        // 创建一个List集合封装权限数据
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        for (String perm : userPermsList) {
            authList.add(new SimpleGrantedAuthority(perm.trim()));
        }
        return new CustomUser(sysUser, authList);
    }
}
