<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmitHandle()" :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
          <el-form-item label="member_id" prop="memberId">
          <el-input v-model="dataForm.memberId" placeholder="member_id"></el-input>
      </el-form-item>
          <el-form-item label="收货人姓名" prop="name">
          <el-input v-model="dataForm.name" placeholder="收货人姓名"></el-input>
      </el-form-item>
          <el-form-item label="电话" prop="phone">
          <el-input v-model="dataForm.phone" placeholder="电话"></el-input>
      </el-form-item>
          <el-form-item label="邮政编码" prop="postCode">
          <el-input v-model="dataForm.postCode" placeholder="邮政编码"></el-input>
      </el-form-item>
          <el-form-item label="省份/直辖市" prop="province">
          <el-input v-model="dataForm.province" placeholder="省份/直辖市"></el-input>
      </el-form-item>
          <el-form-item label="城市" prop="city">
          <el-input v-model="dataForm.city" placeholder="城市"></el-input>
      </el-form-item>
          <el-form-item label="区" prop="region">
          <el-input v-model="dataForm.region" placeholder="区"></el-input>
      </el-form-item>
          <el-form-item label="详细地址(街道)" prop="detailAddress">
          <el-input v-model="dataForm.detailAddress" placeholder="详细地址(街道)"></el-input>
      </el-form-item>
          <el-form-item label="省市区代码" prop="areacode">
          <el-input v-model="dataForm.areacode" placeholder="省市区代码"></el-input>
      </el-form-item>
          <el-form-item label="是否默认" prop="defaultStatus">
          <el-input v-model="dataForm.defaultStatus" placeholder="是否默认"></el-input>
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
        name: '',
        phone: '',
        postCode: '',
        province: '',
        city: '',
        region: '',
        detailAddress: '',
        areacode: '',
        defaultStatus: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        memberId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        phone: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        postCode: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        province: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        city: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        region: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        detailAddress: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        areacode: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        defaultStatus: [
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
      this.$http.get(`/member/memberreceiveaddress/${this.dataForm.id}`).then(({ data: res }) => {
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
        this.$http[!this.dataForm.id ? 'post' : 'put']('/member/memberreceiveaddress/', this.dataForm).then(({ data: res }) => {
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
