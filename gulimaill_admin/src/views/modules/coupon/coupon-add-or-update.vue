<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmitHandle()" :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
          <el-form-item label="优惠卷类型[0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券]" prop="couponType">
          <el-input v-model="dataForm.couponType" placeholder="优惠卷类型[0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券]"></el-input>
      </el-form-item>
          <el-form-item label="优惠券图片" prop="couponImg">
          <el-input v-model="dataForm.couponImg" placeholder="优惠券图片"></el-input>
      </el-form-item>
          <el-form-item label="优惠卷名字" prop="couponName">
          <el-input v-model="dataForm.couponName" placeholder="优惠卷名字"></el-input>
      </el-form-item>
          <el-form-item label="数量" prop="num">
          <el-input v-model="dataForm.num" placeholder="数量"></el-input>
      </el-form-item>
          <el-form-item label="金额" prop="amount">
          <el-input v-model="dataForm.amount" placeholder="金额"></el-input>
      </el-form-item>
          <el-form-item label="每人限领张数" prop="perLimit">
          <el-input v-model="dataForm.perLimit" placeholder="每人限领张数"></el-input>
      </el-form-item>
          <el-form-item label="使用门槛" prop="minPoint">
          <el-input v-model="dataForm.minPoint" placeholder="使用门槛"></el-input>
      </el-form-item>
          <el-form-item label="开始时间" prop="startTime">
          <el-input v-model="dataForm.startTime" placeholder="开始时间"></el-input>
      </el-form-item>
          <el-form-item label="结束时间" prop="endTime">
          <el-input v-model="dataForm.endTime" placeholder="结束时间"></el-input>
      </el-form-item>
          <el-form-item label="使用类型[0->全场通用；1->指定分类；2->指定商品]" prop="useType">
          <el-input v-model="dataForm.useType" placeholder="使用类型[0->全场通用；1->指定分类；2->指定商品]"></el-input>
      </el-form-item>
          <el-form-item label="备注" prop="note">
          <el-input v-model="dataForm.note" placeholder="备注"></el-input>
      </el-form-item>
          <el-form-item label="发行数量" prop="publishCount">
          <el-input v-model="dataForm.publishCount" placeholder="发行数量"></el-input>
      </el-form-item>
          <el-form-item label="已使用数量" prop="useCount">
          <el-input v-model="dataForm.useCount" placeholder="已使用数量"></el-input>
      </el-form-item>
          <el-form-item label="领取数量" prop="receiveCount">
          <el-input v-model="dataForm.receiveCount" placeholder="领取数量"></el-input>
      </el-form-item>
          <el-form-item label="可以领取的开始日期" prop="enableStartTime">
          <el-input v-model="dataForm.enableStartTime" placeholder="可以领取的开始日期"></el-input>
      </el-form-item>
          <el-form-item label="可以领取的结束日期" prop="enableEndTime">
          <el-input v-model="dataForm.enableEndTime" placeholder="可以领取的结束日期"></el-input>
      </el-form-item>
          <el-form-item label="优惠码" prop="code">
          <el-input v-model="dataForm.code" placeholder="优惠码"></el-input>
      </el-form-item>
          <el-form-item label="可以领取的会员等级[0->不限等级，其他-对应等级]" prop="memberLevel">
          <el-input v-model="dataForm.memberLevel" placeholder="可以领取的会员等级[0->不限等级，其他-对应等级]"></el-input>
      </el-form-item>
          <el-form-item label="发布状态[0-未发布，1-已发布]" prop="publish">
          <el-input v-model="dataForm.publish" placeholder="发布状态[0-未发布，1-已发布]"></el-input>
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
        couponType: '',
        couponImg: '',
        couponName: '',
        num: '',
        amount: '',
        perLimit: '',
        minPoint: '',
        startTime: '',
        endTime: '',
        useType: '',
        note: '',
        publishCount: '',
        useCount: '',
        receiveCount: '',
        enableStartTime: '',
        enableEndTime: '',
        code: '',
        memberLevel: '',
        publish: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        couponType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        couponImg: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        couponName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        num: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        amount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        perLimit: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        minPoint: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        startTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        endTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        useType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        note: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        publishCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        useCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        receiveCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        enableStartTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        enableEndTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        code: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        memberLevel: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        publish: [
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
      this.$http.get(`/coupon/coupon/${this.dataForm.id}`).then(({ data: res }) => {
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
        this.$http[!this.dataForm.id ? 'post' : 'put']('/coupon/coupon/', this.dataForm).then(({ data: res }) => {
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
