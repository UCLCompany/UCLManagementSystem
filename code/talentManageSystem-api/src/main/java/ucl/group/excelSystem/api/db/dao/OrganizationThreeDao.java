package ucl.group.excelSystem.api.db.dao;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.talentManageSystem.api.common.PageUtils;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OrganizationThreeDao {
    ArrayList<HashMap> selectOrganizationThreeByPage(Map<String, Object> param);

    @Options(useGeneratedKeys = true, keyProperty = "organizationId")
    int insertOrganizationThree(BasicOrganizationEntity basicOrganizationEntity);

    void updateOrganizationThree(BasicOrganizationEntity bean);

    void deleteOrganizationThree(@NotEmpty(message = "organizationId不能为空") Long organizationId);

    BasicOrganizationEntity selectOrganizationThreeById(Long organizationId);

    long selectOrganizationThreeByPageCount();

    List<BasicOrganizationEntity> selectAll();

    long getId(String organizationName);

    List<BasicOrganizationEntity> selectOrganizationThreeByIds(@Param("ids") List<Long> ids);

    BasicOrganizationEntity getById(Long id);

    List<Long> getAllId();
}
