<template>
  <el-form
    :inline="true"
    :model="dataForm"
    ref="dataForm"
  >
    <el-form-item prop="customerName">
      <el-select
        v-model="dataForm.customerName"
        clearable
        placeholder="顧客名"
        @change="selectCustId"
        @clear="custHandleClear"
        style="width: 170px;"
      >
        <el-option
          v-for="item in custList"
          :key="item.customerId"
          :label="item.customerName"
          :value="item.customerName"
        />
      </el-select>
    </el-form-item>
    <el-form-item prop="proName">
      <el-select
        v-model="dataForm.projectName"
        clearable
        placeholder="プロジェクト名"
        style="width: 170px;"
      >
        <el-option
          v-for="item in filteredProNames"
          :key="item.projectId"
          :label="item.projectName"
          :value="item.projectName"
        />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button
        size="medium"
        type="primary"
        @click="searchHandle()"
      >検索</el-button>
      <el-button
        size="medium"
        type="primary"
        @click="addHandle()"
      >追加</el-button>
      <el-button
        size="medium"
        type="danger"
        @click="deleteHandle()"
      >一括削除</el-button>
    </el-form-item>
  </el-form>
  <el-table
    ref="table"
    :data="dataList"
    border
    v-loading="dataListLoading"
    @selection-change="selectionChangeHandle"
    :row-key="getRowKeys"
    @sort-change="orderHandle"
    :cell-style="{ padding: '3px 0' }"
    style="width: 100%"
  >
    <el-table-column
      type="selection"
      :selectable="selectable"
      header-align="center"
      align="center"
      width="40"
    />

    <!--   序号  -->
    <el-table-column
      type="index"
      header-align="center"
      align="center"
      width="60"
      label="番号"
    >
      <template #default="scope">
        <span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
      </template>
    </el-table-column>
    <el-table-column
      header-align="center"
      align="center"
      width="100"
      label="操作"
    >
      <template #default="scope">
        <el-button
          type="text"
          @click="updateHandle(scope.row.projectId)"
        >
          変更
        </el-button>
        <el-button
          type="text"
          @click="deleteHandle(scope.row.projectId)"
        >
          削除
        </el-button>
      </template>
    </el-table-column>
    <!--   循环展示表格内容   -->
    <el-table-column
      v-for="(column, index) in visibleColumns"
      :label="column.label"
      :key="index"
      :prop="column.prop"
      header-align="center"
      align="center"
      min-width="150"
      :sortable="column.sortable"
      :show-overflow-tooltip="true"
    />
  </el-table>
  <el-pagination
    @size-change="sizeChangeHandle"
    @current-change="currentChangeHandle"
    :current-page="pageIndex"
    :page-sizes="[10, 20, 50]"
    :page-size="pageSize"
    :total="totalCount"
    layout="total, sizes, prev, pager, next, jumper"
  ></el-pagination>
  <add-or-update
    ref="addOrUpdate"
    @refreshDataList="loadProjectList"
  ></add-or-update>
</template>

<script>
import { ElMessage } from "element-plus";
import AddOrUpdate from "./project-add-or-update.vue";
export default {
  components: { AddOrUpdate },
  data: function () {
    return {
      selectedOption: null,
      // 技術者下拉列表
      custList: [],
      proList: [],

      filteredProNames: [],

      // 提交表单时的数据
      dataForm: {
        projectName: null,
        principal: null,
        principalCompany: null,
        remark: null,
      },

      // 存放查询到的顧客数据
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalCount: 0,

      // 加载进度条
      dataListLoading: false,

      columns: [
        {
          key: 0,
          label: "顧客名",
          prop: "customerName",
          visible: true,
          sortable: true,
        },
        {
          key: 5,
          label: "カテゴリー",
          prop: "category",
          visible: true,
          sortable: true,
        },
        {
          key: 1,
          label: "プロジェクト名",
          prop: "projectName",
          visible: true,
        },
        {
          key: 2,
          label: "案件責任者（顧客先）",
          prop: "principal",
          visible: true,
        },
        {
          key: 3,
          label: "案件責任者所属（顧客先）",
          prop: "principalCompany",
          visible: true,
        },
        {
          key: 4,
          label: "備考",
          prop: "remark",
          visible: true,
        },
      ],

      dataListSelections: [],
    };
  },
  computed: {
    visibleColumns() {
      return this.columns.filter((column) => column.visible);
    },
  },
  methods: {
    loadProjectList() {
      let that = this;
      that.dataListLoading = true;
      let data = {
        page: that.pageIndex,
        length: that.pageSize,
        projectName:
          that.dataForm.projectName == null ? "" : that.dataForm.projectName,
        customerName:
          that.dataForm.customerName == null ? "" : that.dataForm.customerName,
      };
      that.$httpV2("/project", "GET", data, true, function (resp) {
        let result = resp.result;
        that.dataList = result.list;
        that.totalCount = result.totalCount;
        that.dataListLoading = false;
      });
    },
    // 获取プロジェクト全部信息
    loadProList() {
      let that = this;
      that.$httpV2("/protech/project", "GET", null, true, function (resp) {
        let result = resp.result;
        that.proList = result;
        // console.log('project111: ', JSON.stringify(that.proList, null, 2));
      });
    },
    selectionChangeHandle: function (val) {
      this.dataListSelections = val;
    },
    getRowKeys(row) {
      return row.projectId;
    },
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.loadProjectList();
    },
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.loadProjectList();
    },

    searchHandle() {
      this.pageIndex = 1;
      this.loadProjectList();
    },
    addHandle() {
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init();
      });
    },
    updateHandle: function (id) {
      const technicianInfo = this.dataList.find(
        (technician) => technician.projectId === id
      );
      console.log(technicianInfo);
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(technicianInfo);
      });
    },
    deleteHandle(id) {
      console.log("id: " + id);
      let that = this;
      let customerIds = id
        ? [id]
        : that.dataListSelections.map((item) => {
            console.log(item);
            return item.projectId;
          });
      console.log("customerids: " + customerIds);
      if (customerIds.length == 0) {
        ElMessage({
          message: "レコードが選択されていません。",
          type: "warning",
          duration: 1200,
        });
      } else {
        ElMessageBox.confirm("レコード削除してよろしいでしょうか？", "警告", {
          confirmButtonText: "確定",
          cancelButtonText: "キャンセル",
          type: "warning",
        }).then(() => {
          that.$httpV2(
            "/project",
            "DELETE",
            { projectIds: customerIds },
            true,
            function (resp) {
              ElMessage({
                message: "削除完了しました。",
                type: "success",
                duration: 1200,
                onClose: () => {
                  that.loadProjectList();
                },
              });
            }
          );
        });
      }
    },
    filteredProList() {
      if (!this.selectedCustomerId) {
        console.log("project222: ", JSON.stringify(this.proList, null, 2));
        return this.proList;
      }
      this.filteredProNames = this.proList.filter(
        (item) => item.customerId === this.selectedCustomerId
      );
      return this.filteredProNames;
    },
    selectCustId() {
      const customer = this.custList.find(
        (cust) => cust.customerName === this.dataForm.customerName
      );
      if (customer) {
        console.log("selectedCustomerId:" + customer.customerId); // 确认字段名为 customerId
        this.dataForm.custId = customer.customerId;
      } else {
        this.dataForm.custId = null; // 如果未找到匹配的客户，设置为 null 或其他默认值
      }
      this.selectedCustomerId = this.dataForm.custId;
      this.dataForm.proName = null;
      this.filteredProList();
    },
    custHandleClear() {
      this.filteredProNames = [];
    },
    // 获取顧客全部信息
    loadCustList() {
      let that = this;
      that.$httpV2("/protech/customer", "GET", null, true, function (resp) {
        let result = resp.result;
        that.custList = result;
      });
    },
  },
  created() {
    this.loadProjectList();
    this.loadProList();
    this.loadCustList();
  },
};
</script>
