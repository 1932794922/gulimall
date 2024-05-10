<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmitHandle()" :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
          <el-form-item label="subject_id" prop="subjectId">
          <el-input v-model="dataForm.subjectId" placeholder="subject_id"></el-input>
      </el-form-item>
          <el-form-item label="subject_name" prop="subjectName">
          <el-input v-model="dataForm.subjectName" placeholder="subject_name"></el-input>
      </el-form-item>
          <el-form-item label="subject_img" prop="subjectImg">
          <el-input v-model="dataForm.subjectImg" placeholder="subject_img"></el-input>
      </el-form-item>
          <el-form-item label="活动url" prop="subjectUrll">
          <el-input v-model="dataForm.subjectUrll" placeholder="活动url"></el-input>
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
        subjectId: '',
        subjectName: '',
        subjectImg: '',
        subjectUrll: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        subjectId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        subjectName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        subjectImg: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        subjectUrll: [
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
      this.$http.get(`/member/membercollectsubject/${this.dataForm.id}`).then(({ data: res }) => {
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
        this.$http[!this.dataForm.id ? 'post' : 'put']('/member/membercollectsubject/', this.dataForm).then(({ data: res }) => {
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
