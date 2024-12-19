package ucl.group.excelSystem.api.db.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ucl.group.talentManageSystem.api.db.pojo.BasicEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 项目-技术者关联表实体类
 */
@Data
public class RelatedProjectTechnician extends BasicEntity implements Cloneable {
    /**
     * 主键id
     */
    private Long projectTechnicianId;

    /**
     * 外键——项目主键id
     */
    private Long projectId;

    /**
     * 外键——技术者主键id
     */
    private Long technicianId;

    /**
     * 真正的契约开始时间，不参与逻辑运算，只做数据展示
     */
    private LocalDate cRealBeginMonth;

    /**
     * 真正的契约结束时间，不参与逻辑运算，只做数据展示
     */
    private LocalDate cRealEndMonth;

    /**
     * 真正的退场月，不参与逻辑运算，只做数据展示
     */
    private LocalDate realStopMonth;

    /**
     * 契约开始时间
     */
    private LocalDate cBeginMonth;

    /**
     * 契约结束时间
     */
    private LocalDate cEndMonth;

    /**
     * 契约精算幅（上限）
     */
    private BigDecimal cHigherHours;

    /**
     * 契约精算幅（下限）
     */
    private BigDecimal cLowerHours;

    /**
     * 契约増単金
     */
    private BigDecimal cIncreasePrice;

    /**
     * 契约減単金
     */
    private BigDecimal cReductPrice;

    /**
     * 契約書类型
     */
    private int contractType;

    /**
     * 契約書
     */
    private String contract;

    /**
     * 契約書番号
     */
    private String contractNum;

    /**
     * 契約単価（顧客先）
     */
    private BigDecimal cPrice;

    /**
     * 下請开始时间
     */
    private LocalDate hBeginMonth;

    /**
     * 下請结束时间
     */
    private LocalDate hEndMonth;

    /**
     * 下請精算幅（上限）
     */
    private BigDecimal hHigherHours;

    /**
     * 下請精算幅（下限）
     */
    private BigDecimal hLowerHours;

    /**
     * 下請増単金
     */
    private BigDecimal hIncreasePrice;

    /**
     * 下請減単金
     */
    private BigDecimal hReductPrice;

    /**
     * 下請単価
     */
    private BigDecimal hPrice;

    /**
     * 价格变动月
     */
    private LocalDate priceMonth;

    /**
     * 1日標準稼働時間
     */
    private BigDecimal standardHours;

    /**
     * 退场月
     */
    private LocalDate stopMonth;

    /**
     * 父点id（用于价格变动）
     */
    private Long parentId;
    @JsonIgnore
    private String selected;

    @Override
    public RelatedProjectTechnician clone() {
        try {
            return (RelatedProjectTechnician) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
