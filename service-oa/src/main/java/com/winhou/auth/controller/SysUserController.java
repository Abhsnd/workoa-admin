package com.winhou.auth.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winhou.auth.service.SysUserService;
import com.winhou.common.result.Result;
import com.winhou.common.utils.MD5;
import com.winhou.model.system.SysUser;
import com.winhou.vo.system.SysUserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author winhou
 * @since 2023-05-03
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    // 用户条件分页查询
    @ApiOperation("用户条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        SysUserQueryVo sysUserQueryVo) {
        // 创建Page对象，传递分页相关参数
        Page<SysUser> pageParam = new Page<>(page, limit);
        // 封装条件，判断条件是否为空，不为空则拼装
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        String username = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();
        if(!StringUtils.isEmpty(username)) {
            wrapper.like(SysUser::getUsername, username);   // like模糊查询
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge(SysUser::getCreateTime, createTimeBegin);    // ge 大于等于
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le(SysUser::getCreateTime, createTimeEnd);      // le 小于等于
        }

        // 调用方法实现
        IPage<SysUser> pageModel = sysUserService.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }

    @ApiOperation("根据id获取用户")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("新增用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser sysUser) {
        // MD5加密
        String passwordMD5 = MD5.encrypt(sysUser.getPassword());
        sysUser.setPassword(passwordMD5);

        sysUserService.save(sysUser);
        return Result.ok();
    }

    @ApiOperation("修改用户")
    @PutMapping("update")
    public Result updateById(@RequestBody SysUser sysUser) {
        boolean is_success = sysUserService.updateById(sysUser);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean is_success = sysUserService.removeById(id);
        if (is_success) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("更新用户状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id,
                               @PathVariable Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.ok();
    }
}

