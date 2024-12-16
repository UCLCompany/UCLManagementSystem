package ucl.group.excelSystem.api.controller.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrganizationPageRequest {

    @NotNull(message = "page不能为空")
    private int page;
    @NotNull(message = "length不能为空")
    private int length;

    //private String organizationName;
}
