package com.august.gulimall.search.service;

import com.august.gulimall.search.vo.SearchParam;
import com.august.gulimall.search.vo.SearchResult;

/**
 * @author xzy
 */
public interface MallSearchService {
    /**
     * 检索
     * @param param 检索的所有参数
     * @return 检索的结果
     */
    SearchResult search(SearchParam param);
}
