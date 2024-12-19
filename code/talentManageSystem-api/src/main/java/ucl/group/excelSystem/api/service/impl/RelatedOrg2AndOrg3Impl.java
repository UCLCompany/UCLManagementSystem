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



    /**
     * 插入组织2和组织3之间的关联关系。
     *
     * @param org2Id 组织2的ID
     * @param org3Id 组织3的ID
     */
    @Override
    @Transactional
    public void insertOrg2AndOrg3(Long org2Id, Long org3Id) {
        // 调用DAO方法插入组织2和组织3之间的关联关系
        relatedOrg2AndOrg3Dao.insertOrg2AndOrg3(org2Id, org3Id);
    }

    /**
     * 根据组织3的ID删除相关的组织2和组织3的关系。
     *
     * @param org3Id 组织3的ID
     */
    @Override
    @Transactional
    public void deleteOrg2AndOrg3ByOrg3Id(Long org3Id) {
        // 调用DAO方法删除相关记录
        relatedOrg2AndOrg3Dao.deleteOrg2AndOrg3ByOrg3Id(org3Id);
    }

    /**
     * 检查给定的二级组织ID和三级组织ID是否相关联。
     *
     * @param org2Id 二级组织ID
     * @param org3Id 三级组织ID
     * @return 如果二级组织ID和三级组织ID相关联则返回true，否则返回false
     */
    @Override
    @Transactional
    public boolean isRelatedByOrg2IdAndOrg3Id(Long org2Id, Long org3Id) {
        // 调用DAO方法检查二级组织ID和三级组织ID是否相关联
        return relatedOrg2AndOrg3Dao.isRelatedByOrg2IdAndOrg3Id(org2Id, org3Id);
    }

    /**
     * 检查给定的组织2 ID 是否与任何组织3 相关。
     *
     * @param org2Id 组织2的ID
     * @return 如果组织2与任何组织3相关，则返回true；否则返回false
     */
    @Override
    @Transactional
    public boolean isRelatedByOrg2Id(Long org2Id) {
        // 调用DAO方法检查组织2 ID是否与任何组织3相关
        return relatedOrg2AndOrg3Dao.isRelatedByOrg2Id(org2Id);
    }

    /**
     * 根据二级组织ID查询相关的二级和三级组织ID列表。
     *
     * @param org2Id 二级组织ID
     * @return 相关的二级和三级组织ID列表
     */
    @Override
    @Transactional
    public List<Long> selectOrg2AndOrg3ByOrg2Id(Long org2Id) {
        // 调用数据访问对象方法查询相关组织ID列表
        return relatedOrg2AndOrg3Dao.selectOrg2AndOrg3ByOrg2Id(org2Id);
    }

    /**
     * 根据组织3的ID查询组织2和组织3的关系。
     *
     * @param org3Id 组织3的ID
     * @return 返回与组织3关联的组织2的ID
     */
    @Override
    public Long selectOrg2AndOrg3ByOrg3Id(Long org3Id) {
        return relatedOrg2AndOrg3Dao.selectOrg2AndOrg3ByOrg3Id(org3Id);
    }

    /**
     * 获取与给定的org2Id和org3Id相关的ID。
     *
     * @param org2Id 组织2的ID
     * @param org3Id 组织3的ID
     * @return 与给定的org2Id和org3Id相关的ID
     */
    @Override
    public Long getId(Long org2Id, Long org3Id) {
        // 调用相关DAO方法获取ID
        return relatedOrg2AndOrg3Dao.getId(org2Id, org3Id);
    }

    /**
     * 更新指定相关ID的组织2和组织3的ID。
     *
     * @param relatedId 相关记录的ID
     * @param org2Id 组织2的ID
     * @param org3Id 组织3的ID
     */
    @Override
    public void updateOrg2AndOrg3ById(Long relatedId, Long org2Id, Long org3Id) {
        // 调用DAO层方法更新组织2和组织3的ID
        relatedOrg2AndOrg3Dao.updateOrg2AndOrg3ById(relatedId, org2Id, org3Id);
    }
}
