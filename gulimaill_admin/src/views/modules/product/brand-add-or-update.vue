<template>
  <div>
    <el-dialog
      :visible.sync="visible"
      :title="!dataForm.brandId ? $t('add') : $t('update')"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form
        :model="dataForm"
        :rules="dataRule"
        ref="dataForm"
        @keyup.enter.native="dataFormSubmitHandle()"
        :label-width="$i18n.locale === 'en-US' ? '140px' : '120px'"
      >
        <el-form-item label="品牌名" prop="name">
          <el-input v-model="dataForm.name" placeholder="品牌名"></el-input>
        </el-form-item>
        <el-form-item label="品牌logo地址" prop="logo">
          <!-- <el-upload
            list-type="picture"
            action="#"
            :limit="1"
            :auto-upload="false"
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :file-list="fileList"
            :on-change="handleFileChange"
          >
            <el-button size="small" type="primary">选择图片</el-button>
          </el-upload> -->
          <singleUpload v-model="dataForm.logo"></singleUpload>
          <el-button
            v-if="isVisibleUploadBtn"
            @click="uploadFile"
            size="small"
            type="primary"
            >确定上传</el-button
          >
        </el-form-item>
        <el-form-item label="介绍" prop="descript">
          <el-input v-model="dataForm.descript" placeholder="介绍"></el-input>
        </el-form-item>
        <el-form-item label="显示状态" prop="switchStatus">
          <el-switch
            v-model="dataForm.switchStatus"
            active-color="#13ce66"
            inactive-color="#ff4949"
            :active-value="1"
            :inactive-value="0"
          >
          </el-switch>
        </el-form-item>
        <el-form-item label="检索首字母" prop="firstLetter">
          <el-input
            v-model="dataForm.firstLetter"
            placeholder="检索首字母"
          ></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="dataForm.sort" placeholder="排序"></el-input>
        </el-form-item>
      </el-form>
      <template slot="footer">
        <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
        <el-button type="primary" @click="dataFormSubmitHandle()">{{
          $t('confirm')
        }}</el-button>
      </template>
    </el-dialog>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt="" />
    </el-dialog>
  </div>
</template>

<script>
import debounce from 'lodash/debounce'
import { ossURL } from '@/api/thirdparty'
import { getUUID } from '@/utils/index'
import singleUpload from '@/components/upload/singleUpload'
export default {
  components: {
    singleUpload
  },
  data() {
    return {
      visible: false,
      isVisibleUploadBtn: false,
      host: '#',
      dataForm: {
        brandId: '',
        name: '',
        logo: '',
        descript: '',
        switchStatus: '',
        firstLetter: '',
        sort: ''
      },
      dialogImageUrl: '',
      dialogVisible: false,
      fileList: []
    }
  },
  computed: {
    dataRule() {
      return {
        name: [
          {
            required: true,
            message: '请输入品牌名',
            trigger: 'blur'
          }
        ],
        logo: [
          {
            required: true,
            message: '请上传品牌logo图片',
            trigger: 'blur'
          }
        ],
        descript: [
          {
            required: true,
            message: '请输入介绍信息',
            trigger: 'blur'
          }
        ],
        switchStatus: [
          {
            required: true,
            message: this.$t('validate.required'),
            trigger: 'blur'
          }
        ],
        firstLetter: [
          {
            validator: (rule, value, callback) => {
              if (value === '') {
                callback(new Error('请输入检索首字母'))
              } else if (value) {
                // 判断是否是字母
                if (!/^[a-zA-Z]+$/.test(value)) {
                  callback(new Error('请输入字母'))
                } else {
                  callback()
                }
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        sort: [
          {
            validator: (rule, value, callback) => {
              if (value === '') {
                callback(new Error('请输入排序数字'))
              }
              // 判断是否是数字并且大于0
              else if (value && !/^[1-9]\d*$/.test(value)) {
                callback(new Error('请输入大于0的数字'))
              } else {
                callback()
              }
            },
            required: true,
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    init() {
      this.visible = true
      this.dataForm.brandId = this.dataForm.id
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.brandId) {
          this.getInfo()
        }
      })
    },
    // 获取信息
    getInfo() {
      this.$http
        .get(`/product/brand/${this.dataForm.brandId}`)
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

    handlePreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    handleRemove(file) {
      console.dir(this.$http)
      // 讲fileList中的file删除
      this.fileList = this.fileList.filter(item => item.uid !== file.uid)
      this.isVisibleUploadBtn = this.fileList.length > 0
    },
    handleFileChange(file, fileList) {
      this.fileList = fileList
      this.isVisibleUploadBtn = fileList.length > 0
    },

    // 执行上传
    uploadFile() {
      // 获取签名数据
      this.$http
        .get(ossURL.getPolicy)
        .then(({ data: res }) => {
          console.log(res)
          if (res.code !== 200) {
            return this.message.error({
              message: res.msg,
              center: true
            })
          }
          // 拿到签名数据
          let { policy, signature, accessId, host, dir, callback } = res.data
          // 上传文件
          this.fileList.forEach(file => {
            let formData = new FormData()
            // filename表示待上传的本地文件名称。
            formData.append('key', dir + getUUID() + file.name)
            formData.append('policy', policy)
            formData.append('OSSAccessKeyId', accessId)
            // 设置服务端返回状态码为200，不设置则默认返回状态码204。
            formData.append('success_action_status', '200')
            // formData.append('callback', callback)
            formData.append('signature', signature)
            formData.append('file', file.raw)
            this.$http.defaults.withCredentials = false
            this.$http
              .post(host, formData, {
                headers: {
                  'Content-Type': 'multipart/form-data'
                }
              })
              .then(({ data: res }) => {
                if (res.code !== 200) {
                  return this.$message.error(res.msg)
                }
                this.dataForm.logo = res.data
                this.isVisibleUploadBtn = false
              })
              .catch(() => {})
              .finally(() => {
                this.$http.defaults.withCredentials = true
              })
          })
        })
        .catch(err => {})
        .finally(() => {
          this.$http.defaults.withCredentials = true
        })
    },

    // 表单提交
    dataFormSubmitHandle: debounce(
      function() {
        this.$refs['dataForm'].validate(valid => {
          if (!valid) {
            return false
          }
          this.$http[!this.dataForm.brandId ? 'post' : 'put'](
            '/product/brand/',
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
