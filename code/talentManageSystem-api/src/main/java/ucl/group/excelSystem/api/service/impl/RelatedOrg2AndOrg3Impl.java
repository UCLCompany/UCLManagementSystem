package ucl.group.excelSystem.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.db.dao.RelatedOrg2AndOrg3Dao;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.excelSystem.api.service.OrganizationTwoService;
import ucl.group.excelSystem.api.service.RelatedOrg2AndOrg3;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class RelatedOrg2AndOrg3Impl implements RelatedOrg2AndOrg3 {

    @Resource
    private RelatedOrg2AndOrg3Dao relatedOrg2AndOrg3Dao;



    @Override
    @Transactional
    public void insertOrg2AndOrg3(Long org2Id, Long org3Id) {
        relatedOrg2AndOrg3Dao.insertOrg2AndOrg3(org2Id, org3Id);
    }

    @Override
    @Transactional
    public void deleteOrg2AndOrg3ByOrg3Id(Long org3Id) {
        relatedOrg2AndOrg3Dao.deleteOrg2AndOrg3ByOrg3Id(org3Id);
    }

    @Override
    @Transactional
    public boolean isRelatedByOrg2IdAndOrg3Id(Long org2Id, Long org3Id) {
        return relatedOrg2AndOrg3Dao.isRelatedByOrg2IdAndOrg3Id(org2Id, org3Id);
    }

    @Override
    @Transactional
    public boolean isRelatedByOrg2Id(Long org2Id) {
        return relatedOrg2AndOrg3Dao.isRelatedByOrg2Id(org2Id);
    }

    @Override
    @Transactional
    public List<Long> selectOrg2AndOrg3ByOrg2Id(Long org2Id) {
        return relatedOrg2AndOrg3Dao.selectOrg2AndOrg3ByOrg2Id(org2Id);
    }

    @Override
    public Long selectOrg2AndOrg3ByOrg3Id(Long org3Id) {
        return relatedOrg2AndOrg3Dao.selectOrg2AndOrg3ByOrg3Id(org3Id);
    }

    @Override
    public Long getId(Long org2Id, Long org3Id) {
        return relatedOrg2AndOrg3Dao.getId(org2Id, org3Id);
    }
}
