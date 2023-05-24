package com.winhou.auth;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winhou.auth.mapper.SysRoleMapper;
import com.winhou.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SysRoleMapperTest {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Test
    public void getAll() {
        List<SysRole> sysRoles = sysRoleMapper.selectList(null);
        System.out.println(sysRoles);
    }

    @Test
    public void getQuery1() {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name", "系统管理员");
        List<SysRole> sysRoles = sysRoleMapper.selectList(wrapper);
        System.out.println(sysRoles);
    }

    @Test
    public void getQuery2() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName, "系统管理员");
        List<SysRole> sysRoles = sysRoleMapper.selectList(wrapper);
        System.out.println(sysRoles);
    }
}
