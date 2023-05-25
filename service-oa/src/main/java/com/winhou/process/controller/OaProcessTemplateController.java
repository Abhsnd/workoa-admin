package com.winhou.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.winhou.common.result.Result;
import com.winhou.model.process.ProcessTemplate;
import com.winhou.process.service.OaProcessTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author winhou
 * @since 2023-05-25
 */
@Api(value = "审批模板管理", tags = "审批模板管理")
@RestController
@RequestMapping("/admin/process/processTemplate")
public class OaProcessTemplateController {
    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page, @PathVariable Long limit) {
        Page<ProcessTemplate> pageParam = new Page<>(page, limit);
        IPage<ProcessTemplate> pageModel = oaProcessTemplateService.selectPageProcessTemplate(pageParam);
        return Result.ok(pageModel);
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "根据id获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        oaProcessTemplateService.save(processTemplate);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result udpate(@RequestBody ProcessTemplate processTemplate) {
        oaProcessTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        oaProcessTemplateService.removeById(id);
        return Result.ok();
    }
}

