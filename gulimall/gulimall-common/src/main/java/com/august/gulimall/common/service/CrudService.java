package com.august.gulimall.common.service;


import com.august.gulimall.common.page.PageData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;

import java.util.List;
import java.util.Map;

/**
 *  CRUD基础服务接口
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface CrudService<T, D> extends BaseService<T> {

    /**
     * 页面
     *
     * @param params 参数个数
     * @return {@link PageData}<{@link D}>
     */
    PageData<D> page(Map<String, Object> params);

    /**
     * 列表
     *
     * @param params 参数个数
     * @return {@link List}<{@link D}>
     */
    List<D> list(Map<String, Object> params);

    /**
     * 得到
     *
     * @param id id
     * @return {@link D}
     */
    D get(Long id);

    /**
     * @param dto dto
     */
    void save(D dto);

    void update(D dto);

    void delete(Long[] ids);

}