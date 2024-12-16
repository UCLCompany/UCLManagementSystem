package ucl.group.excelSystem.api.db.pojo.bo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateOrganizationBO {
    @NotNull(message="organizationId不能为空")
    private Long organizationId;

    private String organizationName;

    private String belong;

    private String preOrganizationId;
}
