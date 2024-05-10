<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-product__spucomment}">
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
          <el-button v-if="$hasPermission('product:spucomment:save')" type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('product:spucomment:delete')" type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="id" label="id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="skuId" label="sku_id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="spuId" label="spu_id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="spuName" label="商品名字" header-align="center" align="center"></el-table-column>
        <el-table-column prop="memberNickName" label="会员昵称" header-align="center" align="center"></el-table-column>
        <el-table-column prop="star" label="星级" header-align="center" align="center"></el-table-column>
        <el-table-column prop="memberIp" label="会员ip" header-align="center" align="center"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" header-align="center" align="center"></el-table-column>
        <el-table-column prop="showStatus" label="显示状态[0-不显示，1-显示]" header-align="center" align="center"></el-table-column>
        <el-table-column prop="spuAttributes" label="购买时属性组合" header-align="center" align="center"></el-table-column>
        <el-table-column prop="likesCount" label="点赞数" header-align="center" align="center"></el-table-column>
        <el-table-column prop="replyCount" label="回复数" header-align="center" align="center"></el-table-column>
        <el-table-column prop="resources" label="评论图片/视频[json数据；[{type:文件类型,url:资源路径}]]" header-align="center" align="center"></el-table-column>
        <el-table-column prop="content" label="内容" header-align="center" align="center"></el-table-column>
        <el-table-column prop="memberIcon" label="用户头像" header-align="center" align="center"></el-table-column>
        <el-table-column prop="commentType" label="评论类型[0 - 对商品的直接评论，1 - 对评论的回复]" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('product:spucomment:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('product:spucomment:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './spucomment-add-or-update'
export default {
  mixins: [mixinViewModule],
  data () {
    return {
      mixinViewModuleOptions: {
        getDataListURL: '/product/spucomment/page',
        getDataListIsPage: true,
        exportURL: '/product/spucomment/export',
        deleteURL: '/product/spucomment',
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
