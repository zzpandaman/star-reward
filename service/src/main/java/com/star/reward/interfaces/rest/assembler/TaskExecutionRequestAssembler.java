package com.star.reward.interfaces.rest.assembler;

import com.star.reward.application.command.StartTaskCommand;
import com.star.reward.interfaces.rest.dto.request.StartTaskRequest;

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
}
