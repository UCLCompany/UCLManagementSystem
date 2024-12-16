package ucl.group.excelSystem.api.service;

import java.util.List;

public interface RelatedOrg1AndOrg2 {
    void insertOrg1AndOrg2(Long org1Id, Long org2Id);

    void deleteOrg1AndOrg2ByOrg2Id(Long org2Id);

    boolean isRelatedByOrg1IdAndOrg2Id(Long org1Id, Long org2Id);

    boolean isRelatedByOrg1Id(Long org1Id);

    List<Long> selectOrg1AndOrg2ByOrg1Id(Long organizationId);
}
