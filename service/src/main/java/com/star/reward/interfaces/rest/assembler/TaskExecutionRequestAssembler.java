package com.star.reward.interfaces.rest.assembler;

import com.star.reward.application.command.StartTaskCommand;
import com.star.reward.application.command.TaskExecutionQueryCommand;
import com.star.reward.application.command.TaskOperationCommand;
import com.star.reward.interfaces.rest.dto.request.StartTaskRequest;
import com.star.reward.interfaces.rest.dto.request.TaskExecutionQueryRequest;
import com.star.reward.interfaces.rest.dto.request.TaskOperationRequest;
import org.springframework.beans.BeanUtils;

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
        cmd.setClientTime(request.getClientTime());
        return cmd;
    }

    public static TaskOperationCommand requestToTaskOperationCommand(Long id, TaskOperationRequest request) {
        TaskOperationCommand cmd = new TaskOperationCommand();
        cmd.setId(id);
        cmd.setClientTime(request.getClientTime());
        return cmd;
    }

    public static TaskExecutionQueryCommand requestToQueryCommand(TaskExecutionQueryRequest request) {
        if (request == null) {
            return new TaskExecutionQueryCommand();
        }
        TaskExecutionQueryCommand cmd = new TaskExecutionQueryCommand();
        BeanUtils.copyProperties(request, cmd);
        return cmd;
    }
}
