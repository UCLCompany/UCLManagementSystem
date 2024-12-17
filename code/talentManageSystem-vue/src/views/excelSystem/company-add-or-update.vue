<template>
  <el-dialog
      :title="!dataForm.partnerCompanyId ? '追加' : '変更'"
      :close-on-click-modal="false"
      v-model="visible"
      width="450px"
  >
    <el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="150px">
      <el-form-item label="会社名" prop="partnerCompanyName">
        <el-input v-model="dataForm.partnerCompanyName" style="width:100%" clearable />
      </el-form-item>
      <el-form-item label="〒郵便番号" prop="partnerpostalCode">
        <el-input v-model="dataForm.partnerpostalCode" clearable />
      </el-form-item>
      <el-form-item label="住所" prop="partnerCompanyAddress">
        <el-input v-model="dataForm.partnerCompanyAddress" clearable />
      </el-form-item>
      <el-form-item label="支払い条件" prop="termsofPayment">
        <el-input v-model="dataForm.termsofPayment" clearable />
      </el-form-item>
      <el-form-item label="代表名" prop="representativeName">
        <el-input v-model="dataForm.representativeName" clearable />
      </el-form-item>
      <el-form-item label="下請締結日" prop="coopDate">
        <el-date-picker
            v-model="dataForm.coopDate"
            type="date"
            placeholder="下請締結日を選択"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            clearable
            style="width: 100%"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">キャンセル</el-button>
        <el-button type="primary" @click="dataFormSubmit">確定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { ElMessage } from "element-plus";

export default {
  data: function () {
    return {
      token: localStorage.getItem("token"),
      action: `${this.$baseUrl}/file/upload`,

      visible: false,
      dataForm: {
        partnerCompanyId: null,
        partnerCompanyName: null,
        partnerpostalCode: null,
        partnerCompanyAddress: null,
        termsofPayment: null,
        representativeName: null,
        coopDate: null,
      },
      dataRule: {
        partnerCompanyName: [{ required: true, message: "会社名の入力をお願いします" }],
        partnerpostalCode: [
          { pattern: /^\d{3}-\d{4}$/, message: "正しい郵便番号形式で入力してください (例: 123-4567)" },
        ],
        partnerCompanyAddress: [],
        termsofPayment: [],
        representativeName: [],
        coopDate: [],
      },
    };
  },

  methods: {
    init: function (companyInfo) {
      let that = this;
      that.setFormRules();
      that.reset();
      that.$nextTick(() => {
        that.$refs["dataForm"].resetFields();
        if (companyInfo != null && companyInfo != "") {
          that.dataForm = {
            ...JSON.parse(JSON.stringify(companyInfo)),
          };
          console.log(this.dataForm);
        }
      });
      that.visible = true;
    },
    reset: function () {
      let dataForm = {
        partnerCompanyId: null,
        partnerCompanyName: null,
        partnerpostalCode: null,
        partnerCompanyAddress: null,
        termsofPayment: null,
        representativeName: null,
        coopDate: null,
      };
      this.dataForm = dataForm;
    },
    setFormRules: function () {
      this.dataRule.partnerCompanyName = [{ required: true, message: "会社名の入力をお願いします" }];
    },
    dataFormSubmit: function () {
      let that = this;
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          const operation = !that.dataForm.partnerCompanyId ? "insert" : "update";
          const method = operation === "insert" ? "POST" : "PUT";
          let data = {
            partnerCompanyId: this.dataForm.partnerCompanyId || "",
            partnerCompanyName: this.dataForm.partnerCompanyName || "",
            partnerpostalCode: this.dataForm.partnerpostalCode || "",
            partnerCompanyAddress: this.dataForm.partnerCompanyAddress || "",
            termsofPayment: this.dataForm.termsofPayment || "",
            representativeName: this.dataForm.representativeName || "",
            coopDate: this.dataForm.coopDate || "",
          };
          that.$httpV2(`/company/Partner`, method, data, true, function (resp) {
            ElMessage({
              message: "操作成功",
              type: "success",
            });
            that.visible = false;
            that.$emit("refreshDataList");
          });
        }
      });
    },
  },
};
</script>

<style lang="less" scoped></style>
