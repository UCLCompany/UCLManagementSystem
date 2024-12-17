package ucl.group.excelSystem.api.service;

import org.apache.ibatis.annotations.Param;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.talentManageSystem.api.common.PageUtils;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public interface OrganizationThreeService {

    PageUtils selectOrganizationThreeByPage(@Param("param") Map<String, Object> param);

    void insertOrganizationThree(@Param("basicOrganizationEntity")BasicOrganizationEntity basicOrganizationEntity);

    void updateOrganizationThree(@Param("bean") BasicOrganizationEntity bean);

    void deleteOrganizationThree(@NotEmpty(message = "organizationId不能为空") @Param("organizationId") Long organizationId);

    BasicOrganizationEntity selectOrganizationThreeById(@Param("organizationId") Long organizationId);

    List<BasicOrganizationEntity> selectAll();

    List<BasicOrganizationEntity> selectOrganizationThreeByIds(List<Long> ids);

    PageUtils getUpper(@Param("param") Map<String, Object> param);

    BasicOrganizationEntity getById(Long id);

    List<Long> getAllId();
}
