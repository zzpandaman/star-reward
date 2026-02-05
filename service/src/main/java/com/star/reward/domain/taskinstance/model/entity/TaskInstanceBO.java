package com.star.reward.domain.taskinstance.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.star.common.attribute.AttributeHolder;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionRecordVO;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.taskinstance.model.valueobject.PointsCalculationSnapshot;
import com.star.reward.domain.tasktemplate.model.valueobject.MinUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 任务实例领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInstanceBO implements AttributeHolder {

    private static final String KEY_EXECUTION_RECORDS = "executionRecords";
    private static final String KEY_POINTS_CALCULATION_SNAPSHOT = "pointsCalculationSnapshot";

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 实例编号
     */
    private String instanceNo;

    /**
     * 模板编号
     */
    private String templateNo;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 1最小单位积分
     */
    private Integer minUnitPoint;

    /**
     * 最小单位
     */
    private MinUnit minUnit;

    /**
     * 发布人账号
     */
    private String publishBy;

    /**
     * 发布人ID
     */
    private Long publishById;

    /**
     * 实例开始时间
     */
    private LocalDateTime startTime;

    /**
     * 实例结束时间
     */
    private LocalDateTime endTime;

    /**
     * 实例状态
     */
    private InstanceState instanceState;

    /**
     * 执行人账号
     */
    private String executeBy;

    /**
     * 执行人ID
     */
    private Long executeById;

    /**
     * 是否为预设任务。0-否 1-是
     */
    private Boolean isPreset;

    /**
     * 是否删除。0-否 1-是
     */
    private Boolean isDeleted;

    /**
     * 创建人账号
     */
    private String createBy;

    /**
     * 创建人ID
     */
    private Long createById;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人账号
     */
    private String updateBy;

    /**
     * 更新人ID
     */
    private Long updateById;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 扩展字段（JSONObject，存储执行记录、积分计算快照等）
     */
    @JsonIgnore
    private com.alibaba.fastjson2.JSONObject attributes;

    @Override
    public com.alibaba.fastjson2.JSONObject getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(com.alibaba.fastjson2.JSONObject attributes) {
        this.attributes = attributes;
    }

    public List<ExecutionRecordVO> getExecutionRecords() {
        List<ExecutionRecordVO> list = parseArrayAttributes(KEY_EXECUTION_RECORDS, ExecutionRecordVO.class);
        return list != null ? list : Collections.emptyList();
    }

    public void setExecutionRecords(List<ExecutionRecordVO> records) {
        if (records != null && !records.isEmpty()) {
            addArrayAttributes(KEY_EXECUTION_RECORDS, records, true, ExecutionRecordVO.class);
        }
    }

    public void appendExecutionRecord(ExecutionRecordVO record) {
        if (record != null) {
            addArrayAttributes(KEY_EXECUTION_RECORDS, record, false, ExecutionRecordVO.class);
        }
    }

    public PointsCalculationSnapshot getPointsCalculationSnapshot() {
        return parseAttributes(KEY_POINTS_CALCULATION_SNAPSHOT, PointsCalculationSnapshot.class);
    }

    public void setPointsCalculationSnapshot(PointsCalculationSnapshot snapshot) {
        addAttributes(KEY_POINTS_CALCULATION_SNAPSHOT, snapshot);
    }
}
