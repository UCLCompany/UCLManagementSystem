package ucl.group.excelSystem.api.service;

import org.apache.ibatis.annotations.Param;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.talentManageSystem.api.common.PageUtils;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public interface OrganizationOneService {

    PageUtils selectOrganizationOneByPage(@Param("param") Map<String, Object> param);

    void insertOrganizationOne(@Param("basicOrganizationEntity")BasicOrganizationEntity basicOrganizationEntity);

    void updateOrganizationOne(@Param("bean") BasicOrganizationEntity bean);

    void deleteOrganizationOne(@NotEmpty(message = "organizationId不能为空") @Param("organizationId") Long organizationId);

    List<BasicOrganizationEntity> selectOrganizationOneById(@Param("organizationId") Long organizationId);

    List<BasicOrganizationEntity> selectAll();
}
