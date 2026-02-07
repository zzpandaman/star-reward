package com.star.reward.domain.shared.constant;

/**
 * 奖励系统通用常量
 */
public final class RewardConstants {

    private RewardConstants() {
    }

    /** UUID 去掉分隔符后的截取长度（用于编号后缀） */
    public static final int NO_SUFFIX_LENGTH = 16;

    /** UUID 字符串中的分隔符 */
    public static final String UUID_SEPARATOR = "-";

    /** 默认每分钟积分（缺省时） */
    public static final int DEFAULT_POINTS_PER_MINUTE = 1;
}
