package com.august.gulimall.ware.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.ware.dto.WareOrderTaskDTO;
import com.august.gulimall.ware.entity.WareOrderTaskEntity;

/**
 * 库存工作单
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
public interface WareOrderTaskService extends CrudService<WareOrderTaskEntity, WareOrderTaskDTO> {

    /**
     * 根据订单号查询库存工作单
     * @param orderSn
     * @return
     */
    WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}