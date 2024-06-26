package com.august.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.ware.dao.WareOrderTaskDao;
import com.august.gulimall.ware.dto.WareOrderTaskDTO;
import com.august.gulimall.ware.entity.WareOrderTaskEntity;
import com.august.gulimall.ware.service.WareOrderTaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class WareOrderTaskServiceImpl extends CrudServiceImpl<WareOrderTaskDao, WareOrderTaskEntity, WareOrderTaskDTO> implements WareOrderTaskService {

    @Override
    public QueryWrapper<WareOrderTaskEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<WareOrderTaskEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn) {
        WareOrderTaskEntity wareOrderTask = this.baseDao.selectOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", orderSn));
        return wareOrderTask;
    }
}