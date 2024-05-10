package com.august.gulimall.ware.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.common.to.SkuHasStore;
import com.august.gulimall.common.to.mq.OrderTO;
import com.august.gulimall.common.to.mq.StockLockedTO;
import com.august.gulimall.ware.dto.WareSkuDTO;
import com.august.gulimall.ware.entity.WareSkuEntity;
import com.august.gulimall.ware.vo.WareSkuLockVO;

import java.util.List;

/**
 * 商品库存
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
public interface WareSkuService extends CrudService<WareSkuEntity, WareSkuDTO> {

    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 查询sku是否有库存
     * @param skuIds
     * @return
     */
    List<SkuHasStore> hasStock(List<Long> skuIds);

    /**
     * 锁定库存
     * @param vo
     */
    Boolean orderLockStock(WareSkuLockVO vo);

    /**
     * 解锁库存
     * @param to
     */
    void unlockStock(StockLockedTO to);

    void unlockStock(OrderTO to);
}