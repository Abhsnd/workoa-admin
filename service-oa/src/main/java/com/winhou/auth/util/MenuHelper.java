package com.winhou.auth.util;

import com.winhou.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author wiho
 * @Date 2023/5/7 18:06
 * @Description 菜单类的工具类
 * @Since version-1.0
 */
public class MenuHelper {
    // 构建菜单的树形结构
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        ArrayList<SysMenu> trees = new ArrayList<>();
        for (SysMenu sysMenu : sysMenuList) {
            if (sysMenu.getParentId().longValue() == 0) {
                trees.add(getChildren(sysMenu, sysMenuList));
            }
        }
        return trees;
    }

    /**
     * @Author wiho
     * @Date 2023/5/7 18:23
     * @Description 查找子节点
     * @Param [sysMenu 当前节点, sysMenuList 菜单列表]
     * @Return com.winhou.model.system.SysMenu
     * @Since version-1.0
     */
    private static SysMenu getChildren(SysMenu sysMenu, List<SysMenu> sysMenuList) {
        sysMenu.setChildren(new ArrayList<SysMenu>());
        // 遍历菜单数据
        for (SysMenu menu : sysMenuList) {
            // 判断当前节点id是否为其他节点的parentId(是否为父节点)
            if (sysMenu.getId().longValue() == menu.getParentId().longValue()) {
                if (sysMenu.getChildren() == null) {
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(getChildren(menu, sysMenuList));
            }
        }
        return sysMenu;
    }
}
