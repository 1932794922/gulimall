<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmitHandle()" :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
          <el-form-item label="活动id" prop="promotionId">
          <el-input v-model="dataForm.promotionId" placeholder="活动id"></el-input>
      </el-form-item>
          <el-form-item label="活动场次id" prop="promotionSessionId">
          <el-input v-model="dataForm.promotionSessionId" placeholder="活动场次id"></el-input>
      </el-form-item>
          <el-form-item label="商品id" prop="skuId">
          <el-input v-model="dataForm.skuId" placeholder="商品id"></el-input>
      </el-form-item>
          <el-form-item label="秒杀价格" prop="seckillPrice">
          <el-input v-model="dataForm.seckillPrice" placeholder="秒杀价格"></el-input>
      </el-form-item>
          <el-form-item label="秒杀总量" prop="seckillCount">
          <el-input v-model="dataForm.seckillCount" placeholder="秒杀总量"></el-input>
      </el-form-item>
          <el-form-item label="每人限购数量" prop="seckillLimit">
          <el-input v-model="dataForm.seckillLimit" placeholder="每人限购数量"></el-input>
      </el-form-item>
          <el-form-item label="排序" prop="seckillSort">
          <el-input v-model="dataForm.seckillSort" placeholder="排序"></el-input>
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
        promotionId: '',
        promotionSessionId: '',
        skuId: '',
        seckillPrice: '',
        seckillCount: '',
        seckillLimit: '',
        seckillSort: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        promotionId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        promotionSessionId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        skuId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        seckillPrice: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        seckillCount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        seckillLimit: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        seckillSort: [
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
      this.$http.get(`/coupon/seckillskurelation/${this.dataForm.id}`).then(({ data: res }) => {
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
        this.$http[!this.dataForm.id ? 'post' : 'put']('/coupon/seckillskurelation/', this.dataForm).then(({ data: res }) => {
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
