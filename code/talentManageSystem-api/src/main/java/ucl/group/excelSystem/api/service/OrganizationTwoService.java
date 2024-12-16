package ucl.group.excelSystem.api.service;

import org.apache.ibatis.annotations.Param;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.talentManageSystem.api.common.PageUtils;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public interface OrganizationTwoService {

    PageUtils selectOrganizationTwoByPage(@Param("param") Map<String, Object> param);

    void insertOrganizationTwo(@Param("organizationEntity")BasicOrganizationEntity organizationEntity);

    void updateOrganizationTwo(@Param("bean") BasicOrganizationEntity bean);

    void deleteOrganizationTwo(@NotEmpty(message = "organizationId不能为空") @Param("organizationId") Long organizationId);

    List<BasicOrganizationEntity> selectOrganizationTwoById(@Param("organizationId") Long organizationId);

    List<BasicOrganizationEntity> selectAll();

    List<BasicOrganizationEntity> selectOrganizationTwoByIds(@Param("ids") List<Long> ids);
}
