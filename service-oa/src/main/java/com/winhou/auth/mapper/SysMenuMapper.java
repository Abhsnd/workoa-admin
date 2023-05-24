package com.winhou.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winhou.model.system.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author winhou
 * @since 2023-05-06
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    // 根据用户id查询权限菜单，多表联查：用户角色表、角色菜单表、菜单表
    List<SysMenu> findMenuListByUserId(@Param("userId") Long userId);
}
