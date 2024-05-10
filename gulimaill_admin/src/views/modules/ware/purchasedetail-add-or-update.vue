<template>
    <el-dialog
        :visible.sync="visible"
        :title="!dataForm.id ? $t('add') : $t('update')"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
    >
        <el-form
            :model="dataForm"
            :rules="dataRule"
            ref="dataForm"
            @keyup.enter.native="dataFormSubmitHandle()"
            :label-width="$i18n.locale === 'en-US' ? '140px' : '120px'"
        >
            <el-form-item label="采购商品id" prop="skuId">
                <el-input
                    v-model="dataForm.skuId"
                    placeholder="采购商品id"
                ></el-input>
            </el-form-item>
            <el-form-item label="采购数量" prop="skuNum">
                <el-input
                    v-model="dataForm.skuNum"
                    placeholder="采购数量"
                ></el-input>
            </el-form-item>
            <el-form-item label="仓库" prop="wareId">
                <el-select
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
            <!-- [0新建，1已分配，2正在采购，3已完成，4采购失败] -->
            <el-form-item label="状态" prop="status">
                <el-select
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
        </el-form>
        <template slot="footer">
            <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
            <el-button type="primary" @click="dataFormSubmitHandle()">{{
                $t('confirm')
            }}</el-button>
        </template>
    </el-dialog>
</template>

<script>
import debounce from 'lodash/debounce'
export default {
    data() {
        return {
            visible: false,
            wareList: [],
            dataForm: {
                id: 0,
                purchaseId: '',
                skuId: '',
                skuNum: '',
                skuPrice: '',
                wareId: '',
                status: 0
            }
        }
    },
    computed: {
        dataRule() {
            return {
                purchaseId: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                skuId: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                skuNum: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                skuPrice: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                wareId: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                status: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ]
            }
        }
    },
    created() {
        this.getWares()
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
        },
        init() {
            this.visible = true
            this.$nextTick(() => {
                this.$refs['dataForm'].resetFields()
                if (this.dataForm.id) {
                    this.getInfo()
                }
            })
        },
        // 获取信息
        getInfo() {
            this.$http
                .get(`/ware/purchasedetail/${this.dataForm.id}`)
                .then(({ data: res }) => {
                    if (res.code !== 200) {
                        return this.$message.error(res.msg)
                    }
                    this.dataForm = {
                        ...this.dataForm,
                        ...res.data
                    }
                })
                .catch(() => {})
        },
        // 表单提交
        dataFormSubmitHandle: debounce(
            function() {
                this.$refs['dataForm'].validate(valid => {
                    if (!valid) {
                        return false
                    }
                    this.$http[!this.dataForm.id ? 'post' : 'put'](
                        '/ware/purchasedetail/',
                        this.dataForm
                    )
                        .then(({ data: res }) => {
                            if (res.code !== 200) {
                                return this.$message.error(res.msg)
                            }
                            this.$message({
                                message: this.$t('prompt.success'),
                                type: 'success',
                                duration: 500,
                                onClose: () => {
                                    this.visible = false
                                    this.$emit('refreshDataList')
                                }
                            })
                        })
                        .catch(() => {})
                })
            },
            1000,
            { leading: true, trailing: false }
        )
    }
}
</script>
