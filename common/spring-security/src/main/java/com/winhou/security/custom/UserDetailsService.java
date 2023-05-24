package com.winhou.security.custom;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Author wiho
 * @Date 2023/5/13 0:03
 * @Description 自定义loadUserByUsername
 * @Since version-1.0
 */
public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {
    // 根据用户名获取用户对象（获取不到直接抛异常）
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
