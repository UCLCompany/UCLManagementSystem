package ucl.group.excelSystem.api.controller.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InsertProTechForm {

	@JsonProperty("projectTechnicianId")
	private Long projectTechnicianId;
	/**
	 * 技术者主键id
	 */
	@NotNull(message = "technicianId不能为空")
	@JsonProperty("technicianId")
	private Long technicianId;
	/**
	 * 项目主键id
	 */
	@NotNull(message = "projectId不能为空")
	@JsonProperty("projectId")
	private Long projectId;
	/**
	 * 契约开始时间
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("cBeginMonth")
	private LocalDate cBeginMonth;
	/**
	 * 契约结束时间
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("cEndMonth")
	private LocalDate cEndMonth;
	/**
	 * 契约精算幅（上限）
	 */
	@JsonProperty("cHigherHours")
	private BigDecimal cHigherHours;
	/**
	 * 契约増単金
	 */
	@JsonProperty("cIncreasePrice")
	private BigDecimal cIncreasePrice;
	/**
	 * 契约精算幅（下限）
	 */
	@JsonProperty("cLowerHours")
	private BigDecimal cLowerHours;
	/**
	 * 契約書类型，1：注文书；2：出向个别契约书
	 */
	@JsonProperty("contractType")
	private int contractType;
	/**
	 * 契約書
	 */
	@JsonProperty("contract")
	private String contract;
	/**
	 * 契約書番号
	 */
	@JsonProperty("contractNum")
	private String contractNum;
	/**
	 * 契約単価（顧客先）
	 */
	@JsonProperty("cPrice")
	private BigDecimal cPrice;
	/**
	 * 契约減単金
	 */
	@JsonProperty("cReductPrice")
	private BigDecimal cReductPrice;
	/**
	 * 下請开始时间
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("hBeginMonth")
	private LocalDate hBeginMonth;
	/**
	 * 下請结束时间
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("hEndMonth")
	private LocalDate hEndMonth;
	/**
	 * 下請精算幅（上限）
	 */
	@JsonProperty("hHigherHours")
	private BigDecimal hHigherHours;
	/**
	 * 下請増単金
	 */
	@JsonProperty("hIncreasePrice")
	private BigDecimal hIncreasePrice;
	/**
	 * 下請精算幅（下限）
	 */
	@JsonProperty("hLowerHours")
	private BigDecimal hLowerHours;
	/**
	 * 下請単価
	 */
	@JsonProperty("hPrice")
	private BigDecimal hPrice;
	/**
	 * 下請減単金
	 */
	@JsonProperty("hReductPrice")
	private BigDecimal hReductPrice;
	/**
	 * 价格变动月
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("priceMonth")
	private LocalDate priceMonth;
	/**
	 * 备注
	 */
	@JsonProperty("remark")
	private String remark;
	/**
	 * 1日標準稼働時間
	 */
	@JsonProperty("standardHours")
	private BigDecimal standardHours;
	/**
	 * 形态
	 */
	@JsonProperty("status")
	private String status;
	/**
	 * 退场月
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("stopMonth")
	private LocalDate stopMonth;

	@JsonProperty("selected")
	private String selected;

}
