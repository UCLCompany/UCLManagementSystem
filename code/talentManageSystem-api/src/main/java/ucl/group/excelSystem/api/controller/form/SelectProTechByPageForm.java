package ucl.group.excelSystem.api.controller.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SelectProTechByPageForm {

	@NotNull(message = "page不能为空")
	private int page;

	@NotNull(message = "length不能为空")
	private int length;

	private String techName;

	private String proName;

	private String custName;

}
