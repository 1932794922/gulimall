<template>
  <div>
    <el-input
      size="small"
      placeholder="输入关键字进行过滤"
      v-model="filterText"
      clearable
    ></el-input>
    <el-tree
      :data="menus"
      :props="defaultProps"
      :filter-node-method="filterNode"
      @node-click="nodeClick"
      node-key="catId"
      ref="menuTree"
    >
    </el-tree>
  </div>
</template>

<script>
import { categoryUrl } from '@/api/product/index'
export default {
  name: 'CategoryComm',
  data() {
    return {
      filterText: '',
      menus: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  //监控data中的数据变化
  watch: {
    filterText(val) {
      this.$refs.menuTree.filter(val)
    }
  },
  created() {
    this.getMenuList()
  },
  methods: {
    getMenuList() {
      this.$http
        .get(categoryUrl.getCategoryTreeList)
        .then(({ data: res }) => {
          if (res.code !== 200) {
            return this.$message.error(res.msg)
          }
          this.menus = res.data
        })
        .catch(() => {})
    },
    //树节点过滤
    filterNode(value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
    },

    nodeClick(data, node, component) {
      //向父组件发送事件；
      this.$emit('tree-node-click', data, node, component)
    }
  }
}
</script>

<style lang="scss" scoped></style>
