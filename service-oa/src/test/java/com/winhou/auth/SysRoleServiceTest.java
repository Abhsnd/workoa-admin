package com.winhou.auth;

import com.winhou.auth.service.SysRoleService;
import com.winhou.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SysRoleServiceTest {
    @Autowired
    private SysRoleService sysRoleService;

    @Test
    public void getAll() {
        List<SysRole> list = sysRoleService.list();
        System.out.println(list);
    }
}
