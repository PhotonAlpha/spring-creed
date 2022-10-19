<template>
  <div>

    <el-row>
      <el-col :span="12">
        <div class="dashboard-container">
          <el-upload
            ref="upload"
            class="upload-demo"
            drag
            action="#"
            :http-request="uploadFile"
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :on-success="onSuccess"
            :on-error="onError"
            :before-upload="onBeforeUpload"

            :file-list="fileList"
            :auto-upload="false"
            multiple
          >
            <i class="el-icon-upload" />
            <div class="el-upload__text">Drop file here or <em>click to upload</em></div>
            <div slot="tip" class="el-upload__tip">jpg/png files with a size less than 500kb</div>
          </el-upload>
          <el-button style="margin-left: 10px;margin-top: 30px;" size="small" type="success" @click="submitUpload">upload to server</el-button>
        </div>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <el-form ref="form" :model="form" label-width="120px">
            <el-form-item label="Password">
              <el-input v-model="form.pwd" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row type="flex" justify="space-around">
      <el-col :span="12">
        <el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-width="120px" class="demo-ruleForm">
          <el-form-item label="Activity form" prop="desc">
            <el-input
              v-model="ruleForm.command"
              :autosize="{ minRows: 6}"
              class="dashboard-text-area"
              type="textarea"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('ruleForm')">submit</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>result</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="clean()">Clean</el-button>
          </div>
          <div v-for="o in executionResult" :key="o" class="text item">
            {{ o }}
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

</template>

<script>
import { mapGetters } from 'vuex'
import streamSaver from 'streamsaver'
import path from 'path'
import { listFiles, uploadFiles, submitComm, downloadRepo, removeFile } from '@/api/upload'

export default {
  name: 'Dashboard',
  data() {
    return {
      fileList: [
        { name: 'food.jpeg', url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100' },
        { name: 'food2.jpeg', url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100' }
      ],
      uploadUrl: `${window.location.origin}/api/v1/upload`,
      form: {
        pwd: ''
      },
      ruleForm: {
        command: ''

      },
      rules: {
        command: [
          { required: true, message: 'Please input command', trigger: 'blur' }
        ]
      },
      executionResult: []
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    onBeforeUpload() {
      // if(this.form.pwd) {
      //   return true
      // } else {
      //   this.$message.error('pwd!');
      //   return false
      // }
    },
    handleRemove(file, fileList) {
      console.log(file, fileList)

      removeFile({ path: file.name })
        .then(res => {
          console.log(res)
          const { success } = res
          if (success) {
            this.$message.success('delete successful')
          } else {
            this.$message.error('delete failure')
          }
        })
    },
    handlePreview(file) {
      console.log(file)
      console.log(file.name.split('\\').pop().split('/').pop())
      // streamSaver.mitm = 'https://example.com/js/lib/streamsaver/mitm.html'
      downloadRepo({ path: file.name })
        .then(res => res.blob())
        .then(data => {
          const a = document.createElement('a')
          a.href = window.URL.createObjectURL(data)
          a.download = file.name.split('\\').pop().split('/').pop()
          a.click()
        })
      // downloadRepo({ path: file.name }).then(res => {
      //   // 使用StreamSaver.js+fetch解决这个问题，fetch比axios区别在于只要响应马上就可以开始弹出浏览器下载保存对话框，并且不影响文件流下载，而axios非要等到整个文件流下完了才有响应。所以axios比较适合restful接口调用这种场景。
      //   console.log('res', res)
      //   const readableStream = res.body

      //   // These code section is adapted from an example of the StreamSaver.js
      //   // https://jimmywarting.github.io/StreamSaver.js/examples/fetch.html
      //   // If the WritableStream is not available (Firefox, Safari), take it from the ponyfill
      //   // if (!window.WritableStream) {
      //   //   streamSaver.WritableStream = WritableStream
      //   //   window.WritableStream = WritableStream
      //   // }
      //   const fileName = file.name.split('\\').pop().split('/').pop()

      //   const fileStream = streamSaver.createWriteStream(fileName)

      //   // More optimized
      //   if (window.WritableStream && readableStream.pipeTo) {
      //     return readableStream.pipeTo(fileStream)
      //       .then(() => this.$message.success(`Download successful ${fileName}`))
      //   }
      //   const writer = fileStream.getWriter()
      //   const reader = res.body.getReader()
      //   const pump = () => reader.read()
      //     .then(result => {
      //       if (result.done) {
      //         writer.close()
      //         this.$message.success(`Download successful ${fileName}`)
      //       } else {
      //         writer.write(res.value).then(pump)
      //       }
      //     })
      //   pump()
      // })
    },
    handleExceed(files, fileList) {
      this.$message.warning(`The limit is 3, you selected ${files.length} files this time, add up to ${files.length + fileList.length} totally`)
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`Cancel the transfert of ${file.name} ?`)
    },
    submitUpload() {
      this.$refs.upload.submit()
    },
    onSuccess(res, file, fileList) {
      console.log('success', res, file, fileList)
    },
    onError(err, file, fileList) {
      console.log('onError', err, file, fileList)
    },
    uploadFile(file) {
      var formData = new FormData()
      // 添加参数
      console.log('file', file.file)
      formData.append('files', file.file)
      formData.append('pwd', this.form.pwd)
      uploadFiles(formData).then(res => {
        console.log('res', res)
        const { success } = res
        if (success) {
          this.$message.success('文件上传成功')
        } else {
          this.$message.error('文件上传失败')
        }
      })
    },
    fetchData() {
      this.listLoading = true
      listFiles().then(response => {
        this.fileList = response.data
        this.listLoading = false
      })
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        console.log('valid', valid)
        if (valid) {
          // submit msg
          submitComm(this.ruleForm).then(res => {
            console.log('res', res)
            const { data } = res
            console.log('data', data)
            this.executionResult.push(...data)
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    clean() {
      this.executionResult = []
    }
  }
  // computed: {
  //   ...mapGetters([
  //     'name'
  //   ]),
  // data() {
  //   return {
  //     fileList: [
  //       { name: 'food.jpeg', url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100' },
  //       { name: 'food2.jpeg', url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100' }
  //     ],
  //     uploadUrl: `${window.location.origin}/api/v1/upload`
  //   }
  // },

  // }
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
  }
  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}
</style>

<style lang="scss">
.dashboard {
  &-text-area {
    height: 200px;
  }
}
</style>
