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
            :label-width="$i18n.locale === 'en-US' ? '180px' : '120px'"
        >
            <el-form-item label="场次名称" prop="name">
                <el-input
                    v-model="dataForm.name"
                    placeholder="场次名称"
                ></el-input>
            </el-form-item>
            <el-form-item label="每日开始时间" prop="startTime">
                <el-date-picker
                    type="datetime"
                    placeholder="每日开始时间"
                    v-model="dataForm.startTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                >
                    ></el-date-picker
                >
            </el-form-item>
            <el-form-item label="每日结束时间" prop="endTime">
                <el-date-picker
                    type="datetime"
                    placeholder="每日结束时间"
                    v-model="dataForm.endTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                >
                    ></el-date-picker
                >
            </el-form-item>
            <el-form-item label="启用状态" prop="status">
                <el-input
                    v-model="dataForm.status"
                    placeholder="启用状态"
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
                name: '',
                startTime: '',
                endTime: '',
                status: ''
            }
        }
    },
    computed: {
        dataRule() {
            return {
                name: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                startTime: [
                    {
                        required: true,
                        message: this.$t('validate.required'),
                        trigger: 'blur'
                    }
                ],
                endTime: [
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
                createTime: [
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
                .get(`/coupon/seckillsession/${this.dataForm.id}`)
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
                        '/coupon/seckillsession/',
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
