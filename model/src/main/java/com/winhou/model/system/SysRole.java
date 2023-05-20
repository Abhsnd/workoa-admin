package com.winhou.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.winhou.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author wiho
 * @Date 2023/5/3 12:53
 * @Description 角色实体类
 * @Since version-1.0
 */
@Data
@ApiModel(description = "角色")
@TableName("sys_role")
public class SysRole extends BaseEntity {
    private static final long serialVersionUID = 1L;

    //@NotBlank(message = "角色名称不能为空")
    @ApiModelProperty(value = "角色名称")
    @TableField("role_name")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    @TableField("role_code")
    private String roleCode;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;

}
