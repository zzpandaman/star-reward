package com.star.reward.domain.taskinstance.service;

import org.springframework.stereotype.Service;

import com.star.reward.domain.taskinstance.model.valueobject.ExecutionInterval;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionIntervalType;
import com.star.reward.domain.taskinstance.model.valueobject.PointsConversionSegment;
import com.star.reward.domain.taskinstance.model.valueobject.PointsDetailItem;
import com.star.reward.domain.shared.constant.RewardConstants;
import com.star.reward.domain.taskinstance.model.valueobject.PointsCalculationResult;
import com.star.reward.domain.taskinstance.model.valueobject.TimeRange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 积分计算领域服务：执行区间 ∩ 倍率卡区间
 */
@Service
public class PointsCalculationService {

    /**
     * 计算积分
     *
     * @param executionIntervals 从 executionRecords 解析出的计分区间
     * @param multiplierToSegments 积分倍率卡：倍数 -> 有效时段列表（预留，默认空则全按 1 倍）
     * @param pointsPerMinute 每分钟基础积分（缺省 1）
     */
    public PointsCalculationResult calculate(
            List<ExecutionInterval> executionIntervals,
            Map<Integer, List<PointsConversionSegment>> multiplierToSegments,
            Integer pointsPerMinute) {
        int basePoints = pointsPerMinute != null ? pointsPerMinute : RewardConstants.DEFAULT_POINTS_PER_MINUTE;
        if (executionIntervals == null || executionIntervals.isEmpty()) {
            return PointsCalculationResult.empty();
        }

        List<PointsDetailItem> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        List<SegmentWithMultiplier> allMultiplierSegments = flattenMultiplierSegments(multiplierToSegments);

        for (ExecutionInterval execInterval : executionIntervals) {
            List<SegmentWithMultiplier> subSegments = partitionByMultipliers(execInterval.getRange(), allMultiplierSegments);
            for (SegmentWithMultiplier sub : subSegments) {
                BigDecimal points = computePointsForSegment(sub.range, sub.multiplier, basePoints);
                if (points.compareTo(BigDecimal.ZERO) > 0) {
                    PointsDetailItem item = PointsDetailItem.builder()
                            .intervalType(execInterval.getIntervalType())
                            .startTime(sub.range.getStart())
                            .endTime(sub.range.getEnd())
                            .durationMinutes(sub.range.durationMinutes())
                            .multiplier(sub.multiplier)
                            .points(points)
                            .build();
                    details.add(item);
                    total = total.add(points);
                }
            }
        }

        return PointsCalculationResult.of(total, details);
    }

    private List<SegmentWithMultiplier> flattenMultiplierSegments(Map<Integer, List<PointsConversionSegment>> multiplierToSegments) {
        List<SegmentWithMultiplier> result = new ArrayList<>();
        if (multiplierToSegments == null) {
            return result;
        }
        for (Map.Entry<Integer, List<PointsConversionSegment>> e : multiplierToSegments.entrySet()) {
            int multiplier = e.getKey();
            for (PointsConversionSegment seg : e.getValue()) {
                TimeRange range = seg.toTimeRange();
                if (range != null) {
                    result.add(new SegmentWithMultiplier(range, multiplier));
                }
            }
        }
        return result;
    }

    /**
     * 将执行区间按倍率卡切分，每段取最高有效倍数
     */
    private List<SegmentWithMultiplier> partitionByMultipliers(
            TimeRange execRange,
            List<SegmentWithMultiplier> multiplierSegments
    ) {
        if (multiplierSegments == null || multiplierSegments.isEmpty()) {
            return Collections.singletonList(new SegmentWithMultiplier(execRange, 1));
        }

        TreeSet<LocalDateTime> boundaries = new TreeSet<>();
        boundaries.add(execRange.getStart());
        boundaries.add(execRange.getEnd());

        // 收集所有有效交集边界
        for (SegmentWithMultiplier seg : multiplierSegments) {
            TimeRange overlap = execRange.intersect(seg.range);
            if (overlap != null) {
                boundaries.add(overlap.getStart());
                boundaries.add(overlap.getEnd());
            }
        }

        List<LocalDateTime> sortedBoundaries = boundaries.stream()
                .filter(t -> !t.isBefore(execRange.getStart()) && !t.isAfter(execRange.getEnd()))
                .collect(Collectors.toList());

        if (sortedBoundaries.size() < 2) {
            return Collections.singletonList(new SegmentWithMultiplier(execRange, 1));
        }

        List<SegmentWithMultiplier> result = new ArrayList<>(sortedBoundaries.size() - 1);

        for (int i = 0; i < sortedBoundaries.size() - 1; i++) {
            LocalDateTime segStart = sortedBoundaries.get(i);
            LocalDateTime segEnd = sortedBoundaries.get(i + 1);

            if (!segStart.isBefore(segEnd)) {
                continue;
            }

            TimeRange subRange = TimeRange.of(segStart, segEnd);
            int maxMultiplier = 1;

            for (SegmentWithMultiplier seg : multiplierSegments) {
                if (seg.range.intersect(subRange) != null) {
                    maxMultiplier = Math.max(maxMultiplier, seg.multiplier);
                }
            }

            result.add(new SegmentWithMultiplier(subRange, maxMultiplier));
        }

        return result;
    }

    private BigDecimal computePointsForSegment(TimeRange segment, int multiplier, int pointsPerMin) {
        long minutes = segment.durationMinutes();
        if (minutes <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(minutes * pointsPerMin * multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private static class SegmentWithMultiplier {
        final TimeRange range;
        final int multiplier;

        SegmentWithMultiplier(TimeRange range, int multiplier) {
            this.range = range;
            this.multiplier = multiplier;
        }
    }
}
