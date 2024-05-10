package com.august.gulimall.member.service.impl;

import com.august.gulimall.member.vo.MemberAddressVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.member.dao.MemberReceiveAddressDao;
import com.august.gulimall.member.dto.MemberReceiveAddressDTO;
import com.august.gulimall.member.entity.MemberReceiveAddressEntity;
import com.august.gulimall.member.service.MemberReceiveAddressService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 会员收货地址
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class MemberReceiveAddressServiceImpl extends CrudServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity, MemberReceiveAddressDTO> implements MemberReceiveAddressService {

    @Override
    public QueryWrapper<MemberReceiveAddressEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<MemberReceiveAddressEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<MemberReceiveAddressEntity> getAddressById(Long memberId) {
        LambdaQueryWrapper<MemberReceiveAddressEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberReceiveAddressEntity::getMemberId, memberId);
        return this.baseDao.selectList(wrapper);
    }
}