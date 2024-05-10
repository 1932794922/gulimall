package com.august.gulimall.ware.service.impl;

import com.august.gulimall.common.utils.Result;
import com.august.gulimall.ware.feign.MemberFeignService;
import com.august.gulimall.ware.vo.FareVO;
import com.august.gulimall.ware.vo.MemberAddressVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.ware.dao.WareInfoDao;
import com.august.gulimall.ware.dto.WareInfoDTO;
import com.august.gulimall.ware.entity.WareInfoEntity;
import com.august.gulimall.ware.service.WareInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class WareInfoServiceImpl extends CrudServiceImpl<WareInfoDao, WareInfoEntity, WareInfoDTO> implements WareInfoService {
    @Resource
    MemberFeignService memberFeignService;


    @Override
    public QueryWrapper<WareInfoEntity> getWrapper(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().like("name", key).or().
                        like("address", key).or().like("areacode", key);
            });
        }


        return wrapper;
    }


    @Override
    public FareVO getFare(Long addrId) {
        FareVO fareVo = new FareVO();
        // 远程调用获取地址信息
        Result<MemberAddressVO> result = memberFeignService.addrInfo(addrId);
        MemberAddressVO data = result.getData();
        // 算运费，简单处理了，不然要调三方接口
        if (data != null) {
            String phone = data.getPhone();
            String substring = phone.substring(phone.length() - 1, phone.length());
            BigDecimal bigDecimal = new BigDecimal(substring);
            fareVo.setAddress(data);
            fareVo.setFare(bigDecimal);
            return fareVo;
        }
        return null;

    }
}