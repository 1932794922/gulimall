<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-member__member}">
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
          <el-button v-if="$hasPermission('member:member:save')" type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('member:member:delete')" type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="id" label="id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="levelId" label="会员等级id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="username" label="用户名" header-align="center" align="center"></el-table-column>
        <el-table-column prop="password" label="密码" header-align="center" align="center"></el-table-column>
        <el-table-column prop="nickname" label="昵称" header-align="center" align="center"></el-table-column>
        <el-table-column prop="mobile" label="手机号码" header-align="center" align="center"></el-table-column>
        <el-table-column prop="email" label="邮箱" header-align="center" align="center"></el-table-column>
        <el-table-column prop="header" label="头像" header-align="center" align="center"></el-table-column>
        <el-table-column prop="gender" label="性别" header-align="center" align="center"></el-table-column>
        <el-table-column prop="birth" label="生日" header-align="center" align="center"></el-table-column>
        <el-table-column prop="city" label="所在城市" header-align="center" align="center"></el-table-column>
        <el-table-column prop="job" label="职业" header-align="center" align="center"></el-table-column>
        <el-table-column prop="sign" label="个性签名" header-align="center" align="center"></el-table-column>
        <el-table-column prop="sourceType" label="用户来源" header-align="center" align="center"></el-table-column>
        <el-table-column prop="integration" label="积分" header-align="center" align="center"></el-table-column>
        <el-table-column prop="growth" label="成长值" header-align="center" align="center"></el-table-column>
        <el-table-column prop="status" label="启用状态" header-align="center" align="center"></el-table-column>
        <el-table-column prop="createTime" label="注册时间" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('member:member:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('member:member:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './member-add-or-update'
export default {
  mixins: [mixinViewModule],
  data () {
    return {
      mixinViewModuleOptions: {
        getDataListURL: '/member/member/page',
        getDataListIsPage: true,
        exportURL: '/member/member/export',
        deleteURL: '/member/member',
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
