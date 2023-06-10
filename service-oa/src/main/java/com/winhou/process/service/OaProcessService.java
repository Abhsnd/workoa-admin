package com.winhou.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.winhou.model.process.Process;
import com.winhou.vo.process.ProcessFormVo;
import com.winhou.vo.process.ProcessQueryVo;
import com.winhou.vo.process.ProcessVo;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author winhou
 * @since 2023-05-27
 */
public interface OaProcessService extends IService<Process> {

    // 审批管理列表
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

    // 部署流程定义
    void deployZip(String deployPath);

    // 启动流程
    void startUp(ProcessFormVo processFormVo);
}
