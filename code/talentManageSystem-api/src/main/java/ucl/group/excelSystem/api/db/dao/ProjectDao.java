package ucl.group.excelSystem.api.db.dao;

import org.apache.ibatis.annotations.Param;
import ucl.group.excelSystem.api.db.pojo.BasicProjectEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @projectName: code
 * @package: ucl.group.excelSystem.api.db.dao
 * @className: ProjectDao
 * @author: he_jiale
 * @description: TODO
 * @date: 2024/07/05 17:44
 * @version: 1.0
 */
public interface ProjectDao {
    public BasicProjectEntity searchById(Long projectId);
    public List<BasicProjectEntity> searchByCustomerId(Long customerId);
    public void insertProject(BasicProjectEntity basicProjectEntity);

    public void updateProject(BasicProjectEntity basicProjectEntity);

    public void deleteProject(Long[] projectIds);

    public ArrayList<HashMap> selectProjectByPage(Map param);
    public long selectProjectByPageCount();


    List<BasicProjectEntity> searchByIds(@Param("projectIds") ArrayList<Long> projectIds);
}
