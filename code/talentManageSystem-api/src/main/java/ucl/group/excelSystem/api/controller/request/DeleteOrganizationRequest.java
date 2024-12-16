package ucl.group.excelSystem.api.controller.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class DeleteOrganizationRequest {

    //@NotEmpty(message = "organizationId不能为空")
    @NotNull
    private Long organizationId;
}
