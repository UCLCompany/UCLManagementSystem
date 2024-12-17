package ucl.group.excelSystem.api.service;

import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;

import java.util.List;

public interface RelatedOrg1AndOrg2 {
    void insertOrg1AndOrg2(Long org1Id, Long org2Id);

    void deleteOrg1AndOrg2ByOrg2Id(Long org2Id);

    boolean isRelatedByOrg1IdAndOrg2Id(Long org1Id, Long org2Id);

    boolean isRelatedByOrg1Id(Long org1Id);

    //往下查
    List<Long> selectOrg1AndOrg2ByOrg1Id(Long organizationId);

    //往上查
    Long getOrganizationOneByOrganizationTwoId(Long org2Id);
}
