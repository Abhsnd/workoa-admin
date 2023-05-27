package com.winhou.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.winhou.model.process.ProcessTemplate;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author winhou
 * @since 2023-05-25
 */
public interface OaProcessTemplateService extends IService<ProcessTemplate> {

    // 分页查询审批模板，查询审批类型对应的名称
    IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam);

    // 部署流程定义文件 发布
    void pushlish(Long id);
}
