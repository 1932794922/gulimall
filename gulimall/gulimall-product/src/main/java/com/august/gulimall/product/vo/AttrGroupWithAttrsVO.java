package com.august.gulimall.product.vo;

import com.august.gulimall.product.dto.AttrDTO;
import com.august.gulimall.product.dto.AttrGroupDTO;
import lombok.Data;

import java.util.List;

/**
 * @author : Crazy_August
 * @description :
 * @Time: 2023-04-23   17:13
 */
@Data
public class AttrGroupWithAttrsVO extends AttrGroupDTO {

    List<AttrDTO> attrs;

}
