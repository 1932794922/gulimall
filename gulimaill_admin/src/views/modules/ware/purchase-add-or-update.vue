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
            <el-form-item label="优先级" prop="priority">
                <el-input
                    v-model="dataForm.priority"
                    placeholder="优先级"
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
                assigneeId: '',
                assigneeName: '',
                phone: '',
                priority: '',
                status: '',
                wareId: '',
                amount: '',
                createTime: '',
                updateTime: ''
            }
        }
    },
    computed: {
        dataRule() {
            return {
                assigneeId: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                assigneeName: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                phone: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                priority: [
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
                ],
                wareId: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                amount: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                createTime: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                updateTime: [
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
                .get(`/ware/purchase/${this.dataForm.id}`)
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
                        '/ware/purchase/',
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
