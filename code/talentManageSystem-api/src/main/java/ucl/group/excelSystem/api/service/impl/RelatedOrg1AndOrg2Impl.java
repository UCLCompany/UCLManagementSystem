package ucl.group.excelSystem.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.db.dao.RelatedOrg1AndOrg2Dao;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.excelSystem.api.service.RelatedOrg1AndOrg2;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class RelatedOrg1AndOrg2Impl implements RelatedOrg1AndOrg2 {

    @Resource
    private RelatedOrg1AndOrg2Dao relatedOrg1AndOrg2Dao;

    @Override
    @Transactional
    public void insertOrg1AndOrg2(Long org1Id, Long org2Id) {
        relatedOrg1AndOrg2Dao.insertOrg1AndOrg2(org1Id, org2Id);
    }

    @Override
    @Transactional
    public void deleteOrg1AndOrg2ByOrg2Id(Long org2Id) {
        relatedOrg1AndOrg2Dao.deleteOrg1AndOrg2ByOrg2Id(org2Id);
    }

    @Override
    @Transactional
    public boolean isRelatedByOrg1IdAndOrg2Id(Long org1Id, Long org2Id) {
        return relatedOrg1AndOrg2Dao.isRelatedByOrg1IdAndOrg2Id(org1Id, org2Id);
    }

    @Override
    @Transactional
    public boolean isRelatedByOrg1Id(Long org1Id) {
        return relatedOrg1AndOrg2Dao.isRelatedByOrg1Id(org1Id);
    }

    @Override
    @Transactional
    public List<Long> selectOrg1AndOrg2ByOrg1Id(Long organizationId) {
        return relatedOrg1AndOrg2Dao.selectOrg1AndOrg2ByOrg1Id(organizationId);
    }

    @Override
    public Long getOrganizationOneByOrganizationTwoId(Long org2Id) {
        return relatedOrg1AndOrg2Dao.getOrganizationOneByOrganizationTwoId(org2Id);
    }
}
