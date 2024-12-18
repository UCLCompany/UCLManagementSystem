package ucl.group.excelSystem.api.db.pojo;

import lombok.Data;


@Data
public class BasicPartnerEntity {
    private Integer partnerCompanyId;

    private String partnerCompanyName;

    private String partnerpostalCode;

    private String partnerCompanyAddress;

    private String representativeName;

    private String termsofPayment;

    private String coopDate;
}
