package com.star.reward.interfaces.rest.controller;

import com.star.common.page.PageResponse;
import com.star.common.result.Result;
import com.star.reward.application.service.TaskExecutionApplicationService;
import com.star.reward.interfaces.rest.dto.request.StartTaskRequest;
import com.star.reward.interfaces.rest.dto.response.TaskExecutionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 任务执行控制器
 */
@RestController
@RequestMapping("/api/reward/task-executions")
@RequiredArgsConstructor
public class TaskExecutionController {
    
    private final TaskExecutionApplicationService taskExecutionApplicationService;
    
    /**
     * 获取任务执行列表
     */
    @GetMapping
    public Result<PageResponse<TaskExecutionResponse>> getTaskExecutions() {
        PageResponse<TaskExecutionResponse> response = taskExecutionApplicationService.getTaskExecutions();
        return Result.success(response);
    }
    
    /**
     * 开始任务
     */
    @PostMapping("/start")
    public Result<TaskExecutionResponse> startTask(@Validated @RequestBody StartTaskRequest request) {
        TaskExecutionResponse response = taskExecutionApplicationService.startTask(request);
        return Result.success(response);
    }
    
    /**
     * 暂停任务
     */
    @PostMapping("/{id}/pause")
    public Result<TaskExecutionResponse> pauseTask(@PathVariable Long id) {
        TaskExecutionResponse response = taskExecutionApplicationService.pauseTask(id);
        return Result.success(response);
    }
    
    /**
     * 恢复任务
     */
    @PostMapping("/{id}/resume")
    public Result<TaskExecutionResponse> resumeTask(@PathVariable Long id) {
        TaskExecutionResponse response = taskExecutionApplicationService.resumeTask(id);
        return Result.success(response);
    }
    
    /**
     * 完成任务
     */
    @PostMapping("/{id}/complete")
    public Result<TaskExecutionResponse> completeTask(@PathVariable Long id) {
        TaskExecutionResponse response = taskExecutionApplicationService.completeTask(id);
        return Result.success(response);
    }
    
    /**
     * 取消任务
     */
    @PostMapping("/{id}/cancel")
    public Result<TaskExecutionResponse> cancelTask(@PathVariable Long id) {
        TaskExecutionResponse response = taskExecutionApplicationService.cancelTask(id);
        return Result.success(response);
    }
}
