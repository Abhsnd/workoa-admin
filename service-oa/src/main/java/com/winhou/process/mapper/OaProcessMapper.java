package com.winhou.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winhou.model.process.Process;
import com.winhou.vo.process.ProcessQueryVo;
import com.winhou.vo.process.ProcessVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author winhou
 * @since 2023-05-27
 */
public interface OaProcessMapper extends BaseMapper<Process> {
    // 审批管理列表
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, @Param("vo") ProcessQueryVo processQueryVo);
}
