package com.example.member.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import com.example.common.exception.BizCodeEnum;
import com.example.member.exception.PhoneExistException;
import com.example.member.exception.UsernameExistException;
import com.example.member.feign.CouponFeignService;
import com.example.member.vo.MemberLoginVo;
import com.example.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.member.entity.MemberEntity;
import com.example.member.service.MemberService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;


/**
 * 会员
 *
 * @author xx
 * @email xx@gmail.com
 * @date 2022-07-13 10:17:01
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){
        MemberEntity member = memberService.login(vo);
        if(member == null){
            return R.error(BizCodeEnum.ACCOUNT_PASSWORD_INVALID_EXCEPTION.getCode(),BizCodeEnum.ACCOUNT_PASSWORD_INVALID_EXCEPTION.getMsg());
        }
        return R.ok().put("data",member);
    }

    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo vo){
        try{
            memberService.register(vo);
        }catch (PhoneExistException e){
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        }catch (UsernameExistException e){
            return R.error(BizCodeEnum.USERNAME_EXIST_EXCEPTION.getCode(), BizCodeEnum.USERNAME_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }


    @RequestMapping("/coupons")
    public R coupons(){
        MemberEntity member = new MemberEntity();
        member.setNickname("Hisoka");

        R memberCoupons = couponFeignService.memberCoupons();
        return R.ok().put("member",member).put("coupons",memberCoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
