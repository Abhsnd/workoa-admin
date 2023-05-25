package com.winhou.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.winhou.model.process.ProcessType;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author winhou
 * @since 2023-05-25
 */
@Mapper
public interface OaProcessTypeMapper extends BaseMapper<ProcessType> {

}
