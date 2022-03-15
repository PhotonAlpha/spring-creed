<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="" prop="branchId">
      <el-input v-model="dataForm.branchId" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="xid">
      <el-input v-model="dataForm.xid" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="context">
      <el-input v-model="dataForm.context" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="rollbackInfo">
      <el-input v-model="dataForm.rollbackInfo" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="logStatus">
      <el-input v-model="dataForm.logStatus" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="logCreated">
      <el-input v-model="dataForm.logCreated" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="logModified">
      <el-input v-model="dataForm.logModified" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="ext">
      <el-input v-model="dataForm.ext" placeholder=""></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          id: 0,
          branchId: '',
          xid: '',
          context: '',
          rollbackInfo: '',
          logStatus: '',
          logCreated: '',
          logModified: '',
          ext: ''
        },
        dataRule: {
          branchId: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          xid: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          context: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          rollbackInfo: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          logStatus: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          logCreated: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          logModified: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          ext: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/ware/undolog/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.branchId = data.undoLog.branchId
                this.dataForm.xid = data.undoLog.xid
                this.dataForm.context = data.undoLog.context
                this.dataForm.rollbackInfo = data.undoLog.rollbackInfo
                this.dataForm.logStatus = data.undoLog.logStatus
                this.dataForm.logCreated = data.undoLog.logCreated
                this.dataForm.logModified = data.undoLog.logModified
                this.dataForm.ext = data.undoLog.ext
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/ware/undolog/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'branchId': this.dataForm.branchId,
                'xid': this.dataForm.xid,
                'context': this.dataForm.context,
                'rollbackInfo': this.dataForm.rollbackInfo,
                'logStatus': this.dataForm.logStatus,
                'logCreated': this.dataForm.logCreated,
                'logModified': this.dataForm.logModified,
                'ext': this.dataForm.ext
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
