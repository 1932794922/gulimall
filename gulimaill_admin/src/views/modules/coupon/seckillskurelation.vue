<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-coupon__seckillskurelation}">
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
                        v-if="$hasPermission('coupon:seckillskurelation:save')"
                        type="primary"
                        @click="addOrUpdateHandle()"
                        >{{ $t('add') }}</el-button
                    >
                </el-form-item>
                <el-form-item>
                    <el-button
                        v-if="
                            $hasPermission('coupon:seckillskurelation:delete')
                        "
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
                    label="id"
                    header-align="center"
                    align="center"
                ></el-table-column>
                <el-table-column
                    prop="promotionId"
                    label="活动id"
                    header-align="center"
                    align="center"
                ></el-table-column>
                <el-table-column
                    prop="promotionSessionId"
                    label="活动场次id"
                    header-align="center"
                    align="center"
                ></el-table-column>
                <el-table-column
                    prop="skuId"
                    label="商品id"
                    header-align="center"
                    align="center"
                ></el-table-column>
                <el-table-column
                    prop="seckillPrice"
                    label="秒杀价格"
                    header-align="center"
                    align="center"
                ></el-table-column>
                <el-table-column
                    prop="seckillCount"
                    label="秒杀总量"
                    header-align="center"
                    align="center"
                ></el-table-column>
                <el-table-column
                    prop="seckillLimit"
                    label="每人限购数量"
                    header-align="center"
                    align="center"
                ></el-table-column>
                <el-table-column
                    prop="seckillSort"
                    label="排序"
                    header-align="center"
                    align="center"
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
                            v-if="
                                $hasPermission(
                                    'coupon:seckillskurelation:update'
                                )
                            "
                            type="text"
                            size="small"
                            @click="addOrUpdateHandle(scope.row.id)"
                            >{{ $t('update') }}</el-button
                        >
                        <el-button
                            v-if="
                                $hasPermission(
                                    'coupon:seckillskurelation:delete'
                                )
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
        </div>
    </el-card>
</template>

<script>
import mixinViewModule from '@/mixins/view-module'
import AddOrUpdate from './seckillskurelation-add-or-update'
export default {
    mixins: [mixinViewModule],
    data() {
        return {
            mixinViewModuleOptions: {
                getDataListURL: '/coupon/seckillskurelation/page',
                getDataListIsPage: true,
                exportURL: '/coupon/seckillskurelation/export',
                deleteURL: '/coupon/seckillskurelation',
                deleteIsBatch: true
            },
            dataForm: {
                id: '',
                promotionSessionId: ''
            }
        }
    },
    components: {
        AddOrUpdate
    },
    props: {
        sessionId: {
            type: Number,
            default: 0
        }
    },
    mounted() {},
    methods: {
        // 获取数据列表
        getDataList() {
            this.dataListLoading = true
            this.$http
                .get('/coupon/seckillskurelation/page', {
                    params: {
                        key: this.dataForm.key,
                        promotionSessionId: this.sessionId
                    }
                })
                .then(({ data: data }) => {
                    if (data && data.code === 200) {
                        this.dataList = data.data.list
                        this.total = data.data.total
                    } else {
                        this.dataList = []
                        this.total = 0
                    }
                    this.dataListLoading = false
                })
        }
    }
}
</script>
