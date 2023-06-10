package com.winhou.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winhou.auth.service.SysUserService;
import com.winhou.model.process.Process;
import com.winhou.model.process.ProcessRecord;
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
import org.activiti.engine.task.TaskQuery;
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
public class  OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

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

    // 查询待处理任务列表
    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        // 封装查询条件，根据当前登录用户名称
        TaskQuery query = taskService.createTaskQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime()
                .desc();
        // 调用分页条件查询，代办任务集合
        int begin = (int) ((pageParam.getCurrent() - 1) * pageParam.getSize());
        int size = (int) pageParam.getSize();
        List<Task> taskList = query.listPage(begin, size);
        // 总记录数
        long totalCount = query.count();
        // 封装taskList到List<ProcessVo>
        List<ProcessVo> processVoList = new ArrayList<>();
        for (Task task : taskList) {
            // 获取流程实例id
            String processInstanceId = task.getProcessInstanceId();
            // 获取实例对象
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            // 获取业务key - processId
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null) {
                continue;
            }
            // 根据业务key获取Process对象
            long processId = Long.parseLong(businessKey);
            Process process = baseMapper.selectById(processId);
            // 复制Process到ProcessVo
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(task.getId());

            processVoList.add(processVo);
        }
        // 封装到IPage
        IPage<ProcessVo> page = new Page<ProcessVo>(
                pageParam.getCurrent(),
                pageParam.getSize(),
                totalCount
        );
        page.setRecords(processVoList);
        return page;
    }

    // 查看审批详情信息
    @Override
    public Map<String, Object> show(Long id) {
        // 根据流程id获取流程信息
        Process process = baseMapper.selectById(id);
        // 根据流程id获取流程记录
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId, id);
        List<ProcessRecord> processRecordList = oaProcessRecordService.list(wrapper);
        // 根据模板id获取审批模板
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(process.getProcessTemplateId());
        // 判断当前用户是否可以审批
        boolean isApprove = false;
        String processInstanceId = process.getProcessInstanceId();
        List<Task> taskList = this.getCurrentTaskList(processInstanceId);
        for (Task task : taskList) {
            String username = LoginUserInfoHelper.getUsername();
            if (task.getAssignee().equals(username)) {
                isApprove = true;
            }
        }
        // 封装到map
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        map.put("isApprove", isApprove);
        return map;
    }

    // 当前业务列表
    private List<Task> getCurrentTaskList(String id) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(id).list();
        return tasks;
    }
}
