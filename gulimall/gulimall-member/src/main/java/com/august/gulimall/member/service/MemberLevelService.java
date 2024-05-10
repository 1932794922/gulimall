package com.august.gulimall.member.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.member.dto.MemberLevelDTO;
import com.august.gulimall.member.entity.MemberLevelEntity;

/**
 * 会员等级
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
public interface MemberLevelService extends CrudService<MemberLevelEntity, MemberLevelDTO> {

    /**
     * 获取默认会员等级
     * @return
     */
    MemberLevelEntity getDefaultLevel();
}