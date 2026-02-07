package com.star.reward.domain.shared.util;

import com.star.reward.domain.shared.constant.RewardConstants;

import java.util.UUID;

/**
 * 奖励业务编号生成器
 */
public final class RewardNoGenerator {

    private RewardNoGenerator() {
    }

    /**
     * 生成带前缀的业务编号（前缀 + 16位大写UUID无横线）
     *
     * @param prefix 编号前缀（如 EXE、INV、PRD）
     * @return 业务编号
     */
    public static String generate(String prefix) {
        String suffix = UUID.randomUUID().toString()
                .replace(RewardConstants.UUID_SEPARATOR, "")
                .substring(0, RewardConstants.NO_SUFFIX_LENGTH)
                .toUpperCase();
        return prefix + suffix;
    }
}
