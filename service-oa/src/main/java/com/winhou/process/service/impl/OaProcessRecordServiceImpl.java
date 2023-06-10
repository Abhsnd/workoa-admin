package com.winhou.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winhou.auth.service.SysUserService;
import com.winhou.model.process.ProcessRecord;
import com.winhou.model.system.SysUser;
import com.winhou.process.mapper.OaProcessRecordMapper;
import com.winhou.process.service.OaProcessRecordService;
import com.winhou.security.custom.LoginUserInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author winhou
 * @since 2023-06-10
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {

    @Autowired
    private SysUserService sysUserService;

    // 提交记录
    @Override
    public void record(Long processId, Integer status, String description) {
        Long userId = LoginUserInfoHelper.getUserId();
        SysUser user = sysUserService.getById(userId);

        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUserId(userId);
        processRecord.setOperateUser(user.getName());

        baseMapper.insert(processRecord);
    }
}
