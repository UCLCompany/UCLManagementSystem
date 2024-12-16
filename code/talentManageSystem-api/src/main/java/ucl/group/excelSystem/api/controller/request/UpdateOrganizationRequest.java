package ucl.group.excelSystem.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateOrganizationRequest {
    @NotNull(message="organizationId不能为空")
    private Long organizationId;

    private String organizationName;

    private String belong;

    private String preOrganizationId;
}
