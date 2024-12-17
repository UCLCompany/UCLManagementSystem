package ucl.group.excelSystem.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InsertOrganizationRequest {

    @NotNull(message = "组织名1不能为空")
    @JsonProperty("organizationName")
    private String organizationName;

    @NotNull(message = "belong不能为空")
    private String belong;


    private Long organizationId;
}
