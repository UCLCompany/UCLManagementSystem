package ucl.group.excelSystem.api.db.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ucl.group.talentManageSystem.api.db.pojo.BasicEntity;

import java.time.LocalDate;

/**
 * 技术者表实体类
 */
@Data
public class BasicTechnicianEntity extends BasicEntity {

    private Long technicianId;
    private String name;
    private String picture;
    private String katakana;
    private String roman;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String representative;
    private String belongCompany;
    private String belongCompanyId;
}
