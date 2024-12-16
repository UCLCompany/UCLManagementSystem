package ucl.group.excelSystem.api.db.dao;

import org.apache.ibatis.annotations.Options;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.talentManageSystem.api.common.PageUtils;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OrganizationTwoDao {
    ArrayList<HashMap> selectOrganizationTwoByPage(Map<String, Object> param);

    @Options(useGeneratedKeys = true, keyProperty = "organizationId")
    long insertOrganizationTwo(BasicOrganizationEntity basicOrganizationEntity);

    void updateOrganizationTwo(BasicOrganizationEntity bean);

    void deleteOrganizationTwo(@NotEmpty(message = "organizationId不能为空") Long organizationId);

    BasicOrganizationEntity selectOrganizationTwoById(Long organizationId);

    long selectOrganizationTwoByPageCount();

    List<BasicOrganizationEntity> selectAll();

    long getId(String organizationName);

    List<BasicOrganizationEntity> selectOrganizationTwoByIds(List<Long> ids);
}
