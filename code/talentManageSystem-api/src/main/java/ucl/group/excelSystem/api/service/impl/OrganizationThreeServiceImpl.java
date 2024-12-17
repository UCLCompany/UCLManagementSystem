package ucl.group.excelSystem.api.service.impl;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.db.dao.OrganizationOneDao;
import ucl.group.excelSystem.api.db.dao.OrganizationThreeDao;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.excelSystem.api.db.pojo.vo.OrganizationThreeVO;
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
public class OrganizationThreeServiceImpl implements OrganizationThreeService {

    @Resource
    private OrganizationThreeDao organizationThreeDao;

    @Resource
    private RelatedOrg2AndOrg3 relatedOrg2AndOrg3;

    @Resource
    private RelatedOrg1AndOrg2 relatedOrg1AndOrg2;


    private final OrganizationTwoService organizationTwoService;
    private final OrganizationOneService organizationOneService;

    @Autowired
    public OrganizationThreeServiceImpl(@Lazy OrganizationTwoService organizationTwoService, @Lazy OrganizationOneService organizationOneService) {
        this.organizationTwoService = organizationTwoService;
        this.organizationOneService = organizationOneService;
    }


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

    @Override
    @Transactional
    public PageUtils getUpper(Map<String, Object> param) {
        int length = (int) param.get("length");
        int start = (int) param.get("start");
        int page = (int) param.get("page");
        List<OrganizationThreeVO> list = new ArrayList<>();
        List<Long> organizationIds = organizationThreeDao.getAllId();
        for (Long organizationId : organizationIds) {
            OrganizationThreeVO orgVO = new OrganizationThreeVO();
            BasicOrganizationEntity org3 = organizationThreeDao.getById(organizationId);
            orgVO.setOrganizationId(org3.getOrganizationId());
            orgVO.setOrganizationName(org3.getOrganizationName());
            orgVO.setBelong(org3.getBelong());

            Long org2Id = relatedOrg2AndOrg3.selectOrg2AndOrg3ByOrg3Id(organizationId);
            if (org2Id == null) {
                orgVO.setOrganizationTwoName(null);
                orgVO.setOrganizationTwoBelong(null);
            } else {
                BasicOrganizationEntity org2 = organizationTwoService.getById(org2Id);
                orgVO.setOrganizationTwoName(org2.getOrganizationName());
                orgVO.setOrganizationTwoBelong(org2.getBelong());
            }
            Long org1Id = relatedOrg1AndOrg2.getOrganizationOneByOrganizationTwoId(org2Id);
            if (org1Id == null) {
                orgVO.setOrganizationOneName(null);
                orgVO.setOrganizationOneBelong(null);
            } else {
                BasicOrganizationEntity org1 = organizationOneService.getById(org1Id);
                orgVO.setOrganizationOneName(org1.getOrganizationName());
                orgVO.setOrganizationOneBelong(org1.getBelong());
            }
            list.add(orgVO);
        }

        List<OrganizationThreeVO> vos = list.subList(start, length);
        int count = vos.size();
        PageUtils pageUtils = new PageUtils(vos, count, page, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public BasicOrganizationEntity getById(Long id) {
        return organizationThreeDao.getById(id);
    }

    @Override
    public List<Long> getAllId() {
        return organizationThreeDao.getAllId();
    }
}
