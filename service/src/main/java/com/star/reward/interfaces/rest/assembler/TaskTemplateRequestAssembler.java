package com.star.reward.interfaces.rest.assembler;

import com.star.reward.application.command.CreateTaskTemplateCommand;
import com.star.reward.application.command.TaskTemplateQueryCommand;
import com.star.reward.application.command.UpdateTaskTemplateCommand;
import com.star.reward.interfaces.rest.dto.request.CreateTaskTemplateRequest;
import com.star.reward.interfaces.rest.dto.request.TaskTemplateQueryRequest;
import com.star.reward.interfaces.rest.dto.request.UpdateTaskTemplateRequest;

/**
 * 任务模板 Request → Command 转换器（Interfaces → Application 边界）
 */
public final class TaskTemplateRequestAssembler {

    private TaskTemplateRequestAssembler() {
    }

    public static TaskTemplateQueryCommand requestToQueryCommand(TaskTemplateQueryRequest request) {
        if (request == null) {
            return new TaskTemplateQueryCommand();
        }
        TaskTemplateQueryCommand cmd = new TaskTemplateQueryCommand();
        cmd.setPage(request.getPage());
        cmd.setPageSize(request.getPageSize());
        return cmd;
    }

    public static CreateTaskTemplateCommand requestToCreateCommand(CreateTaskTemplateRequest request) {
        if (request == null) {
            return null;
        }
        CreateTaskTemplateCommand cmd = new CreateTaskTemplateCommand();
        cmd.setName(request.getName());
        cmd.setDescription(request.getDescription());
        return cmd;
    }

    public static UpdateTaskTemplateCommand requestToUpdateCommand(UpdateTaskTemplateRequest request) {
        if (request == null) {
            return null;
        }
        UpdateTaskTemplateCommand cmd = new UpdateTaskTemplateCommand();
        cmd.setName(request.getName());
        cmd.setDescription(request.getDescription());
        return cmd;
    }
}
