package com.august.gulimall.ware.service;

import com.august.gulimall.common.page.PageData;
import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.ware.dto.PurchaseDTO;
import com.august.gulimall.ware.entity.PurchaseEntity;
import com.august.gulimall.ware.vo.MergeVO;
import com.august.gulimall.ware.vo.PurchaseDoneVO;

import java.util.List;

/**
 * 采购信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
public interface PurchaseService extends CrudService<PurchaseEntity, PurchaseDTO> {

    /**
     * 查询未领取的采购单
     * @return
     */
    PageData<PurchaseDTO> unReceiveList();

    /**
     * 合并采购需求
     * @param purchaseIds
     */
    void mergePurchase(MergeVO mergeVO);

    /**
     * 领取采购单
     * @param ids
     */
    void received(List<Long> ids);

    void done(PurchaseDoneVO doneVo);
}