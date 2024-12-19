package ucl.group.excelSystem.api.controller.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InsertProjectForm {
    @NotNull(message = "项目名不能为空")
    @JsonProperty("projectName")
    private String projectName;

    @NotNull(message = "客户Id不能为空")
    @JsonProperty("customerId")
    private Long customerId;

    @JsonProperty("principal")
    private String principal;

    @JsonProperty("principalCompany")
    private String principalCompany;

    @JsonProperty("category")
    private String category;

    @JsonProperty("uclPrincipal")
    private String uclPrincipal;

    @JsonProperty("principalId")
    private String principalId;

    @JsonProperty("remark")
    private String remark;

}
