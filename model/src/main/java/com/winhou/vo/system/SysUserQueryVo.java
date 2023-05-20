package com.winhou.vo.system;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author wiho
 * @Date 2023/5/3 12:59
 * @Description 用户查询实体
 * @Since version-1.0
 */
@Data
public class SysUserQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    // 查询关键字
    private String keyword;

    private String createTimeBegin;
    private String createTimeEnd;

    // 角色id
    private Long roleId;
    // 职位id
    private Long postId;
    // 部门id
    private Long deptId;
}
