<template>
  <!--  操作面板  -->
  <el-form
    :inline="true"
    :model="dataForm"
    ref="dataForm"
  >
    <el-form-item prop="techName">
      <el-select
        v-model="dataForm.techName"
        filterable
        clearable
        placeholder="技術者氏名"
        :filter-method="filterTechName"
        @visible-change="handleVisibleChange"
        style="width: 170px;"
      >
        <el-option
          v-for="item in filteredTechNames"
          :key="item.technicianId"
          :label="item.technicianName"
          :value="item.technicianName"
        />
      </el-select>
    </el-form-item>
    <el-form-item prop="custName">
      <el-select
        v-model="dataForm.custName"
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
        v-model="dataForm.proName"
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
    <el-form-item>
      <el-select
        placeholder="增减列"
        multiple
        style="width: 170px;"
      >
        <el-option
          v-for="item in columns"
          :key="item.key"
          :label="item.label"
          :value="item.key"
        >
          <el-checkbox
            :label="item.label"
            @change="visibleColumnsChangeHandle(item.key)"
            :checked="item.visible"
          >
            {{ item.label }}
          </el-checkbox>
        </el-option>
      </el-select>
      &nbsp;&nbsp;&nbsp;
      <div>
        <span style="background-color: #daf0fa; font-weight: bold ">価格変動中</span>&nbsp;
        <span style="background-color: #d9d9d9; font-weight: bold">未使用</span>
      </div>
    </el-form-item>
  </el-form>

  <!--  数据展示面板  -->
  <el-table
    ref="table"
    :data="dataList"
    border
    v-loading="dataListLoading"
    @selection-change="selectionChangeHandle"
    :row-key="getRowKeys"
    :row-class-name="tableRowClassName"
    @sort-change="orderHandle"
    :cell-style="{ padding: '3px 0' }"
    style="width: 100%"
  >
    <!--   折叠面板   -->
    <el-table-column type="expand">
      <template #default="scope">
        <div>
          <table class="content">
            <tr>
              <td rowspan="20">
                <el-image
                  style="width: 120px; height: 153px; border: 2px solid #D3EDFA; border-radius: 5px;"
                  :src="dataList[scope.$index].picture"
                >
                  <template #error>
                    <div class="error-img">
                      <el-icon>
                        <Picture />
                      </el-icon>
                    </div>
                  </template>
                </el-image>
              </td>
              <th>技術者氏名</th>
              <td>{{ dataList[scope.$index].technicianName }}</td>
              <th>所属（会社名）：</th>
              <td>{{ dataList[scope.$index].belongCompany }}</td>
            </tr>
            <tr>
              <th>カタカナ：</th>
              <td>{{ dataList[scope.$index].katakana }}</td>
              <th>営業担当者：</th>
              <td>{{ dataList[scope.$index].representative }}</td>
            </tr>
            <tr>
              <th>ローマ字：</th>
              <td>{{ dataList[scope.$index].roman }}</td>
              <th>契約書：</th>
              <td>
                <span
                  v-for="(oneFile,index) in dataList[scope.$index].contract"
                  :key="index"
                >
                  <el-button
                    type="text"
                    @click="downloadHandle(oneFile)"
                  >
                    {{oneFile.substring(oneFile.lastIndexOf("/") + 1)}}
                  </el-button>
                </span>
                <div v-if="scope.row.contract.length == 0">契約書がアップロードされていません</div>
              </td>
            </tr>
            <tr>
              <th>生年月日：</th>
              <td>{{ dataList[scope.$index].birthday }}</td>
              <th>契約書番号：</th>
              <td>{{ dataList[scope.$index].contractNum }}</td>
            </tr>
            <tr>
              <th>顧客名：</th>
              <td>{{ dataList[scope.$index].customerName }}</td>
              <th>下請単価：</th>
              <td>{{ dataList[scope.$index].hPrice }}</td>
            </tr>
            <tr>
              <th>プロジェクト名：</th>
              <td>{{ dataList[scope.$index].projectName }}</td>
              <th>下請开始月：</th>
              <td>{{ dataList[scope.$index].hBeginMonth }}</td>
            </tr>
            <tr>
              <th>案件責任者（顧客先）：</th>
              <td>{{ dataList[scope.$index].principal }}</td>
              <th>下請结束月：</th>
              <td>{{ dataList[scope.$index].hEndMonth }}</td>
            </tr>
            <tr>
              <th>案件責任者所属（顧客先）：</th>
              <td>{{ dataList[scope.$index].principalCompany }}</td>
              <th>下請精算幅（下限）：</th>
              <td>{{ dataList[scope.$index].hLowerHours }}</td>
            </tr>
            <tr>
              <th>形態：</th>
              <td>{{ dataList[scope.$index].status }}</td>
              <th>下請精算幅（上限）：</th>
              <td>{{ dataList[scope.$index].hHigherHours }}</td>
            </tr>
            <tr>
              <th>契約単価（顧客先）*月こと：</th>
              <td>{{ dataList[scope.$index].cPrice }}</td>
              <th>下請減単金：</th>
              <td>{{ dataList[scope.$index].hReductPrice }}</td>
            </tr>
            <tr>
              <th>契約开始月：</th>
              <td>{{ dataList[scope.$index].cRealBeginMonth }}</td>
              <th>下請増単金：</th>
              <td>{{ dataList[scope.$index].hIncreasePrice }}</td>
            </tr>
            <tr>
              <th>契約结束月：</th>
              <td>{{ dataList[scope.$index].cRealEndMonth }}</td>
              <th>价格变动月：</th>
              <td>{{ dataList[scope.$index].priceMonth }}</td>
            </tr>
            <tr>
              <th>退場月（年月）：</th>
              <td>{{ dataList[scope.$index].realStopMonth }}</td>
              <th>備考：</th>
              <td>{{ dataList[scope.$index].remark }}</td>
            </tr>
            <tr>
              <th>契約精算幅（下限）：</th>
              <td>{{ dataList[scope.$index].cLowerHours }}</td>
            </tr>
            <tr>
              <th>契約精算幅（上限）：</th>
              <td>{{ dataList[scope.$index].cHigherHours }}</td>
            </tr>
            <tr>
              <th>契約減単金：</th>
              <td>{{ dataList[scope.$index].cReductPrice }}</td>
            </tr>
            <tr>
              <th>契約増単金：</th>
              <td>{{ dataList[scope.$index].cIncreasePrice }}</td>
            </tr>
            <tr>
              <th>1日標準稼働時間：</th>
              <td>{{ dataList[scope.$index].standardHours }}</td>
            </tr>
          </table>
        </div>
      </template>
    </el-table-column>
    <el-table-column
      type="selection"
      :selectable="selectable"
      header-align="center"
      align="center"
      width="40"
    />
    <!--    行内操作按钮    -->
    <el-table-column
      header-align="center"
      align="center"
      width="100%"
      label="操作"
    >
      <template #default="scope">
        <el-select
          v-model="selectedOption"
          placeholder="操作"
          @change="handleSelectionChange(scope.row.projectTechnicianId)"
          clearable
          style="width: 100%;"
        >
          <el-option
            label="変更"
            value="modify"
          ></el-option>
          <el-option
            label="価格変動"
            value="priceChange"
            v-if="!scope.row.priceMonth"
          ></el-option>
          <el-option
            label="退場"
            value="exit"
          ></el-option>
          <el-option
            label="削除"
            value="delete"
          ></el-option>
        </el-select>
      </template>
    </el-table-column>
    <!--   序号  -->
    <el-table-column
      type="index"
      header-align="center"
      align="center"
      width="60"
      label="序号"
    >
      <template #default="scope">
        <span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
      </template>
    </el-table-column>

    <!--   循环展示表格内容   -->
    <el-table-column
      v-for="(column, index) in visibleColumns"
      :label="column.label"
      :key="index"
      :prop="column.prop"
      :width="column.width"
      header-align="center"
      align="center"
      min-width="200"
      :sortable="column.sortable"
      :show-overflow-tooltip="true"
    />
  </el-table>
  <el-pagination
    @size-change="sizeChangeHandle"
    @current-change="currentChangeHandle"
    :current-page="pageIndex"
    :page-sizes="[10, 20, 50, 100]"
    :page-size="pageSize"
    :total="totalCount"
    layout="total, sizes, prev, pager, next, jumper"
  ></el-pagination>
  <add-or-update
    ref="addOrUpdate"
    @refreshDataList="loadProTechList"
  ></add-or-update>

</template>

<script>
import AddOrUpdate from "./appoint-add-or-update-1.vue";
import { ElMessage, ElMessageBox } from "element-plus";

export default {
  components: { AddOrUpdate },
  data: function () {
    return {
      token: localStorage.getItem("token"),
      action: `${this.$baseUrl}/file/upload`,
      selectedOption: null,
      // 技術者、プロジェクト、顧客下拉列表
      techList: [],
      proList: [],
      custList: [],

      // 根据customerId过滤project的数据
      selectedCustomerId: null,
      // 提交表单时的数据
      dataForm: {
        techName: null,
        proName: null,
        custName: null,
        custId: null,
      },
      // 过滤技術者的姓名列表
      filteredTechNames: [],
      filteredProNames: [],

      // 存放查询到的技術者、プロジェクト、顧客、プロジェクト-技術者数据
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalCount: 0,

      // 加载进度条
      dataListLoading: false,

      columns: [
        {
          key: 0,
          label: "技術者氏名",
          prop: "technicianName",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 1,
          label: "カタカナ",
          prop: "katakana",
          visible: false,
          sortable: false,
        },
        {
          key: 2,
          label: "ローマ字",
          prop: "roman",
          visible: false,
          sortable: false,
        },
        {
          key: 3,
          label: "生年月日",
          prop: "birthday",
          visible: false,
          sortable: false,
        },
        {
          key: 4,
          label: "顧客名",
          prop: "customerName",
          visible: true,
          sortable: true,
        },
        {
          key: 5,
          prop: "projectName",
          label: "プロジェクト名",
          visible: true,
          sortable: true,
        },
        {
          key: 6,
          prop: "principal",
          label: "案件責任者（顧客先）",
          visible: false,
          sortable: false,
        },
        {
          key: 7,
          prop: "principalCompany",
          label: "案件責任者所属（顧客先）",
          visible: false,
          sortable: false,
        },
        {
          key: 8,
          prop: "belongCompany",
          label: "所属（会社名）",
          visible: true,
          sortable: true,
        },
        {
          key: 9,
          prop: "status",
          label: "形態",
          visible: true,
          sortable: false,
          width: 80,
        },
        {
          key: 10,
          prop: "cPrice",
          label: "契約単価（顧客先）",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 11,
          prop: "cRealBeginMonth",
          label: "契約开始月",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 12,
          prop: "cRealEndMonth",
          label: "契約结束月",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 13,
          prop: "realStopMonth",
          label: "退場月",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 14,
          prop: "cLowerHours",
          label: "契約精算幅（下限）",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 15,
          prop: "cHigherHours",
          label: "契約精算幅（上限）",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 16,
          prop: "cReductPrice",
          label: "契约減単金",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 17,
          prop: "cIncreasePrice",
          label: "契约増単金",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 18,
          prop: "standardHours",
          label: "1日標準稼働時間",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 19,
          prop: "representative",
          label: "営業担当者",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 20,
          prop: "contractName",
          label: "契約書",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 21,
          prop: "contractNum",
          label: "契約書番号",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 22,
          prop: "hPrice",
          label: "下請単価",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 23,
          prop: "hBeginMonth",
          label: "下請开始月",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 24,
          prop: "hEndMonth",
          label: "下請结束月",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 25,
          prop: "hLowerHours",
          label: "下請精算幅（下限）",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 26,
          prop: "hHigherHours",
          label: "下請精算幅（上限）",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 27,
          prop: "hReductPrice",
          label: "下請減単金",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 28,
          prop: "hIncreasePrice",
          label: "下請増単金",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 29,
          prop: "priceMonth",
          label: "价格变动月",
          visible: true,
          sortable: false,
          width: 100,
        },
        {
          key: 30,
          prop: "remark",
          label: "備考",
          visible: true,
          sortable: false,
          width: 200,
        },
      ],

      dataListSelections: [],
      // 用于保存用户点击的排序列及顺序
      sortOrder: [],
    };
  },
  computed: {
    visibleColumns() {
      return this.columns.filter((column) => column.visible);
    },
  },
  methods: {
    // 以下2个方法用于实现下拉框+查询技術者姓名
    filterTechName(query) {
      if (query !== "") {
        this.filteredTechNames = this.techList.filter((item) => {
          return item.technicianName
            .toLowerCase()
            .includes(query.toLowerCase());
        });
      } else {
        this.filteredTechNames = this.techList;
      }
    },
    handleVisibleChange(visible) {
      if (visible) {
        this.filteredTechNames = this.techList;
        // console.log(JSON.stringify(this.filteredTechNames, null, 2))
      }
    },

    // 获取技術者全部信息
    loadTechList() {
      let that = this;
      that.$httpV2("/protech/technician", "GET", null, true, function (resp) {
        let result = resp.result;
        that.techList = result;
        // console.log('techList111: ', JSON.stringify(that.techList, null, 2));
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
    // 获取顧客全部信息
    loadCustList() {
      let that = this;
      that.$httpV2("/protech/customer", "GET", null, true, function (resp) {
        let result = resp.result;
        that.custList = result;
        // console.log('custList111: ', JSON.stringify(that.custList, null, 2));
      });
    },
    filteredProList() {
      if (!this.selectedCustomerId) {
        // console.log('project222: ', JSON.stringify(this.proList, null, 2));
        return this.proList;
      }
      this.filteredProNames = this.proList.filter(
        (item) => item.customerId === this.selectedCustomerId
      );
      // console.log('project333: ', JSON.stringify(this.proList, null, 2));
      // console.log('project333filtered: ', JSON.stringify(this.filteredProNames, null, 2));
      return this.filteredProNames;
    },
    selectCustId() {
      // 查询时同步选中的顧客ID
      // console.log('dataForm.custName:', this.dataForm.custName);
      // console.log('custList:', this.custList);
      // 使用 find 方法查找匹配的客户
      const customer = this.custList.find(
        (cust) => cust.customerName === this.dataForm.custName
      );
      // console.log('Found customer:', customer);

      // 如果找到匹配的客户，更新 custId
      if (customer) {
        console.log("selectedCustomerId:" + customer.customerId); // 确认字段名为 customerId
        this.dataForm.custId = customer.customerId;
      } else {
        this.dataForm.custId = null; // 如果未找到匹配的客户，设置为 null 或其他默认值
      }
      this.selectedCustomerId = this.dataForm.custId;
      this.dataForm.proName = null;
      // console.log("selectedCustomerId:" + this.dataForm.custId);
      // console.log("selectedCustomerId:" + this.selectedCustomerId);
      this.filteredProList();
    },
    custHandleClear() {
      this.filteredProNames = [];
    },

    loadProTechList() {
      let that = this;
      that.dataListLoading = true;
      let data = {
        page: that.pageIndex,
        length: that.pageSize,
        techName: that.dataForm.techName == null ? "" : that.dataForm.techName,
        proName: that.dataForm.proName == null ? "" : that.dataForm.proName,
        custName: that.dataForm.custName == null ? "" : that.dataForm.custName,
      };
      that.$httpV2("/protech", "GET", data, true, function (resp) {
        let result = resp.result;
        // console.log('resp.result' + resp.result);
        // console.log("resp.result: ", JSON.stringify(resp.result, null, 2));
        that.dataList = result.list.map((item) => {
          for (let key in item) {
            if (
              item.hasOwnProperty(key) &&
              key !== "birthday" &&
              typeof item[key] === "string" &&
              item[key].match(/^\d{4}-\d{2}-\d{2}$/)
            ) {
              item[key] = item[key].substring(0, 7); // 提取 YYYY-MM
            }
          }
          return item;
        });

        that.totalCount = result.totalCount;
        that.dataListLoading = false;

        // 初始数据加载完成后进行默认排序
        that.sortOrder = [
          { prop: "customerName", order: "ascending" },
          { prop: "projectName", order: "ascending" },
        ];
        that.applySorting();
        // console.log("loadProTechList:", JSON.stringify(that.sortOrder, null, 2));
        that.dataList.forEach((one) => {
          let tmp = one.contract || "";
          one.contract = tmp.split("?");
          one.contract = one.contract.filter(
            (item) => item !== null && item !== undefined && item !== ""
          );
          if (one.contract.length > 0) {
            one.contractName = one.contract.at(-1);
            one.contractName = one.contractName.substring(
              one.contractName.lastIndexOf("/") + 1);
            console.log("one.contractName---", one.contractName);
            console.log("one.contract---", one.contract);
          } else {
            one.contractName = "";
          }
        });
      });
    },

    // 分页查询任用信息
    searchHandle() {
      this.pageIndex = 1;
      console.log("dataForm.techName:" + this.dataForm.techName);
      console.log("dataForm.proName:" + this.dataForm.proName);
      console.log("dataForm.custName:" + this.dataForm.custName);
      this.loadProTechList();
    },

    handleSelectionChange(projectTechnicianId) {
      switch (this.selectedOption) {
        case "modify":
          // 调用修改函数
          this.updateHandle(projectTechnicianId, this.selectedOption);
          break;
        case "priceChange":
          // 调用价格变动函数
          this.priceChangeHandle(projectTechnicianId, this.selectedOption);
          break;
        case "exit":
          // 调用退場函数
          this.exitHandle(projectTechnicianId, this.selectedOption);
          break;
        case "delete":
          // 调用删除函数
          this.deleteHandle(projectTechnicianId);
          break;
      }
      console.log(projectTechnicianId, this.selectedOption);
      this.selectedOption = null;
      console.log(projectTechnicianId, this.selectedOption);
    },
    // 新增任用
    addHandle() {
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(null, "insert");
      });
    },
    updateHandle: function (id, selected) {
      const projectTechnicianInfo = this.dataList.find(
        (protech) => protech.projectTechnicianId === id
      );
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(projectTechnicianInfo, selected);
        console.log(projectTechnicianInfo);
      });
    },
    priceChangeHandle: function (id, selected) {
      const projectTechnicianInfo = this.dataList.find(
        (protech) => protech.projectTechnicianId === id
      );
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(projectTechnicianInfo, selected);
      });
    },
    exitHandle: function (id, selected) {
      const projectTechnicianInfo = this.dataList.find(
        (protech) => protech.projectTechnicianId === id
      );
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(projectTechnicianInfo, selected);
      });
    },
    // 删除任用
    // async deleteHandle(id) {
    // let that = this;
    // let ids = id
    //   ? [id]
    //   : that.dataListSelections.map((item) => {
    // 	  console.log(item);
    // 	  return item.projectTechnicianId;
    //   });
    // if (ids.length === 0) {
    //   ElMessage({
    // 	  message: "レコードが選択されていません。",
    // 	  type: "warning",
    // 	  duration: 1200,
    //   });
    // } else {
    //   for (let id1 in ids) {
    // 	  console.log('111111');
    // 	  for (let j1 = 0; j1 < this.dataList.length; j1++) {
    // 		  console.log('222222');
    // 		  var item = this.dataList[j1];
    // 		  console.log(item);
    // 		  console.log(ids[id1]);
    // 		  console.log(item['projectTechnicianId']);
    // 		  // 找到 ids[id1] 对应的元素
    // 		  if (item['projectTechnicianId'] === ids[id1]) {
    // 			  console.log('匹配的元素');
    // 			  // 判断该元素是否有 parentId
    // 			  if (
    // 				  item['parentId'] !== 0 &&
    // 				  item['parentId'] != null &&
    // 				  item['parentId'] !== undefined
    // 			  ) {
    // 				  // 查看 ids 中是否含有 parentId
    // 				  let parentIdInIds = false;
    // 				  for (let id2 in ids) {
    // 					  if (item['parentId'] === ids[id2]) {
    // 						  parentIdInIds = true;
    // 						  break;
    // 					  }
    // 				  }
    // 				  if (!parentIdInIds) {
    // 					  if (item['priceMonth'] != null) {
    // 						  // 该元素是中间节点
    // 						  // 如果 ids 中包含中间节点的子节点，则可以删除
    // 						  let hasChildInIds = false;
    // 						  for (let i = 0; i < this.dataList.length; i++) {
    // 							  if (
    // 								  this.dataList[i]['parentId'] ===
    // 								  item['projectTechnicianId']
    // 							  ) {
    // 								  for (let id3 in ids) {
    // 									  if (this.dataList[i]['projectTechnicianId'] === ids[id3]) {
    // 										  hasChildInIds = true;
    // 										  break;
    // 									  }
    // 								  }
    // 							  }
    // 						  }
    // 						  if (!hasChildInIds) {
    // 							  // 该元素是中间节点，并且 ids 中不包含其子节点的记录，不能删除
    // 							  await ElMessageBox.confirm(
    // 								  item['technicianName'] +
    // 								  "は価格が変動した後のデータであり、同時にもう1回価格が変動しました。削除できません。",
    // 								  "警告",
    // 								  {
    // 									  confirmButtonText: "確定",
    // 									  showCancelButton: false,
    // 									  type: "warning",
    // 								  }
    // 							  );
    // 							  // 用户点击“确定”，从 ids 中移除当前 id
    // 							  ids.splice(ids.indexOf(ids[id1]), 1);
    // 								return;
    // 						  }
    // 					  } else {
    // 						  // 否则，在用户确认要删除该元素时，将该 parentId 添加至 ids 末尾。
    // 						  try {
    // 							  await ElMessageBox.confirm(
    // 								  item['technicianName'] +
    // 								  "は価格変動後のデータなので、価格変動前のデータも削除すべきでしょうか？",
    // 								  "警告",
    // 								  {
    // 									  confirmButtonText: "確定",
    // 									  cancelButtonText: "キャンセル",
    // 									  type: "warning",
    // 								  }
    // 							  );
    // 							  // 用户点击“确定”，执行操作
    // 							  ids.push(item['parentId']);
    // 						  } catch (error) {
    // 							  // 用户点击“キャンセル”，捕获异常，继续执行后续代码
    // 						  }
    // 					  }
    // 				  }
    // 			  }
    // 		  }
    // 	  }
    //   }
    //   // 最后弹出确认框，确认删除操作
    //   try {
    // 	  await ElMessageBox.confirm(
    // 		  "レコード削除してよろしいでしょうか？",
    // 		  "警告",
    // 		  {
    // 			  confirmButtonText: "確定",
    // 			  cancelButtonText: "キャンセル",
    // 			  type: "warning",
    // 		  }
    // 	  );
    // 	  // 用户点击“确定”，执行删除操作
    // 	  that.$httpV2(
    // 		  "/protech",
    // 		  "DELETE",
    // 		  { ids: ids },
    // 		  true,
    // 		  function (resp) {
    // 			  ElMessage({
    // 				  message: "削除完了しました。",
    // 				  type: "success",
    // 				  duration: 1200,
    // 				  onClose: () => {
    // 					  that.loadProTechList();
    // 				  },
    // 			  });
    // 		  }
    // 	  );
    //   } catch (error) {
    // 	  // 用户点击“キャンセル”，捕获异常，不执行删除，但代码不会中断
    //   }
    // }
    // },
    deleteHandle(id) {
      console.log("id: " + id);
      let that = this;
      let ids = id
        ? [id]
        : that.dataListSelections.map((item) => {
            console.log(item);
            return item.projectTechnicianId;
          });
      console.log("ids: " + ids);
      if (ids.length == 0) {
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
            "/protech",
            "DELETE",
            { ids: ids },
            true,
            function (resp) {
              ElMessage({
                message: "削除完了しました。",
                type: "success",
                duration: 1200,
                onClose: () => {
                  that.loadProTechList();
                },
              });
            }
          );
        });
      }
    },

    // 应用排序逻辑
    applySorting() {
      this.dataList.sort((a, b) => {
        for (const { prop, order } of this.sortOrder) {
          const comparison = a[prop].localeCompare(b[prop], undefined, {
            numeric: true,
          });
          if (comparison !== 0) {
            return order === "descending" ? comparison : -comparison;
          }
        }
        return 0;
      });
    },
    orderHandle({ column, prop, order }) {
      // 检查列是否已在排序数组中
      const existingIndex = this.sortOrder.findIndex(
        (item) => item.prop === prop
      );
      if (existingIndex >= 0) {
        // 如果已经存在，则更新排序顺序
        if (order) {
          this.sortOrder[existingIndex].order = order;
        } else {
          // 如果排序顺序为null，移除该列（order为null，说明用户取消了对该列的排序）
          this.sortOrder.splice(existingIndex, 1);
        }
      } else {
        // 如果不存在，说明是用户想要对该列进行排序，将该字段加入排序数组中
        if (order) {
          this.sortOrder.push({ prop, order });
        }
      }

      // 应用排序逻辑
      this.applySorting();

      // 手动设置表格排序状态
      this.$nextTick(() => {
        if (this.$refs.table && this.$refs.table.columns) {
          this.sortOrder.forEach(({ prop, order }) => {
            const column = this.$refs.table.columns.find(
              (col) => col.property === prop
            );
            if (column) {
              column.order = order;
            }
          });
        }
      });
    },

    // 选中行
    selectionChangeHandle: function (val) {
      // val：被选中的行的全部信息
      this.dataListSelections = val;
      // console.log(val)
    },
    getRowKeys(row) {
      // console.log(row.projectTechnicianId);
      return row.projectTechnicianId;
    },

    // 增减列
    visibleColumnsChangeHandle(key) {
      const selectedOne = this.columns.find((one) => one.key === key);
      selectedOne.visible = !selectedOne.visible;
    },

    // 下载文件
    getFileName: function (url) {
      const match = url.match(/\/([^\/?#]+)$/);
      const fileName = match ? match[1] : null;
      return fileName;
    },
    downloadFile: function (blob, filename) {
      // 创建一个链接元素
      var link = document.createElement("a");
      // 创建一个指向 Blob 对象的 URL
      link.href = window.URL.createObjectURL(blob);
      // 设置下载属性，包括文件名
      link.download = filename;
      // 隐藏该元素
      link.style.display = "none";
      // 将其添加到 DOM 中
      document.body.appendChild(link);
      // 模拟点击
      link.click();
      // 从 DOM 中移除元素
      document.body.removeChild(link);
      // 清理 blob URL
      window.URL.revokeObjectURL(link.href);
    },
    downloadHandle: function (url) {
      let that = this;
      var xhr = new XMLHttpRequest();
      // var requestUrl = "/api/v1/file/download?name=" + encodeURIComponent(url);
      // var requestUrl = this.baseUrl + "/file/download?name=" + encodeURIComponent(url);
      // "http://192.168.1.2:8092/talentManageSystem-api/api/v1/file/download?name=" +
      // "http://localhost:8092/talentManageSystem-api/api/v1/file/download?name=" +
      var requestUrl =
        "http://localhost:8092/talentManageSystem-api/api/v1/file/download?name=" +
        encodeURIComponent(url);
      console.log("requestUrl:" + requestUrl);
      xhr.open("GET", requestUrl, true);
      xhr.responseType = "blob"; // 重要：设置响应类型为 Blob
      xhr.setRequestHeader("token", localStorage.getItem("token"));
      xhr.send();

      xhr.onload = function () {
        if (xhr.status === 200) {
          // 响应成功，处理下载
          that.downloadFile(xhr.response, that.getFileName(url)); // 假设你知道文件类型和扩展名
        } else {
          console.error("Request failed. Status:", xhr.status);
        }
      };
    },
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.loadProTechList();
    },
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.loadProTechList();
    },
    // 更改价格变动、退場时该行的颜色
    // 未使用的数据显示为灰色，当月正在变更的数据显示为蓝色，正在使用的数据为白色。
    tableRowClassName({ row }) {
      const currentMonth = new Date().getMonth() + 1; // 获取当前月份 (JavaScript 中月份从0开始计数，所以需要+1)
      // console.log("currentMonth: " + currentMonth);

      // 将 priceMonth 和 cBeginMonth 转换为月份的数值
      const priceMonthValue = new Date(row.priceMonth).getMonth() + 1;
      const cBeginMonthValue = new Date(row.cBeginMonth).getMonth() + 1;
      const stopMonthValue = new Date(row.stopMonth).getMonth() + 1;

      if (priceMonthValue) {
        // 如果价格变动月等于当月，则该记录变成蓝色
        if (priceMonthValue === currentMonth)
          return "price-and-stop-month-highlight";
        // 如果价格变动月小于当月，说明价格变动已经结束，则该记录为灰色
        else if (priceMonthValue < currentMonth) return "stop-month-highlight";
        // 其余显示为白色
      }
      if (stopMonthValue) {
        // 如果退场月小于等于当月，说明已经退场了，则该记录为灰色
        if (stopMonthValue <= currentMonth) return "stop-month-highlight";
        // 其余显示为白色
      }
      // 如果契约开始月大于当前月份，说明该数据当前未被使用，则该记录为灰色
      if (cBeginMonthValue > currentMonth) return "stop-month-highlight";
      return "";
    },
  },
  created() {
    this.loadTechList();
    this.loadProList();
    this.loadCustList();
    this.loadProTechList();
  },
};
</script>

<style lang="less" scoped="scoped">
@import url(protech.less);
</style>