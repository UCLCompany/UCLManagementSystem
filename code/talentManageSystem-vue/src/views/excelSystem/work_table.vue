<template>
  <h3>
    <SvgIcon
      name="back"
      class="icon-svg back-icon"
      @click="backToMenu"
    />{{
      tableName
    }}
  </h3>

  <div style="padding-bottom: 10px; padding-left: 5px">
    <!-- 客户下拉框 -->
    <el-select
      v-model="selectedCustomer"
      placeholder="顧客"
      style="width: 200px; padding-right: 20px"
      @change="generateHandle"
    >
      <el-option
        v-for="customer in customerList"
        :key="customer.customerId"
        :label="customer.customerName"
        :value="customer.customerId"
      />
    </el-select>
    <!-- 编辑按钮 -->
    <el-button
      type="primary"
      @click="editHandle"
      :disabled="editable"
    >修正</el-button>
    <!-- 保存按钮 -->
    <el-button
      type="success"
      @click="saveHandle"
      :disabled="!editable"
    >保存</el-button>
    <!-- 导出按钮 -->
    <el-button
      type="info"
      @click="exportHandle"
      :disabled="editable"
    >出力</el-button>
    <!-- 追加一列的按钮 -->
    <el-button
      type="primary"
      @click="addColHandle"
      :disabled="!editable"
    >追加</el-button>
  </div>
  <div
    class="scrollable-table"
    v-if="showTable"
  >
    <table :style="tableWidth">
      <thead>
        <th style="width: 180px">顧客名</th>
        <th style="width: 120px">品番</th>
        <th style="width: 120px">品名</th>
        <th style="width: 120px">案件概要</th>
        <th style="width: 120px">技術者氏名</th>
        <th style="width: 120px">契約単価</th>
        <th style="width: 120px">人月</th>
        <th style="width: 120px">発注金額</th>
        <th style="width: 120px">小計（会社別）税抜</th>
        <!-- 生成日期的列 -->
        <th
          v-for="(one, index) in dataList[0].dateList"
          :key="index"
          style="width: 120px"
        >
          <div v-if="editable">
            <el-input
              v-model="one.name"
              style="white-space: pre-wrap; width: 130px;"
            ></el-input>
            &nbsp;
            <el-button
              type="text"
              @click="deleteDateHandle(index)"
            >消去</el-button>
          </div>
          <div v-else>
            {{ one.name }}
          </div>
        </th>
        <th style="width: 180px">案件責任者</th>
        <th style="width: 200px">備考欄</th>
      </thead>
      <tbody>
        <tr
          v-for="(row, index) in dataList"
          :key="index"
        >
          <!-- 顧客名 -->
          <td
            v-if="row.countCustomer != 0"
            :rowspan="row.countCustomer"
          >
            {{ row.customerName }}
          </td>
          <!-- 品番 -->
          <td
            v-if="row.countProject != 0"
            :rowspan="row.countProject"
          >
            <textarea
              v-if="editable"
              v-model="row.productNum"
              rows="5"
            />
            <span
              v-else
              style="white-space: pre-wrap;"
            >{{ row.productNum }}</span>
          </td>
          <!-- 品名 -->
          <td
            v-if="row.countProject != 0"
            :rowspan="row.countProject"
          >
            <textarea
              type="area"
              v-if="editable"
              v-model="row.productName"
              rows="5"
            ></textarea>
            <div
              v-else
              style="white-space: pre-wrap;"
            >{{row.productName}}</div>
          </td>
          <!-- 案件概要 -->
          <td
            v-if="row.countProject != 0"
            :rowspan="row.countProject"
          >
            <textarea
              rows="5"
              v-if="editable"
              v-model="row.summary"
            />
            <span
              v-else
              style="white-space: pre-wrap;"
            >{{ row.summary }}</span>
          </td>
          <!-- 技術者氏名 -->
          <td>{{ row.name }}</td>
          <!-- 契约单价 -->
          <td>{{ row.cPrice }}</td>
          <!-- 人月 -->
          <td>
            <el-input
              v-if="editable"
              v-model="row.personMonth"
            />
            <span
              v-else
              style="white-space: pre-wrap;"
            >{{ row.personMonth }}</span>
          </td>
          <!-- 发注金额 -->
          <td
            v-if="row.countProject != 0"
            :rowspan="row.countProject"
          >
            <textarea
              rows="5"
              v-if="editable"
              v-model="row.orderAmount"
            />
            <span
              v-else
              style="white-space: pre-wrap;"
            >{{ row.orderAmount }}</span>
          </td>
          <!-- 小计 -->
          <td
            v-if="row.countCustomer != 0"
            :rowspan="row.countCustomer"
          >
            {{ row.orderAmountSum }}
          </td>
          <!-- 若干日期 -->
          <td
            v-for="(date, index) in row.dateList"
            :key="index"
            v-show="row.countProject != 0"
            :rowspan="row.countProject"
          >
            <el-date-picker
              v-if="editable"
              v-model="date.monthDate"
              type="date"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
            <span v-else>{{ date.monthDate }}</span>
          </td>
          <!-- 案件责任者 -->
          <td
            v-if="row.countProject != 0"
            :rowspan="row.countProject"
          >
            {{ row.principal }}
          </td>
          <!-- 備考 -->
          <td
            v-if="row.countProject != 0"
            :rowspan="row.countProject"
          >
            <textarea
              v-if="editable"
              v-model="row.remark"
              rows="5"
            ></textarea>
            <span v-else>{{ row.remark }}</span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import ExcelJS from "exceljs";
import { saveAs } from "file-saver";

export default {
  data() {
    return {
      date: "",
      time: "",
      dataList: [
        {
          customerName: null,
          dateList: [],
        },
      ],
      customerList: [],
      selectedCustomer: "",
      editable: false,
    };
  },
  computed: {
    tableName() {
      var customerName = "";
      if (this.selectedCustomer != null && this.selectedCustomer != "") {
        customerName = this.customerList.find(
          (one) => one.customerId == this.selectedCustomer
        ).customerName;
      }
      return customerName + "管理表（" + this.time + "）";
    },
    tableWidth() {
      var width = 1520 + 120 * this.dataList[0].dateList.length;
      return "width: " + width + "px;";
    },
    showTable() {
      if (this.dataList.length == 0) {
        return false;
      } else if (this.dataList[0].customerName == null) {
        return false;
      } else {
        return true;
      }
    },
  },
  methods: {
    deleteDateHandle(index) {
      let that = this;
      that.dataList.forEach((one) => {
        one.dateList.splice(index, 1);
      });
    },
    init(time) {
      let that = this;
      this.time = time;
      this.getCustomerList();
    },
    // 返回
    backToMenu() {
      this.$emit("backToMenu");
    },
    editHandle() {
      this.editable = true;
    },
    saveHandle() {
      let that = this;

      var send = true;
      that.dataList[0].dateList.forEach((oneDate, index) => {
        if (oneDate.name == null || oneDate.name == "") {
          send = false;
        }
      });

      if (!send) {
        ElMessage({
          message: "列名を空にすることはできません。",
          type: "warning",
          duration: 1200,
        });
        return;
      } else {
        that.$httpV2(
          "/table/projectMonthList",
          "PUT",
          that.dataList,
          false,
          function (resp) {}
        );
        this.editable = false;
        that.getDataList();
      }
    },
    exportHandle() {
      let that = this;
      // ファイル名を付ける（this.tableName-hh-mm.xlsxの形で）
      let now = new Date();
      let hh = now.getHours().toString().padStart(2, "0");
      let mm = now.getMinutes().toString().padStart(2, "0");
      let fileName = `${this.tableName}_${hh}_${mm}.xlsx`;

      // エクセルファイルを生成
      const workbook = new ExcelJS.Workbook();
      const worksheet = workbook.addWorksheet(that.tableName);

      // HTMLテーブルのヘッダーとデータを取得
      const { headers, data, merges } = getCompanyWorkTableData(
        ".scrollable-table table thead",
        ".scrollable-table table tbody"
      );

      // ヘッダーを設定
      worksheet.columns = headers.map((header) => ({ header, key: header }));

      worksheet.getRow(1).height = 30;
      worksheet.getColumn(1).width = 25;
      for (var i = 2; i <= 9; i++) {
        worksheet.getColumn(i).width = 15;
      }
      for (var i = 10; i < 10 + that.dataList[0].dateList.length; i++) {
        worksheet.getColumn(i).width = 15;
      }
      worksheet.getColumn(10 + that.dataList[0].dateList.length).width = 15;
      worksheet.getColumn(11 + that.dataList[0].dateList.length).width = 25;

      // セルのスタイルとデータを設定
      data.forEach((tblData) => {
        const row = {};
        tblData.forEach((cellData, index) => {
          row[headers[index]] = cellData;
        });
        const excelRow = worksheet.addRow(row);

        tblData.forEach((_, index) => {
          const cell = excelRow.getCell(index + 1);
          cell.alignment = {
            vertical: "middle",
            horizontal: "center",
            wrapText: true,
          };
        });
      });

      // マージセルを対応
      merges.forEach((merge) => {
        try {
          worksheet.mergeCells(
            merge.rowIndex + 2,
            merge.colIndex + 1,
            merge.rowIndex + merge.rowSpan + 1,
            merge.colIndex + merge.colSpan
          );
        } catch (e) {
          console.warn(
            `Cannot merge cells at row ${merge.rowIndex + 2}, col ${
              merge.colIndex + 1
            }`
          );
        }
      });

      for (var row = 1; row <= that.dataList.length + 1; row++) {
        for (var col = 1; col <= 11 + that.dataList[0].dateList.length; col++) {
          const cell = worksheet.getCell(row, col);
          if (row == 1) {
            cell.fill = {
              type: "pattern",
              pattern: "solid",
              fgColor: { argb: "c5d9f1" },
            };
          }
          cell.font = {
            name: "Yu Gothic", // 字体名称
            size: 11, // 字号
          };
          cell.alignment = {
            horizontal: "center", // 水平居中
            vertical: "middle", // 垂直居中
            wrapText: true, // 启用自动换行
          };
          cell.border = {
            top: { style: "thin", color: { argb: "FF000000" } }, // 上边框
            left: { style: "thin", color: { argb: "FF000000" } }, // 左边框
            bottom: { style: "thin", color: { argb: "FF000000" } }, // 下边框
            right: { style: "thin", color: { argb: "FF000000" } }, // 右边框
          };
        }
      }

      // 行高
      worksheet.getRow(1).height = 60;
      for (var row = 2; row <= that.dataList.length + 1; row++) {
        worksheet.getRow(row).height = 30;
      }

      // エクセルファイルを出力
      workbook.xlsx
        .writeBuffer()
        .then((buffer) => {
          const blob = new Blob([buffer], {
            type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          });
          saveAs(blob, that.tableName);
        })
        .catch((error) => {
          console.error(
            "作業管理画面でエクセルファイル出力の際にエラー発生しました。:",
            error
          );
        });
    },
    generateHandle() {
      this.getDataList();
    },
    addColHandle() {
      let that = this;
      // 点击追加一列
      if (that.dataList.length == 0) return;
      that.dataList.forEach((one) => {
        one.dateList.push({
          name: "",
          monthDate: "",
        });
      });
    },
    getCustomerList() {
      let that = this;
      that.$httpV2("/protech/customer", "GET", null, false, function (resp) {
        that.customerList = resp.result;
      });
    },
    getDataList() {
      let that = this;
      that.$httpV2(
        "/table/projectMonthList",
        "GET",
        {
          dateMonth: that.time,
          customerId: that.selectedCustomer,
        },
        false,
        function (resp) {
          that.dataList = resp.result;
          if (that.dataList.length == 0) {
            ElMessage({
              message: "選択した顧客にプロジェクトがありません。",
              duration: 1200,
              type: "warning",
            });
          }
        }
      );
    },
  },
};

// 作業管理票TBLを取得
function getCompanyWorkTableData(headerSelector, dataSelector) {
  const headers = [];
  const data = [];
  const merges = [];
  const headerElement = document.querySelector(headerSelector);
  const tableElement = document.querySelector(dataSelector);

  if (headerElement) {
    headers.push(
      ...Array.from(headerElement.querySelectorAll("th")).map((div) =>
        div.innerText.trim()
      )
    );
  }

  if (tableElement) {
    const colOffsetTracker = [];

    tableElement.querySelectorAll("tbody tr").forEach((row, rowIndex) => {
      const rowData = [];
      let currentColOffset = 0;

      row.querySelectorAll("td").forEach((td, colIndex) => {
        const text = td.innerText.trim();
        const rowSpan = td.rowSpan || 1;
        const colSpan = td.colSpan || 1;

        // マージセルを保存するため、tracker配列を作成
        if (!colOffsetTracker[rowIndex]) {
          colOffsetTracker[rowIndex] = [];
        }

        // 上の行にマージセルがあれば、currentColOffsetをインクリメントする
        while (colOffsetTracker[rowIndex][colIndex + currentColOffset]) {
          currentColOffset++;
        }

        const adjustedColIndex = colIndex + currentColOffset;

        rowData[adjustedColIndex] = text;

        // もしrowSpan、colSpanの範囲にあれば、セルの位置をtrackerに付ける
        for (let r = 0; r < rowSpan; r++) {
          for (let c = 0; c < colSpan; c++) {
            if (!colOffsetTracker[rowIndex + r]) {
              colOffsetTracker[rowIndex + r] = [];
            }
            colOffsetTracker[rowIndex + r][adjustedColIndex + c] = true;
          }
        }

        // merges配列を登録
        if (rowSpan > 1 || colSpan > 1) {
          merges.push({
            rowIndex: rowIndex,
            colIndex: adjustedColIndex,
            rowSpan: rowSpan,
            colSpan: colSpan,
          });
        }
      });

      data.push(rowData);
    });
  }

  return { headers, data, merges };
}
</script>

<style scoped>
h3 {
  display: flex;
  align-items: center; /* 垂直对齐 */
}

h3 .icon-svg {
  margin-right: 8px; /* 图标和文字的左右间距 */
}

.back-icon {
  width: 35px; /* 图标大小 */
  height: 35px; /* 图标大小 */
}

.scrollable-table {
  width: 100%;
  overflow-x: auto; /* 使表格可以左右滚动 */
}

thead th {
  height: 40px;
  background-color: #f5f7fa;
  text-align: center;
  border: 1px solid black;
}

tbody td {
  height: 40px;
  white-space: nowrap;
  padding: 0;
  border: 1px solid black;
  text-align: center;
}

table {
  border-collapse: collapse;
}
</style>