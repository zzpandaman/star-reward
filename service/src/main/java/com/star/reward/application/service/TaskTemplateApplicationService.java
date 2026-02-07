package com.star.reward.application.service;

import com.star.common.exception.BusinessException;
import com.star.common.page.PageResponse;
import com.star.common.result.ResultCode;
import com.star.reward.application.assembler.TaskTemplateAssembler;
import com.star.reward.application.command.CreateTaskTemplateCommand;
import com.star.reward.application.command.TaskTemplateQueryCommand;
import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.repository.TaskInstanceRepository;
import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.domain.tasktemplate.model.query.TaskTemplateQueryParam;
import com.star.reward.domain.tasktemplate.repository.TaskTemplateRepository;
import com.star.reward.application.command.UpdateTaskTemplateCommand;
import com.star.reward.interfaces.rest.dto.response.TaskTemplateResponse;
import com.star.reward.shared.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.star.reward.domain.shared.util.RewardNoGenerator;
import com.star.reward.domain.tasktemplate.model.constant.TaskTemplateConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务模板应用服务
 */
@Service
@RequiredArgsConstructor
public class TaskTemplateApplicationService {

    private final TaskTemplateRepository taskTemplateRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    
    /**
     * 分页查询任务模板（支持条件：templateNo、isPreset、isDeleted、orderBy）
     */
    public PageResponse<TaskTemplateResponse> getAllTaskTemplates(TaskTemplateQueryCommand command) {
        TaskTemplateQueryParam param = TaskTemplateAssembler.commandToQueryParam(command);
        List<TaskTemplateBO> templates = taskTemplateRepository.listByQuery(param);
        long total = taskTemplateRepository.countByQuery(param);
        List<TaskTemplateResponse> responses = templates.stream()
                .map(TaskTemplateAssembler::entityToResponse)
                .collect(Collectors.toList());
        return PageResponse.of(responses, total, param);
    }
    
    /**
     * 根据ID获取任务模板
     */
    public TaskTemplateResponse getTaskTemplateById(Long id) {
        TaskTemplateBO template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务模板不存在"));
        return TaskTemplateAssembler.entityToResponse(template);
    }

    /**
     * 创建任务模板
     */
    @Transactional
    public TaskTemplateResponse createTaskTemplate(CreateTaskTemplateCommand command) {
        CurrentUserContext user = CurrentUserContext.get();

        TaskTemplateBO template = TaskTemplateAssembler.createCommandToEntity(command);
        template.initForCreate(
                RewardNoGenerator.generate(TaskTemplateConstants.TEMPLATE_NO_PREFIX),
                user.getUserNo(), user.getUserId(), LocalDateTime.now());

        TaskTemplateBO saved = taskTemplateRepository.save(template);
        return TaskTemplateAssembler.entityToResponse(saved);
    }
    
    /**
     * 更新任务模板
     */
    @Transactional
    public TaskTemplateResponse updateTaskTemplate(Long id, UpdateTaskTemplateCommand command) {
        TaskTemplateBO template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务模板不存在"));
        
        if (Boolean.TRUE.equals(template.getIsPreset())) {
            throw new BusinessException(ResultCode.CANNOT_UPDATE_PRESET.getCode(),
                    ResultCode.CANNOT_UPDATE_PRESET.getMessage());
        }

        CurrentUserContext user = CurrentUserContext.get();
        TaskTemplateBO patch = TaskTemplateAssembler.updateCommandToPartialEntity(id, command,
                user.getUserNo(), user.getUserId(), LocalDateTime.now());
        TaskTemplateBO updated = taskTemplateRepository.update(patch);
        return TaskTemplateAssembler.entityToResponse(updated);
    }

    /**
     * 删除任务模板
     */
    @Transactional
    public void deleteTaskTemplate(Long id) {
        TaskTemplateBO template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务模板不存在"));
        
        if (Boolean.TRUE.equals(template.getIsPreset())) {
            throw new BusinessException(ResultCode.CANNOT_UPDATE_PRESET.getCode(), "不能删除预设任务模板");
        }
        
        // 检查是否有执行记录
        List<TaskInstanceBO> instances = taskInstanceRepository.findByTemplateNo(template.getTemplateNo());
        if (!instances.isEmpty()) {
            throw new BusinessException(ResultCode.CANNOT_DELETE_WITH_RECORDS.getCode(), 
                    "不能删除有执行记录的任务模板");
        }
        
        taskTemplateRepository.delete(id);
    }
}
