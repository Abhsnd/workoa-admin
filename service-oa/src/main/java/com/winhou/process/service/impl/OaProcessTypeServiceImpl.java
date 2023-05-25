package com.winhou.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winhou.model.process.ProcessType;
import com.winhou.process.mapper.OaProcessTypeMapper;
import com.winhou.process.service.OaProcessTypeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author winhou
 * @since 2023-05-25
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

}
