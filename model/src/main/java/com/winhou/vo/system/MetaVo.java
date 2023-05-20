package com.winhou.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @Author wiho
 * @Date 2023/5/11 17:14
 * @Description 路由显示信息
 * @Since version-1.0
 */
@ApiModel(description = "路由显示对象")
@Data
public class MetaVo {
    @ApiModelProperty(value = "路由名字")
    private String title;

    @ApiModelProperty(value = "路由图标路径")
    private String icon;

    public MetaVo() {
    }

    public MetaVo(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }
}
