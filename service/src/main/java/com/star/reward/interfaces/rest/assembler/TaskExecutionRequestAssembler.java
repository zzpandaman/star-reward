package com.star.reward.interfaces.rest.assembler;

import com.star.reward.application.command.StartTaskCommand;
import com.star.reward.application.command.TaskExecutionQueryCommand;
import com.star.reward.interfaces.rest.dto.request.StartTaskRequest;
import com.star.reward.interfaces.rest.dto.request.TaskExecutionQueryRequest;

/**
 * 任务执行 Request → Command 转换器（Interfaces → Application 边界）
 */
public final class TaskExecutionRequestAssembler {

    private TaskExecutionRequestAssembler() {
    }

    public static StartTaskCommand requestToStartCommand(StartTaskRequest request) {
        if (request == null) {
            return null;
        }
        StartTaskCommand cmd = new StartTaskCommand();
        cmd.setTaskTemplateId(request.getTaskTemplateId());
        return cmd;
    }

    public static TaskExecutionQueryCommand requestToQueryCommand(TaskExecutionQueryRequest request) {
        if (request == null) {
            return new TaskExecutionQueryCommand();
        }
        TaskExecutionQueryCommand cmd = new TaskExecutionQueryCommand();
        cmd.setPage(request.getPage());
        cmd.setPageSize(request.getPageSize());
        cmd.setState(request.getState());
        return cmd;
    }
}
