package com.winhou.auth.controller;


import com.winhou.auth.service.SysMenuService;
import com.winhou.common.result.Result;
import com.winhou.model.system.SysMenu;
import com.winhou.vo.system.AssignMenuVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author winhou
 * @since 2023-05-06
 */
@Api(tags = "菜单管理接口")
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    @ApiOperation(value = "根据角色获取菜单")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId) {
        List<SysMenu> sysMenuList = sysMenuService.findMenuByRoleId(roleId);
        return Result.ok(sysMenuList);
    }

    @ApiOperation(value = "给角色分配权限")
    @PostMapping("doAssign")
    public Result doAssign(@RequestBody AssignMenuVo assignMenuVo) {
        sysMenuService.doAssign(assignMenuVo);
        return Result.ok();
    }

    @ApiOperation(value = "获取菜单")
    @GetMapping("findNodes")
    public Result findNodes() {
        List<SysMenu> sysMenuList = sysMenuService.findNodes();
        return Result.ok(sysMenuList);
    }

    @ApiOperation(value = "新增菜单")
    @PostMapping("save")
    public Result save(@RequestBody SysMenu sysMenu) {
        boolean is_success = sysMenuService.save(sysMenu);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping("update")
    public Result updateById(@RequestBody SysMenu sysMenu) {
        sysMenuService.updateById(sysMenu);
        return Result.ok();
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysMenuService.removeMenuById(id);
        return Result.ok();
    }
}

