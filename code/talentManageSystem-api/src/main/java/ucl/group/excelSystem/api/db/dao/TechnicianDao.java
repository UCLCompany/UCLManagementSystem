package ucl.group.excelSystem.api.db.dao;

import org.apache.ibatis.annotations.*;
import ucl.group.excelSystem.api.controller.form.SaveTechnicianListForm;
import ucl.group.excelSystem.api.db.pojo.BasicTechnicianEntity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TechnicianDao {

    BasicTechnicianEntity searchById(Long technicianId);


    void insertTech(BasicTechnicianEntity basicTechnicianEntity);

    long selectTechByPageCount(Map param);

    ArrayList<HashMap> selectTechByPage(Map param);

    void updateTech(BasicTechnicianEntity basicTechnicianEntity);

    void deleteTech(Long[] ids);

    void updateTotalByYearMonth(Map param);

    List<BasicTechnicianEntity> searchByIds(@Param("technicianIds") ArrayList<Long> technicianIds);
}
