<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmitHandle()" :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
          <el-form-item label="会员等级id" prop="levelId">
          <el-input v-model="dataForm.levelId" placeholder="会员等级id"></el-input>
      </el-form-item>
          <el-form-item label="用户名" prop="username">
          <el-input v-model="dataForm.username" placeholder="用户名"></el-input>
      </el-form-item>
          <el-form-item label="密码" prop="password">
          <el-input v-model="dataForm.password" placeholder="密码"></el-input>
      </el-form-item>
          <el-form-item label="昵称" prop="nickname">
          <el-input v-model="dataForm.nickname" placeholder="昵称"></el-input>
      </el-form-item>
          <el-form-item label="手机号码" prop="mobile">
          <el-input v-model="dataForm.mobile" placeholder="手机号码"></el-input>
      </el-form-item>
          <el-form-item label="邮箱" prop="email">
          <el-input v-model="dataForm.email" placeholder="邮箱"></el-input>
      </el-form-item>
          <el-form-item label="头像" prop="header">
          <el-input v-model="dataForm.header" placeholder="头像"></el-input>
      </el-form-item>
          <el-form-item label="性别" prop="gender">
          <el-input v-model="dataForm.gender" placeholder="性别"></el-input>
      </el-form-item>
          <el-form-item label="生日" prop="birth">
          <el-input v-model="dataForm.birth" placeholder="生日"></el-input>
      </el-form-item>
          <el-form-item label="所在城市" prop="city">
          <el-input v-model="dataForm.city" placeholder="所在城市"></el-input>
      </el-form-item>
          <el-form-item label="职业" prop="job">
          <el-input v-model="dataForm.job" placeholder="职业"></el-input>
      </el-form-item>
          <el-form-item label="个性签名" prop="sign">
          <el-input v-model="dataForm.sign" placeholder="个性签名"></el-input>
      </el-form-item>
          <el-form-item label="用户来源" prop="sourceType">
          <el-input v-model="dataForm.sourceType" placeholder="用户来源"></el-input>
      </el-form-item>
          <el-form-item label="积分" prop="integration">
          <el-input v-model="dataForm.integration" placeholder="积分"></el-input>
      </el-form-item>
          <el-form-item label="成长值" prop="growth">
          <el-input v-model="dataForm.growth" placeholder="成长值"></el-input>
      </el-form-item>
          <el-form-item label="启用状态" prop="status">
          <el-input v-model="dataForm.status" placeholder="启用状态"></el-input>
      </el-form-item>
          <el-form-item label="注册时间" prop="createTime">
          <el-input v-model="dataForm.createTime" placeholder="注册时间"></el-input>
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
        levelId: '',
        username: '',
        password: '',
        nickname: '',
        mobile: '',
        email: '',
        header: '',
        gender: '',
        birth: '',
        city: '',
        job: '',
        sign: '',
        sourceType: '',
        integration: '',
        growth: '',
        status: '',
        createTime: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        levelId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        username: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        password: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        mobile: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        email: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        header: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        gender: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        birth: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        city: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        job: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sign: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sourceType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        integration: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        growth: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        status: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        createTime: [
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
      this.$http.get(`/member/member/${this.dataForm.id}`).then(({ data: res }) => {
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
        this.$http[!this.dataForm.id ? 'post' : 'put']('/member/member/', this.dataForm).then(({ data: res }) => {
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
