package com.example.member.dao;

import com.example.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.member.entity.MemberLevelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-13 10:17:01
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

	MemberLevelEntity getDefaultLevel();
}
