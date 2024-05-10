<template>
  <div>
    <el-upload
      :action="host"
      :data="dataObj"
      list-type="picture-card"
      :file-list="fileList"
      :before-upload="beforeUpload"
      :on-remove="handleRemove"
      :on-success="handleUploadSuccess"
      :on-preview="handlePreview"
      :limit="maxCount"
      :on-exceed="handleExceed"
    >
      <i class="el-icon-plus"></i>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt />
    </el-dialog>
  </div>
</template>
<script>
import { getUUID } from '@/utils'
import { ossURL } from '@/api/thirdparty'
export default {
  name: 'multiUpload',
  props: {
    //图片属性数组
    value: Array,
    //最大上传图片数量
    maxCount: {
      type: Number,
      default: 30
    }
  },
  data() {
    return {
      dataObj: {
        policy: '',
        signature: '',
        key: '',
        ossaccessKeyId: '',
        dir: '',
        host: '',
        uuid: ''
      },
      dialogVisible: false,
      dialogImageUrl: null,
      host: '#'
    }
  },
  computed: {
    fileList() {
      let fileList = []
      for (let i = 0; i < this.value.length; i++) {
        fileList.push({ url: this.value[i] })
      }

      return fileList
    }
  },
  mounted() {},
  methods: {
    emitInput(fileList) {
      let value = []
      for (let i = 0; i < fileList.length; i++) {
        value.push(fileList[i].url)
      }
      this.$emit('input', value)
    },
    handleRemove(file, fileList) {
      this.emitInput(fileList)
    },
    handlePreview(file) {
      this.dialogVisible = true
      this.dialogImageUrl = file.url
    },
    beforeUpload(file) {
      let _self = this
      return new Promise((resolve, reject) => {
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
            _self.dataObj.policy = policy
            _self.dataObj.signature = signature
            _self.dataObj.ossaccessKeyId = accessId
            _self.dataObj.key = dir + getUUID() + '_${filename}'
            _self.dataObj.dir = dir
            _self.dataObj.host = host
            _self.dataObj.success_action_status = '200'
            this.host = host
            console.log('host ', host)
            resolve(true)
          })
          .catch(err => {
            reject(false)
          })
      })
    },
    handleUploadSuccess(res, file) {
      this.fileList.push({
        name: file.name,
        // url: this.dataObj.host + "/" + this.dataObj.dir + "/" + file.name； 替换${filename}为真正的文件名
        url:
          this.dataObj.host +
          '/' +
          this.dataObj.key.replace('${filename}', file.name)
      })
      this.emitInput(this.fileList)
    },
    handleExceed(files, fileList) {
      this.$message({
        message: '最多只能上传' + this.maxCount + '张图片',
        type: 'warning',
        duration: 1000
      })
    }
  }
}
</script>
<style></style>
