package com.winhou.vo.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "任务审批")
public class ApprovalVo {
    private Long processId;

    private String taskId;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "审批描述")
    private String description;
}
