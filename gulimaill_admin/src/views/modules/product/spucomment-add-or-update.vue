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
      :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'"
    >
      <el-form-item label="sku_id" prop="skuId">
        <el-input v-model="dataForm.skuId" placeholder="sku_id"></el-input>
      </el-form-item>
      <el-form-item label="spu_id" prop="spuId">
        <el-input v-model="dataForm.spuId" placeholder="spu_id"></el-input>
      </el-form-item>
      <el-form-item label="商品名字" prop="spuName">
        <el-input v-model="dataForm.spuName" placeholder="商品名字"></el-input>
      </el-form-item>
      <el-form-item label="会员昵称" prop="memberNickName">
        <el-input
          v-model="dataForm.memberNickName"
          placeholder="会员昵称"
        ></el-input>
      </el-form-item>
      <el-form-item label="星级" prop="star">
        <el-input v-model="dataForm.star" placeholder="星级"></el-input>
      </el-form-item>
      <el-form-item label="会员ip" prop="memberIp">
        <el-input v-model="dataForm.memberIp" placeholder="会员ip"></el-input>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-input
          v-model="dataForm.createTime"
          placeholder="创建时间"
        ></el-input>
      </el-form-item>
      <el-form-item label="显示状态[0-不显示，1-显示]" prop="showStatus">
        <el-input
          v-model="dataForm.showStatus"
          placeholder="显示状态[0-不显示，1-显示]"
        ></el-input>
      </el-form-item>
      <el-form-item label="购买时属性组合" prop="spuAttributes">
        <el-input
          v-model="dataForm.spuAttributes"
          placeholder="购买时属性组合"
        ></el-input>
      </el-form-item>
      <el-form-item label="点赞数" prop="likesCount">
        <el-input v-model="dataForm.likesCount" placeholder="点赞数"></el-input>
      </el-form-item>
      <el-form-item label="回复数" prop="replyCount">
        <el-input v-model="dataForm.replyCount" placeholder="回复数"></el-input>
      </el-form-item>
      <el-form-item
        label="评论图片/视频[json数据；[{type:文件类型,url:资源路径}]]"
        prop="resources"
      >
        <el-input
          v-model="dataForm.resources"
          placeholder="评论图片/视频[json数据；[{type:文件类型,url:资源路径}]]"
        ></el-input>
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input v-model="dataForm.content" placeholder="内容"></el-input>
      </el-form-item>
      <el-form-item label="用户头像" prop="memberIcon">
        <el-input
          v-model="dataForm.memberIcon"
          placeholder="用户头像"
        ></el-input>
      </el-form-item>
      <el-form-item
        label="评论类型[0 - 对商品的直接评论，1 - 对评论的回复]"
        prop="commentType"
      >
        <el-input
          v-model="dataForm.commentType"
          placeholder="评论类型[0 - 对商品的直接评论，1 - 对评论的回复]"
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
        id: '',
        skuId: '',
        spuId: '',
        spuName: '',
        memberNickName: '',
        star: '',
        memberIp: '',
        createTime: '',
        showStatus: '',
        spuAttributes: '',
        likesCount: '',
        replyCount: '',
        resources: '',
        content: '',
        memberIcon: '',
        commentType: ''
      }
    }
  },
  computed: {
    dataRule() {
      return {
        skuId: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        spuId: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        spuName: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        memberNickName: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        star: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        memberIp: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        createTime: [
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
        spuAttributes: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        likesCount: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        replyCount: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        resources: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        content: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        memberIcon: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        commentType: [
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
        if (this.dataForm.id) {
          this.getInfo()
        }
      })
    },
    // 获取信息
    getInfo() {
      this.$http
        .get(`/product/spucomment/${this.dataForm.id}`)
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
            '/product/spucomment/',
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
