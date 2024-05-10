<template>
  <el-card shadow="never" class="aui-card--fill">
    <el-row :gutter="20">
      <el-col :span="6">
        <CategoryComm @tree-node-click="treeNodeclick"></CategoryComm>
      </el-col>
      <el-col :span="18">
        <div class="mod-product__attr}">
          <el-form
            :inline="true"
            :model="dataForm"
            @keyup.enter.native="getDataList()"
          >
            <el-form-item>
              <el-input
                v-model="dataForm.key"
                placeholder="请输入关键字"
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
                v-if="$hasPermission('product:attr:save')"
                type="primary"
                @click="addOrUpdateHandle()"
                >{{ $t('add') }}</el-button
              >
            </el-form-item>
            <el-form-item>
              <el-button
                v-if="$hasPermission('product:attr:delete')"
                type="danger"
                @click="deleteHandle()"
                >{{ $t('deleteBatch') }}</el-button
              >
            </el-form-item>
            <el-form-item>
              <el-button
                v-if="$hasPermission('product:brand:save')"
                type="primary"
                @click="queryAll"
                >{{ $t('queryAll') }}</el-button
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
              prop="attrId"
              header-align="center"
              align="center"
              label="id"
            ></el-table-column>
            <el-table-column
              prop="attrName"
              header-align="center"
              align="center"
              label="属性名"
            ></el-table-column>
            <el-table-column
              v-if="attrtype == 1"
              prop="searchType"
              header-align="center"
              align="center"
              label="可检索"
            >
              <template slot-scope="scope">
                <i class="el-icon-success" v-if="scope.row.searchType == 1"></i>
                <i class="el-icon-error" v-else></i>
              </template>
            </el-table-column>
            <el-table-column
              prop="valueType"
              header-align="center"
              align="center"
              label="值类型"
            >
              <template slot-scope="scope">
                <el-tag type="success" v-if="scope.row.valueType == 0"
                  >单选</el-tag
                >
                <el-tag v-else>多选</el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="icon"
              header-align="center"
              align="center"
              label="图标"
            ></el-table-column>
            <el-table-column
              prop="valueSelect"
              header-align="center"
              align="center"
              label="可选值"
            >
              <template slot-scope="scope">
                <el-tooltip placement="top">
                  <div slot="content">
                    <span
                      v-for="(i, index) in scope.row.valueSelect.split(';')"
                      :key="index"
                      >{{ i }}<br
                    /></span>
                  </div>
                  <el-tag>{{
                    scope.row.valueSelect.split(';')[0] + ' ...'
                  }}</el-tag>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column
              prop="enable"
              header-align="center"
              align="center"
              label="启用"
            >
              <template slot-scope="scope">
                <i class="el-icon-success" v-if="scope.row.enable == 1"></i>
                <i class="el-icon-error" v-else></i>
              </template>
            </el-table-column>
            <el-table-column
              prop="catelogName"
              header-align="center"
              align="center"
              label="所属分类"
            ></el-table-column>
            <el-table-column
              v-if="attrtype == 1"
              prop="groupName"
              header-align="center"
              align="center"
              label="所属分组"
            ></el-table-column>
            <el-table-column
              v-if="attrtype == 1"
              prop="showDesc"
              header-align="center"
              align="center"
              label="快速展示"
            >
              <template slot-scope="scope">
                <i class="el-icon-success" v-if="scope.row.showDesc == 1"></i>
                <i class="el-icon-error" v-else></i>
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('handle')"
              fixed="right"
              header-align="center"
              align="center"
              width="150"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="$hasPermission('product:attr:update')"
                  type="text"
                  size="small"
                  @click="addOrUpdateHandle(scope.row.attrId)"
                  >{{ $t('update') }}</el-button
                >
                <el-button
                  v-if="$hasPermission('product:attr:delete')"
                  type="text"
                  size="small"
                  @click="deleteHandle(scope.row.attrId)"
                  >{{ $t('delete') }}</el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            :current-page="page"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="limit"
            :total="totalPage"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="pageSizeChangeHandle"
            @current-change="pageCurrentChangeHandle"
          >
          </el-pagination>
          <!-- 弹窗, 新增 / 修改 -->
          <add-or-update
            :type="attrtype"
            v-if="addOrUpdateVisible"
            ref="addOrUpdate"
            @refreshDataList="getDataList"
          ></add-or-update>
        </div>
      </el-col>
    </el-row>
  </el-card>
</template>

<script>
import mixinViewModule from '@/mixins/view-module'
import AddOrUpdate from './attr-add-or-update'
import CategoryComm from '@/views/modules/common/categorycomm'
export default {
  mixins: [mixinViewModule],
  props: {
    attrtype: {
      type: Number,
      default: 1
    }
  },
  data() {
    return {
      mixinViewModuleOptions: {
        getDataListURL: '/product/attr/page',
        getDataListIsPage: true,
        exportURL: '/product/attr/export',
        deleteURL: '/product/attr',
        deleteIsBatch: true,
        deleteIsBatchKey: 'attrId',
        createdIsNeed: false,
        updateKey: 'attrId'
      },
      catId: 0,
      dataList: [],
      dataForm: {
        attrId: ''
      },
      totalPage: 0
    }
  },
  components: {
    AddOrUpdate,
    CategoryComm
  },
  methods: {
    queryAll() {
      this.catId = 0
      this.getDataList()
    },
    treeNodeclick(data, node, component) {
      if (node.level == 3) {
        this.catId = data.catId
        this.getDataList() //重新查询
      }
    }, // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      let type = this.attrtype == 0 ? 'sale' : 'base'
      this.$http
        .get(`/product/attr/${type}/list/${this.catId}`, {
          params: {
            key: this.dataForm.key,
            page: this.pageIndex,
            limit: this.pageSize,
            key: this.dataForm.key
          }
        })
        .then(({ data: res }) => {
          if (res.code !== 200) {
            this.dataList = []
            this.totalPage = 0
            return this.message.error({
              message: res.msg,
              center: true
            })
          }
          this.dataList = res.data.list
          this.totalPage = res.data.total
          this.dataListLoading = false
        })
        .catch(err => {})
    }
  },
  created() {
    this.getDataList()
    console.log('this.attrtype', this.attrtype)
  }
}
</script>
