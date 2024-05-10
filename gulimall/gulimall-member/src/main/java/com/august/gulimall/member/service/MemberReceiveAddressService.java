package com.august.gulimall.member.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.member.dto.MemberReceiveAddressDTO;
import com.august.gulimall.member.entity.MemberReceiveAddressEntity;
import com.august.gulimall.member.vo.MemberAddressVO;

import java.util.List;

/**
 * 会员收货地址
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
public interface MemberReceiveAddressService extends CrudService<MemberReceiveAddressEntity, MemberReceiveAddressDTO> {

    /**
     * 根据会员id获取会员收货地址
     * @param memberId
     * @return
     */
    List<MemberReceiveAddressEntity> getAddressById(Long memberId);
}