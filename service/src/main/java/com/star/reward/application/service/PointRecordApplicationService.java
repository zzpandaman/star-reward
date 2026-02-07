package com.star.reward.application.service;

import com.star.common.exception.BusinessException;
import com.star.common.page.PageResponse;
import com.star.common.result.ResultCode;
import com.star.reward.application.assembler.PointRecordAssembler;
import com.star.reward.domain.pointrecord.model.entity.PointRecordBO;
import com.star.reward.domain.pointrecord.model.valueobject.PointRecordType;
import com.star.reward.domain.pointrecord.repository.PointRecordRepository;
import com.star.reward.interfaces.rest.dto.response.PointRecordResponse;
import com.star.reward.shared.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分记录应用服务
 */
@Service
@RequiredArgsConstructor
public class PointRecordApplicationService {

    private final PointRecordRepository pointRecordRepository;

    /**
     * 获取当前用户积分记录列表
     *
     * @param type 可选，earn/spend 过滤类型
     * @param page 页码，从1开始
     * @param size 每页条数
     */
    public PageResponse<PointRecordResponse> listUserPointRecords(String type, Integer page, Integer size) {
        CurrentUserContext user = CurrentUserContext.get();
        List<PointRecordBO> records;
        if (type != null && !type.isEmpty()) {
            PointRecordType recordType = "earn".equalsIgnoreCase(type) ? PointRecordType.EARN : PointRecordType.SPEND;
            records = pointRecordRepository.findByBelongToIdAndType(user.getUserId(), recordType);
        } else {
            records = pointRecordRepository.findByBelongToId(user.getUserId());
        }
        List<PointRecordResponse> responses = records.stream()
                .map(PointRecordAssembler::entityToResponse)
                .collect(Collectors.toList());

        int total = responses.size();
        int pageNum = (page != null && page > 0) ? page : 1;
        int pageSize = (size != null && size > 0) ? size : total;
        int fromIndex = Math.min((pageNum - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<PointRecordResponse> paged = fromIndex < total ? responses.subList(fromIndex, toIndex) : responses;

        return PageResponse.of(paged, total, pageNum, pageSize);
    }

    /**
     * 获取积分记录单条详情，校验 belongToId=当前用户
     */
    public PointRecordResponse getPointRecordById(Long id) {
        CurrentUserContext user = CurrentUserContext.get();
        PointRecordBO bo = pointRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "积分记录不存在"));
        if (!user.getUserId().equals(bo.getBelongToId())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "无权访问该积分记录");
        }
        return PointRecordAssembler.entityToResponse(bo);
    }
}
