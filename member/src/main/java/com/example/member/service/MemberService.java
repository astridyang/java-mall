package com.example.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.member.entity.MemberEntity;
import com.example.member.exception.PhoneExistException;
import com.example.member.exception.UsernameExistException;
import com.example.member.vo.MemberLoginVo;
import com.example.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-13 10:17:01
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void register(MemberRegisterVo vo);

	void checkUsernameExist(String username) throws UsernameExistException;

	void checkPhoneExist(String phone) throws PhoneExistException;

	MemberEntity login(MemberLoginVo vo);
}

