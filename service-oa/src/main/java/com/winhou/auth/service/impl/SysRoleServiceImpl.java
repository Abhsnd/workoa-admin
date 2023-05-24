package com.winhou.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winhou.auth.mapper.SysRoleMapper;
import com.winhou.auth.service.SysRoleService;
import com.winhou.auth.service.SysUserRoleService;
import com.winhou.model.system.SysRole;
import com.winhou.model.system.SysUserRole;
import com.winhou.vo.system.AssignRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    // 根据用户获取角色数据
    @Override
    public Map<String, Object> findRoleByAdminId(Long userId) {
        // 查询所有角色信息
        List<SysRole> allRoleList = baseMapper.selectList(null);

        // 根据用户id查询拥有的角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> existUserRoleList = sysUserRoleService.list(wrapper);
        // 从用户存在角色集合中获取已分配角色id
        List<Long> existRoleIdList = existUserRoleList.stream().map(c -> c.getRoleId()).collect(Collectors.toList());
        // 根据已分配角色id找到对应的已分配角色信息
        ArrayList<SysRole> assignRoleList = new ArrayList<>();
        for (SysRole sysRole : allRoleList) {
            if (existRoleIdList.contains(sysRole.getId())) {
                assignRoleList.add(sysRole);
            }
        }
        // 封装到map中
        HashMap<String, Object> roleMap = new HashMap<>();
        roleMap.put("assignRoleList", assignRoleList);  // 已分配角色
        roleMap.put("allRoleList", allRoleList);        // 所有角色
        return roleMap;
    }

    // 分配角色
    @Override
    public void doAssign(AssignRoleVo assignRoleVo) {
        // 删除已分配的角色信息
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, assignRoleVo.getUserId());
        sysUserRoleService.remove(wrapper);

        // 重新分配
        List<Long> roleIdList = assignRoleVo.getRoleIdList();
        for (Long roleId : roleIdList) {
            if (!StringUtils.isEmpty(roleId)) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(assignRoleVo.getUserId());
                sysUserRole.setRoleId(roleId);
                sysUserRoleService.save(sysUserRole);
            }
        }

    }
}
