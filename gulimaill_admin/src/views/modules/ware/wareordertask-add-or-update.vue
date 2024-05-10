<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmitHandle()" :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
          <el-form-item label="order_id" prop="orderId">
          <el-input v-model="dataForm.orderId" placeholder="order_id"></el-input>
      </el-form-item>
          <el-form-item label="order_sn" prop="orderSn">
          <el-input v-model="dataForm.orderSn" placeholder="order_sn"></el-input>
      </el-form-item>
          <el-form-item label="收货人" prop="consignee">
          <el-input v-model="dataForm.consignee" placeholder="收货人"></el-input>
      </el-form-item>
          <el-form-item label="收货人电话" prop="consigneeTel">
          <el-input v-model="dataForm.consigneeTel" placeholder="收货人电话"></el-input>
      </el-form-item>
          <el-form-item label="配送地址" prop="deliveryAddress">
          <el-input v-model="dataForm.deliveryAddress" placeholder="配送地址"></el-input>
      </el-form-item>
          <el-form-item label="订单备注" prop="orderComment">
          <el-input v-model="dataForm.orderComment" placeholder="订单备注"></el-input>
      </el-form-item>
          <el-form-item label="付款方式【 1:在线付款 2:货到付款】" prop="paymentWay">
          <el-input v-model="dataForm.paymentWay" placeholder="付款方式【 1:在线付款 2:货到付款】"></el-input>
      </el-form-item>
          <el-form-item label="任务状态" prop="taskStatus">
          <el-input v-model="dataForm.taskStatus" placeholder="任务状态"></el-input>
      </el-form-item>
          <el-form-item label="订单描述" prop="orderBody">
          <el-input v-model="dataForm.orderBody" placeholder="订单描述"></el-input>
      </el-form-item>
          <el-form-item label="物流单号" prop="trackingNo">
          <el-input v-model="dataForm.trackingNo" placeholder="物流单号"></el-input>
      </el-form-item>
          <el-form-item label="create_time" prop="createTime">
          <el-input v-model="dataForm.createTime" placeholder="create_time"></el-input>
      </el-form-item>
          <el-form-item label="仓库id" prop="wareId">
          <el-input v-model="dataForm.wareId" placeholder="仓库id"></el-input>
      </el-form-item>
          <el-form-item label="工作单备注" prop="taskComment">
          <el-input v-model="dataForm.taskComment" placeholder="工作单备注"></el-input>
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
        orderId: '',
        orderSn: '',
        consignee: '',
        consigneeTel: '',
        deliveryAddress: '',
        orderComment: '',
        paymentWay: '',
        taskStatus: '',
        orderBody: '',
        trackingNo: '',
        createTime: '',
        wareId: '',
        taskComment: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        orderId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        orderSn: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        consignee: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        consigneeTel: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        deliveryAddress: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        orderComment: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        paymentWay: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        taskStatus: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        orderBody: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        trackingNo: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        createTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        wareId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        taskComment: [
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
      this.$http.get(`/ware/wareordertask/${this.dataForm.id}`).then(({ data: res }) => {
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
        this.$http[!this.dataForm.id ? 'post' : 'put']('/ware/wareordertask/', this.dataForm).then(({ data: res }) => {
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
