package ucl.group.excelSystem.api.controller.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Locale;

@Data
public class InsertTechForm {

	@NotEmpty(message = "name不能为空")
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
