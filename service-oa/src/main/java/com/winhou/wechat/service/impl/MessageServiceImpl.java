package com.winhou.wechat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.winhou.auth.service.SysUserService;
import com.winhou.model.process.Process;
import com.winhou.model.process.ProcessTemplate;
import com.winhou.model.system.SysUser;
import com.winhou.process.service.OaProcessService;
import com.winhou.process.service.OaProcessTemplateService;
import com.winhou.security.custom.LoginUserInfoHelper;
import com.winhou.wechat.service.MessageService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private OaProcessService oaProcessService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    private WxMpService wxMpService;

    // 推送待审批人员
    @Override
    public void pushPendingMessage(Long processId, Long userId, String taskId) {
        // 获取流程信息
        Process process = oaProcessService.getById(processId);
        // 获取推送人信息
        SysUser sysUser = sysUserService.getById(userId);
        // 获取审批模板信息
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(process.getProcessTemplateId());

        // 获取提交人信息
        SysUser submitSysUser = sysUserService.getById(process.getUserId());
        // 获取推送人的openId
        String openId = sysUser.getOpenId();
        if (StringUtils.isEmpty(openId)) {
            // TODO 测试接口使用
            openId = "ovD0o6ngm2jcDfKyOSi0izORnN0w";
        }

        // 模板信息基础设置
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)     // 要推送的用户
                .templateId("VyRYo82Ye20K2t_lFjmhkBGAkw6SDBgQoIb2hnDIuHw")  // 模板信息的id
                .url("http://ggkt1.vipgz1.91tunnel.com/#/show/" + processId + "/" + taskId)    // 点击信息后跳转的url
                .build();

        // 模板信息的内容
        JSONObject jsonObject = JSONObject.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n ");
        }

        templateMessage.addData(new WxMpTemplateData("keyword1", submitSysUser.getName()+"提交了"+processTemplate.getName()+"审批申请，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));

        try {
            // 调用微信工具类推送信息
            String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            System.out.println("msg = " + msg);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    // 审批后推送提交审批人员
    @Override
    public void pushProcessedMessage(Long processId, Long userId, Integer status) {
        // 获取流程信息
        Process process = oaProcessService.getById(processId);
        // 获取推送人信息
        SysUser sysUser = sysUserService.getById(userId);
        // 获取审批模板信息
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(process.getProcessTemplateId());

        // 获取当前用户
        SysUser currentSysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        // 获取推送人的openId
        String openId = sysUser.getOpenId();
        if (StringUtils.isEmpty(openId)) {
            // TODO 测试接口使用
            openId = "ovD0o6ngm2jcDfKyOSi0izORnN0w";
        }

        // 模板信息基础设置
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)     // 要推送的用户
                .templateId("uXEmHiWqXmiuHt9nYUzZbjhBIbb1PO47MK0I829_8fc")  // 模板信息的id
                .url("http://ggkt1.vipgz1.91tunnel.com/#/show/"+processId+"/0")    // 点击信息后跳转的url
                .build();

        // 模板信息的内容
        JSONObject jsonObject = JSONObject.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n ");
        }

        templateMessage.addData(new WxMpTemplateData("keyword1", "你发起的"+processTemplate.getName()+"审批申请已经被处理了，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword4", currentSysUser.getName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword5", status == 1 ? "审批通过" : "审批拒绝", status == 1 ? "#009966" : "#FF0033"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));

        try {
            String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            System.out.println("msg = " + msg);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
