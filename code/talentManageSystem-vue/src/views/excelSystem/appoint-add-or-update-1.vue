<template>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
    v-model="visible"
    width="90%"
    top="1%"
  >
    <el-scrollbar
      height="900px"
      v-if="selected === 'insert'"
    >
      <el-form
        :model="dataForm"
        ref="dataForm"
        :rules="dataRule"
      >
        <el-row :gutter="20">
          <el-col :span="4">
            <el-form-item
              label=" "
              prop="picture"
            >
              <el-image
                style="width: 120px; height: 153px; border: 2px solid #D3EDFA; border-radius: 5px;"
                :src="dataForm.picture"
                fit="cover"
              >
                <template #error>
                  <div class="error-img">
                    <el-icon>
                      <Picture />
                    </el-icon>
                  </div>
                </template>
              </el-image>
            </el-form-item>
          </el-col>
          <el-col :span="20">
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="技術者氏名："
                  prop="technicianName"
                >
                  <el-select
                    v-model="dataForm.technicianName"
                    filterable
                    clearable
                    placeholder="選択をお願いします"
                    :filter-method="filterTechName"
                    @visible-change="handleVisibleChange"
                    @change="handleTechChange"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in filteredTechNames"
                      :key="item.technicianId"
                      :label="item.technicianName"
                      :value="item.technicianId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="所属（会社名）："
                  prop="companyName"
                >
                  <el-input
                    v-model="dataForm.companyName"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="カタカナ："
                  prop="katakana"
                >
                  <el-input
                    v-model="dataForm.katakana"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="営業担当者："
                  prop="representative"
                >
                  <el-input
                    v-model="dataForm.representative"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="ローマ字："
                  prop="roman"
                >
                  <el-input
                    v-model="dataForm.roman"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">

                <!-- 开始 -->
                <el-form-item
                  label="契約書："
                  prop="contract"
                >
                  <el-upload
                    :action="action"
                    :headers="{ token: token }"
                    with-credentials="true"
                    :on-success="updateFileSuccess"
                    :on-error="updateFileError"
                    :show-file-list="false"
                  >
                    <el-button type="primary">アップロード</el-button>
                  </el-upload>
                  <br>
                  <!-- 显示传上去的文件 -->
                  <span
                    v-for="(oneFile,index) in dataForm.contract"
                    :key="index"
                  >
                    &nbsp;&nbsp;&nbsp;{{ oneFile.substring(oneFile.lastIndexOf('/') + 1) }} &nbsp; <el-button
                      type="text"
                      @click="deleteFileHandle(index)"
                    >消去</el-button>
                  </span>
                </el-form-item>

                <!-- 结束 -->
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="生年月日："
                  prop="birthday"
                >
                  <el-input
                    v-model="dataForm.birthday"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="契約書番号："
                  prop="contractNum"
                >
                  <el-input
                    v-model="dataForm.contractNum"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="顧客名："
                  prop="customerName"
                >
                  <el-select
                    v-model="dataForm.customerName"
                    placeholder="選択をお願いします"
                    @change="selectCustId"
                    @clear="custHandleClear"
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
              </el-col>
              <el-col :span="10">
                <el-form-item
                  label="月こと契約書種類："
                  prop="contractType"
                >
                  <el-select
                    v-model="dataForm.contractType"
                    clearable
                    style="width: 100%"
                    @change="testadd()"
                  >
                    <el-option
                      label="注文書"
                      value=1
                    >注文書</el-option>
                    <el-option
                      label="出向個別契約書"
                      value=2
                    >出向個別契約書</el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="プロジェクト名："
                  prop="projectName"
                >
                  <el-select
                    v-model="dataForm.projectName"
                    placeholder="選択をお願いします"
                    clearable
                    @change="handleProChange"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in filteredProNames"
                      :key="item.projectId"
                      :label="item.projectName"
                      :value="item.projectId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="10">
                <el-form-item
                  label="お支払方法"
                  prop="termsOfPayment"
                >
                  <el-input
                    v-model="dataForm.termsOfPayment"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10"></el-col>
              <el-col :span="10">
                <el-form-item
                  label="下請締結日"
                  prop="coopDate"
                >
                  <el-date-picker
                    v-model="dataForm.coopDate"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="案件責任者（顧客先）："
                  prop="principal"
                >
                  <el-input
                    v-model="dataForm.principal"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請単価："
                  prop="hPrice"
                >
                  <el-input
                    v-model="dataForm.hPrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="案件責任者所属（顧客先）："
                  prop="principalCompany"
                >
                  <el-input
                    v-model="dataForm.principalCompany"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請精算幅（下限）："
                  prop="hLowerHours"
                >
                  <el-input
                    v-model="dataForm.hLowerHours"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="形態："
                  prop="status"
                >
                  <el-select
                    v-model="dataForm.status"
                    clearable
                    placeholder="選択をお願いします"
                  >
                    <el-option
                      label="準委任"
                      value="準委任"
                    />
                    <el-option
                      label="派遣"
                      value="派遣"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請精算幅（上限）："
                  prop="hHigherHours"
                >
                  <el-input
                    v-model="dataForm.hHigherHours"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約単価（顧客先）："
                  prop="cPrice"
                >
                  <el-input
                    v-model="dataForm.cPrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請減単金："
                  prop="hReductPrice"
                >
                  <el-input
                    v-model="dataForm.hReductPrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約开始月："
                  prop="cBeginMonth"
                >
                  <el-date-picker
                    v-model="dataForm.cBeginMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    clearable
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請増単金："
                  prop="hIncreasePrice"
                >
                  <el-input
                    v-model="dataForm.hIncreasePrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約结束月："
                  prop="cRealEndMonth"
                >
                  <el-date-picker
                    v-model="dataForm.cEndMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    clearable
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="価格変動月："
                  prop="priceMonth"
                >
                  <el-date-picker
                    v-model="dataForm.priceMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    disabled
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="退場月："
                  prop="stopMonth"
                >
                  <el-date-picker
                    v-model="dataForm.stopMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="備考："
                  prop="remark"
                >
                  <el-input
                    type="textarea"
                    rows="1"
                    v-model="dataForm.remark"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約精算幅（下限）："
                  prop="cLowerHours"
                >
                  <el-input
                    v-model="dataForm.cLowerHours"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約精算幅（上限）："
                  prop="cHigherHours"
                >
                  <el-input
                    v-model="dataForm.cHigherHours"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約減単金："
                  prop="cReductPrice"
                >
                  <el-input
                    v-model="dataForm.cReductPrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約増単金："
                  prop="cIncreasePrice"
                >
                  <el-input
                    v-model="dataForm.cIncreasePrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="1日標準稼働時間："
                  prop="standardHours"
                >
                  <el-input
                    v-model="dataForm.standardHours"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
        <!--        <el-row :gutter="20" style="position: absolute; top: 18%; left: 4%">-->
        <!--          <el-col :span="11">-->
        <!--            <el-form-item>-->
        <!--              <el-button @click="resetForm">清空</el-button>-->
        <!--            </el-form-item>-->
        <!--          </el-col>-->
        <!--        </el-row>-->
      </el-form>
    </el-scrollbar>

    <el-scrollbar
      height="900px"
      v-if="selected === 'modify'"
    >
      <el-form
        :model="dataForm"
        ref="dataForm"
        :rules="dataRule"
      >
        <el-row :gutter="20">
          <el-col :span="4">
            <el-form-item
              label=" "
              prop="picture"
            >
              <el-image
                style="width: 120px; height: 153px; border: 2px solid #D3EDFA; border-radius: 5px;"
                :src="dataForm.picture"
                fit="cover"
              >
                <template #error>
                  <div class="error-img">
                    <el-icon>
                      <Picture />
                    </el-icon>
                  </div>
                </template>
              </el-image>
            </el-form-item>
          </el-col>
          <el-col :span="20">
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="技術者氏名："
                  prop="technicianName"
                >
                  <el-select
                    v-model="dataForm.technicianName"
                    filterable
                    disabled
                    placeholder="選択をお願いします"
                    :filter-method="filterTechName"
                    @visible-change="handleVisibleChange"
                    @change="handleTechChange"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in filteredTechNames"
                      :key="item.technicianId"
                      :label="item.technicianName"
                      :value="item.technicianId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="所属（会社名）："
                  prop="companyName"
                >
                  <el-input
                    v-model="dataForm.companyName"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="カタカナ："
                  prop="katakana"
                >
                  <el-input
                    v-model="dataForm.katakana"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="営業担当者："
                  prop="representative"
                >
                  <el-input
                    v-model="dataForm.representative"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="ローマ字："
                  prop="roman"
                >
                  <el-input
                    v-model="dataForm.roman"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <!-- 开始 -->
                <el-form-item
                  label="契約書："
                  prop="contract"
                >
                  <el-upload
                    :action="action"
                    :headers="{ token: token }"
                    with-credentials="true"
                    :on-success="updateFileSuccess"
                    :on-error="updateFileError"
                    :show-file-list="false"
                  >
                    <el-button type="primary">アップロード</el-button>
                  </el-upload>
                  <br>
                  <!-- 显示传上去的文件 -->
                  <span
                    v-for="(oneFile,index) in dataForm.contract"
                    :key="index"
                  >
                    &nbsp;&nbsp;&nbsp;{{ oneFile.substring(oneFile.lastIndexOf('/') + 1) }} &nbsp; <el-button
                      type="text"
                      @click="deleteFileHandle(index)"
                    >消去</el-button>
                  </span>
                </el-form-item>

                <!-- 结束 -->
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="生年月日："
                  prop="birthday"
                >
                  <el-input
                    v-model="dataForm.birthday"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="契約書番号："
                  prop="contractNum"
                >
                  <el-input
                    v-model="dataForm.contractNum"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="顧客名："
                  prop="customerName"
                >
                  <el-select
                    v-model="dataForm.customerName"
                    placeholder="選択をお願いします"
                    @change="selectCustId"
                    @clear="custHandleClear"
                    disabled
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
              </el-col>
              <el-col :span="10">
                <el-form-item
                  label="月こと契約書種類："
                  prop="contractType"
                >
                  <el-select
                    v-model="dataForm.contractType"
                    clearable
                    style="width: 100%"
                    @change="testmodify()"
                  >
                    <el-option
                      label="注文書"
                      value=1
                    >注文書
                    </el-option>
                    <el-option
                      label="出向個別契約書"
                      value=2
                    >出向個別契約書
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="プロジェクト名："
                  prop="projectName"
                >
                  <el-select
                    v-model="dataForm.projectName"
                    placeholder="選択をお願いします"
                    disabled
                    @change="handleProChange"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in filteredProNames"
                      :key="item.projectId"
                      :label="item.projectName"
                      :value="item.projectId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="10">
                <el-form-item
                  label="お支払方法"
                  prop="termsOfPayment"
                >
                  <el-input
                    v-model="dataForm.termsOfPayment"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10"></el-col>
              <el-col :span="10">
                <el-form-item
                  label="下請締結日"
                  prop="coopDate"
                >
                  <el-date-picker
                    v-model="dataForm.coopDate"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="案件責任者（顧客先）："
                  prop="principal"
                >
                  <el-input
                    v-model="dataForm.principal"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請単価："
                  prop="hPrice"
                >
                  <el-input
                    v-model="dataForm.hPrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="案件責任者所属（顧客先）："
                  prop="principalCompany"
                >
                  <el-input
                    v-model="dataForm.principalCompany"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請精算幅（下限）："
                  prop="hLowerHours"
                >
                  <el-input
                    v-model="dataForm.hLowerHours"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="形態："
                  prop="status"
                >
                  <el-select
                    v-model="dataForm.status"
                    clearable
                    placeholder="選択をお願いします"
                  >
                    <el-option
                      label="準委任"
                      value="準委任"
                    />
                    <el-option
                      label="派遣"
                      value="派遣"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請精算幅（上限）："
                  prop="hHigherHours"
                >
                  <el-input
                    v-model="dataForm.hHigherHours"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約単価（顧客先）："
                  prop="cPrice"
                >
                  <el-input
                    v-model="dataForm.cPrice"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請減単金："
                  prop="hReductPrice"
                >
                  <el-input
                    v-model="dataForm.hReductPrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約开始月："
                  prop="cRealBeginMonth"
                >
                  <el-date-picker
                    v-model="dataForm.cBeginMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    clearable
                    :disabled="dataForm.cBeginMonth !== null && dataForm.cBeginMonth !== ''"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請増単金："
                  prop="hIncreasePrice"
                >
                  <el-input
                    v-model="dataForm.hIncreasePrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約结束月："
                  prop="cRealEndMonth"
                >
                  <el-date-picker
                    v-model="dataForm.cEndMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    clearable
                    :disabled="dataForm.cEndMonth !== null && dataForm.cEndMonth !== ''"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="价格变动月："
                  prop="priceMonth"
                >
                  <el-date-picker
                    v-model="dataForm.priceMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    disabled
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="退場月："
                  prop="stopMonth"
                >
                  <el-date-picker
                    v-model="dataForm.stopMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="備考："
                  prop="remark"
                >
                  <el-input
                    type="textarea"
                    rows="1"
                    v-model="dataForm.remark"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約精算幅（下限）："
                  prop="cLowerHours"
                >
                  <el-input
                    v-model="dataForm.cLowerHours"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約精算幅（上限）："
                  prop="cHigherHours"
                >
                  <el-input
                    v-model="dataForm.cHigherHours"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約減単金："
                  prop="cReductPrice"
                >
                  <el-input
                    v-model="dataForm.cReductPrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約増単金："
                  prop="cIncreasePrice"
                >
                  <el-input
                    v-model="dataForm.cIncreasePrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="1日標準稼働時間："
                  prop="standardHours"
                >
                  <el-input
                    v-model="dataForm.standardHours"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </el-form>
    </el-scrollbar>

    <el-scrollbar
      height="900px"
      v-if="selected === 'exit'"
    >
      <el-form
        :model="dataForm"
        ref="dataForm"
        :rules="dataRule"
      >
        <el-row :gutter="20">
          <el-col :span="4">
            <el-form-item
              label=" "
              prop="picture"
            >
              <el-image
                style="width: 120px; height: 153px; border: 2px solid #D3EDFA; border-radius: 5px;"
                :src="dataForm.picture"
                fit="cover"
              >
                <template #error>
                  <div class="error-img">
                    <el-icon>
                      <Picture />
                    </el-icon>
                  </div>
                </template>
              </el-image>
            </el-form-item>
          </el-col>
          <el-col :span="20">
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="技術者氏名："
                  prop="technicianName"
                >
                  <el-select
                    v-model="dataForm.technicianName"
                    filterable
                    disabled
                    placeholder="選択をお願いします"
                    :filter-method="filterTechName"
                    @visible-change="handleVisibleChange"
                    @change="handleTechChange"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in filteredTechNames"
                      :key="item.technicianId"
                      :label="item.technicianName"
                      :value="item.technicianId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="所属（会社名）："
                  prop="companyName"
                >
                  <el-input
                    v-model="dataForm.companyName"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="カタカナ："
                  prop="katakana"
                >
                  <el-input
                    v-model="dataForm.katakana"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="営業担当者："
                  prop="representative"
                >
                  <el-input
                    v-model="dataForm.representative"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="ローマ字："
                  prop="roman"
                >
                  <el-input
                    v-model="dataForm.roman"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <!-- 开始 -->
                <el-form-item
                  label="契約書："
                  prop="contract"
                >
                  <el-upload
                    :action="action"
                    :headers="{ token: token }"
                    with-credentials="true"
                    :on-success="updateFileSuccess"
                    :on-error="updateFileError"
                    :show-file-list="false"
                  >
                    <el-button type="primary">アップロード</el-button>
                  </el-upload>
                  <br>
                  <!-- 显示传上去的文件 -->
                  <span
                    v-for="(oneFile,index) in dataForm.contract"
                    :key="index"
                  >
                    &nbsp;&nbsp;&nbsp;{{ oneFile.substring(oneFile.lastIndexOf('/') + 1) }} &nbsp; <el-button
                      type="text"
                      @click="deleteFileHandle(index)"
                    >消去</el-button>
                  </span>
                </el-form-item>

                <!-- 结束 -->
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="生年月日："
                  prop="birthday"
                >
                  <el-input
                    v-model="dataForm.birthday"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="契約書番号："
                  prop="contractNum"
                >
                  <el-input
                    v-model="dataForm.contractNum"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="顧客名："
                  prop="customerName"
                >
                  <el-select
                    v-model="dataForm.customerName"
                    placeholder="選択をお願いします"
                    @change="selectCustId"
                    @clear="custHandleClear"
                    disabled
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
              </el-col>
              <el-col :span="10">
                <el-form-item
                  label="月こと契約書種類："
                  prop="contractType"
                >
                  <el-select
                    v-model="dataForm.contractType"
                    clearable
                    style="width: 100%"
                  >
                    <el-option
                      label="注文書"
                      value=1
                    >注文書</el-option>
                    <el-option
                      label="出向個別契約書"
                      value=2
                    >出向個別契約書</el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="プロジェクト名："
                  prop="projectName"
                >
                  <el-select
                    v-model="dataForm.projectName"
                    placeholder="選択をお願いします"
                    disabled
                    @change="handleProChange"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in filteredProNames"
                      :key="item.projectId"
                      :label="item.projectName"
                      :value="item.projectId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="10">
                <el-form-item
                  label="お支払方法"
                  prop="termsOfPayment"
                >
                  <el-input
                    v-model="dataForm.termsOfPayment"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10"></el-col>
              <el-col :span="10">
                <el-form-item
                  label="下請締結日"
                  prop="coopDate"
                >
                  <el-date-picker
                    v-model="dataForm.coopDate"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="案件責任者（顧客先）："
                  prop="principal"
                >
                  <el-input
                    v-model="dataForm.principal"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="10">
                <el-form-item
                  label="下請単価："
                  prop="hPrice"
                >
                  <el-input
                    v-model="dataForm.hPrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="案件責任者所属（顧客先）："
                  prop="principalCompany"
                >
                  <el-input
                    v-model="dataForm.principalCompany"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請精算幅（下限）："
                  prop="hLowerHours"
                >
                  <el-input
                    v-model="dataForm.hLowerHours"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="形態："
                  prop="status"
                >
                  <el-select
                    v-model="dataForm.status"
                    disabled
                    placeholder="選択をお願いします"
                  >
                    <el-option
                      label="準委任"
                      value="準委任"
                    />
                    <el-option
                      label="派遣"
                      value="派遣"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請精算幅（上限）："
                  prop="hHigherHours"
                >
                  <el-input
                    v-model="dataForm.hHigherHours"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約単価（顧客先）："
                  prop="cPrice"
                >
                  <el-input
                    v-model="dataForm.cPrice"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請減単金："
                  prop="hReductPrice"
                >
                  <el-input
                    v-model="dataForm.hReductPrice"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約开始月："
                  prop="cRealBeginMonth"
                >
                  <el-date-picker
                    v-model="dataForm.cBeginMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    disabled
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請増単金："
                  prop="hIncreasePrice"
                >
                  <el-input
                    v-model="dataForm.hIncreasePrice"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約结束月："
                  prop="cRealEndMonth"
                >
                  <el-date-picker
                    v-model="dataForm.cEndMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    disabled
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="价格变动月："
                  prop="priceMonth"
                >
                  <el-date-picker
                    v-model="dataForm.priceMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    disabled
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="退場月："
                  prop="stopMonth"
                >
                  <el-date-picker
                    v-model="dataForm.stopMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="備考："
                  prop="remark"
                >
                  <el-input
                    type="textarea"
                    rows="1"
                    v-model="dataForm.remark"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約精算幅（下限）："
                  prop="cLowerHours"
                >
                  <el-input
                    v-model="dataForm.cLowerHours"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約精算幅（上限）："
                  prop="cHigherHours"
                >
                  <el-input
                    v-model="dataForm.cHigherHours"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約減単金："
                  prop="cReductPrice"
                >
                  <el-input
                    v-model="dataForm.cReductPrice"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約増単金："
                  prop="cIncreasePrice"
                >
                  <el-input
                    v-model="dataForm.cIncreasePrice"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="1日標準稼働時間："
                  prop="standardHours"
                >
                  <el-input
                    v-model="dataForm.standardHours"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </el-form>
    </el-scrollbar>

    <el-scrollbar
      height="900px"
      v-if="selected === 'priceChange'"
    >
      <el-form
        :model="dataForm"
        ref="dataForm"
        :rules="dataRule"
      >
        <el-row :gutter="20">
          <el-col :span="4">
            <el-form-item
              label=" "
              prop="picture"
            >
              <el-image
                style="width: 120px; height: 153px; border: 2px solid #D3EDFA; border-radius: 5px;"
                :src="dataForm.picture"
                fit="cover"
              >
                <template #error>
                  <div class="error-img">
                    <el-icon>
                      <Picture />
                    </el-icon>
                  </div>
                </template>
              </el-image>
            </el-form-item>
          </el-col>
          <el-col :span="20">
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="技術者氏名："
                  prop="technicianName"
                >
                  <el-select
                    v-model="dataForm.technicianName"
                    filterable
                    disabled
                    placeholder="選択をお願いします"
                    :filter-method="filterTechName"
                    @visible-change="handleVisibleChange"
                    @change="handleTechChange"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in filteredTechNames"
                      :key="item.technicianId"
                      :label="item.technicianName"
                      :value="item.technicianId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="所属（会社名）："
                  prop="companyName"
                >
                  <el-input
                    v-model="dataForm.companyName"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="カタカナ："
                  prop="katakana"
                >
                  <el-input
                    v-model="dataForm.katakana"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="営業担当者："
                  prop="representative"
                >
                  <el-input
                    v-model="dataForm.representative"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="ローマ字："
                  prop="roman"
                >
                  <el-input
                    v-model="dataForm.roman"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <!-- 开始 -->
                <el-form-item
                  label="契約書："
                  prop="contract"
                >
                  <el-upload
                    :action="action"
                    :headers="{ token: token }"
                    with-credentials="true"
                    :on-success="updateFileSuccess"
                    :on-error="updateFileError"
                    :show-file-list="false"
                  >
                    <el-button type="primary">アップロード</el-button>
                  </el-upload>
                  <br>
                  <!-- 显示传上去的文件 -->
                  <span
                    v-for="(oneFile,index) in dataForm.contract"
                    :key="index"
                  >
                    &nbsp;&nbsp;&nbsp;{{ oneFile.substring(oneFile.lastIndexOf('/') + 1) }} &nbsp; <el-button
                      type="text"
                      @click="deleteFileHandle(index)"
                    >消去</el-button>
                  </span>
                </el-form-item>

                <!-- 结束 -->
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="生年月日："
                  prop="birthday"
                >
                  <el-input
                    v-model="dataForm.birthday"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="契約書番号："
                  prop="contractNum"
                >
                  <el-input
                    v-model="dataForm.contractNum"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="顧客名："
                  prop="customerName"
                >
                  <el-select
                    v-model="dataForm.customerName"
                    placeholder="選択をお願いします"
                    @change="selectCustId"
                    @clear="custHandleClear"
                    disabled
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
              </el-col>
              <el-col :span="10">
                <el-form-item
                  label="月こと契約書種類："
                  prop="contractType"
                >
                  <el-select
                    v-model="dataForm.contractType"
                    clearable
                    style="width: 100%"
                  >
                    <el-option
                      label="注文書"
                      value=1
                    >注文書</el-option>
                    <el-option
                      label="出向個別契約書"
                      value=2
                    >出向個別契約書</el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="プロジェクト名："
                  prop="projectName"
                >
                  <el-select
                    v-model="dataForm.projectName"
                    placeholder="選択をお願いします"
                    disabled
                    @change="handleProChange"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in filteredProNames"
                      :key="item.projectId"
                      :label="item.projectName"
                      :value="item.projectId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="10">
                <el-form-item
                  label="お支払方法"
                  prop="termsOfPayment"
                >
                  <el-input
                    v-model="dataForm.termsOfPayment"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10"></el-col>
              <el-col :span="10">
                <el-form-item
                  label="下請締結日"
                  prop="coopDate"
                >
                  <el-date-picker
                    v-model="dataForm.coopDate"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="案件責任者（顧客先）："
                  prop="principal"
                >
                  <el-input
                    v-model="dataForm.principal"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請単価："
                  prop="hPrice"
                >
                  <el-input
                    v-model="dataForm.hPrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="案件責任者所属（顧客先）："
                  prop="principalCompany"
                >
                  <el-input
                    v-model="dataForm.principalCompany"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請精算幅（下限）："
                  prop="hLowerHours"
                >
                  <el-input
                    v-model="dataForm.hLowerHours"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="形態："
                  prop="status"
                >
                  <el-select
                    v-model="dataForm.status"
                    disabled
                    placeholder="選択をお願いします"
                  >
                    <el-option
                      label="準委任"
                      value="準委任"
                    />
                    <el-option
                      label="派遣"
                      value="派遣"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請精算幅（上限）："
                  prop="hHigherHours"
                >
                  <el-input
                    v-model="dataForm.hHigherHours"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約単価（顧客先）："
                  prop="cPrice"
                >
                  <el-input
                    v-model="dataForm.cPrice"
                    clearable
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請減単金："
                  prop="hReductPrice"
                >
                  <el-input
                    v-model="dataForm.hReductPrice"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約开始月："
                  prop="cRealBeginMonth"
                >
                  <el-date-picker
                    v-model="dataForm.cBeginMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    disabled
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="下請増単金："
                  prop="hIncreasePrice"
                >
                  <el-input
                    v-model="dataForm.hIncreasePrice"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約结束月："
                  prop="cRealEndMonth"
                >
                  <el-date-picker
                    v-model="dataForm.cEndMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    disabled
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="价格变动月："
                  prop="priceMonth"
                >
                  <el-date-picker
                    v-model="dataForm.priceMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    clearable
                    :disabled-date="disabledPriceMonth"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="退場月："
                  prop="stopMonth"
                >
                  <el-date-picker
                    v-model="dataForm.stopMonth"
                    type="month"
                    placeholder="年月を選びます"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  label="備考："
                  prop="remark"
                >
                  <el-input
                    type="textarea"
                    rows="1"
                    v-model="dataForm.remark"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約精算幅（下限）："
                  prop="cLowerHours"
                >
                  <el-input
                    v-model="dataForm.cLowerHours"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約精算幅（上限）："
                  prop="cHigherHours"
                >
                  <el-input
                    v-model="dataForm.cHigherHours"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約減単金："
                  prop="cReductPrice"
                >
                  <el-input
                    v-model="dataForm.cReductPrice"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="契約増単金："
                  prop="cIncreasePrice"
                >
                  <el-input
                    v-model="dataForm.cIncreasePrice"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="10">
                <el-form-item
                  label="1日標準稼働時間："
                  prop="standardHours"
                >
                  <el-input
                    v-model="dataForm.standardHours"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </el-form>
    </el-scrollbar>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">キャンセル</el-button>
        <el-button
          type="primary"
          @click="dataFormSubmit"
        >確定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
export default {
  data: function () {
    return {
      token: localStorage.getItem("token"),
      action: `${this.$baseUrl}/file/upload`,
      selected: null,
      visible: false,

      // 技術者、プロジェクト、顧客下拉列表
      techList: [],
      proList: [],
      custList: [],

      // 新增字段来控制 el-scrollbar 的显示
      showScrollBar: false,

      // 标题
      title: "",

      dataForm: {
        projectTechnicianId: null,
        technicianId: null,
        technicianName: null,
        picture: null,
        katakana: null,
        roman: null,
        birthday: null,
        customerId: null,
        customerName: null,
        projectId: null,
        projectName: null,
        principal: null,
        principalCompany: null,
        status: null,
        cPrice: null,
        cBeginMonth: null,
        cEndMonth: null,
        stopMonth: null,
        cLowerHours: null,
        cHigherHours: null,
        cReductPrice: null,
        cIncreasePrice: null,
        standardHours: null,
        companyName: null,
        representative: null,
        contract: null,
        contractType: null,
        contractNum: null,
        termsOfPayment: null,
        coopDate: null,
        hPrice: null,
        hBeginMonth: null,
        hEndMonth: null,
        hLowerHours: null,
        hHigherHours: null,
        hReductPrice: null,
        hIncreasePrice: null,
        priceMonth: null,
        cRealBeginMonth: null,
        cRealEndMonth: null,
        realStopMonth: null,
        remark: null,
      },
      // 过滤技術者的姓名列表
      filteredTechNames: [],
      filteredProNames: [],

      dataRule: {
        technicianName: [
          {
            required: true,
            message: "技術者名は必須項目です",
          },
        ],
        projectName: [
          {
            required: true,
            message: "プロジェクト名は必須項目です",
          },
        ],
        cBeginMonth: [
          {
            required: true,
            message: "契約開始月は必須項目です",
          },
        ],
        cPrice: [
          {
            required: true,
            message: "契約単価は必須項目です",
          },
        ],
        priceMonth: [
          {
            required: true,
            message: "価格変動月は必須項目です",
            trigger: "change",
          },
          {
            required: true,
            validator: this.validatePriceMonth,
            trigger: "change",
          },
        ],
        stopMonth: [
          {
            required: true,
            message: "退場月は必須項目です",
          },
        ],
      },
    };
  },

  methods: {
    testadd() {
      let that = this;
      alert(that.dataForm.contractType);
    },
    testmodify() {
      let that = this;
      alert(that.dataForm.contractType);
    },
    // 删除上传的文件
    deleteFileHandle(index) {
      this.dataForm.contract.splice(index, 1);
    },
    init(projectTechnicianInfo, selected) {
      let that = this;
      that.reset();
      that.$nextTick(() => {
        if (that.$refs["dataForm"]) {
          that.$refs["dataForm"].resetFields();
        }
        that.reset();
        if (projectTechnicianInfo != null && projectTechnicianInfo != "") {
          console.log(projectTechnicianInfo);

          that.dataForm = {
            ...JSON.parse(JSON.stringify(projectTechnicianInfo)),
          };
          if (that.dataForm.contract == null || that.dataForm.contract == "") {
            that.dataForm.contract = [];
          }
          if (projectTechnicianInfo.contract == null) {
            projectTechnicianInfo.contract = "";
          }
        }
        if (selected != null && selected != "") {
          this.selected = selected;
        }
        if (selected === "insert") that.title = "追加";
        else if (selected === "modify") that.title = "変更";
        else if (selected === "exit") that.title = "退場";
        else that.title = "価格変動";
        that.updateFormRulesAndStatus();
      });
      that.visible = true;
    },
    reset() {
      let dataForm = {
        projectTechnicianId: "",
        technicianId: "",
        technicianName: "",
        picture: "",
        katakana: "",
        roman: "",
        birthday: "",
        customerId: "",
        customerName: "",
        projectId: "",
        projectName: "",
        principal: "",
        principalCompany: "",
        status: "",
        cPrice: "",
        cBeginMonth: "",
        cEndMonth: "",
        stopMonth: "",
        cLowerHours: "",
        cHigherHours: "",
        cReductPrice: "",
        cIncreasePrice: "",
        standardHours: "",
        companyName: "",
        representative: "",
        contract: [],
        contractType: "",
        contractNum: "",
        termsOfPayment: "",
        coopDate: "",
        hPrice: "",
        hBeginMonth: "",
        hEndMonth: "",
        hLowerHours: "",
        hHigherHours: "",
        hReductPrice: "",
        hIncreasePrice: "",
        priceMonth: "",
        remark: "",
      };
      this.dataForm = dataForm;

      Object.keys(this.dataRule).forEach((ruleKey) => {
        this.dataRule[ruleKey].forEach((rule) => {
          rule.required = true;
        });
      });
    },
    // 动态更新表单规则和禁用状态
    updateFormRulesAndStatus() {
      if (this.selected === "modify") {
        this.dataRule.technicianName[0].required = false;
        this.dataRule.projectName[0].required = false;
        this.dataRule.priceMonth[0].required = false;
        this.dataRule.priceMonth[1].required = false;
        this.dataRule.stopMonth[0].required = false;
        this.dataRule.cPrice[0].required = false;
      } else if (this.selected === "priceChange") {
        this.dataRule.technicianName[0].required = false;
        this.dataRule.projectName[0].required = false;
        this.dataRule.cBeginMonth[0].required = false;
        this.dataRule.stopMonth[0].required = false;
      } else if (this.selected === "exit") {
        this.dataRule.technicianName[0].required = false;
        this.dataRule.projectName[0].required = false;
        this.dataRule.cBeginMonth[0].required = false;
        this.dataRule.cPrice[0].required = false;
        this.dataRule.priceMonth[0].required = false;
        this.dataRule.priceMonth[1].required = false;
      } else if (this.selected === "insert") {
        this.dataRule.priceMonth[0].required = false;
        this.dataRule.priceMonth[1].required = false;
        this.dataRule.stopMonth[0].required = false;
      }
    },
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
      }
    },
    // 获取技術者全部信息
    loadTechList() {
      let that = this;
      that.$httpV2("/protech/technician", "GET", null, true, function (resp) {
        let result = resp.result;
        that.techList = result;
      });
    },
    // 获取プロジェクト全部信息
    loadProList() {
      let that = this;
      that.$httpV2("/protech/project", "GET", null, true, function (resp) {
        let result = resp.result;
        that.proList = result;
      });
    },
    // 获取顧客全部信息
    loadCustList() {
      let that = this;
      that.$httpV2("/protech/customer", "GET", null, true, function (resp) {
        let result = resp.result;
        that.custList = result;
      });
    },
    filteredProList() {
      if (!this.selectedCustomerId) {
        return this.proList;
      }
      this.filteredProNames = this.proList.filter(
        (item) => item.customerId === this.selectedCustomerId
      );
      return this.filteredProNames;
    },
    selectCustId() {
      this.dataForm.customerId = this.dataForm.customerName;
      this.selectedCustomerId = this.dataForm.customerId;
      this.dataForm.projectId = null;
      this.dataForm.projectName = null;
      this.dataForm.principalCompany = null;
      this.dataForm.principal = null;
      this.filteredProList();
    },
    custHandleClear() {
      this.filteredProNames = [];
    },

    // 将选择好的技術者、プロジェクト、顧客信息更新到dataForm上面
    handleTechChange(technicianId) {
      const selectedTechnician = this.techList.find(
        (item) => item.technicianId === technicianId
      );
      if (selectedTechnician) {
        this.dataForm = {
          ...this.dataForm,
          technicianId: selectedTechnician.technicianId,
          technicianName: selectedTechnician.technicianName,
          picture: selectedTechnician.picture,
          katakana: selectedTechnician.katakana,
          roman: selectedTechnician.roman,
          birthday: selectedTechnician.birthday,
          companyName: selectedTechnician.companyName,
          representative: selectedTechnician.representative,
        };
      }
    },
    handleProChange(projectId) {
      const selectedProject = this.proList.find(
        (item) => item.projectId === projectId
      );
      if (selectedProject) {
        this.dataForm = {
          ...this.dataForm,
          projectId: selectedProject.projectId,
          projectName: selectedProject.projectName,
          principal: selectedProject.principal,
          principalCompany: selectedProject.principalCompany,
        };
      }
    },

    // 上传图片
    updatePhotoSuccess: function (resp) {
      this.dataForm.picture = resp.file.url;
    },
    // 清空图片
    resetForm: function () {
      this.dataForm.picture = null;
    },
    // 上传文件
    updateFileSuccess: function (resp) {
      let that = this;
      if (resp && resp.file && resp.file.url) {
        if (that.dataForm.contract == null || that.dataForm.contract == "") {
          that.dataForm.contract == [];
        }
        // <...>符号用来分隔文件
        that.dataForm.contract.push(resp.file.url);
      }
    },
    // 获取上传文件名
    getFileName: function (url) {
      const match = url.match(/\/([^\/?#]+)$/);
      const fileName = match ? match[1] : null;
      return fileName;
    },

    // 格式化日期，确保为 YYYY-MM-DD 格式
    formatDate(dateStr) {
      if (dateStr) {
        let parts = dateStr.split("-");
        if (parts.length === 2) {
          return `${dateStr}-01`;
        }
        if (parts.length === 3) {
          return dateStr;
        }
      }
      return dateStr;
    },
    // 禁用priceMonth不符合条件的日期
    disabledPriceMonth(date) {
      const cBeginMonth = this.dataForm.cBeginMonth
        ? new Date(this.dataForm.cBeginMonth + "-01")
        : null;
      const stopMonth = this.dataForm.stopMonth
        ? new Date(this.dataForm.stopMonth + "-01")
        : null;

      // 禁用小于 cBeginMonth 或大于 stopMonth 的日期
      if (cBeginMonth && date < cBeginMonth) {
        return true;
      }
      if (stopMonth && date > stopMonth) {
        return true;
      }
      return false;
    },

    // 提交表单时验证 priceMonth
    validatePriceMonth(rule, value, callback) {
      if (!value) {
        callback();
        return;
      }
      const priceMonth = new Date(value + "-01");
      const cBeginMonth = this.dataForm.cBeginMonth
        ? new Date(this.dataForm.cBeginMonth + "-01")
        : null;
      const stopMonth = this.dataForm.stopMonth
        ? new Date(this.dataForm.stopMonth + "-01")
        : null;

      if (isNaN(priceMonth)) {
        callback(new Error("価格変動月の形式が正しくありません"));
        return;
      }

      if (cBeginMonth && isNaN(cBeginMonth)) {
        callback(new Error("契約開始月の形式が正しくありません"));
        return;
      }

      if (stopMonth && isNaN(stopMonth)) {
        callback(new Error("退場月の形式が正しくありません"));
        return;
      }

      if (cBeginMonth && priceMonth < cBeginMonth) {
        callback(new Error("価格変動月は契約開始月より早く設定できません"));
      } else if (stopMonth && priceMonth > stopMonth) {
        callback(new Error("価格変動月は退場月より遅く設定できません"));
      } else {
        callback();
      }
    },

    dataFormSubmit: function () {
      let that = this;
      that.$refs["dataForm"].validate(function (valid) {
        if (valid) {
          let data = {
            selected: that.selected,
            projectTechnicianId: that.dataForm.projectTechnicianId,
            technicianId: that.dataForm.technicianId,
            picture: that.dataForm.picture == null ? "" : that.dataForm.picture,
            customerId: that.dataForm.customerId,
            projectId: that.dataForm.projectId,
            parentId: that.dataForm.parentId,
            status: that.dataForm.status,
            cPrice: that.dataForm.cPrice,
            cBeginMonth: that.formatDate(that.dataForm.cBeginMonth),
            cEndMonth: that.formatDate(that.dataForm.cEndMonth),
            stopMonth: that.formatDate(that.dataForm.stopMonth),
            cLowerHours: that.dataForm.cLowerHours,
            cHigherHours: that.dataForm.cHigherHours,
            cReductPrice: that.dataForm.cReductPrice,
            cIncreasePrice: that.dataForm.cIncreasePrice,
            standardHours: that.dataForm.standardHours,
            companyName: that.dataForm.companyName,
            representative: that.dataForm.representative,
            contract: that.dataForm.contract.map((item) => `${item}?`).join(""),
            contractType: that.dataForm.contractType,
            contractNum: that.dataForm.contractNum,
            hPrice: that.dataForm.hPrice,
            hBeginMonth: that.formatDate(that.dataForm.hBeginMonth),
            hEndMonth: that.formatDate(that.dataForm.hEndMonth),
            hLowerHours: that.dataForm.hLowerHours,
            hHigherHours: that.dataForm.hHigherHours,
            hReductPrice: that.dataForm.hReductPrice,
            hIncreasePrice: that.dataForm.hIncreasePrice,
            priceMonth: that.formatDate(that.dataForm.priceMonth),
            cRealBeginMonth: that.formatDate(that.dataForm.cRealBeginMonth),
            cRealEndMonth: that.formatDate(that.dataForm.cRealEndMonth),
            realStopMonth: that.formatDate(that.dataForm.realStopMonth),
            remark: that.dataForm.remark,
          };
          let method = "";
          if (that.selected === "insert" || that.selected === "priceChange") {
            method = "POST";
          } else {
            method = "PUT";
            data.projectTechnicianId = that.dataForm.projectTechnicianId;
          }
          that.$httpV2("/protech", method, data, false, function (resp) {
            ElMessage({
              message: "成功した操作",
              type: "success",
            });
            that.visible = false;
            that.$emit("refreshDataList");
          });
        }
      });
    },
  },
  created() {
    this.loadTechList();
    this.loadProList();
    this.loadCustList();
  },
};
</script>

<style scoped>
.error-img {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #c0c4cc;
}
</style>
