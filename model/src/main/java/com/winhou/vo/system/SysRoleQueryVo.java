package com.winhou.vo.system;

import java.io.Serializable;

/**
 * @Author wiho
 * @Date 2023/4/30 0:00
 * @Description 角色查询实体
 * @Since version-1.0
 */
public class SysRoleQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
