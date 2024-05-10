<template>
  <el-dialog
    :visible.sync="visible"
    :title="!dataForm.catId ? $t('add') : $t('update')"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmitHandle()"
      :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'"
    >
      <el-form-item label="分类名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="分类名称"></el-input>
      </el-form-item>
      <el-form-item label="父分类id" prop="parentCid">
        <el-input
          v-model="dataForm.parentCid"
          placeholder="父分类id"
        ></el-input>
      </el-form-item>
      <el-form-item label="层级" prop="catLevel">
        <el-input v-model="dataForm.catLevel" placeholder="层级"></el-input>
      </el-form-item>
      <el-form-item label="是否显示[0-不显示，1显示]" prop="showStatus">
        <el-input
          v-model="dataForm.showStatus"
          placeholder="是否显示[0-不显示，1显示]"
        ></el-input>
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input v-model="dataForm.sort" placeholder="排序"></el-input>
      </el-form-item>
      <el-form-item label="图标地址" prop="icon">
        <el-input v-model="dataForm.icon" placeholder="图标地址"></el-input>
      </el-form-item>
      <el-form-item label="计量单位" prop="productUnit">
        <el-input
          v-model="dataForm.productUnit"
          placeholder="计量单位"
        ></el-input>
      </el-form-item>
      <el-form-item label="商品数量" prop="productCount">
        <el-input
          v-model="dataForm.productCount"
          placeholder="商品数量"
        ></el-input>
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
        catId: '',
        name: '',
        parentCid: '',
        catLevel: '',
        showStatus: '',
        sort: '',
        icon: '',
        productUnit: '',
        productCount: ''
      }
    }
  },
  computed: {
    dataRule() {
      return {
        name: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        parentCid: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        catLevel: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        showStatus: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        sort: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        icon: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        productUnit: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        productCount: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    init() {
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.catId) {
          this.getInfo()
        }
      })
    },
    // 获取信息
    getInfo() {
      this.$http
        .get(`/product/category/${this.dataForm.catId}`)
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
          this.$http[!this.dataForm.catId ? 'post' : 'put'](
            '/product/category/',
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
