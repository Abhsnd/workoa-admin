package com.winhou.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winhou.model.process.ProcessType;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author winhou
 * @since 2023-05-25
 */
public interface OaProcessTypeService extends IService<ProcessType> {

    // 获取全部审批分类及模板
    List<ProcessType> findProcessType();
}
