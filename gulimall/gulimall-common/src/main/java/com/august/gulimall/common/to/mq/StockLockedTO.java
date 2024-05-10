package com.august.gulimall.common.to.mq;

import lombok.Data;
 /**
  * @author xzy
  * 发送到mq消息队列的to
  */
@Data
public class StockLockedTO {
    /** 库存工作单的id **/
    private Long id;

    /** 工作单详情**/
    private StockDetailTO detailTo;
}
