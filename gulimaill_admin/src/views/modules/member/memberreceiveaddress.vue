<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-member__memberreceiveaddress}">
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
          <el-button v-if="$hasPermission('member:memberreceiveaddress:save')" type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('member:memberreceiveaddress:delete')" type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="id" label="id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="memberId" label="member_id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="name" label="收货人姓名" header-align="center" align="center"></el-table-column>
        <el-table-column prop="phone" label="电话" header-align="center" align="center"></el-table-column>
        <el-table-column prop="postCode" label="邮政编码" header-align="center" align="center"></el-table-column>
        <el-table-column prop="province" label="省份/直辖市" header-align="center" align="center"></el-table-column>
        <el-table-column prop="city" label="城市" header-align="center" align="center"></el-table-column>
        <el-table-column prop="region" label="区" header-align="center" align="center"></el-table-column>
        <el-table-column prop="detailAddress" label="详细地址(街道)" header-align="center" align="center"></el-table-column>
        <el-table-column prop="areacode" label="省市区代码" header-align="center" align="center"></el-table-column>
        <el-table-column prop="defaultStatus" label="是否默认" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('member:memberreceiveaddress:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('member:memberreceiveaddress:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './memberreceiveaddress-add-or-update'
export default {
  mixins: [mixinViewModule],
  data () {
    return {
      mixinViewModuleOptions: {
        getDataListURL: '/member/memberreceiveaddress/page',
        getDataListIsPage: true,
        exportURL: '/member/memberreceiveaddress/export',
        deleteURL: '/member/memberreceiveaddress',
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
