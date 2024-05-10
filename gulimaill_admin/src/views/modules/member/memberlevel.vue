<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-member__memberlevel}">
      <el-form
        :inline="true"
        :model="dataForm"
        @keyup.enter.native="getDataList()"
      >
        <el-form-item>
          <el-input v-model="dataForm.id" placeholder="id" clearable></el-input>
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
            v-if="$hasPermission('member:memberlevel:save')"
            type="primary"
            @click="addOrUpdateHandle()"
            >{{ $t('add') }}</el-button
          >
        </el-form-item>
        <el-form-item>
          <el-button
            v-if="$hasPermission('member:memberlevel:delete')"
            type="danger"
            @click="deleteHandle()"
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
          prop="id"
          header-align="center"
          align="center"
          label="id"
        ></el-table-column>
        <el-table-column
          prop="name"
          header-align="center"
          align="center"
          label="等级名称"
        ></el-table-column>
        <el-table-column
          prop="growthPoint"
          header-align="center"
          align="center"
          label="所需成长值"
        ></el-table-column>
        <el-table-column
          prop="defaultStatus"
          header-align="center"
          align="center"
          label="默认等级"
        >
          <template slot-scope="scope">
            <i class="el-icon-success" v-if="scope.row.defaultStatus == 1"></i>
            <i class="el-icon-error" v-else></i>
          </template>
        </el-table-column>
        <el-table-column
          prop="freeFreightPoint"
          header-align="center"
          align="center"
          label="免运费标准"
        ></el-table-column>
        <el-table-column
          prop="commentGrowthPoint"
          header-align="center"
          align="center"
          label="每次评价获取的成长值"
        ></el-table-column>
        <el-table-column align="center" label="特权">
          <el-table-column
            prop="priviledgeFreeFreight"
            header-align="center"
            align="center"
            label="免邮特权"
          >
            <template slot-scope="scope">
              <i
                class="el-icon-success"
                v-if="scope.row.priviledgeFreeFreight == 1"
              ></i>
              <i class="el-icon-error" v-else></i>
            </template>
          </el-table-column>
          <el-table-column
            prop="priviledgeMemberPrice"
            header-align="center"
            align="center"
            label="会员价格特权"
          >
            <template slot-scope="scope">
              <i
                class="el-icon-success"
                v-if="scope.row.priviledgeMemberPrice == 1"
              ></i>
              <i class="el-icon-error" v-else></i>
            </template>
          </el-table-column>
          <el-table-column
            prop="priviledgeBirthday"
            header-align="center"
            align="center"
            label="生日特权"
          >
            <template slot-scope="scope">
              <i
                class="el-icon-success"
                v-if="scope.row.priviledgeBirthday == 1"
              ></i>
              <i class="el-icon-error" v-else></i>
            </template>
          </el-table-column>
        </el-table-column>
        <el-table-column
          prop="note"
          header-align="center"
          align="center"
          label="备注"
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
              v-if="$hasPermission('member:memberlevel:update')"
              type="text"
              size="small"
              @click="addOrUpdateHandle(scope.row.id)"
              >{{ $t('update') }}</el-button
            >
            <el-button
              v-if="$hasPermission('member:memberlevel:delete')"
              type="text"
              size="small"
              @click="deleteHandle(scope.row.id)"
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
    </div>
  </el-card>
</template>

<script>
import mixinViewModule from '@/mixins/view-module'
import AddOrUpdate from './memberlevel-add-or-update'
export default {
  mixins: [mixinViewModule],
  data() {
    return {
      mixinViewModuleOptions: {
        getDataListURL: '/member/memberlevel/page',
        getDataListIsPage: true,
        exportURL: '/member/memberlevel/export',
        deleteURL: '/member/memberlevel',
        deleteIsBatch: true
      },
      dataForm: {
        id: ''
      }
    }
  },
  components: {
    AddOrUpdate
  }
}
</script>
