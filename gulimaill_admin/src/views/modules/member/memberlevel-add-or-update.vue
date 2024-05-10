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
      :label-width="$i18n.locale === 'en-US' ? '120px' : '120px'"
    >
      <el-form-item label="等级名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="等级名称"></el-input>
      </el-form-item>
      <el-form-item label="所需成长值" prop="growthPoint">
        <el-input-number
          v-model="dataForm.growthPoint"
          :min="0"
        ></el-input-number>
      </el-form-item>
      <el-form-item label="默认等级" prop="defaultStatus">
        <el-checkbox
          v-model="dataForm.defaultStatus"
          :true-label="1"
          :false-label="0"
        ></el-checkbox>
      </el-form-item>
      <el-form-item label="免运费标准" prop="freeFreightPoint">
        <el-input-number
          :min="0"
          v-model="dataForm.freeFreightPoint"
        ></el-input-number>
      </el-form-item>
      <el-form-item label="每次评价获取的成长值" prop="commentGrowthPoint">
        <el-input-number
          :min="0"
          v-model="dataForm.commentGrowthPoint"
        ></el-input-number>
      </el-form-item>
      <el-form-item label="是否有免邮特权" prop="priviledgeFreeFreight">
        <el-checkbox
          v-model="dataForm.priviledgeFreeFreight"
          :true-label="1"
          :false-label="0"
        ></el-checkbox>
      </el-form-item>
      <el-form-item label="是否有会员价格特权" prop="priviledgeMemberPrice">
        <el-checkbox
          v-model="dataForm.priviledgeMemberPrice"
          :true-label="1"
          :false-label="0"
        ></el-checkbox>
      </el-form-item>
      <el-form-item label="是否有生日特权" prop="priviledgeBirthday">
        <el-checkbox
          v-model="dataForm.priviledgeBirthday"
          :true-label="1"
          :false-label="0"
        ></el-checkbox>
      </el-form-item>
      <el-form-item label="备注" prop="note">
        <el-input v-model="dataForm.note" placeholder="备注"></el-input>
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
        growthPoint: '',
        defaultStatus: '',
        freeFreightPoint: '',
        commentGrowthPoint: '',
        priviledgeFreeFreight: '',
        priviledgeMemberPrice: '',
        priviledgeBirthday: '',
        note: ''
      }
    }
  },
  computed: {
    dataRule() {
      return {
        name: [
          { required: true, message: '等级名称不能为空', trigger: 'blur' }
        ],
        growthPoint: [
          {
            required: true,
            message: '等级需要的成长值不能为空',
            trigger: 'blur'
          }
        ],
        defaultStatus: [
          {
            required: true,
            message: '是否为默认等级不能为空',
            trigger: 'blur'
          }
        ],
        freeFreightPoint: [
          { required: true, message: '免运费标准不能为空', trigger: 'blur' }
        ],
        commentGrowthPoint: [
          {
            required: true,
            message: '每次评价获取的成长值不能为空',
            trigger: 'blur'
          }
        ],
        priviledgeFreeFreight: [
          { required: true, message: '是否有免邮特权不能为空', trigger: 'blur' }
        ],
        priviledgeMemberPrice: [
          {
            required: true,
            message: '是否有会员价格特权不能为空',
            trigger: 'blur'
          }
        ],
        priviledgeBirthday: [
          { required: true, message: '是否有生日特权不能为空', trigger: 'blur' }
        ],
        note: [{ required: true, message: '备注不能为空', trigger: 'blur' }]
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
        .get(`/member/memberlevel/${this.dataForm.id}`)
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
            '/member/memberlevel/',
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
