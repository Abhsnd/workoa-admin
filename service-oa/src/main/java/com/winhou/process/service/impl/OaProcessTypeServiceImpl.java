package com.winhou.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winhou.model.process.ProcessTemplate;
import com.winhou.model.process.ProcessType;
import com.winhou.process.mapper.OaProcessTypeMapper;
import com.winhou.process.service.OaProcessTemplateService;
import com.winhou.process.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author winhou
 * @since 2023-05-25
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    // 获取全部审批分类及模板
    @Override
    public List<ProcessType> findProcessType() {
        // 查询所有审批分类
        List<ProcessType> processTypeList = baseMapper.selectList(null);
        // 遍历所有审批分类
        for (ProcessType processType : processTypeList) {
            // 根据审批分类id查询对应的审批模板
            Long processTypeId = processType.getId();
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId, processTypeId);
            List<ProcessTemplate> processTemplateList = oaProcessTemplateService.list(wrapper);
            // 将审批模板封装到对应的审批分类对象中
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }
}
