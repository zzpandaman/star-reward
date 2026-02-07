package com.star.reward.interfaces.rest.controller;

import com.star.common.page.PageResponse;
import com.star.common.result.Result;
import com.star.reward.application.service.TaskExecutionApplicationService;
import com.star.reward.interfaces.rest.assembler.TaskExecutionRequestAssembler;
import com.star.reward.interfaces.rest.dto.request.StartTaskRequest;
import com.star.reward.interfaces.rest.dto.request.TaskExecutionQueryRequest;
import com.star.reward.interfaces.rest.dto.request.TaskOperationRequest;
import com.star.reward.interfaces.rest.dto.response.TaskExecutionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务执行控制器
 */
@RestController
@RequestMapping("/api/reward/task-executions")
@RequiredArgsConstructor
public class TaskExecutionController {

    private final TaskExecutionApplicationService taskExecutionApplicationService;

    /**
     * 根据实例编号获取任务执行详情
     */
    @GetMapping("/by-no/{instanceNo}")
    public Result<TaskExecutionResponse> getTaskExecutionByInstanceNo(
            @PathVariable String instanceNo) {
        TaskExecutionResponse response = taskExecutionApplicationService.getTaskExecutionByInstanceNo(instanceNo);
        return Result.success(response);
    }

    /**
     * 获取任务执行列表
     * 参数：page(默认1)、pageSize(默认10)、state(ongoing|running|paused|all，默认ongoing)
     */
    @PostMapping
    public Result<PageResponse<TaskExecutionResponse>> getTaskExecutions(
            @RequestBody(required = false) TaskExecutionQueryRequest request) {
        PageResponse<TaskExecutionResponse> response = taskExecutionApplicationService
                .getTaskExecutions(TaskExecutionRequestAssembler.requestToQueryCommand(request));
        return Result.success(response);
    }

    /**
     * 开始任务
     */
    @PostMapping("/start")
    public Result<TaskExecutionResponse> startTask(@Validated @RequestBody StartTaskRequest request) {
        TaskExecutionResponse response = taskExecutionApplicationService.startTask(
                TaskExecutionRequestAssembler.requestToStartCommand(request));
        return Result.success(response);
    }

    /**
     * 暂停任务
     */
    @PostMapping("/pause")
    public Result<TaskExecutionResponse> pauseTask(@Validated @RequestBody TaskOperationRequest request) {
        TaskExecutionResponse response = taskExecutionApplicationService.pauseTask(
                TaskExecutionRequestAssembler.requestToTaskOperationCommand(request));
        return Result.success(response);
    }

    /**
     * 恢复任务
     */
    @PostMapping("/resume")
    public Result<TaskExecutionResponse> resumeTask(@Validated @RequestBody TaskOperationRequest request) {
        TaskExecutionResponse response = taskExecutionApplicationService.resumeTask(
                TaskExecutionRequestAssembler.requestToTaskOperationCommand(request));
        return Result.success(response);
    }

    /**
     * 完成任务
     */
    @PostMapping("/complete")
    public Result<TaskExecutionResponse> completeTask(@Validated @RequestBody TaskOperationRequest request) {
        TaskExecutionResponse response = taskExecutionApplicationService.completeTask(
                TaskExecutionRequestAssembler.requestToTaskOperationCommand(request));
        return Result.success(response);
    }

    /**
     * 取消任务
     */
    @PostMapping("/cancel")
    public Result<TaskExecutionResponse> cancelTask(@Validated @RequestBody TaskOperationRequest request) {
        TaskExecutionResponse response = taskExecutionApplicationService.cancelTask(
                TaskExecutionRequestAssembler.requestToTaskOperationCommand(request));
        return Result.success(response);
    }
}
