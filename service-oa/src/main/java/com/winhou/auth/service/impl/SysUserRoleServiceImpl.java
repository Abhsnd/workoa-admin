package com.winhou.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winhou.auth.mapper.SysUserRoleMapper;
import com.winhou.auth.service.SysUserRoleService;
import com.winhou.model.system.SysUserRole;
import org.springframework.stereotype.Service;

@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {
}
