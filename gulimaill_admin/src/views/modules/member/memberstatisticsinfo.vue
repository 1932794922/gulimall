<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-member__memberstatisticsinfo}">
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
          <el-button v-if="$hasPermission('member:memberstatisticsinfo:save')" type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('member:memberstatisticsinfo:delete')" type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="id" label="id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="memberId" label="会员id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="consumeAmount" label="累计消费金额" header-align="center" align="center"></el-table-column>
        <el-table-column prop="couponAmount" label="累计优惠金额" header-align="center" align="center"></el-table-column>
        <el-table-column prop="orderCount" label="订单数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="couponCount" label="优惠券数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="commentCount" label="评价数" header-align="center" align="center"></el-table-column>
        <el-table-column prop="returnOrderCount" label="退货数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="loginCount" label="登录次数" header-align="center" align="center"></el-table-column>
        <el-table-column prop="attendCount" label="关注数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="fansCount" label="粉丝数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="collectProductCount" label="收藏的商品数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="collectSubjectCount" label="收藏的专题活动数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="collectCommentCount" label="收藏的评论数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="inviteFriendCount" label="邀请的朋友数量" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('member:memberstatisticsinfo:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('member:memberstatisticsinfo:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './memberstatisticsinfo-add-or-update'
export default {
  mixins: [mixinViewModule],
  data () {
    return {
      mixinViewModuleOptions: {
        getDataListURL: '/member/memberstatisticsinfo/page',
        getDataListIsPage: true,
        exportURL: '/member/memberstatisticsinfo/export',
        deleteURL: '/member/memberstatisticsinfo',
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
