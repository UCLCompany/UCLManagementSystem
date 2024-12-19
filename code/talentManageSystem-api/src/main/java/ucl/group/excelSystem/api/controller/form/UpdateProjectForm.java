package ucl.group.excelSystem.api.controller.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateProjectForm {
    @NotNull(message = "项目Id不能为空")
    @JsonProperty("projectId")
    private Long projectId;

    @NotNull(message = "项目名不能为空")
    @JsonProperty("projectName")
    private String projectName;

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
