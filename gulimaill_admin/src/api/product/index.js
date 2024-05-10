/*
 * @Description:
 * @Author: August_xiao
 * @Date: 2022-10-17 20:12:23
 * @LastEditors: August_xiao
 * @LastEditTime: 2022-10-23 23:05:39
 * @FilePath: /gulimaill_admin/src/api/product/index.js
 * QQ: 1932794922
 * email: 1932794922@qq.com
 * Copyright (c) 2022 by August_xiao, All Rights Reserved.
 */
const categoryUrl = {
  BaseURL: '/product/category',
  // 获取树形分类列表
  getCategoryTreeList: '/product/category/tree',
  // 添加分类
  addCategory: '/product/category/add',
  // 修改分类
  updateCategory: '/product/category/update',
  // 删除分类
  deleteCategory: '/product/category/delete'
}

const brandUrl = {
  BaseURL: '/product/brand/'
}

const categorybrandrelationUrl = {
  BaseURL: '/product/categorybrandrelation',
  getAtegorybrandrelation: '/product/categorybrandrelation/catelog/list',
  ategorybrandrelation: '/product/categorybrandrelation/save'
}

const attrGroupUrl = {
  BaseURL: '/product/attrgroup',
  list: 'product/attrgroup/list'
}

export { categoryUrl, brandUrl, categorybrandrelationUrl, attrGroupUrl }
