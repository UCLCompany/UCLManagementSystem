<template>
  <el-dialog
      :title=" dataForm.projectId == '' || dataForm.projectId == null ? '新增' : '修改' "
      :close-on-click-modal="false"
      v-model="visible"

  >
    <el-scrollbar>
      <el-form
          :model="dataForm"
          ref="dataForm"
          :rules="dataRule"
      >
        <el-form-item label="顧客名：" prop="customerName">
          <el-select
              v-model="dataForm.customerName"
              placeholder="選んでお願いします"
              :disabled="isUpdated"
              clearable
              style="width: 100%"
          >
            <el-option
                v-for="item in custList"
                :key="item.customerId"
                :label="item.customerName"
                :value="item.customerId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="プロジェクト名：" prop="projectName">
          <el-input v-model="dataForm.projectName" clearable/>
        </el-form-item>
        <el-form-item label="案件責任者（顧客先）：" prop="principal">
          <el-input v-model="dataForm.principal" clearable/>
        </el-form-item>
        <el-form-item label="案件責任者所属（顧客先）：" prop="principalCompany">
          <el-input v-model="dataForm.principalCompany" clearable/>
        </el-form-item>
        <el-form-item label="カテグリー" prop="category">
          <el-input v-model="dataForm.category" clearable/>
        </el-form-item>
        <el-form-item label="UCL開発部担当者" prop="uclPrincipal">
          <el-input v-model="dataForm.uclPrincipal" clearable/>
        </el-form-item>
        <el-form-item label="備考：" prop="remark">
          <el-input type="textarea" rows="1" v-model="dataForm.remark" clearable/>
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">キャンセル</el-button>
        <el-button type="primary" @click="dataFormSubmit">確定</el-button>
      </span>
    </template>
  </el-dialog>

</template>

<script>
import {ElMessage} from "element-plus";

export default {
  data: function () {
    return {
      visible: false,
      isUpdated: false,

      custList: [],
      dataForm: {
        customerName: null,
        projectId: null,
        projectName: null,
        principal: null,
        principalCompany: null,
        remark: null,
      },

      dataRule: {
        customerName: [
          {
            required: true,
            message: "顧客名不能为空"
          }
        ],
        projectName: [
          {
            required: true,
            message: "プロジェクト名不能为空",
          },
        ],
      },
    }
  },
  methods: {
    init(technicianInfo) {
      let that = this;
      that.reset();
      that.$nextTick(() => {
        that.$refs["dataForm"].resetFields();
        if (technicianInfo != null && technicianInfo != "") {
          that.dataForm = {
            ...JSON.parse(JSON.stringify(technicianInfo)),
          };
          that.isUpdated = true;
          console.log(that.dataForm);
        }
      })
      that.visible = true;
    },
    reset() {
      let dataForm = {
        customerName: null,
        projectId: null,
        projectName: null,
        principal: null,
        principalCompany: null,
        remark: null,
      };
      this.dataForm = dataForm;
      this.isUpdated = false;
    },
    dataFormSubmit: function () {
      let that = this;
      console.log(this.dataForm);
      that.$refs["dataForm"].validate(function (valid) {
        if (valid) {
          let data = {
            customerId: that.dataForm.customerName,
            projectId: that.dataForm.projectId,
            projectName: that.dataForm.projectName,
            principal: that.dataForm.principal,
            principalCompany: that.dataForm.principalCompany,
            category: that.dataForm.category,
            uclPrincipal: that.dataForm.uclPrincipal,
            remark: that.dataForm.remark,
          };
          console.log("data1111: " + that.dataForm)
          let method = "";
          if (that.dataForm.projectId == "" || that.dataForm.projectId == null) {
            method = "POST";
          } else {
            method = "PUT";
            data.projectId = that.dataForm.projectId;
          }
          that.$httpV2("/project", method, data, false, function (resp) {
            ElMessage({
              message: "成功した操作",
              type: "success",
            });
            that.visible = false;
            that.$emit("refreshDataList");
          })
        }
      });
    },
    // 获取顧客全部信息
    loadCustList() {
      let that = this;
      that.$httpV2('/protech/customer', 'GET', null, true, function (resp) {
        let result = resp.result;
        that.custList = result;
        // console.log('custList111: ', JSON.stringify(that.custList, null, 2));
      })
    },
  },
  created() {
    this.loadCustList();
  }
}
</script>