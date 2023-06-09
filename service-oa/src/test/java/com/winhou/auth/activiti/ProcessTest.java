package com.winhou.auth.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProcessTest {
    // 资源管理类
    @Autowired
    private RepositoryService repositoryService;

    // 流程运行管理类
    @Autowired
    private RuntimeService runtimeService;

    // 任务管理类
    @Autowired
    private TaskService taskService;

    // 历史管理类
    @Autowired
    private HistoryService historyService;

    // 单个文件部署
    @Test
    public void deployProcess() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    // 启动流程实例
    @Test
    public void startProcess() {
        ProcessInstance process = runtimeService.startProcessInstanceByKey("qingjia");
        System.out.println("流程定义id：" + process.getProcessDefinitionId());
        System.out.println("流程实例id：" + process.getId());
        System.out.println("当前活动Id：" + process.getActivityId());

    }

    // 查询当前个人待执行的任务
    @Test
    public void findTaskList() {
        String assign = "zhangsan";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assign).list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    @Test
    public void delete() {
        runtimeService.deleteProcessInstance("66b8bcdd-fa1a-11ed-933b-0a0027000003", "null");
    }

    // 处理当前任务
    @Test
    public void completeTask() {
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan")
                .singleResult();
        taskService.complete(task.getId());
    }

    // 查询已处理历史任务
    @Test
    public void findCompleteTask() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("lisi")
                .finished().list();
        for (HistoricTaskInstance historicTaskInstance : list) {
            System.out.println("流程实例id：" + historicTaskInstance.getProcessInstanceId());
            System.out.println("任务id：" + historicTaskInstance.getId());
            System.out.println("任务负责人：" + historicTaskInstance.getAssignee());
            System.out.println("任务名称：" + historicTaskInstance.getName());
        }
    }

    // 启动流程实例,添加businessKey
    @Test
    public void startUpProcessAddBusinessKey() {
        String businessKey = "1";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia", businessKey);
        System.out.println("业务id:" + processInstance.getBusinessKey());
    }

    // 全部实例挂起
    @Test
    public void suspendProcessInstanceAll() {
        // 获取流程定义的对象
        ProcessDefinition qingjia = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("qingjia")
                .singleResult();
        // 流程定义对象的状态
        boolean suspended = qingjia.isSuspended();
        // 如果被挂起则激活
        if (suspended) {
            repositoryService.activateProcessDefinitionById(qingjia.getId(), true, null);
            System.out.println(qingjia.getId() + "激活了");
        } else {    // 如果被激活则挂起
            repositoryService.suspendProcessDefinitionById(qingjia.getId(), true, null);
            System.out.println(qingjia.getId() + "挂起");
        }
    }

}
