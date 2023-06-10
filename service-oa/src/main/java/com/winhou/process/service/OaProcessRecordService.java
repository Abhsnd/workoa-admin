package com.winhou.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winhou.model.process.ProcessRecord;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author winhou
 * @since 2023-06-10
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
    // 提交记录
    void record(Long processId, Integer status, String description);
}
