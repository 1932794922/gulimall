<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-ware__purchase}">
            <el-form
                :inline="true"
                :model="dataForm"
                @keyup.enter.native="getDataList()"
            >
                <el-form-item label="状态">
                    <el-select
                        style="width:120px;"
                        v-model="dataForm.status"
                        placeholder="请选择状态"
                        clearable
                    >
                        <el-option label="新建" :value="0"></el-option>
                        <el-option label="已分配" :value="1"></el-option>
                        <el-option label="已领取" :value="2"></el-option>
                        <el-option label="已完成" :value="3"></el-option>
                        <el-option label="有异常" :value="4"></el-option>
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
                        v-if="$hasPermission('ware:purchase:save')"
                        type="primary"
                        @click="addOrUpdateHandle()"
                        >{{ $t('add') }}</el-button
                    >
                </el-form-item>
                <el-form-item>
                    <el-button
                        v-if="$hasPermission('ware:purchase:delete')"
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
                    label="采购单id"
                ></el-table-column>
                <el-table-column
                    prop="assigneeId"
                    header-align="center"
                    align="center"
                    label="采购人id"
                ></el-table-column>
                <el-table-column
                    prop="assigneeName"
                    header-align="center"
                    align="center"
                    label="采购人名"
                ></el-table-column>
                <el-table-column
                    prop="phone"
                    header-align="center"
                    align="center"
                    label="联系方式"
                ></el-table-column>
                <el-table-column
                    prop="priority"
                    header-align="center"
                    align="center"
                    label="优先级"
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
                        <el-tag type="warning" v-if="scope.row.status == 2"
                            >已领取</el-tag
                        >
                        <el-tag type="success" v-if="scope.row.status == 3"
                            >已完成</el-tag
                        >
                        <el-tag type="danger" v-if="scope.row.status == 4"
                            >有异常</el-tag
                        >
                    </template>
                </el-table-column>
                <el-table-column
                    prop="wareId"
                    header-align="center"
                    align="center"
                    label="仓库id"
                ></el-table-column>
                <el-table-column
                    prop="amount"
                    header-align="center"
                    align="center"
                    label="总金额"
                ></el-table-column>
                <el-table-column
                    prop="createTime"
                    header-align="center"
                    align="center"
                    label="创建日期"
                ></el-table-column>
                <el-table-column
                    prop="updateTime"
                    header-align="center"
                    align="center"
                    label="更新日期"
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
                            v-if="
                                scope.row.status == 0 || scope.row.status == 1
                            "
                            @click="opendrawer(scope.row)"
                            >分配</el-button
                        >
                        <el-button
                            v-if="$hasPermission('ware:purchase:update')"
                            type="text"
                            size="small"
                            @click="addOrUpdateHandle(scope.row.id)"
                            >{{ $t('update') }}</el-button
                        >
                        <el-button
                            v-if="$hasPermission('ware:purchase:delete')"
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
            <el-dialog
                title="分配采购人员"
                :visible.sync="caigoudialogVisible"
                width="30%"
            >
                <el-select v-model="userId" filterable placeholder="请选择">
                    <el-option
                        v-for="item in userList"
                        :key="item.id"
                        :label="item.username"
                        :value="item.id"
                    ></el-option>
                </el-select>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="caigoudialogVisible = false"
                        >取 消</el-button
                    >
                    <el-button type="primary" @click="assignUser"
                        >确 定</el-button
                    >
                </span>
            </el-dialog>
        </div>
    </el-card>
</template>

<script>
import mixinViewModule from '@/mixins/view-module'
import AddOrUpdate from './purchase-add-or-update'
export default {
    mixins: [mixinViewModule],
    data() {
        return {
            mixinViewModuleOptions: {
                getDataListURL: '/ware/purchase/page',
                getDataListIsPage: true,
                exportURL: '/ware/purchase/export',
                deleteURL: '/ware/purchase',
                deleteIsBatch: true
            },
            dataForm: {
                key: ''
            },
            dataListLoading: false,
            dataListSelections: [],
            caigoudialogVisible: false,
            userId: '',
            userList: [],
            currentRow: {}
        }
    },
    components: {
        AddOrUpdate
    },
    methods: {
        opendrawer(row) {
            this.getUserList()
            this.currentRow = row
            this.caigoudialogVisible = true
        },
        assignUser() {
            let _this = this
            let user = {}
            this.userList.forEach(item => {
                console.log(item)
                if (item.id == _this.userId) {
                    user = item
                }
            })
            this.caigoudialogVisible = false
            this.$http
                .put(`/ware/purchase`, {
                    id: this.currentRow.id || undefined,
                    assigneeId: user.id,
                    assigneeName: user.username,
                    phone: user.mobile,
                    status: 1
                })
                .then(({ data }) => {
                    if (data && data.code === 200) {
                        this.$message({
                            message: '操作成功',
                            type: 'success',
                            duration: 1500
                        })

                        this.userId = ''
                        this.getDataList()
                    } else {
                        this.$message.error(data.msg)
                    }
                })
        },
        getUserList() {
            this.$http
                .get('/sys/user/page', {
                    params: {
                        page: 1,
                        limit: 500
                    }
                })
                .then(({ data }) => {
                    this.userList = data.data.list
                })
        }
    }
}
</script>
