package com.august.gulimall.common.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPrice {
    /**
     * id
     */
    @JsonProperty("id")
    private Long id;
    /**
     * name
     */
    @JsonProperty("name")
    private String name;
    /**
     * price
     */
    @JsonProperty("price")
    private BigDecimal price;
}
