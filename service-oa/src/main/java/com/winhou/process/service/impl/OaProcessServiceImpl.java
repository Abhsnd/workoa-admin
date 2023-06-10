package com.winhou.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winhou.auth.service.SysUserService;
import com.winhou.model.process.Process;
import com.winhou.model.process.ProcessTemplate;
import com.winhou.model.system.SysUser;
import com.winhou.process.mapper.OaProcessMapper;
import com.winhou.process.service.OaProcessRecordService;
import com.winhou.process.service.OaProcessService;
import com.winhou.process.service.OaProcessTemplateService;
import com.winhou.security.custom.LoginUserInfoHelper;
import com.winhou.vo.process.ProcessFormVo;
import com.winhou.vo.process.ProcessQueryVo;
import com.winhou.vo.process.ProcessVo;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private OaProcessRecordService oaProcessRecordService;

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

    // 启动流程
    @Override
    public void startUp(ProcessFormVo processFormVo) {
        // 获取用户信息
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        // 查询审批模板信息
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(processFormVo.getProcessTemplateId());

        // 保存提交审批信息到业务表中
        Process process = new Process();
        // processFormVo复制到process
        BeanUtils.copyProperties(processFormVo, process);
        // 其他值
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        process.setStatus(1);
        baseMapper.insert(process);

        // 启动流程实例
        // 流程定义key
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();
        // 业务key processId
        String businessKey = String.valueOf(process.getId());

        // 流程参数form表单json数据，转换map集合
        String formValues = processFormVo.getFormValues();
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");
        // 遍历formData，封装map
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        // 流程参数
        Map<String, Object> variables = new HashMap<>();
        variables.put("data", map);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);

        // 查询下一个审批人
        List<Task> taskList = this.getCurrentTaskList(processInstance.getId());
        List<String> nameList = new ArrayList<>();
        for (Task task : taskList) {
            String assigneeName = task.getAssignee();
            SysUser user = sysUserService.getUserByUserName(assigneeName);
            String name = user.getName();
            nameList.add(name);

            // TODO 推送信息
        }
        // 业务和流程关联
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待" + StringUtils.join(nameList.toArray(), ",") +  "审批");
        // 更新oa_process
        baseMapper.updateById(process);

        // 记录操作审批信息记录
        oaProcessRecordService.record(process.getId(), 1, "发起申请");
    }

    // 当前业务列表
    private List<Task> getCurrentTaskList(String id) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(id).list();
        return tasks;
    }
}
