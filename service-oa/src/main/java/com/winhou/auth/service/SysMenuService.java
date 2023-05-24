package com.winhou.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winhou.model.system.SysMenu;
import com.winhou.vo.system.AssignMenuVo;
import com.winhou.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author winhou
 * @since 2023-05-06
 */
public interface SysMenuService extends IService<SysMenu> {

    // 获取树形结构菜单
    List<SysMenu> findNodes();

    // 删除菜单
    void removeMenuById(Long id);

    // 根据角色获取菜单
    List<SysMenu> findMenuByRoleId(Long roleId);

    // 给角色分配权限
    void doAssign(AssignMenuVo assignMenuVo);

    // 根据用户id获取用户的菜单权限
    List<RouterVo> findUserMenuListByUserId(Long userId);

    // 根据用户id获取用户的按钮权限
    List<String> findUserPermsByUserId(Long userId);
}
