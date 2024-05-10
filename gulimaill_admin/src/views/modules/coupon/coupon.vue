<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-coupon__coupon}">
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
          <el-button v-if="$hasPermission('coupon:coupon:save')" type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('coupon:coupon:delete')" type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="id" label="id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="couponType" label="优惠卷类型[0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券]" header-align="center" align="center"></el-table-column>
        <el-table-column prop="couponImg" label="优惠券图片" header-align="center" align="center"></el-table-column>
        <el-table-column prop="couponName" label="优惠卷名字" header-align="center" align="center"></el-table-column>
        <el-table-column prop="num" label="数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="amount" label="金额" header-align="center" align="center"></el-table-column>
        <el-table-column prop="perLimit" label="每人限领张数" header-align="center" align="center"></el-table-column>
        <el-table-column prop="minPoint" label="使用门槛" header-align="center" align="center"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" header-align="center" align="center"></el-table-column>
        <el-table-column prop="endTime" label="结束时间" header-align="center" align="center"></el-table-column>
        <el-table-column prop="useType" label="使用类型[0->全场通用；1->指定分类；2->指定商品]" header-align="center" align="center"></el-table-column>
        <el-table-column prop="note" label="备注" header-align="center" align="center"></el-table-column>
        <el-table-column prop="publishCount" label="发行数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="useCount" label="已使用数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="receiveCount" label="领取数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="enableStartTime" label="可以领取的开始日期" header-align="center" align="center"></el-table-column>
        <el-table-column prop="enableEndTime" label="可以领取的结束日期" header-align="center" align="center"></el-table-column>
        <el-table-column prop="code" label="优惠码" header-align="center" align="center"></el-table-column>
        <el-table-column prop="memberLevel" label="可以领取的会员等级[0->不限等级，其他-对应等级]" header-align="center" align="center"></el-table-column>
        <el-table-column prop="publish" label="发布状态[0-未发布，1-已发布]" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('coupon:coupon:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('coupon:coupon:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './coupon-add-or-update'
export default {
  mixins: [mixinViewModule],
  data () {
    return {
      mixinViewModuleOptions: {
        getDataListURL: '/coupon/coupon/page',
        getDataListIsPage: true,
        exportURL: '/coupon/coupon/export',
        deleteURL: '/coupon/coupon',
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
