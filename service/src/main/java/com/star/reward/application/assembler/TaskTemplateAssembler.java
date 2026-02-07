package com.star.reward.application.assembler;

import com.star.reward.application.command.CreateTaskTemplateCommand;
import com.star.reward.application.command.UpdateTaskTemplateCommand;
import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.interfaces.rest.dto.response.TaskTemplateResponse;

import java.time.LocalDateTime;

/**
 * 任务模板转换器（Application → Domain 边界）
 * Command → Entity、Entity → Response
 */
public final class TaskTemplateAssembler {

    private TaskTemplateAssembler() {
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
