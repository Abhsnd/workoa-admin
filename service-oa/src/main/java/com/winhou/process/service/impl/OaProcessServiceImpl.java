package com.winhou.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winhou.process.mapper.OaProcessMapper;
import com.winhou.process.service.OaProcessService;
import com.winhou.vo.process.ProcessQueryVo;
import com.winhou.vo.process.ProcessVo;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.winhou.model.process.Process;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author winhou
 * @since 2023-05-27
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

    @Autowired
    private RepositoryService repositoryService;

    // 审批管理列表
    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam, processQueryVo);
        return pageModel;
    }

    // 部署流程定义
    @Override
    public void deployZip(String deployPath) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(deployPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deployment = repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
    }
}
