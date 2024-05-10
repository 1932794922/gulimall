<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-ware__waresku}">
            <el-form
                :inline="true"
                :model="dataForm"
                @keyup.enter.native="getDataList()"
            >
                <el-form-item label="仓库">
                    <el-select
                        style="width:160px;"
                        v-model="dataForm.wareId"
                        placeholder="请选择仓库"
                        clearable
                    >
                        <el-option
                            :label="w.name"
                            :value="w.id"
                            v-for="w in wareList"
                            :key="w.id"
                        ></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-input
                        v-model="dataForm.skuId"
                        placeholder="skuId"
                        clearable
                    ></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button @click="getDataList()">{{
                        $t('query')
                    }}</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="info" @click="exportHandle()">{{
                        $t('export')
                    }}</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button
                        v-if="$hasPermission('ware:waresku:save')"
                        type="primary"
                        @click="addOrUpdateHandle()"
                        >{{ $t('add') }}</el-button
                    >
                </el-form-item>
                <el-form-item>
                    <el-button
                        v-if="$hasPermission('ware:waresku:delete')"
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
                    prop="skuId"
                    header-align="center"
                    align="center"
                    label="sku_id"
                ></el-table-column>
                <el-table-column
                    prop="wareId"
                    header-align="center"
                    align="center"
                    label="仓库id"
                ></el-table-column>
                <el-table-column
                    prop="stock"
                    header-align="center"
                    align="center"
                    label="库存数"
                ></el-table-column>
                <el-table-column
                    prop="skuName"
                    header-align="center"
                    align="center"
                    label="sku_name"
                ></el-table-column>
                <el-table-column
                    prop="stockLocked"
                    header-align="center"
                    align="center"
                    label="锁定库存"
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
                            v-if="$hasPermission('ware:waresku:update')"
                            type="text"
                            size="small"
                            @click="addOrUpdateHandle(scope.row.id)"
                            >{{ $t('update') }}</el-button
                        >
                        <el-button
                            v-if="$hasPermission('ware:waresku:delete')"
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
import AddOrUpdate from './waresku-add-or-update'
export default {
    mixins: [mixinViewModule],
    data() {
        return {
            mixinViewModuleOptions: {
                getDataListURL: '/ware/waresku/page',
                getDataListIsPage: true,
                exportURL: '/ware/waresku/export',
                deleteURL: '/ware/waresku',
                deleteIsBatch: true
            },
            wareList: [],
            dataForm: {
                wareId: '',
                skuId: ''
            }
        }
    },
    components: {
        AddOrUpdate
    },
    methods: {
        getWares() {
            this.$http
                .get('/ware/wareinfo/page', {
                    params: {
                        page: 1,
                        limit: 500
                    }
                })
                .then(({ data }) => {
                    this.wareList = data.data.list
                })
        }
    },
    activated() {
        console.log('接收到', this.$route.query.skuId)
        if (this.$route.query.skuId) {
            this.dataForm.skuId = this.$route.query.skuId
        }
        this.getWares()
        this.getDataList()
    }
}
</script>
