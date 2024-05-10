<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-coupon__seckillsession}">
            <el-form
                :inline="true"
                :model="dataForm"
                @keyup.enter.native="getDataList()"
            >
                <el-form-item>
                    <el-input
                        v-model="dataForm.id"
                        placeholder="id"
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
                        v-if="$hasPermission('coupon:seckillsession:save')"
                        type="primary"
                        @click="addOrUpdateHandle()"
                        >{{ $t('add') }}</el-button
                    >
                </el-form-item>
                <el-form-item>
                    <el-button
                        v-if="$hasPermission('coupon:seckillsession:delete')"
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
                    label="场次名称"
                ></el-table-column>
                <el-table-column
                    prop="startTime"
                    header-align="center"
                    align="center"
                    label="每日开始时间"
                ></el-table-column>
                <el-table-column
                    prop="endTime"
                    header-align="center"
                    align="center"
                    label="每日结束时间"
                ></el-table-column>
                <el-table-column
                    prop="status"
                    header-align="center"
                    align="center"
                    label="启用状态"
                ></el-table-column>
                <el-table-column
                    prop="createTime"
                    header-align="center"
                    align="center"
                    label="创建时间"
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
                            type="text"
                            size="small"
                            @click="relationProduct(scope.row.id)"
                            >关联商品</el-button
                        >
                        <el-button
                            v-if="
                                $hasPermission('coupon:seckillsession:update')
                            "
                            type="text"
                            size="small"
                            @click="addOrUpdateHandle(scope.row.id)"
                            >{{ $t('update') }}</el-button
                        >
                        <el-button
                            v-if="
                                $hasPermission('coupon:seckillsession:delete')
                            "
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
            <!-- 弹窗, 关联商品 -->
            <div>
                <el-dialog
                    append-to-body
                    :close-on-click-modal="false"
                    :visible.sync="visible"
                    title="关联秒杀商品"
                    width="60%"
                >
                    <seckillsku-relation
                        ref="seckillskuRelation"
                        :sessionId="currentId"
                    ></seckillsku-relation>
                </el-dialog>
            </div>
        </div>
    </el-card>
</template>

<script>
import mixinViewModule from '@/mixins/view-module'
import AddOrUpdate from './seckillsession-add-or-update'
import SeckillskuRelation from './seckillskurelation'
export default {
    mixins: [mixinViewModule],
    data() {
        return {
            mixinViewModuleOptions: {
                getDataListURL: '/coupon/seckillsession/page',
                getDataListIsPage: true,
                exportURL: '/coupon/seckillsession/export',
                deleteURL: '/coupon/seckillsession',
                deleteIsBatch: true
            },
            dataForm: {
                id: ''
            },
            dataListLoading: false,
            dataListSelections: [],
            visible: false,
            currentId: 0
        }
    },
    components: {
        AddOrUpdate,
        SeckillskuRelation
    },
    methods: {
        // 获取数据列表
        getDataList() {
            this.dataListLoading = true
            this.$http
                .get('/coupon/seckillsession/page', {
                    params: {
                        key: this.dataForm.key
                    }
                })
                .then(({ data }) => {
                    if (data && data.code === 200) {
                        this.dataList = data.data.list
                        this.total = data.data.total
                    } else {
                        this.dataList = []
                        this.total = 0
                    }
                    this.dataListLoading = false
                })
        },
        relationProduct(id) {
            this.visible = true
            this.currentId = id
            this.$nextTick(() => {
                this.$refs.seckillskuRelation.getDataList()
            })
        }
    }
}
</script>
