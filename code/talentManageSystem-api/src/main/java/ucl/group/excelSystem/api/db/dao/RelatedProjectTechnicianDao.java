package ucl.group.excelSystem.api.db.dao;

import org.apache.ibatis.annotations.Param;
import ucl.group.excelSystem.api.controller.form.SaveTechnicianListForm;
import ucl.group.excelSystem.api.db.pojo.RelatedProjectTechnician;

import java.util.List;

public interface RelatedProjectTechnicianDao {
    public List<RelatedProjectTechnician> searchBetweenStartAndEnd(@Param("dateStart") String dateStart,@Param("dateEnd") String dateEnd);
    int saveRemark(SaveTechnicianListForm form);
    public RelatedProjectTechnician searchByProjectTechId(Long projectTechnicianId);
    public List<RelatedProjectTechnician> searchByProjectId(Long projectId);
    public List<RelatedProjectTechnician> searchByTechnicianId(Long technicianId);

    List<RelatedProjectTechnician> findAllById(@Param("projectTechnicianIds") List<Long> projectTechnicianIds);

}
