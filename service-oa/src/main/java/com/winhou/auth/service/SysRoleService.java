package com.winhou.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winhou.model.system.SysRole;
import com.winhou.vo.system.AssignRoleVo;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {
    // 根据用户获取角色数据
    Map<String, Object> findRoleByAdminId(Long userId);

    // 分配角色
    void doAssign(AssignRoleVo assignRoleVo);
}
