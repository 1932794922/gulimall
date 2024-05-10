package com.august.gulimall.member.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.member.dto.MemberDTO;
import com.august.gulimall.member.entity.MemberEntity;
import com.august.gulimall.member.vo.UserLoginVO;
import com.august.gulimall.member.vo.UserRegistVO;

/**
 * 会员
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
public interface MemberService extends CrudService<MemberEntity, MemberDTO> {

    /**
     * 用户注册
     * @param vo
     */
    void regist(UserRegistVO vo);

    MemberEntity login(UserLoginVO vo);
}