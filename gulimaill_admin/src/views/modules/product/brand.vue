<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-product__brand}">
      <el-form
        :inline="true"
        :model="dataForm"
        @keyup.enter.native="getDataList()"
      >
        <el-form-item>
          <el-input
            v-model="dataForm.brandId"
            placeholder="brandId"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="info" @click="exportHandle()">{{
            $t('export')
          }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            v-if="$hasPermission('product:brand:save')"
            type="primary"
            @click="addOrUpdateHandle()"
            >{{ $t('add') }}</el-button
          >
        </el-form-item>
       
        <el-form-item>
          <el-button
            v-if="$hasPermission('product:brand:delete')"
            type="danger"
            @click="deleteBrand()"
            >{{ $t('deleteBatch') }}</el-button
          >
        </el-form-item>
      </el-form>
      <el-table
        v-loading="dataListLoading"
        :data="dataList"
        border
        @selection-change="dataListSelectionChangeHandle"
        style="width: 100%;"
      >
        <el-table-column
          type="selection"
          header-align="center"
          align="center"
          width="50"
        ></el-table-column>
        <el-table-column
          prop="brandId"
          label="品牌id"
          header-align="center"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="name"
          label="品牌名"
          header-align="center"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="logo"
          label="品牌logo地址"
          header-align="center"
          align="center"
        >
          <template slot-scope="scope">
            <el-image
              :preview-src-list="[scope.row.logo]"
              style="width: 100px; height: 100px"
              :src="scope.row.logo"
              fit="fit"
            ></el-image>
          </template>
        </el-table-column>
        <el-table-column
          prop="descript"
          label="介绍"
          header-align="center"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="showStatus"
          label="显示状态"
          header-align="center"
          align="center"
        >
          <template slot-scope="scope">
            <el-switch
              @change="changeShowStatus(scope.row)"
              v-model="scope.row.switchStatus"
              active-color="#13ce66"
              inactive-color="#ff4949"
              :active-value="1"
              :inactive-value="0"
            >
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column
          prop="firstLetter"
          label="检索首字母"
          header-align="center"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="sort"
          label="排序"
          header-align="center"
          align="center"
        ></el-table-column>
        <el-table-column
          :label="$t('handle')"
          fixed="right"
          header-align="center"
          align="center"
          width="150"
        >
          <template slot-scope="scope">
            <el-button
              v-if="$hasPermission('product:brand:update')"
              type="text"
              size="small"
              @click="addRelationHandle(scope.row.brandId)"
              >{{ $t('relation') }}</el-button
            >
            <el-button
              v-if="$hasPermission('product:brand:update')"
              type="text"
              size="small"
              @click="addOrUpdateHandle(scope.row.brandId)"
              >{{ $t('update') }}</el-button
            >
            <el-button
              v-if="$hasPermission('product:brand:delete')"
              type="text"
              size="small"
              @click="deleteBrand(scope.row.brandId, scope.row.name)"
              >{{ $t('delete') }}</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="limit"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="pageSizeChangeHandle"
        @current-change="pageCurrentChangeHandle"
      >
      </el-pagination>
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update
        v-if="addOrUpdateVisible"
        ref="addOrUpdate"
        @refreshDataList="getDataList"
      ></add-or-update>

      <el-dialog
        title="关联分类"
        :visible.sync="cateRelationDialogVisible"
        width="30%"
      >
        <el-popover placement="right-end" v-model="popCatelogSelectVisible">
          <category-cascader
            :catelogPath.sync="catelogPath"
          ></category-cascader>
          <div style="text-align: right; margin: 0">
            <el-button
              size="mini"
              type="text"
              @click="popCatelogSelectVisible = false"
              >取消</el-button
            >
            <el-button type="primary" size="mini" @click="addCatelogSelect"
              >确定</el-button
            >
          </div>
          <el-button slot="reference">新增关联</el-button>
        </el-popover>
        <el-table :data="cateRelationTableData" style="width: 100%">
          <el-table-column prop="id" label="#"></el-table-column>
          <el-table-column prop="brandName" label="品牌名"></el-table-column>
          <el-table-column prop="catelogName" label="分类名"></el-table-column>
          <el-table-column
            fixed="right"
            header-align="center"
            align="center"
            label="操作"
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                size="small"
                @click="
                  deleteCateRelationHandle(scope.row.id, scope.row.brandId)
                "
                >移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <span slot="footer" class="dialog-footer">
          <el-button @click="cateRelationDialogVisible = false"
            >取 消</el-button
          >
          <el-button type="primary" @click="cateRelationDialogVisible = false"
            >确 定</el-button
          >
        </span>
      </el-dialog>
    </div>
  </el-card>
</template>

<script>
import mixinViewModule from '@/mixins/view-module'
import AddOrUpdate from './brand-add-or-update'
import { brandUrl, categorybrandrelationUrl } from '@/api/product/index'
import CategoryCascader from '../common/CategoryCascader'
export default {
  mixins: [mixinViewModule],
  data() {
    return {
      mixinViewModuleOptions: {
        getDataListURL: '/product/brand/page',
        getDataListIsPage: true,
        exportURL: '/product/brand/export',
        deleteURL: '/product/brand',
        deleteIsBatch: true
      },
      dataForm: {
        brandId: ''
      },
      ids: [],
      catelogPath: [],
      cateRelationTableData: [],
      ateRelationDialogVisible: false,
      cateRelationDialogVisible: false,
      popCatelogSelectVisible: false
    }
  },
  components: {
    AddOrUpdate,
    CategoryCascader
  },
  methods: {

    addCatelogSelect() {
      //{"brandId":1,"catelogId":2}
      this.popCatelogSelectVisible = false
      this.$http
        .post(categorybrandrelationUrl.ategorybrandrelation, {
          brandId: this.brandId,
          catelogId: this.catelogPath[this.catelogPath.length - 1]
        })
        .then(({ data }) => {
          this.getCateRelation()
        })
    },
    // 关联分类
    addRelationHandle(brandId) {
      this.cateRelationDialogVisible = true
      this.brandId = brandId
      this.getCateRelation()
    },
    getCateRelation() {
      this.$http
        .get(categorybrandrelationUrl.getAtegorybrandrelation, {
          params: {
            brandId: this.brandId
          }
        })
        .then(({ data }) => {
          this.cateRelationTableData = data.data
        })
    },
    deleteCateRelationHandle(id, brandId) {
      let ids = [id]
      this.$http
        .delete(categorybrandrelationUrl.BaseURL, {
          data: ids
        })
        .then(({ data }) => {
          if (data.code !== 200) {
            return this.$message.error(data.msg)
          }
          this.$message.success(data.msg)
          this.getCateRelation()
        })
    },
    deleteBrand(id, name) {
      if (id) {
        this.ids = [id]
      } else {
        this.ids = this.ids
      }
      if (this.ids.length === 0) {
        this.$message.warning('请选择要删除的数据')
        return
      }

      this.$confirm(
        name
          ? `此操作将永久删除该 [${name}] , 是否继续?`
          : `此操作将永久删除该数据, 是否继续?`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(() => {
          this.$http
            .delete(brandUrl.BaseURL, {
              data: this.ids
            })
            .then(({ data: res }) => {
              if (res.code !== 200) {
                return this.$message.error(res.msg)
              }
              this.$message.success(res.msg)
            })
            .catch(err => {})
          // 获取列表数据
          this.getDataList()
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    dataListSelectionChangeHandle(val) {
      this.ids = val.map(item => item.brandId)
    },
    getBrand() {
      this.$http
        .get(brandUrl.BaseURL)
        .then(({ data: res }) => {
          if (res.code !== 200) {
            return this.message.error(res.msg)
          }
          this.message.success(res.msg)
        })
        .catch(err => {})
    },
    // 显示状态
    changeShowStatus(row) {
      this.$http
        .put(brandUrl.BaseURL, {
          brandId: row.brandId,
          switchStatus: row.switchStatus
        })
        .then(({ data: res }) => {
          if (res.code !== 200) {
            return this.$message.error(res.msg)
          }
          this.$message.success(res.msg)
          this.getDataList()
        })
        .catch(err => {})
    }
  }
}
</script>
