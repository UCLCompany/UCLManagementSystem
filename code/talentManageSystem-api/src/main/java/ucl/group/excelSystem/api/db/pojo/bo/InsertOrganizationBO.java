package ucl.group.excelSystem.api.db.pojo.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InsertOrganizationBO {

    @NotNull(message = "组织名1不能为空")
    @JsonProperty("organizationName")
    private String organizationName;

    @NotNull(message = "belong不能为空")
    private String belong;

    @NotNull(message = "preOrganizationId不能为空")
    private String preOrganizationId;
}
