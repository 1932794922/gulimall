<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmitHandle()" :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
          <el-form-item label="优惠券id" prop="couponId">
          <el-input v-model="dataForm.couponId" placeholder="优惠券id"></el-input>
      </el-form-item>
          <el-form-item label="会员id" prop="memberId">
          <el-input v-model="dataForm.memberId" placeholder="会员id"></el-input>
      </el-form-item>
          <el-form-item label="会员名字" prop="memberNickName">
          <el-input v-model="dataForm.memberNickName" placeholder="会员名字"></el-input>
      </el-form-item>
          <el-form-item label="获取方式[0->后台赠送；1->主动领取]" prop="getType">
          <el-input v-model="dataForm.getType" placeholder="获取方式[0->后台赠送；1->主动领取]"></el-input>
      </el-form-item>
          <el-form-item label="创建时间" prop="createTime">
          <el-input v-model="dataForm.createTime" placeholder="创建时间"></el-input>
      </el-form-item>
          <el-form-item label="使用状态[0->未使用；1->已使用；2->已过期]" prop="useType">
          <el-input v-model="dataForm.useType" placeholder="使用状态[0->未使用；1->已使用；2->已过期]"></el-input>
      </el-form-item>
          <el-form-item label="使用时间" prop="useTime">
          <el-input v-model="dataForm.useTime" placeholder="使用时间"></el-input>
      </el-form-item>
          <el-form-item label="订单id" prop="orderId">
          <el-input v-model="dataForm.orderId" placeholder="订单id"></el-input>
      </el-form-item>
          <el-form-item label="订单号" prop="orderSn">
          <el-input v-model="dataForm.orderSn" placeholder="订单号"></el-input>
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
        couponId: '',
        memberId: '',
        memberNickName: '',
        getType: '',
        createTime: '',
        useType: '',
        useTime: '',
        orderId: '',
        orderSn: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        couponId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        memberId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        memberNickName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        getType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        createTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        useType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        useTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        orderId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        orderSn: [
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
      this.$http.get(`/coupon/couponhistory/${this.dataForm.id}`).then(({ data: res }) => {
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
        this.$http[!this.dataForm.id ? 'post' : 'put']('/coupon/couponhistory/', this.dataForm).then(({ data: res }) => {
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
