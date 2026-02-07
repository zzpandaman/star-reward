package com.star.reward.domain.pointrecord.model.valueobject;

import com.star.reward.domain.pointrecord.model.constant.PointRecordConstants;

/**
 * 积分记录类型
 */
public enum PointRecordType {

    EARN(PointRecordConstants.TYPE_EARN, "获取"),
    SPEND(PointRecordConstants.TYPE_SPEND, "消耗");

    private final String code;
    private final String desc;

    PointRecordType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PointRecordType fromCode(String code) {
        for (PointRecordType t : values()) {
            if (t.code.equals(code)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown point record type: " + code);
    }
}
