package com.winhou.auth.activiti;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class ProcessTestDemo1 {
    // 资源管理类
    @Autowired
    private RepositoryService repositoryService;

    // 流程运行管理类
    @Autowired
    private RuntimeService runtimeService;

    // 任务管理类
    @Autowired
    private TaskService taskService;

    // 流程流程
    @Test
    public void deployProcess() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban.bpmn20.xml")
                .name("加班申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    // 启动流程实例
    @Test
    public void startProcessInstance() {
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("assignee1", "lucy");
        variables.put("assignee2", "mary");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban", variables);
        System.out.println(processInstance.getProcessDefinitionId());
        System.out.println(processInstance.getId());
    }

    // 查询当前个人待执行的任务
    @Test
    public void findTaskList() {
        String assign = "lucy";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assign).list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }
}
