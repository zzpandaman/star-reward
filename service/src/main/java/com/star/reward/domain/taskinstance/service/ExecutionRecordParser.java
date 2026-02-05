package com.star.reward.domain.taskinstance.service;

import com.star.reward.domain.taskinstance.model.valueobject.ExecutionAction;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionInterval;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionIntervalType;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionRecordVO;
import com.star.reward.domain.taskinstance.model.valueobject.TimeRange;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行记录解析器：将执行记录解析为执行区间
 */
public final class ExecutionRecordParser {

    private ExecutionRecordParser() {
    }

    /**
     * 解析执行记录为计分区间列表（仅包含 isScoring 的区间类型）
     */
    public static List<ExecutionInterval> toExecutionIntervals(List<ExecutionRecordVO> records) {
        List<ExecutionInterval> result = new ArrayList<>();
        if (records == null || records.size() < 2) {
            return result;
        }
        for (int i = 0; i < records.size() - 1; i++) {
            ExecutionRecordVO fromRecord = records.get(i);
            ExecutionRecordVO toRecord = records.get(i + 1);
            if (fromRecord.getAction() == null || toRecord.getAction() == null
                    || fromRecord.getActionTime() == null || toRecord.getActionTime() == null) {
                continue;
            }
            ExecutionIntervalType type = ExecutionIntervalType.from(fromRecord.getAction(), toRecord.getAction());
            if (!type.isScoring()) {
                continue;
            }
            TimeRange range = TimeRange.of(fromRecord.getActionTime(), toRecord.getActionTime());
            result.add(ExecutionInterval.of(range, type));
        }
        return result;
    }
}
