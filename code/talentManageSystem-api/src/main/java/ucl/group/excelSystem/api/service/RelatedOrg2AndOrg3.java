package ucl.group.excelSystem.api.service;

import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;

import java.util.List;

public interface RelatedOrg2AndOrg3 {
    void insertOrg2AndOrg3(Long org2Id, Long org3Id);

    void deleteOrg2AndOrg3ByOrg3Id(Long org3Id);

    boolean isRelatedByOrg2IdAndOrg3Id(Long org2Id, Long org3Id);

    boolean isRelatedByOrg2Id(Long org2Id);

    //往下查
    List<Long> selectOrg2AndOrg3ByOrg2Id(Long org2Id);

    //往上查
    Long selectOrg2AndOrg3ByOrg3Id(Long org3Id);
}
