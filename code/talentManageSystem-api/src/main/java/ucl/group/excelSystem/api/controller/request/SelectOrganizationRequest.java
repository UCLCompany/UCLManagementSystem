package ucl.group.excelSystem.api.controller.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SelectOrganizationRequest {

    //@NotNull(message="organizationId不能为空")
    private Long organizationId;
}
