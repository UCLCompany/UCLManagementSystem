<template>
	<el-form :inline="true" :model="dataForm" ref="dataForm">
		<el-form-item prop="partnerCompanyName">
			<el-select
				v-model="dataForm.partnerCompanyName"
				filterable
				clearable
				placeholder="会社名"
				:filter-method="filterCompanyName"
				@visible-change="handleVisibleChange"
				style="width: 170px;"
			>
				<el-option
					v-for="item in filteredCompanyNames"
					:key="item.partnerCompanyId"
					:label="item.partnerCompanyName"
					:value="item.partnerCompanyName"
				/>
			</el-select>
		</el-form-item>
		<el-form-item>
			<el-button size="medium" type="primary" @click="searchHandle()">検索</el-button>
			<el-button size="medium" type="primary" @click="addHandle()">追加</el-button>
			<el-button size="medium" type="danger" @click="deleteHandle()">一括削除</el-button>
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
		<el-table-column
			header-align="center"
			align="center"
			width="100"
			label="操作"
		>
			<template #default="scope">
				<el-button
					type="text"
					@click="updateHandle(scope.row.partnerCompanyId)"
				>
					変更
				</el-button>
				<el-button
					type="text"
					@click="deleteHandle(scope.row.partnerCompanyId)"
				>
					削除
				</el-button>
			</template>
		</el-table-column>
		<!--   No.   -->
		<el-table-column
			type="index"
			header-align="center"
			align="center"
			width="60"
			label="No."
		>
			<template #default="scope">
				<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
			</template>
		</el-table-column>

		<el-table-column
			header-align="center"
			align="center"
			width="150"
			label="会社名"
			prop="partnerCompanyName"
		/>
		<el-table-column
			header-align="center"
			align="center"
			width="150"
			label="〒郵便番号"
			prop="partnerpostalCode"
		/>
		<el-table-column
			header-align="center"
			align="center"
			width="150"
			label="住所"
			prop="partnerCompanyAddress"
		/>
		<el-table-column
			header-align="center"
			align="center"
			width="150"
			label="支払い条件"
			prop="termsofPayment"
		/>
		<el-table-column
			header-align="center"
			align="center"
			width="150"
			label="代表名"
			prop="representativeName"
		/>
		<el-table-column
			header-align="center"
			align="center"
			width="150"
			label="下請締結日"
			prop="coopDate"
		/>
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
		@refreshDataList="loadCompanyList"
	></add-or-update>
</template>

<script>
import AddOrUpdate from "./company-add-or-update.vue";
import { ElMessage } from "element-plus";

export default {
	components: { AddOrUpdate },
	data: function () {
		return {
			token: localStorage.getItem("token"),
			action: `${this.$baseUrl}/file/upload`,
			selectedOption: null,
			// 公司下拉列表
			companyList: [],

			// 提交表单时的数据
			dataForm: {
				partnerCompanyName: null,
			},
			// 过滤后的公司名称列表
			filteredCompanyNames: [],

			// 存放查询到的公司数据
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,

			// 加载进度条
			dataListLoading: false,

			columns: [
				{
					key: 0,
					label: "会社名",
					prop: "partnerCompanyName",
					visible: true,
					sortable: false,
				},
				{
					key: 1,
					label: "〒郵便番号",
					prop: "partnerpostalCode",
					visible: true,
					sortable: false,
				},
				{
					key: 2,
					label: "住所",
					prop: "partnerCompanyAddress",
					visible: true,
					sortable: false,
				},
				{
					key: 3,
					label: "支払い条件",
					prop: "termsofPayment",
					visible: true,
					sortable: false,
				},
				{
					key: 4,
					label: "代表名",
					prop: "representativeName",
					visible: true,
					sortable: true,
				},
				{
					key: 5,
					label: "下請締結日",
					prop: "coopDate",
					visible: true,
					sortable: false,
				},
			],

			dataListSelections: [],
		};
	},
  // computed: {
  //   visibleColumns() {
  //     const manualProps = [
  //       "partnerCompanyName",
  //       "partnerpostalCode",
  //       "partnerCompanyAddress",
  //       "termsofPayment",
  //       "representativeName",
  //       "coopDate",
  //     ];
  //
  //     // 过滤掉与手动定义列重复的项
  //     return this.columns.filter((column) => !manualProps.includes(column.prop));
  //   },
  // },
	methods: {
		loadCompanyList() {
			let that = this;
			that.dataListLoading = true;
			let data = {
				page: that.pageIndex,
				length: that.pageSize,
				partnerCompanyName:
            that.dataForm.partnerCompanyName == null ? "" : that.dataForm.partnerCompanyName
			};
			that.$httpV2("/company/getpartnerById", "GET", data, true, function (resp) {
				let result = resp.result;
				that.dataList = result.list;
				that.totalCount = result.totalCount;
				that.dataListLoading = false;
			});
		},
		filterCompanyName(query) {
			if (query !== "") {
				this.filteredCompanyNames = this.companyList.filter((item) => {
					return item.partnerCompanyName.toLowerCase().includes(query.toLowerCase());
				});
			} else {
				this.filteredCompanyNames = this.companyList;
			}
		},
		handleVisibleChange(visible) {
			if (visible) {
				this.filteredCompanyNames = this.companyList;
			}
		},
		loadCompanyData() {
			let that = this;
			that.$httpV2("/company/company", "GET", null, true, function (resp) {
				let result = resp.result;
				that.companyList = result;
			});
		},
		selectionChangeHandle(val) {
			this.dataListSelections = val;
		},
		getRowKeys(row) {
			return row.partnerCompanyId;
		},
		sizeChangeHandle(val) {
			this.pageSize = val;
			this.pageIndex = 1;
			this.loadCompanyList();
		},
		currentChangeHandle(val) {
			this.pageIndex = val;
			this.loadCompanyList();
		},

		searchHandle() {
			this.pageIndex = 1;
			this.loadCompanyList();
		},
		addHandle() {
			this.$refs.addOrUpdate.init();
		},
		updateHandle: function (id) {
			const companyInfo = this.dataList.find((company) => company.partnerCompanyId === id);
			this.$refs.addOrUpdate.init(companyInfo);
		},
		deleteHandle(id) {
			console.log("id: " + id);
			let that = this;
			let ids = id
				? [id]
				: that.dataListSelections.map((item) => {
						return item.partnerCompanyId;
				  });
			if (ids.length == 0) {
				ElMessage({
					message: "レコードが選択されていません。",
					type: "warning",
					duration: 1200,
				});
			} else {
				ElMessageBox.confirm(
					"レコード削除してよろしいでしょうか？",
					"警告",
					{
						confirmButtonText: "確定",
						cancelButtonText: "キャンセル",
						type: "warning",
					}
				).then(() => {
					that.$httpV2("/company/Delete", "DELETE", { ids: ids }, true, function () {
						ElMessage({
							message: "削除完了しました。",
							type: "success",
							duration: 1200,
							onClose: () => {
								that.loadCompanyList();
							},
						});
					});
				});
			}
		},
	},
	created() {
		this.loadCompanyList();
		this.loadCompanyData();
	},
};
</script>
