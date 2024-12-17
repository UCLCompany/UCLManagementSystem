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

    @Override
    @Transactional
    public PageUtils selectOrganizationOneByPage(Map<String, Object> param) {
        // 初始化客户信息列表为null
        ArrayList<HashMap> list = null;
        // 获取客户信息的总数
        long count = organizationOneDao.selectOrganizationOneByPageCount();

        // 如果存在客户信息，则根据参数分页查询
        if (count > 0) list = organizationOneDao.selectOrganizationOneByPage(param);
            // 否则，创建一个空的列表
        else list = new ArrayList<>();

        // 从参数中获取当前页码
        int page = MapUtil.getInt(param, "page");
        // 从参数中获取每页长度
        int length = MapUtil.getInt(param, "length");
        // 创建并返回一个包含分页信息和客户信息列表的PageUtils对象
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public void insertOrganizationOne(BasicOrganizationEntity basicOrganizationEntity) {
        organizationOneDao.insertOrganizationOne(basicOrganizationEntity);
    }

    @Override
    @Transactional
    public void updateOrganizationOne(BasicOrganizationEntity bean) {
        organizationOneDao.updateOrganizationOne(bean);
    }

    //判断要删除的组织下还有没有其他组织的人
    @Override
    @Transactional
    public void deleteOrganizationOne(@NotEmpty(message = "organizationId不能为空") Long organizationId) {
        boolean related = relatedOrg1AndOrg2.isRelatedByOrg1Id(organizationId);
        if(related) {
            throw new RuntimeException("该组织下还有其他组织，无法删除");
        } else {
            organizationOneDao.deleteOrganizationOne(organizationId);
        }
    }

    @Override
    @Transactional
    public List<BasicOrganizationEntity> selectOrganizationOneById(Long organizationId) {
        BasicOrganizationEntity first = organizationOneDao.selectOrganizationOneById(organizationId);
        List<BasicOrganizationEntity> list = new ArrayList<>();
        list.add(first);
        boolean related = relatedOrg1AndOrg2.isRelatedByOrg1Id(first.getOrganizationId());
        if(related) {
            List<Long> longList = relatedOrg1AndOrg2.selectOrg1AndOrg2ByOrg1Id(first.getOrganizationId());
            List<BasicOrganizationEntity> basicOrganizationEntities = organizationTwoService.selectOrganizationTwoByIds(longList);
            list.addAll(basicOrganizationEntities);
        }
        return list;
    }

    @Override
    @Transactional
    public List<BasicOrganizationEntity> selectAll() {
        return organizationOneDao.selectAll();
    }

    @Override
    public BasicOrganizationEntity getById(Long id) {
        return organizationOneDao.selectOrganizationOneById(id);
    }
}
