package com.winhou.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.winhou.model.system.SysUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author winhou
 * @since 2023-05-03
 */
public interface SysUserService extends IService<SysUser> {

    // 更新用户状态
    void updateStatus(Long id, Integer status);

    // 根据用户名查询
    SysUser getUserByUserName(String username);
}
