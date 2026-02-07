package com.star.reward.infrastructure.persistence.dao.mapper;

import com.star.reward.infrastructure.persistence.dao.entity.RewardPointRecordDO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPointRecordDOExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 积分记录 Mapper
 */
public interface RewardPointRecordDOMapper {

    long countByExample(RewardPointRecordDOExample example);

    int deleteByExample(RewardPointRecordDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(RewardPointRecordDO row);

    int insertSelective(RewardPointRecordDO row);

    List<RewardPointRecordDO> selectByExample(RewardPointRecordDOExample example);

    RewardPointRecordDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") RewardPointRecordDO row, @Param("example") RewardPointRecordDOExample example);

    int updateByExample(@Param("row") RewardPointRecordDO row, @Param("example") RewardPointRecordDOExample example);

    int updateByPrimaryKeySelective(RewardPointRecordDO row);

    int updateByPrimaryKey(RewardPointRecordDO row);
}
