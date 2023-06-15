package com.winhou.wechat.service;

/**
 * @Author wiho
 * @Date 2023/6/15 12:45
 * @Description 微信信息推送服务类
 * @Since version-1.0
 */
public interface MessageService {
    // 推送待审批人员
    void pushPendingMessage(Long processId, Long userId, String taskId);

    // 审批后推送提交审批人员
    void pushProcessedMessage(Long processId, Long userId, Integer status);
}
