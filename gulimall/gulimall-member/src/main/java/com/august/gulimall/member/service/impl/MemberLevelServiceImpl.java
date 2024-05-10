package com.august.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.member.dao.MemberLevelDao;
import com.august.gulimall.member.dto.MemberLevelDTO;
import com.august.gulimall.member.entity.MemberLevelEntity;
import com.august.gulimall.member.service.MemberLevelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 会员等级
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class MemberLevelServiceImpl extends CrudServiceImpl<MemberLevelDao, MemberLevelEntity, MemberLevelDTO> implements MemberLevelService {

    @Override
    public QueryWrapper<MemberLevelEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<MemberLevelEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public MemberLevelEntity getDefaultLevel() {
        LambdaQueryWrapper<MemberLevelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberLevelEntity::getDefaultStatus, 1);
        List<MemberLevelEntity> memberLevelEntities = baseDao.selectList(wrapper);
        if (memberLevelEntities != null && memberLevelEntities.size() > 0) {
            return memberLevelEntities.get(0);
        }
        return null;
    }
}