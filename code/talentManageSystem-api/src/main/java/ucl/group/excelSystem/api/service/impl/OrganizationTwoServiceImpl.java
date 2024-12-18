package ucl.group.excelSystem.api.service.impl;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.db.dao.OrganizationOneDao;
import ucl.group.excelSystem.api.db.dao.OrganizationTwoDao;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.excelSystem.api.db.pojo.vo.OrganizationTwoVO;
import ucl.group.excelSystem.api.service.*;
import ucl.group.talentManageSystem.api.common.PageUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrganizationTwoServiceImpl implements OrganizationTwoService {

    @Resource
    private OrganizationTwoDao organizationTwoDao;

    @Resource
    private RelatedOrg1AndOrg2 relatedOrg1AndOrg2;

    @Resource
    private RelatedOrg2AndOrg3 relatedOrg2AndOrg3;


    private final OrganizationThreeService organizationThreeService;
    private final OrganizationOneService organizationOneService;

    @Autowired
    public OrganizationTwoServiceImpl(@Lazy OrganizationThreeService organizationThreeService, @Lazy OrganizationOneService organizationOneService) {
        this.organizationThreeService = organizationThreeService;
        this.organizationOneService = organizationOneService;
    }

    @Override
    @Transactional
    public PageUtils selectOrganizationTwoByPage(Map<String, Object> param) {
        // 初始化客户信息列表为null
        ArrayList<HashMap> list = null;
        // 获取客户信息的总数
        long count = organizationTwoDao.selectOrganizationTwoByPageCount();

        // 如果存在客户信息，则根据参数分页查询
        if (count > 0) list = organizationTwoDao.selectOrganizationTwoByPage(param);
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
    public void insertOrganizationTwo(BasicOrganizationEntity basicOrganizationEntity) {
        //先获取所属组织的 id 值
        Long preOrganization = basicOrganizationEntity.getOrganizationId();
        basicOrganizationEntity.setOrganizationId(null);
        //再获取插入的数据的 id
        organizationTwoDao.insertOrganizationTwo(basicOrganizationEntity);
        long id = basicOrganizationEntity.getOrganizationId();
        relatedOrg1AndOrg2.insertOrg1AndOrg2(preOrganization, id);
    }

    @Override
    @Transactional
    public void updateOrganizationTwo(BasicOrganizationEntity bean) {
        organizationTwoDao.updateOrganizationTwo(bean);
    }

    //判断要删除的组织下还有没有其他组织的人
    @Override
    @Transactional
    public void deleteOrganizationTwo(@NotEmpty(message = "organizationId不能为空") Long organizationId) {
        boolean related = relatedOrg2AndOrg3.isRelatedByOrg2Id(organizationId);
        if (related) {
            throw new RuntimeException("该组织下还有其他组织，无法删除");
        } else {
            organizationTwoDao.deleteOrganizationTwo(organizationId);
        }
    }

    @Override
    @Transactional
    public List<BasicOrganizationEntity> selectOrganizationTwoById(Long organizationId) {
        List<BasicOrganizationEntity> list = new ArrayList<>();
        BasicOrganizationEntity first = organizationTwoDao.selectOrganizationTwoById(organizationId);
        list.add(first);
        boolean related = relatedOrg2AndOrg3.isRelatedByOrg2Id(first.getOrganizationId());
        if (related) {
            List<Long> longList = relatedOrg2AndOrg3.selectOrg2AndOrg3ByOrg2Id(first.getOrganizationId());
            List<BasicOrganizationEntity> basicOrganizationEntities = organizationThreeService.selectOrganizationThreeByIds(longList);
            list.addAll(basicOrganizationEntities);
        }
        return list;
    }

    @Override
    @Transactional
    public List<BasicOrganizationEntity> selectAll() {
        return organizationTwoDao.selectAll();
    }

    @Override
    @Transactional
    public List<BasicOrganizationEntity> selectOrganizationTwoByIds(List<Long> ids) {
        return organizationTwoDao.selectOrganizationTwoByIds(ids);
    }

    @Override
    @Transactional
    public PageUtils getUpper(Map<String, Object> param) {
        int length = (int) param.get("length");
        int start = (int) param.get("start");
        int page = (int) param.get("page");
        List<Long> organizationIds = organizationTwoDao.getAllId();
        List<OrganizationTwoVO> list = new ArrayList<>();
        for (Long organizationId : organizationIds) {
            OrganizationTwoVO orgVO = new OrganizationTwoVO();
            BasicOrganizationEntity org2 = getById(organizationId);
            orgVO.setOrganizationTwoId(org2.getOrganizationId());
            orgVO.setOrganizationName(org2.getOrganizationName());
            orgVO.setBelong(org2.getBelong());

            //组织1
            Long id = relatedOrg1AndOrg2.getOrganizationOneByOrganizationTwoId(organizationId);
            if(id==null) {
                orgVO.setOrganizationOneName(null);
                orgVO.setOrganizationOneBelong(null);
            }else {
                BasicOrganizationEntity org1 = organizationOneService.getById(id);
                Long rId = relatedOrg1AndOrg2.getId(id, organizationId);
                orgVO.setRelatedOrg1AndOrg2Id(rId);
                orgVO.setOrganizationOneId(org1.getOrganizationId());
                orgVO.setOrganizationOneName(org1.getOrganizationName());
                orgVO.setOrganizationOneBelong(org1.getBelong());
            }
            list.add(orgVO);
        }
        List<OrganizationTwoVO> vos = list.subList(start, length);
        int count = vos.size();
        PageUtils pageUtils = new PageUtils(vos, count, page, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public BasicOrganizationEntity getById(Long id) {
        return organizationTwoDao.getById(id);
    }

    @Override
    public List<Long> getAllId() {
        return organizationTwoDao.getAllId();
    }
}
