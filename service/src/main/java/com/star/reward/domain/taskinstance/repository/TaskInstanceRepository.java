package com.star.reward.domain.taskinstance.repository;

import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;

import java.util.List;
import java.util.Optional;

/**
 * 任务实例仓储接口
 */
public interface TaskInstanceRepository {
    
    /**
     * 根据实例编号查询
     */
    Optional<TaskInstanceBO> findByInstanceNo(String instanceNo);
    
    /**
     * 根据ID查询
     */
    Optional<TaskInstanceBO> findById(Long id);
    
    /**
     * 保存任务实例
     */
    TaskInstanceBO save(TaskInstanceBO taskInstance);
    
    /**
     * 更新任务实例
     */
    TaskInstanceBO update(TaskInstanceBO taskInstance);
    
    /**
     * 删除任务实例（逻辑删除）
     */
    void delete(Long id);
    
    /**
     * 根据模板编号查询
     */
    List<TaskInstanceBO> findByTemplateNo(String templateNo);
    
    /**
     * 根据执行人ID查询
     */
    List<TaskInstanceBO> findByExecuteById(Long executeById);
    
    /**
     * 根据实例状态查询
     */
    List<TaskInstanceBO> findByInstanceState(InstanceState instanceState);
    
    /**
     * 根据执行人ID和状态查询
     */
    List<TaskInstanceBO> findByExecuteByIdAndState(Long executeById, InstanceState instanceState);
}
