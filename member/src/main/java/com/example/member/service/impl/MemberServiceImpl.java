package com.example.member.service.impl;

import com.example.member.dao.MemberLevelDao;
import com.example.member.entity.MemberLevelEntity;
import com.example.member.exception.PhoneExistException;
import com.example.member.exception.UsernameExistException;
import com.example.member.service.MemberLevelService;
import com.example.member.vo.MemberLoginVo;
import com.example.member.vo.MemberRegisterVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.member.dao.MemberDao;
import com.example.member.entity.MemberEntity;
import com.example.member.service.MemberService;

import javax.annotation.Resource;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

	@Resource
	MemberLevelDao memberLevelDao;


	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<MemberEntity> page = this.page(
				new Query<MemberEntity>().getPage(params),
				new QueryWrapper<MemberEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public void register(MemberRegisterVo vo) {
		MemberDao memberDao = this.baseMapper;
		MemberEntity member = new MemberEntity();

		// 检查用户名和手机号是否唯一
		checkPhoneExist(vo.getPhone());
		checkUsernameExist(vo.getPhone());

		member.setUsername(vo.getUsername());
		member.setNickname(vo.getUsername());
		member.setMobile(vo.getPhone());

		// 查询默认会员等级
		MemberLevelEntity defaultLevel =  memberDao.getDefaultLevel();
		member.setLevelId(defaultLevel.getId());

		// 密码加密存储
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encode = encoder.encode(vo.getPassword());
		member.setPassword(encode);

		memberDao.insert(member);
	}

	@Override
	public void checkUsernameExist(String username) throws UsernameExistException {
		MemberDao memberDao = this.baseMapper;
		Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
		if (count > 0) {
			throw new UsernameExistException();
		}
	}

	@Override
	public void checkPhoneExist(String phone) throws PhoneExistException {
		MemberDao memberDao = this.baseMapper;
		Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
		if (count > 0) {
			throw new PhoneExistException();
		}
	}

	@Override
	public MemberEntity login(MemberLoginVo vo) {
		String account = vo.getAccount();
		MemberDao memberDao = this.baseMapper;
		MemberEntity member = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", account).or().eq("mobile", account));
		if (member == null) {
			return null;
		}else {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			boolean matches = encoder.matches(vo.getPassword(), member.getPassword());
			if(matches){
				return member;
			}else {
				return null;
			}
		}
	}

}