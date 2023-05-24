package com.winhou.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winhou.auth.mapper.SysMenuMapper;
import com.winhou.auth.service.SysMenuService;
import com.winhou.auth.service.SysRoleMenuService;
import com.winhou.auth.util.MenuHelper;
import com.winhou.common.config.exception.MyException;
import com.winhou.model.system.SysMenu;
import com.winhou.model.system.SysRoleMenu;
import com.winhou.vo.system.AssignMenuVo;
import com.winhou.vo.system.MetaVo;
import com.winhou.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author winhou
 * @since 2023-05-06
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    // 获取树形结构菜单
    @Override
    public List<SysMenu> findNodes() {
        // 获取全部菜单数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        // 构建树形结构
        List<SysMenu> resultList = MenuHelper.buildTree(sysMenuList);
        return resultList;
    }

    // 删除菜单
    @Override
    public void removeMenuById(Long id) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new MyException(201, "菜单不能删除");
        }
        baseMapper.deleteById(id);
    }

    // 根据角色获取菜单
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        // 查询所有可用的菜单 (状态为1)
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus, 1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapperSysMenu);

        // 根据roleId查询所属角色的菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapperSysRoleMenu);

        // 根据菜单id获取菜单数据
        List<Long> menuIdList = sysRoleMenuList.stream().map(c -> c.getMenuId()).collect(Collectors.toList());
        // 角色所属id与所有菜单列表比较
        allSysMenuList.stream().forEach(item -> {
            if (menuIdList.contains(item.getId())) {
                item.setSelect(true);
            } else {
                item.setSelect(false);
            }
        });
        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);
        return sysMenuList;
    }

    // 给角色分配权限
    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        // 根据角色id 删除原有分配数据
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, assignMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapper);

        // 获取角色新分配菜单id列表
        List<Long> menuIdList = assignMenuVo.getMenuIdList();
        for (Long menuId : menuIdList) {
            if (StringUtils.isEmpty(menuId)) {
                continue;
            }
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
            sysRoleMenuService.save(sysRoleMenu);
        }
    }

    // 根据用户id获取用户的菜单权限
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        // 判断是否为管理员(管理员拥有全部菜单权限)
        if (userId.longValue() == 1) {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        // 构建树形结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);
        List<RouterVo> routerList = this.buildRouter(sysMenuTreeList);
        return routerList;
    }

    // 根据用户id获取用户的按钮权限
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        if (userId.longValue() == 1) {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        List<String> permsList = sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(item -> item.getPerms())
                .collect(Collectors.toList());

        return permsList;
    }

    // 构建前端路由结构
    private List<RouterVo> buildRouter(List<SysMenu> menus) {
        List<RouterVo> routers = new ArrayList<>();
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            // 下一层路由
            List<SysMenu> children = menu.getChildren();
            // 获取隐藏路由
            if (menu.getType().intValue() == 1) {
                List<SysMenu> hiddenMenuList = children.stream()
                        .filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    if (children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * @Author wiho
     * @Date 2023/5/11 20:52
     * @Description 获取路由地址
     * @Param [menu 菜单信息]
     * @Return java.lang.String 路由地址
     * @Since version-1.0
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }
}
