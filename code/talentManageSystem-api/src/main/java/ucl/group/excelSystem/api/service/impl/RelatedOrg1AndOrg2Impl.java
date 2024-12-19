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

    /**
     * 插入两个组织之间的关联关系。
     *
     * @param org1Id 第一个组织的ID
     * @param org2Id 第二个组织的ID
     */
    @Override
    @Transactional
    public void insertOrg1AndOrg2(Long org1Id, Long org2Id) {
        // 调用DAO方法插入两个组织之间的关联关系
        relatedOrg1AndOrg2Dao.insertOrg1AndOrg2(org1Id, org2Id);
    }

    /**
     * 根据给定的组织2 ID 删除与该组织2相关的所有组织1和组织2的关联关系。
     *
     * @param org2Id 组织2的ID
     */
    @Override
    @Transactional
    public void deleteOrg1AndOrg2ByOrg2Id(Long org2Id) {
        // 调用DAO方法删除与指定组织2 ID相关的所有组织1和组织2的关联关系
        relatedOrg1AndOrg2Dao.deleteOrg1AndOrg2ByOrg2Id(org2Id);
    }

    /**
     * 检查两个组织是否相关联。
     *
     * @param org1Id 第一个组织的ID
     * @param org2Id 第二个组织的ID
     * @return 如果两个组织相关联则返回true，否则返回false
     */
    @Override
    @Transactional
    public boolean isRelatedByOrg1IdAndOrg2Id(Long org1Id, Long org2Id) {
        // 调用DAO方法检查两个组织是否相关联
        return relatedOrg1AndOrg2Dao.isRelatedByOrg1IdAndOrg2Id(org1Id, org2Id);
    }

    /**
     * 检查给定的组织1 ID 是否与任何组织2相关联。
     *
     * @param org1Id 组织1的ID
     * @return 如果组织1 ID 与任何组织2相关联，则返回 true；否则返回 false
     */
    @Override
    @Transactional
    public boolean isRelatedByOrg1Id(Long org1Id) {
        // 调用 DAO 方法检查组织1 ID 是否与任何组织2相关联
        return relatedOrg1AndOrg2Dao.isRelatedByOrg1Id(org1Id);
    }

    /**
     * 根据组织1的ID查询与之相关的组织1和组织2的ID列表。
     *
     * @param organizationId 组织1的ID
     * @return 与组织1相关的组织1和组织2的ID列表
     */
    @Override
    @Transactional
    public List<Long> selectOrg1AndOrg2ByOrg1Id(Long organizationId) {
        // 调用DAO方法查询相关组织ID列表
        return relatedOrg1AndOrg2Dao.selectOrg1AndOrg2ByOrg1Id(organizationId);
    }

    /**
     * 根据二级组织ID获取一级组织ID。
     *
     * @param org2Id 二级组织ID
     * @return 一级组织ID，如果未找到则返回null
     */
    @Override
    public Long getOrganizationOneByOrganizationTwoId(Long org2Id) {
        // 调用DAO方法获取一级组织ID
        return relatedOrg1AndOrg2Dao.getOrganizationOneByOrganizationTwoId(org2Id);
    }

    /**
     * 获取两个组织之间的关联ID。
     *
     * @param org1Id 第一个组织的ID
     * @param org2Id 第二个组织的ID
     * @return 两个组织之间的关联ID
     */
    @Override
    public Long getId(Long org1Id, Long org2Id) {
        // 调用DAO方法获取两个组织之间的关联ID
        return relatedOrg1AndOrg2Dao.getId(org1Id, org2Id);
    }

    /**
     * 更新指定相关ID的组织1和组织2的ID。
     *
     * @param relatedId 相关记录的ID
     * @param org1Id 组织1的新ID
     * @param org2Id 组织2的新ID
     */
    @Override
    public void updateOrg1AndOrg2ById(Long relatedId, Long org1Id, Long org2Id) {
        // 调用DAO层方法更新组织1和组织2的ID
        relatedOrg1AndOrg2Dao.updateOrg1AndOrg2ById(relatedId, org1Id, org2Id);
    }
}
