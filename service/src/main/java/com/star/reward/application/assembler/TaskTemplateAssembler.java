package com.star.reward.application.assembler;

import com.star.reward.application.command.CreateTaskTemplateCommand;
import com.star.reward.application.command.TaskTemplateQueryCommand;
import com.star.reward.application.command.UpdateTaskTemplateCommand;
import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.domain.tasktemplate.model.query.TaskTemplateQueryParam;
import com.star.reward.interfaces.rest.dto.response.TaskTemplateResponse;
import com.star.reward.shared.context.CurrentUserContext;

import java.time.LocalDateTime;

/**
 * 任务模板转换器（Application → Domain 边界）
 * Command → Entity、Entity → Response
 */
public final class TaskTemplateAssembler {

    private TaskTemplateAssembler() {
    }

    /**
     * Command → QueryParam（供 Repository listByQuery 使用）
     * page/pageSize 为 null 或 <=0 时使用默认 1、10
     */
    public static TaskTemplateQueryParam commandToQueryParam(TaskTemplateQueryCommand command) {
        TaskTemplateQueryParam param = command == null
                ? TaskTemplateQueryParam.builder().build()
                : TaskTemplateQueryParam.builder()
                .templateNo(command.getTemplateNo())
                .isPreset(command.getIsPreset())
                .isDeleted(command.getIsDeleted())
                .orderBy(command.getOrderBy())
                .build();
        int page = command != null && command.getPage() > 0 ? command.getPage() : 1;
        int pageSize = command != null && command.getPageSize() > 0 ? command.getPageSize() : 10;
        param.setPage(page);
        param.setPageSize(pageSize);
        param.setPublishBy(CurrentUserContext.get().getUserNo());
        return param;
    }

    /**
     * Command → Entity（仅映射业务字段）
     */
    public static TaskTemplateBO createCommandToEntity(CreateTaskTemplateCommand command) {
        if (command == null) {
            return null;
        }
        TaskTemplateBO bo = new TaskTemplateBO();
        bo.setName(command.getName());
        bo.setDescription(command.getDescription());
        return bo;
    }

    /**
     * UpdateCommand → Partial Entity（仅 id、非空业务字段、审计字段，供 updateByPrimaryKeySelective 用）
     */
    public static TaskTemplateBO updateCommandToPartialEntity(Long id, UpdateTaskTemplateCommand command,
                                                              String updateBy, Long updateById, LocalDateTime updateTime) {
        if (id == null) {
            return null;
        }
        TaskTemplateBO bo = new TaskTemplateBO();
        bo.setId(id);
        bo.setUpdateBy(updateBy);
        bo.setUpdateById(updateById);
        bo.setUpdateTime(updateTime);
        if (command != null) {
            if (command.getName() != null) {
                bo.setName(command.getName());
            }
            if (command.getDescription() != null) {
                bo.setDescription(command.getDescription());
            }
        }
        return bo;
    }

    /**
     * Entity → Response
     */
    public static TaskTemplateResponse entityToResponse(TaskTemplateBO bo) {
        if (bo == null) {
            return null;
        }
        return TaskTemplateResponse.builder()
                .id(bo.getId())
                .templateNo(bo.getTemplateNo())
                .name(bo.getName())
                .description(bo.getDescription())
                .isPreset(bo.getIsPreset())
                .createTime(bo.getCreateTime())
                .updateTime(bo.getUpdateTime())
                .build();
    }
}
