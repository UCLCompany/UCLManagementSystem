package ucl.group.excelSystem.api.controller.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class UpdateTechForm {

	@NotNull(message = "技术者id不能为空")
	private Long technicianId;

	@NotEmpty(message = "技术者姓名不能为空")
	private String name;

	private String picture;

	private String katakana;

	private String roman;

	private LocalDate birthday;

	private String belongCompany;

	private String representative;

	private String belongCompanyId;

	private String remark;

}
