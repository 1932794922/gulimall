<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmitHandle()" :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
          <el-form-item label="会员id" prop="memberId">
          <el-input v-model="dataForm.memberId" placeholder="会员id"></el-input>
      </el-form-item>
          <el-form-item label="累计消费金额" prop="consumeAmount">
          <el-input v-model="dataForm.consumeAmount" placeholder="累计消费金额"></el-input>
      </el-form-item>
          <el-form-item label="累计优惠金额" prop="couponAmount">
          <el-input v-model="dataForm.couponAmount" placeholder="累计优惠金额"></el-input>
      </el-form-item>
          <el-form-item label="订单数量" prop="orderCount">
          <el-input v-model="dataForm.orderCount" placeholder="订单数量"></el-input>
      </el-form-item>
          <el-form-item label="优惠券数量" prop="couponCount">
          <el-input v-model="dataForm.couponCount" placeholder="优惠券数量"></el-input>
      </el-form-item>
          <el-form-item label="评价数" prop="commentCount">
          <el-input v-model="dataForm.commentCount" placeholder="评价数"></el-input>
      </el-form-item>
          <el-form-item label="退货数量" prop="returnOrderCount">
          <el-input v-model="dataForm.returnOrderCount" placeholder="退货数量"></el-input>
      </el-form-item>
          <el-form-item label="登录次数" prop="loginCount">
          <el-input v-model="dataForm.loginCount" placeholder="登录次数"></el-input>
      </el-form-item>
          <el-form-item label="关注数量" prop="attendCount">
          <el-input v-model="dataForm.attendCount" placeholder="关注数量"></el-input>
      </el-form-item>
          <el-form-item label="粉丝数量" prop="fansCount">
          <el-input v-model="dataForm.fansCount" placeholder="粉丝数量"></el-input>
      </el-form-item>
          <el-form-item label="收藏的商品数量" prop="collectProductCount">
          <el-input v-model="dataForm.collectProductCount" placeholder="收藏的商品数量"></el-input>
      </el-form-item>
          <el-form-item label="收藏的专题活动数量" prop="collectSubjectCount">
          <el-input v-model="dataForm.collectSubjectCount" placeholder="收藏的专题活动数量"></el-input>
      </el-form-item>
          <el-form-item label="收藏的评论数量" prop="collectCommentCount">
          <el-input v-model="dataForm.collectCommentCount" placeholder="收藏的评论数量"></el-input>
      </el-form-item>
          <el-form-item label="邀请的朋友数量" prop="inviteFriendCount">
          <el-input v-model="dataForm.inviteFriendCount" placeholder="邀请的朋友数量"></el-input>
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
        consumeAmount: '',
        couponAmount: '',
        orderCount: '',
        couponCount: '',
        commentCount: '',
        returnOrderCount: '',
        loginCount: '',
        attendCount: '',
        fansCount: '',
        collectProductCount: '',
        collectSubjectCount: '',
        collectCommentCount: '',
        inviteFriendCount: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        memberId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        consumeAmount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        couponAmount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        orderCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        couponCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        commentCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        returnOrderCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        loginCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        attendCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        fansCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        collectProductCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        collectSubjectCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        collectCommentCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        inviteFriendCount: [
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
      this.$http.get(`/member/memberstatisticsinfo/${this.dataForm.id}`).then(({ data: res }) => {
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
        this.$http[!this.dataForm.id ? 'post' : 'put']('/member/memberstatisticsinfo/', this.dataForm).then(({ data: res }) => {
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
