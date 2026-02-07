package com.star.reward.domain.taskinstance.service;

import com.star.reward.domain.taskinstance.model.valueobject.ExecutionAction;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionInterval;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionIntervalType;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionRecordVO;
import com.star.reward.domain.taskinstance.model.valueobject.TimeRange;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * 执行记录解析器：将执行记录解析为执行区间
 */
public final class ExecutionRecordParser {

    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("+8");

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

    /**
     * 计算总暂停时长（秒）
     */
    public static long computeTotalPausedDuration(List<ExecutionRecordVO> records) {
        if (records == null || records.size() < 2) {
            return 0L;
        }
        long total = 0L;
        for (int i = 0; i < records.size() - 1; i++) {
            ExecutionRecordVO fromRecord = records.get(i);
            ExecutionRecordVO toRecord = records.get(i + 1);
            if (fromRecord.getAction() == ExecutionAction.PAUSE && toRecord.getAction() == ExecutionAction.RESUME
                    && fromRecord.getActionTime() != null && toRecord.getActionTime() != null) {
                total += Duration.between(fromRecord.getActionTime(), toRecord.getActionTime()).getSeconds();
            }
        }
        return total;
    }

    /**
     * 获取最近一次暂停时刻（秒级时间戳），仅当最后一条为 PAUSE 时有效
     */
    public static Long getLastPausedTime(List<ExecutionRecordVO> records) {
        if (records == null || records.isEmpty()) {
            return null;
        }
        ExecutionRecordVO last = records.get(records.size() - 1);
        if (last.getAction() != ExecutionAction.PAUSE || last.getActionTime() == null) {
            return null;
        }
        return last.getActionTime().toEpochSecond(ZONE_OFFSET);
    }

    /**
     * 计算到最近一次 PAUSE 为止的已执行秒数，仅当最后一条为 PAUSE 时有效
     */
    public static Long computeAccumulatedExecutionSeconds(List<ExecutionRecordVO> records) {
        if (records == null || records.isEmpty()) {
            return null;
        }
        ExecutionRecordVO last = records.get(records.size() - 1);
        if (last.getAction() != ExecutionAction.PAUSE) {
            return null;
        }
        List<ExecutionInterval> intervals = toExecutionIntervals(records);
        long seconds = 0L;
        for (ExecutionInterval interval : intervals) {
            TimeRange range = interval.getRange();
            if (range != null && range.getStart() != null && range.getEnd() != null) {
                seconds += Duration.between(range.getStart(), range.getEnd()).getSeconds();
            }
        }
        return seconds;
    }
}
