<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-product__spuinfo}">
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
                    prop="spuName"
                    header-align="center"
                    align="center"
                    label="名称"
                ></el-table-column>
                <el-table-column
                    prop="spuDescription"
                    header-align="center"
                    align="center"
                    label="描述"
                ></el-table-column>
                <el-table-column
                    prop="catalogId"
                    header-align="center"
                    align="center"
                    label="分类"
                ></el-table-column>
                <el-table-column
                    prop="brandId"
                    header-align="center"
                    align="center"
                    label="品牌"
                ></el-table-column>
                <el-table-column
                    prop="weight"
                    header-align="center"
                    align="center"
                    label="重量"
                ></el-table-column>
                <el-table-column
                    prop="publishStatus"
                    header-align="center"
                    align="center"
                    label="上架状态"
                >
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.publishStatus == 0"
                            >新建</el-tag
                        >
                        <el-tag v-if="scope.row.publishStatus == 1"
                            >已上架</el-tag
                        >
                        <el-tag v-if="scope.row.publishStatus == 2"
                            >已下架</el-tag
                        >
                    </template>
                </el-table-column>
                <el-table-column
                    prop="createTime"
                    header-align="center"
                    align="center"
                    label="创建时间"
                ></el-table-column>
                <el-table-column
                    prop="updateTime"
                    header-align="center"
                    align="center"
                    label="修改时间"
                ></el-table-column>
                <el-table-column
                    fixed="right"
                    header-align="center"
                    align="center"
                    width="150"
                    label="操作"
                >
                    <template slot-scope="scope">
                        <el-button
                            v-if="scope.row.publishStatus == 0"
                            type="text"
                            size="small"
                            @click="productUp(scope.row.id)"
                            >上架</el-button
                        >
                        <el-button
                            type="text"
                            size="small"
                            @click="attrUpdateShow(scope.row)"
                            >规格</el-button
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
import AddOrUpdate from './spuinfo-add-or-update'
export default {
    mixins: [mixinViewModule],
    data() {
        return {
            dataSub: null,
            mixinViewModuleOptions: {
                getDataListURL: '/product/spuinfo/page',
                getDataListIsPage: true,
                exportURL: '/product/spuinfo/export',
                deleteURL: '/product/spuinfo',
                deleteIsBatch: true
            },
            dataForm: {
                id: ''
            }
        }
    },
    components: {
        AddOrUpdate
    },
    methods: {
        productUp(id) {
            this.$http
                .post('/product/spuinfo/' + id + '/up')
                .then(({ data }) => {
                    if (data && data.code === 200) {
                        this.$message({
                            message: '操作成功',
                            type: 'success',
                            duration: 1500,
                            onClose: () => {
                                this.getDataList()
                            }
                        })
                    } else {
                        this.$message.error(data.msg)
                    }
                })
        },
        attrUpdateShow(row) {
            console.log(row)
            this.$router.push({
                path: '/product-attrupdate',
                query: { spuId: row.id, catalogId: row.catalogId }
            })
        },
        // 获取数据列表
        getDataList() {
            this.dataListLoading = true
            let param = {}
            Object.assign(param, this.dataForm, {
                page: this.pageIndex,
                limit: this.pageSize
            })
            this.$http
                .get('/product/spuinfo/page', {
                    params: param
                })
                .then(({ data }) => {
                    if (data && data.code === 200) {
                        this.dataList = data.data.list
                        this.totalPage = data.data.totalCount
                    } else {
                        this.dataList = []
                        this.totalPage = 0
                    }
                    this.dataListLoading = false
                })
        },

        // 新增 / 修改
        addOrUpdateHandle(id) {}
    },
    mounted() {
        this.dataSub = PubSub.subscribe('dataForm', (msg, val) => {
            console.log('~~~~~', val)
            this.dataForm = val
            this.getDataList()
        })
    },
    beforeDestroy() {
        PubSub.unsubscribe(this.dataSub)
    }
}
</script>
