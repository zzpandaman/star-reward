package com.star.reward.domain.tasktemplate.repository;

import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.domain.tasktemplate.model.query.TaskTemplateQueryParam;

import java.util.List;
import java.util.Optional;

/**
 * 任务模板仓储接口
 */
public interface TaskTemplateRepository {
    
    /**
     * 根据模板编号查询
     */
    Optional<TaskTemplateBO> findByTemplateNo(String templateNo);
    
    /**
     * 根据ID查询
     */
    Optional<TaskTemplateBO> findById(Long id);
    
    /**
     * 保存任务模板
     */
    TaskTemplateBO save(TaskTemplateBO taskTemplate);
    
    /**
     * 更新任务模板
     */
    TaskTemplateBO update(TaskTemplateBO taskTemplate);
    
    /**
     * 删除任务模板（逻辑删除）
     */
    void delete(Long id);
    
    /**
     * 查询所有未删除的任务模板
     */
    List<TaskTemplateBO> findAll();
    
    /**
     * 根据是否为预设查询
     */
    List<TaskTemplateBO> findByIsPreset(Boolean isPreset);

    /**
     * 分页查询任务模板（支持条件）
     */
    List<TaskTemplateBO> listByQuery(TaskTemplateQueryParam param);

    /**
     * 统计符合条件的任务模板数量
     */
    long countByQuery(TaskTemplateQueryParam param);
}
