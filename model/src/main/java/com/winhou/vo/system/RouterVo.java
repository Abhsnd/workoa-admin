package com.winhou.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author wiho
 * @Date 2023/5/11 17:11
 * @Description 路由配置信息
 * @Since version-1.0
 */
@ApiModel(description = "路由配置信息")
@Data
public class RouterVo {
    @ApiModelProperty(value = "路由地址")
    private String path;

    @ApiModelProperty(value = "是否隐藏路由，为true则侧边栏不显示")
    private boolean hidden;

    @ApiModelProperty(value = "组件地址")
    private String component;

    @ApiModelProperty(value = "一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面")
    private Boolean alwaysShow;

    @ApiModelProperty(value = "其他元素")
    private MetaVo meta;

    @ApiModelProperty(value = "子路由")
    private List<RouterVo> children;

}
