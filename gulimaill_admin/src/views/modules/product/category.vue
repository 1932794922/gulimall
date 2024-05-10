<template>
  <el-card shadow="never" class="aui-card--fill">
    <el-switch
      v-model="draggable"
      active-text="开启拖拽"
      inactive-text="关闭拖拽"
    ></el-switch>
    <el-button type="primary" size="mini" v-if="draggable" @click="batchSave"
      >批量保存</el-button
    >
    <el-button size="mini" type="danger" @click="batchDelete"
      >批量删除</el-button
    >
    <el-tree
      :data="menus"
      :props="defaultProps"
      show-checkbox
      node-key="catId"
      :expand-on-click-node="false"
      :default-expanded-keys="expandedKey"
      :draggable="draggable"
      :allow-drop="allowDrop"
      @node-drop="handleDrop"
      ref="menuTree"
    >
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button
            v-if="node.level <= 2"
            type="text"
            size="mini"
            @click="appendCategory(data)"
          >
            添加
          </el-button>
          <el-button
            type="text"
            size="mini"
            @click="updataCategory(node, data)"
          >
            修改
          </el-button>
          <el-button
            v-if="data && data.children && data.children.length === 0"
            type="text"
            size="mini"
            @click="removeCategory(node, data)"
          >
            删除
          </el-button>
        </span>
      </span>
    </el-tree>

    <el-dialog
      title="添加菜单分类"
      :rules="rules"
      :visible.sync="dialogFormVisible"
    >
      <el-form :model="categoryForm" :rules="rules" ref="categoryFormRef">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="categoryForm.sort" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item prop="icon" :label="$t('menu.icon')" class="icon-list">
          <el-popover
            v-model="iconListVisible"
            ref="iconListPopover"
            placement="bottom-start"
            trigger="click"
            popper-class="mod-sys__menu-icon-popover"
          >
            <div class="mod-sys__menu-icon-inner">
              <div class="mod-sys__menu-icon-list">
                <el-button
                  v-for="(item, index) in iconList"
                  :key="index"
                  @click="iconListCurrentChangeHandle(item)"
                  :class="{ 'is-active': categoryForm.icon === item }"
                >
                  <svg class="icon-svg" aria-hidden="true">
                    <use :xlink:href="`#${item}`"></use>
                  </svg>
                </el-button>
              </div>
            </div>
          </el-popover>
          <el-input
            v-model="categoryForm.icon"
            v-popover:iconListPopover
            :readonly="true"
            :placeholder="$t('menu.icon')"
          ></el-input>
        </el-form-item>
        <el-form-item label="计量单位" prop="productUnit">
          <el-input
            v-model="categoryForm.productUnit"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAddForm('categoryFormRef')"
          >确 定</el-button
        >
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import mixinViewModule from '@/mixins/view-module'
import AddOrUpdate from './category-add-or-update'
import { categoryUrl } from '@/api/product/index'
import { getIconList } from '@/utils'
export default {
  mixins: [mixinViewModule],
  data() {
    return {
      mixinViewModuleOptions: {
        createdIsNeed: true,
        getDataListURL: '/product/category/page',
        getDataListIsPage: true,
        exportURL: '/product/category/export',
        deleteURL: '/product/category',
        deleteIsBatch: true
      },
      dataForm: {
        catId: ''
      },
      pCid: [],
      draggable: false,
      updateNodes: [],
      menuTree: [],
      maxLevel: 0,
      menus: [],
      expandedKey: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      iconList: [],
      dialogFormVisible: false,
      iconListVisible: false,
      categoryForm: {},
      rules: {
        name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
        sort: [
          { required: false, message: '请输入排序', trigger: 'blur' },
          { type: 'number', message: '排序为数字值' }
        ],
        icon: [{ required: false, message: '请选择图标', trigger: 'blur' }],
        productUnit: [
          { required: false, message: '请输入计量单位', trigger: 'blur' }
        ]
      },
      saveOrUpdata: 'save'
    }
  },
  components: {
    AddOrUpdate
  },
  methods: {
    init() {
      this.$nextTick(() => {
        this.$refs?.categoryFormRef?.resetFields()
        this.iconList = getIconList()
      })
    },
    /**
     * 获取所有菜单
     */
    getMenus() {
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
    /**
     * 添加对话框
     */
    appendCategory(data) {
      this.saveOrUpdata = 'save'
      this.init()
      this.categoryForm = {
        parentCid: data.catId,
        name: null,
        sort: 0,
        icon: null,
        productUnit: null,
        catLevel: data.catLevel + 1
      }
      this.dialogFormVisible = true
    },
    // 图标, 选中
    iconListCurrentChangeHandle(icon) {
      this.categoryForm.icon = icon
      this.iconListVisible = false
    },
    //修改
    updataCategory(node, data) {
      this.saveOrUpdata = 'update'
      this.init()
      this.categoryForm = {
        catId: data.catId,
        parentCid: data.parentCid,
        name: data.name,
        sort: data.sort,
        icon: data.icon,
        productUnit: data.productUnit,
        catLevel: data.catLevel
      }
      this.dialogFormVisible = true
    },

    // 提交添加表单
    submitAddForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          if (this.saveOrUpdata === 'save') {
            this.$http
              .post(categoryUrl.BaseURL, { ...this.categoryForm })
              .then(({ data: res }) => {
                if (res.code !== 200) {
                  return this.$message.error(res.msg)
                }
                this.$message.success(res.msg)
                this.getMenus()
                this.expandedKey = [this.categoryForm.parentCid]
                this.dialogFormVisible = false
              })
              .catch(err => {})
          } else if (this.saveOrUpdata === 'update') {
            this.$http
              .put(categoryUrl.BaseURL, { ...this.categoryForm })
              .then(({ data: res }) => {
                if (res.code !== 200) {
                  return this.$message.error(res.msg)
                }
                this.$message.success(res.msg)
                this.getMenus()
                this.expandedKey = [this.categoryForm.parentCid]
                this.dialogFormVisible = false
              })
              .catch(err => {})
          }
        } else {
          return false
        }
      })
    },
    /**
     * 删除
     */
    removeCategory(node, data) {
      let _data = data
      this.$confirm(
        `此操作将菜单分类[${data.name}]删除该节点, 是否继续?`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(() => {
          let ids = [data.catId]
          this.$http
            .delete(categoryUrl.BaseURL, {
              data: ids
            })
            .then(({ data: res }) => {
              if (res.code !== 200) {
                return this.$message.error(res.msg)
              }
              this.$message.success(res.msg)
              this.getMenus()
              //   展开默认节点
              this.expandedKey = [_data.parentCid]
            })
            .catch(() => {})
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    handleDrop(draggingNode, dropNode, dropType, ev) {
      //1、当前节点最新的父节点id
      let pCid = 0
      let siblings = null
      if (dropType == 'before' || dropType == 'after') {
        pCid =
          dropNode.parent.data.catId == undefined
            ? 0
            : dropNode.parent.data.catId
        siblings = dropNode.parent.childNodes
      } else {
        pCid = dropNode.data.catId
        siblings = dropNode.childNodes
      }
      this.pCid.push(pCid)

      //2、当前拖拽节点的最新顺序，
      for (let i = 0; i < siblings.length; i++) {
        if (siblings[i].data.catId == draggingNode.data.catId) {
          //如果遍历的是当前正在拖拽的节点
          let catLevel = draggingNode.level
          if (siblings[i].level != draggingNode.level) {
            //当前节点的层级发生变化
            catLevel = siblings[i].level
            //修改他子节点的层级
            this.updateChildNodeLevel(siblings[i])
          }
          this.updateNodes.push({
            catId: siblings[i].data.catId,
            sort: i,
            parentCid: pCid,
            catLevel: catLevel
          })
        } else {
          this.updateNodes.push({ catId: siblings[i].data.catId, sort: i })
        }
      }
      //3、当前拖拽节点的最新层级
    },
    updateChildNodeLevel(node) {
      if (node.childNodes.length > 0) {
        for (let i = 0; i < node.childNodes.length; i++) {
          var cNode = node.childNodes[i].data
          this.updateNodes.push({
            catId: cNode.catId,
            catLevel: node.childNodes[i].level
          })
          this.updateChildNodeLevel(node.childNodes[i])
        }
      }
    },
    allowDrop(draggingNode, dropNode, type) {
      //1、被拖动的当前节点以及所在的父节点总层数不能大于3
      //1）、被拖动的当前节点总层数
      this.countNodeLevel(draggingNode)
      //当前正在拖动的节点+父节点所在的深度不大于3即可
      let deep = Math.abs(this.maxLevel - draggingNode.level) + 1
      if (type == 'inner') {
        return deep + dropNode.level <= 3
      } else {
        return deep + dropNode.parent.level <= 3
      }
    },
    countNodeLevel(node) {
      //找到所有子节点，求出最大深度
      if (node.childNodes != null && node.childNodes.length > 0) {
        for (let i = 0; i < node.childNodes.length; i++) {
          if (node.childNodes[i].level > this.maxLevel) {
            this.maxLevel = node.childNodes[i].level
          }
          this.countNodeLevel(node.childNodes[i])
        }
      }
    },
    batchSave() {
      this.$http
        .post(categoryUrl.BaseURL + '/drop', this.updateNodes)
        .then(({ data: res }) => {
          if (res.code !== 200) {
            return this.$message.error(res.msg)
          }
          this.$message.success('分类菜单更新成功')
        })
        .catch(err => {})
      this.getMenus()
      this.updateNodes = []
      this.expandedKey = [this.pCid]
      this.maxLevel = 0
    },
    batchDelete() {
      let ids = []
      let nodes = this.$refs.menuTree.getCheckedNodes()
      nodes.forEach(item => {
        ids.push(item.catId)
      })
      if (ids.length == 0) {
        return this.$message.warning('请先选择要删除的菜单分类')
      }

      this.$confirm('此操作将永久删除该分类, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.$http
            .delete(categoryUrl.BaseURL, {
              data: ids
            })
            .then(({ data: res }) => {
              if (res.code !== 200) {
                return this.$message.error(res.msg)
              }
              this.$message.success(res.msg)
              this.getMenus()
            })
            .catch(() => {})
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    }
  },
  created() {
    this.getMenus()
  }
}
</script>
<style scoped>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
button {
  margin-left: 10px;
}
</style>
