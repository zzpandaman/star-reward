package com.star.reward.interfaces.rest.controller;

import com.star.common.page.PageResponse;
import com.star.common.result.Result;
import com.star.reward.application.service.TaskTemplateApplicationService;
import com.star.reward.interfaces.rest.assembler.TaskTemplateRequestAssembler;
import com.star.reward.interfaces.rest.dto.request.CreateTaskTemplateRequest;
import com.star.reward.interfaces.rest.dto.request.TaskTemplateQueryRequest;
import com.star.reward.interfaces.rest.dto.request.UpdateTaskTemplateRequest;
import com.star.reward.interfaces.rest.dto.response.TaskTemplateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 任务模板控制器
 */
@RestController
@RequestMapping("/api/reward/task-templates")
@RequiredArgsConstructor
public class TaskTemplateController {
    
    private final TaskTemplateApplicationService taskTemplateApplicationService;
    
    /**
     * 分页查询任务模板（POST JSON 传参）
     * 参数：page(默认1)、pageSize(默认10)、templateNo、isPreset、isDeleted、orderBy
     */
    @PostMapping("/query")
    public Result<PageResponse<TaskTemplateResponse>> getAllTaskTemplates(
            @RequestBody(required = false) TaskTemplateQueryRequest request) {
        PageResponse<TaskTemplateResponse> response = taskTemplateApplicationService.getAllTaskTemplates(
                TaskTemplateRequestAssembler.requestToQueryCommand(request));
        return Result.success(response);
    }
    
    /**
     * 根据ID获取任务模板
     */
    @GetMapping("/{id}")
    public Result<TaskTemplateResponse> getTaskTemplateById(@PathVariable Long id) {
        TaskTemplateResponse response = taskTemplateApplicationService.getTaskTemplateById(id);
        return Result.success(response);
    }
    
    /**
     * 创建任务模板
     */
    @PostMapping
    public Result<TaskTemplateResponse> createTaskTemplate(@Validated @RequestBody CreateTaskTemplateRequest request) {
        TaskTemplateResponse response = taskTemplateApplicationService.createTaskTemplate(
                TaskTemplateRequestAssembler.requestToCreateCommand(request));
        return Result.success(response);
    }
    
    /**
     * 更新任务模板
     */
    @PutMapping("/{id}")
    public Result<TaskTemplateResponse> updateTaskTemplate(
            @PathVariable Long id,
            @RequestBody UpdateTaskTemplateRequest request) {
        TaskTemplateResponse response = taskTemplateApplicationService.updateTaskTemplate(id, TaskTemplateRequestAssembler.requestToUpdateCommand(request));
        return Result.success(response);
    }
    
    /**
     * 删除任务模板
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTaskTemplate(@PathVariable Long id) {
        taskTemplateApplicationService.deleteTaskTemplate(id);
        return Result.success();
    }
}
