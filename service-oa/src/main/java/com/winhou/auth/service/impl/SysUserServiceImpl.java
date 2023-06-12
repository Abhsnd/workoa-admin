package com.winhou.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winhou.auth.mapper.SysUserMapper;
import com.winhou.auth.service.SysUserService;
import com.winhou.model.system.SysUser;
import com.winhou.security.custom.LoginUserInfoHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author winhou
 * @since 2023-05-03
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /**
     * @Author wiho
     * @Date 2023/5/6 0:31
     * @Description 更新用户状态
     * @Param [id 用户id, status 状态值]
     * @Return void
     * @Since version-1.0
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        // 根据用户id查询用户
        SysUser sysUser = baseMapper.selectById(id);
        // 设置修改状态值
        sysUser.setStatus(status);
        // 修改
        baseMapper.updateById(sysUser);
    }

    // 根据用户名查询
    @Override
    public SysUser getUserByUserName(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = baseMapper.selectOne(wrapper);
        return sysUser;
    }

    // 获取当前用户基本信息
    @Override
    public Map<String, Object> getCurrentUser() {
        SysUser sysUser = baseMapper.selectById(LoginUserInfoHelper.getUserId());
        Map<String, Object> map = new HashMap<>();
        map.put("name", sysUser.getName());
        map.put("phone", sysUser.getPhone());
        return map;
    }
}
