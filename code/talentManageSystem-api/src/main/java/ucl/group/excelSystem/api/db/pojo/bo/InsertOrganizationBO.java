package ucl.group.excelSystem.api.db.pojo.bo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InsertOrganizationBO {

    private String organizationName;

    private String belong;

    private Long preOrganizationId;
}
