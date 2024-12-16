package ucl.group.excelSystem.api.service.impl;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.db.dao.OrganizationOneDao;
import ucl.group.excelSystem.api.db.dao.OrganizationThreeDao;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.excelSystem.api.service.OrganizationOneService;
import ucl.group.excelSystem.api.service.OrganizationThreeService;
import ucl.group.excelSystem.api.service.RelatedOrg2AndOrg3;
import ucl.group.talentManageSystem.api.common.PageUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrganizationThreeServiceImpl implements OrganizationThreeService {

    @Resource
    private OrganizationThreeDao organizationThreeDao;

    @Resource
    private RelatedOrg2AndOrg3 relatedOrg2AndOrg3;

    @Override
    @Transactional
    public PageUtils selectOrganizationThreeByPage(Map<String, Object> param) {
        // 初始化客户信息列表为null
        ArrayList<HashMap> list = null;
        // 获取客户信息的总数
        long count = organizationThreeDao.selectOrganizationThreeByPageCount();

        // 如果存在客户信息，则根据参数分页查询
        if (count > 0) list = organizationThreeDao.selectOrganizationThreeByPage(param);
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
    public void insertOrganizationThree(BasicOrganizationEntity basicOrganizationEntity) {
        //先获取前一个组织的 id
        Long preOrganizationId = basicOrganizationEntity.getOrganizationId();
        basicOrganizationEntity.setOrganizationId(null);
        organizationThreeDao.insertOrganizationThree(basicOrganizationEntity);
        long id = basicOrganizationEntity.getOrganizationId();
        relatedOrg2AndOrg3.insertOrg2AndOrg3(preOrganizationId, id);
    }

    @Override
    @Transactional
    public void updateOrganizationThree(BasicOrganizationEntity bean) {
        organizationThreeDao.updateOrganizationThree(bean);
    }

    @Override
    @Transactional
    public void deleteOrganizationThree(@NotEmpty(message = "organizationId不能为空") Long organizationId) {
        organizationThreeDao.deleteOrganizationThree(organizationId);
    }

    @Override
    @Transactional
    public BasicOrganizationEntity selectOrganizationThreeById(Long organizationId) {
        return organizationThreeDao.selectOrganizationThreeById(organizationId);
    }

    @Override
    @Transactional
    public List<BasicOrganizationEntity> selectAll() {
        return organizationThreeDao.selectAll();
    }

    @Override
    @Transactional
    public List<BasicOrganizationEntity> selectOrganizationThreeByIds(List<Long> ids) {
        return organizationThreeDao.selectOrganizationThreeByIds(ids);
    }
}
