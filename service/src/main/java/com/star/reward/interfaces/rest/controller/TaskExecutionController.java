package com.star.reward.interfaces.rest.controller;

import com.star.common.page.PageResponse;
import com.star.common.result.Result;
import com.star.reward.application.service.TaskExecutionApplicationService;
import com.star.reward.interfaces.rest.assembler.TaskExecutionRequestAssembler;
import com.star.reward.interfaces.rest.dto.request.StartTaskRequest;
import com.star.reward.interfaces.rest.dto.request.TaskExecutionQueryRequest;
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
     * 根据实例编号获取任务执行详情
     */
    @GetMapping("/by-no/{instanceNo}")
    public Result<TaskExecutionResponse> getTaskExecutionByInstanceNo(
            @PathVariable String instanceNo) {
        TaskExecutionResponse response = taskExecutionApplicationService.getTaskExecutionByInstanceNo(instanceNo);
        return Result.success(response);
    }

    /**
     * 获取任务执行列表（GET，query 参数）
     *
     * @param state 状态过滤：ongoing|running|paused|all，默认 ongoing
     * @param page 页码，默认 1
     * @param pageSize 每页条数，默认 10
     */
    @GetMapping
    public Result<PageResponse<TaskExecutionResponse>> getTaskExecutionsGet(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        TaskExecutionQueryRequest request = new TaskExecutionQueryRequest();
        request.setState(state != null ? state : "ongoing");
        request.setPage(page != null && page > 0 ? page : 1);
        request.setPageSize(pageSize != null && pageSize > 0 ? pageSize : 10);
        PageResponse<TaskExecutionResponse> response = taskExecutionApplicationService
                .getTaskExecutions(TaskExecutionRequestAssembler.requestToQueryCommand(request));
        return Result.success(response);
    }

    /**
     * 获取任务执行列表（POST，Request body）
     *
     * @param request 查询参数：page(默认1)、pageSize(默认10)、state(ongoing|running|paused|all，默认ongoing)
     */
    @PostMapping
    public Result<PageResponse<TaskExecutionResponse>> getTaskExecutions(
            @RequestBody TaskExecutionQueryRequest request) {
        PageResponse<TaskExecutionResponse> response = taskExecutionApplicationService
                .getTaskExecutions(TaskExecutionRequestAssembler.requestToQueryCommand(request));
        return Result.success(response);
    }
    
    /**
     * 开始任务
     */
    @PostMapping("/start")
    public Result<TaskExecutionResponse> startTask(@Validated @RequestBody StartTaskRequest request) {
        TaskExecutionResponse response = taskExecutionApplicationService.startTask(TaskExecutionRequestAssembler.requestToStartCommand(request));
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
