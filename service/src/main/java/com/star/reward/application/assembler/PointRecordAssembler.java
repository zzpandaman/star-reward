package com.star.reward.application.assembler;

import com.star.reward.application.command.PointRecordQueryCommand;
import com.star.reward.domain.pointrecord.model.entity.PointRecordBO;
import com.star.reward.domain.pointrecord.model.query.PointRecordQueryParam;
import com.star.reward.domain.pointrecord.model.valueobject.PointRecordType;
import com.star.reward.interfaces.rest.dto.response.PointRecordResponse;

/**
 * 积分记录转换器（Application → 输出边界）
 * Entity → PointRecordResponse
 * Command → PointRecordQueryParam
 */
public final class PointRecordAssembler {

    private PointRecordAssembler() {
    }

    /**
     * Command → QueryParam（供 Repository listByQuery 使用）
     */
    public static PointRecordQueryParam commandToQueryParam(PointRecordQueryCommand command) {
        if (command == null) {
            return PointRecordQueryParam.builder().build();
        }
        PointRecordQueryParam param = PointRecordQueryParam.builder()
                .belongToId(command.getBelongToId())
                .type(command.getType())
                .build();
        param.setPage(command.getPage());
        param.setPageSize(command.getPageSize());
        return param;
    }

    /**
     * Entity → Response
     */
    public static PointRecordResponse entityToResponse(PointRecordBO bo) {
        if (bo == null) {
            return null;
        }
        String type = bo.getPointType() == PointRecordType.EARN ? "earn" : "spend";
        return PointRecordResponse.builder()
                .id(bo.getId())
                .recordNo(bo.getRecordNo())
                .type(type)
                .amount(bo.getAmount())
                .description(bo.getDescription())
                .relatedId(bo.getRelatedId())
                .createTime(bo.getCreateTime())
                .build();
    }
}
