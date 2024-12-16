package ucl.group.excelSystem.api.db.dao;

import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.talentManageSystem.api.common.PageUtils;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OrganizationOneDao {
    ArrayList<HashMap> selectOrganizationOneByPage(Map<String, Object> param);

    void insertOrganizationOne(BasicOrganizationEntity basicOrganizationEntity);

    void updateOrganizationOne(BasicOrganizationEntity bean);

    void deleteOrganizationOne(@NotEmpty(message = "organizationId不能为空") Long organizationId);

    BasicOrganizationEntity selectOrganizationOneById(Long organizationId);

    List<BasicOrganizationEntity> selectAll();

    long selectOrganizationOneByPageCount();
}
