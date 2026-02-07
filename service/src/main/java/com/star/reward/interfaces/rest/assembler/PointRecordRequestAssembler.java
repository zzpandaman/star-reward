package com.star.reward.interfaces.rest.assembler;

import com.star.reward.application.command.PointRecordQueryCommand;
import com.star.reward.interfaces.rest.dto.request.PointRecordQueryRequest;

/**
 * 积分记录 Request → Command 转换器（Interfaces → Application 边界）
 */
public final class PointRecordRequestAssembler {

    private PointRecordRequestAssembler() {
    }

    public static PointRecordQueryCommand requestToQueryCommand(PointRecordQueryRequest request) {
        if (request == null) {
            return new PointRecordQueryCommand();
        }
        PointRecordQueryCommand cmd = new PointRecordQueryCommand();
        cmd.setType(request.getType());
        return cmd;
    }
}
