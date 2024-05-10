<template>
  <el-dialog
    :visible.sync="visible"
    :title="!dataForm.skuId ? $t('add') : $t('update')"
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
      <el-form-item label="spuId" prop="spuId">
        <el-input v-model="dataForm.spuId" placeholder="spuId"></el-input>
      </el-form-item>
      <el-form-item label="sku名称" prop="skuName">
        <el-input v-model="dataForm.skuName" placeholder="sku名称"></el-input>
      </el-form-item>
      <el-form-item label="sku介绍描述" prop="skuDesc">
        <el-input
          v-model="dataForm.skuDesc"
          placeholder="sku介绍描述"
        ></el-input>
      </el-form-item>
      <el-form-item label="所属分类id" prop="catalogId">
        <el-input
          v-model="dataForm.catalogId"
          placeholder="所属分类id"
        ></el-input>
      </el-form-item>
      <el-form-item label="品牌id" prop="brandId">
        <el-input v-model="dataForm.brandId" placeholder="品牌id"></el-input>
      </el-form-item>
      <el-form-item label="默认图片" prop="skuDefaultImg">
        <el-input
          v-model="dataForm.skuDefaultImg"
          placeholder="默认图片"
        ></el-input>
      </el-form-item>
      <el-form-item label="标题" prop="skuTitle">
        <el-input v-model="dataForm.skuTitle" placeholder="标题"></el-input>
      </el-form-item>
      <el-form-item label="副标题" prop="skuSubtitle">
        <el-input
          v-model="dataForm.skuSubtitle"
          placeholder="副标题"
        ></el-input>
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input v-model="dataForm.price" placeholder="价格"></el-input>
      </el-form-item>
      <el-form-item label="销量" prop="saleCount">
        <el-input v-model="dataForm.saleCount" placeholder="销量"></el-input>
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
        skuId: '',
        spuId: '',
        skuName: '',
        skuDesc: '',
        catalogId: '',
        brandId: '',
        skuDefaultImg: '',
        skuTitle: '',
        skuSubtitle: '',
        price: '',
        saleCount: ''
      }
    }
  },
  computed: {
    dataRule() {
      return {
        spuId: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        skuName: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        skuDesc: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        catalogId: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        brandId: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        skuDefaultImg: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        skuTitle: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        skuSubtitle: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        price: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        saleCount: [
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
        if (this.dataForm.skuId) {
          this.getInfo()
        }
      })
    },
    // 获取信息
    getInfo() {
      this.$http
        .get(`/product/skuinfo/${this.dataForm.skuId}`)
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
          this.$http[!this.dataForm.skuId ? 'post' : 'put'](
            '/product/skuinfo/',
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
