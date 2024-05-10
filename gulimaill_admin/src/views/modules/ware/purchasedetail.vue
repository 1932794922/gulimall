<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-ware__purchasedetail}">
            <el-form
                :inline="true"
                :model="dataForm"
                @keyup.enter.native="getDataList()"
            >
                <el-form-item label="仓库">
                    <el-select
                        style="width:120px;"
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
                <el-form-item label="状态">
                    <el-select
                        style="width:120px;"
                        v-model="dataForm.status"
                        placeholder="请选择状态"
                        clearable
                    >
                        <el-option label="新建" :value="0"></el-option>
                        <el-option label="已分配" :value="1"></el-option>
                        <el-option label="正在采购" :value="2"></el-option>
                        <el-option label="已完成" :value="3"></el-option>
                        <el-option label="采购失败" :value="4"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="关键字">
                    <el-input
                        style="width:120px;"
                        v-model="dataForm.key"
                        placeholder="参数名"
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
                        v-if="$hasPermission('ware:purchasedetail:save')"
                        type="primary"
                        @click="addOrUpdateHandle()"
                        >{{ $t('add') }}</el-button
                    >
                </el-form-item>
                <el-form-item>
                    <el-button
                        v-if="$hasPermission('ware:purchasedetail:delete')"
                        type="danger"
                        @click="deleteHandle()"
                        >{{ $t('deleteBatch') }}</el-button
                    >
                </el-form-item>
                <el-dropdown
                    @command="handleBatchCommand"
                    :disabled="dataListSelections.length <= 0"
                >
                    <el-button type="danger">
                        批量操作
                        <i class="el-icon-arrow-down el-icon--right"></i>
                    </el-button>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item command="delete"
                            >批量删除</el-dropdown-item
                        >
                        <el-dropdown-item command="merge"
                            >合并整单</el-dropdown-item
                        >
                    </el-dropdown-menu>
                </el-dropdown>
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
                    prop="purchaseId"
                    header-align="center"
                    align="center"
                    label="采购单id"
                ></el-table-column>
                <el-table-column
                    prop="skuId"
                    header-align="center"
                    align="center"
                    label="采购商品id"
                ></el-table-column>
                <el-table-column
                    prop="skuNum"
                    header-align="center"
                    align="center"
                    label="采购数量"
                ></el-table-column>
                <el-table-column
                    prop="skuPrice"
                    header-align="center"
                    align="center"
                    label="采购金额"
                ></el-table-column>
                <el-table-column
                    prop="wareId"
                    header-align="center"
                    align="center"
                    label="仓库id"
                ></el-table-column>
                <el-table-column
                    prop="status"
                    header-align="center"
                    align="center"
                    label="状态"
                >
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.status == 0">新建</el-tag>
                        <el-tag type="info" v-if="scope.row.status == 1"
                            >已分配</el-tag
                        >
                        <el-tag type="wanring" v-if="scope.row.status == 2"
                            >正在采购</el-tag
                        >
                        <el-tag type="success" v-if="scope.row.status == 3"
                            >已完成</el-tag
                        >
                        <el-tag type="danger" v-if="scope.row.status == 4"
                            >采购失败</el-tag
                        >
                    </template>
                </el-table-column>
                <el-table-column
                    :label="$t('handle')"
                    fixed="right"
                    header-align="center"
                    align="center"
                    width="150"
                >
                    <template slot-scope="scope">
                        <el-button
                            v-if="$hasPermission('ware:purchasedetail:update')"
                            type="text"
                            size="small"
                            @click="addOrUpdateHandle(scope.row.id)"
                            >{{ $t('update') }}</el-button
                        >
                        <el-button
                            v-if="$hasPermission('ware:purchasedetail:delete')"
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
            <el-dialog title="合并到整单" :visible.sync="mergedialogVisible">
                <!-- id  assignee_id  assignee_name  phone   priority status -->
                <el-select
                    v-model="purchaseId"
                    placeholder="请选择"
                    clearable
                    filterable
                >
                    <el-option
                        v-for="item in purchasetableData"
                        :key="item.id"
                        :label="item.id"
                        :value="item.id"
                    >
                        <span style="float: left">{{ item.id }}</span>
                        <span
                            style="float: right; color: #8492a6; font-size: 13px"
                            >{{ item.assigneeName }}：{{ item.phone }}</span
                        >
                    </el-option>
                </el-select>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="mergedialogVisible = false"
                        >取 消</el-button
                    >
                    <el-button type="primary" @click="mergeItem"
                        >确 定</el-button
                    >
                </span>
            </el-dialog>
        </div>
    </el-card>
</template>

<script>
import mixinViewModule from '@/mixins/view-module'
import AddOrUpdate from './purchasedetail-add-or-update'
export default {
    mixins: [mixinViewModule],
    data() {
        return {
            mixinViewModuleOptions: {
                getDataListURL: '/ware/purchasedetail/page',
                getDataListIsPage: true,
                exportURL: '/ware/purchasedetail/export',
                deleteURL: '/ware/purchasedetail',
                deleteIsBatch: true
            },
            dataForm: {
                key: '',
                status: '',
                wareId: ''
            },
            wareList: [],
            dataListLoading: false,
            dataListSelections: [],
            mergedialogVisible: false,
            purchaseId: '',
            purchasetableData: []
        }
    },
    components: {
        AddOrUpdate
    },
    methods: {
        mergeItem() {
            let items = this.dataListSelections.map(item => {
                return item.id
            })
            if (!this.purchaseId) {
                this.$confirm(
                    '没有选择任何【采购单】，将自动创建新单进行合并。确认吗？',
                    '提示',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                )
                    .then(() => {
                        this.$http
                            .post('/ware/purchase/merge', {
                                items: items
                            })
                            .then(({ data }) => {
                                if (data.code !== 200) {
                                    return this.$message.error(data.msg)
                                }
                                this.$message.success(data.msg)
                                this.getDataList()
                            })
                    })
                    .catch(() => {})
            } else {
                this.$http
                    .post('/ware/purchase/merge', {
                        purchaseId: this.purchaseId,
                        items: items
                    })
                    .then(({ data }) => {
                        this.getDataList()
                    })
            }
            this.mergedialogVisible = false
        },
        getUnreceivedPurchase() {
            this.$http.get('/ware/purchase/unreceive/list').then(({ data }) => {
                this.purchasetableData = data.data.list
            })
        },
        handleBatchCommand(cmd) {
            if (cmd == 'delete') {
                this.deleteHandle()
            }
            if (cmd == 'merge') {
                if (this.dataListSelections.length != 0) {
                    this.getUnreceivedPurchase()
                    this.mergedialogVisible = true
                } else {
                    this.$alert('请先选择需要合并的需求', '提示', {
                        confirmButtonText: '确定',
                        callback: action => {}
                    })
                }
            }
        },
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
        this.getWares()
    }
}
</script>
