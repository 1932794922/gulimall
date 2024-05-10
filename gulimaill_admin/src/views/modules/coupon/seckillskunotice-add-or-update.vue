<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmitHandle()" :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
          <el-form-item label="member_id" prop="memberId">
          <el-input v-model="dataForm.memberId" placeholder="member_id"></el-input>
      </el-form-item>
          <el-form-item label="sku_id" prop="skuId">
          <el-input v-model="dataForm.skuId" placeholder="sku_id"></el-input>
      </el-form-item>
          <el-form-item label="活动场次id" prop="sessionId">
          <el-input v-model="dataForm.sessionId" placeholder="活动场次id"></el-input>
      </el-form-item>
          <el-form-item label="订阅时间" prop="subcribeTime">
          <el-input v-model="dataForm.subcribeTime" placeholder="订阅时间"></el-input>
      </el-form-item>
          <el-form-item label="发送时间" prop="sendTime">
          <el-input v-model="dataForm.sendTime" placeholder="发送时间"></el-input>
      </el-form-item>
          <el-form-item label="通知方式[0-短信，1-邮件]" prop="noticeType">
          <el-input v-model="dataForm.noticeType" placeholder="通知方式[0-短信，1-邮件]"></el-input>
      </el-form-item>
      </el-form>
    <template slot="footer">
      <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
      <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
    </template>
  </el-dialog>
</template>

<script>
import debounce from 'lodash/debounce'
export default {
  data () {
    return {
      visible: false,
      dataForm: {
        id: '',
        memberId: '',
        skuId: '',
        sessionId: '',
        subcribeTime: '',
        sendTime: '',
        noticeType: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        memberId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        skuId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sessionId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        subcribeTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sendTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        noticeType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init () {
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.getInfo()
        }
      })
    },
    // 获取信息
    getInfo () {
      this.$http.get(`/coupon/seckillskunotice/${this.dataForm.id}`).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.msg)
        }
        this.dataForm = {
          ...this.dataForm,
          ...res.data
        }
      }).catch(() => {})
    },
    // 表单提交
    dataFormSubmitHandle: debounce(function () {
      this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          return false
        }
        this.$http[!this.dataForm.id ? 'post' : 'put']('/coupon/seckillskunotice/', this.dataForm).then(({ data: res }) => {
          if (res.code !== 0) {
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
        }).catch(() => {})
      })
    }, 1000, { 'leading': true, 'trailing': false })
  }
}
</script>
