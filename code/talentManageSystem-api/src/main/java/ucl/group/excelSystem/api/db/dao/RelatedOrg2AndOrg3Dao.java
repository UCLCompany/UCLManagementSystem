package ucl.group.excelSystem.api.db.dao;

import java.util.List;

public interface RelatedOrg2AndOrg3Dao {
    void insertOrg2AndOrg3(Long org2Id, Long org3Id);

    void deleteOrg2AndOrg3ByOrg3Id(Long org3Id);

    boolean isRelatedByOrg2IdAndOrg3Id(Long org2Id, Long org3Id);

    boolean isRelatedByOrg2Id(Long org2Id);

    List<Long> selectOrg2AndOrg3ByOrg2Id(Long org2Id);

    Long selectOrg2AndOrg3ByOrg3Id(Long org3Id);

    Long getId(Long org2Id, Long org3Id);

    void updateOrg2AndOrg3ById(Long relatedId, Long org2Id, Long org3Id);
}
