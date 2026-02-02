package com.star.reward.application.service;

import com.star.common.exception.BusinessException;
import com.star.common.page.PageResponse;
import com.star.common.result.ResultCode;
import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.repository.TaskInstanceRepository;
import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.domain.tasktemplate.repository.TaskTemplateRepository;
import com.star.reward.interfaces.rest.dto.request.CreateTaskTemplateRequest;
import com.star.reward.interfaces.rest.dto.request.UpdateTaskTemplateRequest;
import com.star.reward.interfaces.rest.dto.response.TaskTemplateResponse;
import com.star.reward.shared.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
     * 获取所有任务模板
     */
    public PageResponse<TaskTemplateResponse> getAllTaskTemplates() {
        List<TaskTemplateBO> templates = taskTemplateRepository.findAll();
        List<TaskTemplateResponse> responses = templates.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResponse.of(responses, responses.size(), 1, responses.size());
    }
    
    /**
     * 根据ID获取任务模板
     */
    public TaskTemplateResponse getTaskTemplateById(Long id) {
        TaskTemplateBO template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务模板不存在"));
        return toResponse(template);
    }
    
    /**
     * 创建任务模板
     */
    @Transactional
    public TaskTemplateResponse createTaskTemplate(CreateTaskTemplateRequest request) {
        CurrentUserContext user = CurrentUserContext.get();
        
        TaskTemplateBO template = TaskTemplateBO.builder()
                .templateNo(generateTemplateNo())
                .name(request.getName())
                .description(request.getDescription())
                .isPreset(false)
                .isDeleted(false)
                .publishBy(user.getUserNo())
                .publishById(user.getUserId())
                .createBy(user.getUserNo())
                .createById(user.getUserId())
                .createTime(LocalDateTime.now())
                .build();
        
        TaskTemplateBO saved = taskTemplateRepository.save(template);
        return toResponse(saved);
    }
    
    /**
     * 更新任务模板
     */
    @Transactional
    public TaskTemplateResponse updateTaskTemplate(Long id, UpdateTaskTemplateRequest request) {
        TaskTemplateBO template = taskTemplateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务模板不存在"));
        
        if (Boolean.TRUE.equals(template.getIsPreset())) {
            throw new BusinessException(ResultCode.CANNOT_UPDATE_PRESET.getCode(), 
                    ResultCode.CANNOT_UPDATE_PRESET.getMessage());
        }
        
        CurrentUserContext user = CurrentUserContext.get();
        
        if (request.getName() != null) {
            template.setName(request.getName());
        }
        if (request.getDescription() != null) {
            template.setDescription(request.getDescription());
        }
        template.setUpdateBy(user.getUserNo());
        template.setUpdateById(user.getUserId());
        template.setUpdateTime(LocalDateTime.now());
        
        TaskTemplateBO updated = taskTemplateRepository.update(template);
        return toResponse(updated);
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
    
    /**
     * 生成模板编号
     */
    private String generateTemplateNo() {
        return "TMP" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    /**
     * 转换为响应
     */
    private TaskTemplateResponse toResponse(TaskTemplateBO bo) {
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
