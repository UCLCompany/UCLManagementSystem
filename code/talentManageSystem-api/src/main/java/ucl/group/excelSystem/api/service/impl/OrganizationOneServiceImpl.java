package ucl.group.excelSystem.api.service.impl;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.db.dao.OrganizationOneDao;
import ucl.group.excelSystem.api.db.dao.RelatedOrg1AndOrg2Dao;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.excelSystem.api.service.OrganizationOneService;
import ucl.group.excelSystem.api.service.OrganizationTwoService;
import ucl.group.excelSystem.api.service.RelatedOrg1AndOrg2;
import ucl.group.talentManageSystem.api.common.PageUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrganizationOneServiceImpl implements OrganizationOneService {

    @Resource
    private OrganizationOneDao organizationOneDao;

    @Resource
    private RelatedOrg1AndOrg2 relatedOrg1AndOrg2;


    private final OrganizationTwoService organizationTwoService;

    @Autowired
    public OrganizationOneServiceImpl(@Lazy OrganizationTwoService organizationTwoService) {
        this.organizationTwoService = organizationTwoService;
    }

    /**
     * 分页查询组织1信息
     *
     * @param param 包含分页参数的Map，例如 "page" 和 "length"
     * @return 包含分页信息和组织信息列表的PageUtils对象
     */
    @Override
    @Transactional
    public PageUtils selectOrganizationOneByPage(Map<String, Object> param) {
        ArrayList<HashMap> list = null;

        long count = organizationOneDao.selectOrganizationOneByPageCount();

        if (count > 0) {
            list = organizationOneDao.selectOrganizationOneByPage(param);
        } else {
            list = new ArrayList<>();
        }

        // 从参数中获取当前页码
        int page = MapUtil.getInt(param, "page");

        // 从参数中获取每页长度
        int length = MapUtil.getInt(param, "length");

        // 创建并返回一个包含分页信息和客户信息列表的PageUtils对象
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    /**
     * 插入一个新的组织1实体到数据库中。
     *
     * @param basicOrganizationEntity 要插入的组织实体对象
     */
    @Override
    @Transactional
    public void insertOrganizationOne(BasicOrganizationEntity basicOrganizationEntity) {
        // 调用 DAO 层的方法将组织实体插入到数据库中
        organizationOneDao.insertOrganizationOne(basicOrganizationEntity);
    }

    /**
     * 更新组织1信息。
     *
     * @param bean 包含要更新的组织信息的实体对象
     */
    @Override
    @Transactional
    public void updateOrganizationOne(BasicOrganizationEntity bean) {
        // 调用数据访问对象的方法来更新组织信息
        organizationOneDao.updateOrganizationOne(bean);
    }

    /**
     * 删除指定的组织。
     * <p>
     * 此方法首先检查要删除的组织是否与其他组织有关联。如果有关联，则抛出运行时异常；否则，删除该组织。
     *
     * @param organizationId 要删除的组织的ID，不能为空
     */
    @Override
    @Transactional
    public void deleteOrganizationOne(@NotEmpty(message = "organizationId不能为空") Long organizationId) {
        // 检查要删除的组织是否与其他组织有关联
        boolean related = relatedOrg1AndOrg2.isRelatedByOrg1Id(organizationId);

        // 如果有关联，抛出异常
        if (related) {
            throw new RuntimeException("该组织下还有其他组织，无法删除");
        } else {
            // 删除组织
            organizationOneDao.deleteOrganizationOne(organizationId);
        }
    }

    /**
     * 根据组织ID查询组织1信息及其相关联的组织2,3信息。
     *
     * @param organizationId 组织ID
     * @return 包含查询到的组织及其相关联组织的列表
     */
    @Override
    @Transactional
    public List<BasicOrganizationEntity> selectOrganizationOneById(Long organizationId) {
        // 查询组织1信息
        BasicOrganizationEntity first = organizationOneDao.selectOrganizationOneById(organizationId);
        List<BasicOrganizationEntity> list = new ArrayList<>();
        list.add(first);

        // 检查该组织是否有相关联的组织2
        boolean related = relatedOrg1AndOrg2.isRelatedByOrg1Id(first.getOrganizationId());
        if (related) {
            // 获取与组织1相关联的所有组织ID
            List<Long> longList = relatedOrg1AndOrg2.selectOrg1AndOrg2ByOrg1Id(first.getOrganizationId());
            // 查询相关联的组织信息
            List<BasicOrganizationEntity> basicOrganizationEntities = organizationTwoService.selectOrganizationTwoByIds(longList);
            // 将相关联的组织信息添加到结果列表中
            list.addAll(basicOrganizationEntities);
        }

        return list;
    }

    /**
     * 查询所有组织1实体信息。
     *
     * @return 返回一个包含所有 {@code BasicOrganizationEntity} 对象的列表。
     */
    @Override
    @Transactional
    public List<BasicOrganizationEntity> selectAll() {
        // 调用 DAO 层的 selectAll 方法查询所有组织实体信息
        return organizationOneDao.selectAll();
    }

    /**
     * 根据ID获取组织1实体信息。
     *
     * @param id 组织实体的唯一标识符
     * @return 返回与给定ID对应的组织实体对象，如果未找到则返回null
     */
    @Override
    public BasicOrganizationEntity getById(Long id) {
        // 调用DAO层方法，根据ID查询组织实体信息
        return organizationOneDao.selectOrganizationOneById(id);
    }
}
