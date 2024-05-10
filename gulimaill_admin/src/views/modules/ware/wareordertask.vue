<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-ware__wareordertask}">
      <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
        <el-form-item>
          <el-input v-model="dataForm.id" placeholder="id" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('ware:wareordertask:save')" type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('ware:wareordertask:delete')" type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="id" label="id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="orderId" label="order_id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="orderSn" label="order_sn" header-align="center" align="center"></el-table-column>
        <el-table-column prop="consignee" label="收货人" header-align="center" align="center"></el-table-column>
        <el-table-column prop="consigneeTel" label="收货人电话" header-align="center" align="center"></el-table-column>
        <el-table-column prop="deliveryAddress" label="配送地址" header-align="center" align="center"></el-table-column>
        <el-table-column prop="orderComment" label="订单备注" header-align="center" align="center"></el-table-column>
        <el-table-column prop="paymentWay" label="付款方式【 1:在线付款 2:货到付款】" header-align="center" align="center"></el-table-column>
        <el-table-column prop="taskStatus" label="任务状态" header-align="center" align="center"></el-table-column>
        <el-table-column prop="orderBody" label="订单描述" header-align="center" align="center"></el-table-column>
        <el-table-column prop="trackingNo" label="物流单号" header-align="center" align="center"></el-table-column>
        <el-table-column prop="createTime" label="create_time" header-align="center" align="center"></el-table-column>
        <el-table-column prop="wareId" label="仓库id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="taskComment" label="工作单备注" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('ware:wareordertask:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('ware:wareordertask:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
        @current-change="pageCurrentChangeHandle">
      </el-pagination>
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    </div>
  </el-card>
</template>

<script>
import mixinViewModule from '@/mixins/view-module'
import AddOrUpdate from './wareordertask-add-or-update'
export default {
  mixins: [mixinViewModule],
  data () {
    return {
      mixinViewModuleOptions: {
        getDataListURL: '/ware/wareordertask/page',
        getDataListIsPage: true,
        exportURL: '/ware/wareordertask/export',
        deleteURL: '/ware/wareordertask',
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
