package com.winhou.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.winhou.model.system.SysUser;

import java.util.Map;

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

    // 获取当前用户基本信息
    Map<String, Object> getCurrentUser();
}
