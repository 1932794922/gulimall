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
            :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'"
        >
            <el-form-item label="sku_id" prop="skuId">
                <el-input
                    v-model="dataForm.skuId"
                    placeholder="sku_id"
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
            <el-form-item label="库存数" prop="stock">
                <el-input
                    v-model="dataForm.stock"
                    placeholder="库存数"
                ></el-input>
            </el-form-item>
            <el-form-item label="sku_name" prop="skuName">
                <el-input
                    v-model="dataForm.skuName"
                    placeholder="sku_name"
                ></el-input>
            </el-form-item>
            <el-form-item label="锁定库存" prop="stockLocked">
                <el-input
                    v-model="dataForm.stockLocked"
                    placeholder="锁定库存"
                ></el-input>
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
            dataForm: {
                id: '',
                skuId: '',
                wareId: '',
                stock: '',
                skuName: '',
                stockLocked: ''
            }
        }
    },
    computed: {
        dataRule() {
            return {
                skuId: [
                    {
                        required: true,
                        message: 'sku_id不能为空',
                        trigger: 'blur'
                    }
                ],
                wareId: [
                    {
                        required: true,
                        message: '仓库id不能为空',
                        trigger: 'blur'
                    }
                ],
                stock: [
                    {
                        required: true,
                        message: '库存数不能为空',
                        trigger: 'blur'
                    }
                ],
                skuName: [
                    {
                        required: true,
                        message: 'sku_name不能为空',
                        trigger: 'blur'
                    }
                ],
                stockLocked: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ]
            }
        }
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
                .get(`/ware/waresku/${this.dataForm.id}`)
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
                        '/ware/waresku/',
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
    },
    created() {
        this.getWares()
    }
}
</script>
