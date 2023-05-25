package com.winhou.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winhou.model.process.ProcessTemplate;
import com.winhou.model.process.ProcessType;
import com.winhou.process.mapper.OaProcessTemplateMapper;
import com.winhou.process.service.OaProcessTemplateService;
import com.winhou.process.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author winhou
 * @since 2023-05-25
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {

    @Autowired
    private OaProcessTypeService oaProcessTypeService;

    /**
     * @Author wiho
     * @Date 2023/5/26 0:22
     * @Description 分页查询审批模板，查询审批类型对应的名称
     * @Param [pageParam]
     * @Return com.baomidou.mybatisplus.core.metadata.IPage<com.winhou.model.process.ProcessTemplate>
     * @Since version-1.0
     */
    @Override
    public IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam) {
        // 调用mapper进行分页查询
        Page<ProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam, null);
        // 将查询到分页数据获取List集合
        List<ProcessTemplate> processTemplateList = processTemplatePage.getRecords();
        for (ProcessTemplate processTemplate : processTemplateList) {
            // 获取每条数据的审批类型id
            Long processTypeId = processTemplate.getProcessTypeId();
            // 根据审批类型id获取对应的审批类型名称
            LambdaQueryWrapper<ProcessType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessType::getId, processTypeId);
            ProcessType processType = oaProcessTypeService.getOne(wrapper);
            if (processType == null) {
                continue;
            }
            processTemplate.setProcessTypeName(processType.getName());
        }
        return processTemplatePage;
    }
}
