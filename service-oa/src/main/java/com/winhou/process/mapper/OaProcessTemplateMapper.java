package com.winhou.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winhou.model.process.ProcessTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 审批模板 Mapper 接口
 * </p>
 *
 * @author winhou
 * @since 2023-05-25
 */
@Mapper
public interface OaProcessTemplateMapper extends BaseMapper<ProcessTemplate> {

}
