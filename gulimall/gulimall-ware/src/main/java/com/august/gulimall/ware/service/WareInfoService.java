package com.august.gulimall.ware.service;

import com.august.gulimall.common.service.CrudService;
import com.august.gulimall.ware.dto.WareInfoDTO;
import com.august.gulimall.ware.entity.WareInfoEntity;
import com.august.gulimall.ware.vo.FareVO;

/**
 * 仓库信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
public interface WareInfoService extends CrudService<WareInfoEntity, WareInfoDTO> {

    /**
     * 计算运费
     * @param addrId
     * @return
     */
    FareVO getFare(Long addrId);
}