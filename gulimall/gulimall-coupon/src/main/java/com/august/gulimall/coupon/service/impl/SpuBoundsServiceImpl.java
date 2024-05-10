package com.august.gulimall.coupon.service.impl;

import com.august.gulimall.common.to.MemberPrice;
import com.august.gulimall.common.to.SkuReductionTO;
import com.august.gulimall.coupon.entity.MemberPriceEntity;
import com.august.gulimall.coupon.entity.SkuFullReductionEntity;
import com.august.gulimall.coupon.entity.SkuLadderEntity;
import com.august.gulimall.coupon.service.MemberPriceService;
import com.august.gulimall.coupon.service.SkuFullReductionService;
import com.august.gulimall.coupon.service.SkuLadderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.coupon.dao.SpuBoundsDao;
import com.august.gulimall.coupon.dto.SpuBoundsDTO;
import com.august.gulimall.coupon.entity.SpuBoundsEntity;
import com.august.gulimall.coupon.service.SpuBoundsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品spu积分设置
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class SpuBoundsServiceImpl extends CrudServiceImpl<SpuBoundsDao, SpuBoundsEntity, SpuBoundsDTO> implements SpuBoundsService {

    @Resource
    SkuLadderService skuLadderService;

    @Resource
    SkuFullReductionService skuFullReductionService;

    @Resource
    MemberPriceService memberPriceService;


    @Override
    public QueryWrapper<SpuBoundsEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");

        QueryWrapper<SpuBoundsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public void saveSkuReduction(SkuReductionTO skuReductionTO) {
        //1. 保存满减打折  保存会员价格 gulimall_sms->sms_sku_ladder sms_sku_full_reduction sms_member_price
        Long skuId = skuReductionTO.getSkuId();
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuId);
        skuLadderEntity.setFullCount(skuReductionTO.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTO.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTO.getCountStatus());
        if (skuLadderEntity.getFullCount() > 0) {
            //sms_sku_ladder
            skuLadderService.insert(skuLadderEntity);
        }
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        skuFullReductionEntity.setSkuId(skuId);
        skuFullReductionEntity.setFullPrice(skuReductionTO.getFullPrice());
        skuFullReductionEntity.setReducePrice(skuReductionTO.getReducePrice());
        if (skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
            //sms_sku_full_reduction
            skuFullReductionService.insert(skuFullReductionEntity);
        }
        MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
        List<MemberPrice> memberPrice = skuReductionTO.getMemberPrice();
        if (memberPrice != null && memberPrice.size() > 0) {
            List<MemberPriceEntity> collect = memberPrice.stream().filter(e -> {
                        return e.getPrice().compareTo(new BigDecimal("0")) > 0;
                    }
            ).map(item -> {
                memberPriceEntity.setSkuId(skuId);
                memberPriceEntity.setMemberLevelId(item.getId());
                memberPriceEntity.setMemberLevelName(item.getName());
                memberPriceEntity.setMemberPrice(item.getPrice());
                memberPriceEntity.setAddOther(1);
                //sms_member_price
                memberPriceService.insert(memberPriceEntity);
                return memberPriceEntity;
            }).collect(Collectors.toList());
            memberPriceService.insertBatch(collect);
        }
    }
}