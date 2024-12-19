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
import ucl.group.excelSystem.api.db.pojo.bo.InsertOrganizationBO;
import ucl.group.excelSystem.api.db.pojo.bo.UpdateOrganizationBO;
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


    /**
     * 根据分页参数查询组织3信息
     *
     * @param param 分页参数，包含 "page" 和 "length" 键
     * @return 包含查询结果和分页信息的 PageUtils 对象
     */
    @Override
    @Transactional
    public PageUtils selectOrganizationThreeByPage(Map<String, Object> param) {

        ArrayList<HashMap> list = null;

        // 查询总记录数
        long count = organizationThreeDao.selectOrganizationThreeByPageCount();

        // 如果有记录，查询分页数据；否则，创建一个空的列表
        if (count > 0) {
            list = organizationThreeDao.selectOrganizationThreeByPage(param);
        } else {
            list = new ArrayList<>();
        }

        // 从参数中获取当前页码
        int page = MapUtil.getInt(param, "page");
        // 从参数中获取每页长度
        int length = MapUtil.getInt(param, "length");

        // 创建并返回分页工具对象
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    /**
     * 插入一个新的组织3，并将其与前一个组织关联。
     *
     * @param insertOrganizationBO 包含组织信息的业务对象
     */
    @Override
    @Transactional
    public void insertOrganizationThree(InsertOrganizationBO insertOrganizationBO) {
        BasicOrganizationEntity basicOrganizationEntity = new BasicOrganizationEntity();
        basicOrganizationEntity.setOrganizationId(null);
        basicOrganizationEntity.setOrganizationName(insertOrganizationBO.getOrganizationName());
        basicOrganizationEntity.setBelong(insertOrganizationBO.getBelong());

        // 获取前一个组织的 ID
        Long preOrganizationId = insertOrganizationBO.getPreOrganizationId();

        // 插入新的组织3
        organizationThreeDao.insertOrganizationThree(basicOrganizationEntity);

        // 获取新插入组织的 ID
        long id = basicOrganizationEntity.getOrganizationId();

        // 将新插入的组织3与前一个组织关联
        relatedOrg2AndOrg3.insertOrg2AndOrg3(preOrganizationId, id);
    }

    /**
     * 更新组织3信息，并处理相关联的组织关系。
     *
     * @param bean 包含更新组织所需信息的业务对象
     */
    @Override
    @Transactional
    public void updateOrganizationThree(UpdateOrganizationBO bean) {
        // 创建一个新的组织实体对象
        BasicOrganizationEntity org3 = new BasicOrganizationEntity();

        // 获取组织ID并设置到实体对象中
        Long organizationId = bean.getOrganizationId();
        org3.setOrganizationId(organizationId);

        // 设置组织名称和所属信息
        org3.setOrganizationName(bean.getOrganizationName());
        org3.setBelong(bean.getBelong());

        // 更新组织信息
        organizationThreeDao.updateOrganizationThree(org3);

        // 获取前一个组织ID和相关联的ID
        Long preOrganizationId = bean.getPreOrganizationId();
        Long relatedId = bean.getRelatedId();

        // 如果相关联的ID和前一个组织ID都存在，则更新相关联的组织关系
        if (relatedId != null && preOrganizationId != null) {
            relatedOrg2AndOrg3.updateOrg2AndOrg3ById(relatedId, preOrganizationId, organizationId);
        }
    }

    /**
     * 删除指定ID的组织3。
     *
     * @param organizationId 组织机构的唯一标识，不能为空。
     */
    @Override
    @Transactional
    public void deleteOrganizationThree(@NotEmpty(message = "organizationId不能为空") Long organizationId) {
        // 调用DAO层方法删除指定ID的组织机构
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

    /**
     * 获取组织结构的上级信息，并分页返回。
     *
     * @param param 包含分页参数的Map，包括：
     *              - "length"：每页显示的记录数
     *              - "start"：起始索引
     *              - "page"：当前页码
     * @return 分页后的组织结构信息
     */
    @Override
    @Transactional
    public PageUtils getUpper(Map<String, Object> param) {
        int length = (int) param.get("length");
        int start = (int) param.get("start");
        int page = (int) param.get("page");

        // 初始化结果列表
        List<OrganizationThreeVO> list = new ArrayList<>();

        // 获取所有三级组织的ID
        List<Long> organizationIds = organizationThreeDao.getAllId();

        // 遍历每个三级组织ID，构建对应的组织结构信息
        for (Long organizationId : organizationIds) {
            OrganizationThreeVO orgVO = new OrganizationThreeVO();

            // 获取三级组织的基本信息
            BasicOrganizationEntity org3 = organizationThreeDao.getById(organizationId);
            orgVO.setOrganizationThreeId(org3.getOrganizationId());
            orgVO.setOrganizationName(org3.getOrganizationName());
            orgVO.setBelong(org3.getBelong());

            // 获取二级组织的信息
            Long org2Id = relatedOrg2AndOrg3.selectOrg2AndOrg3ByOrg3Id(organizationId);
            if (org2Id == null) {
                orgVO.setOrganizationTwoName(null);
                orgVO.setOrganizationTwoBelong(null);
            } else {
                BasicOrganizationEntity org2 = organizationTwoService.getById(org2Id);
                Long rId23 = relatedOrg2AndOrg3.getId(org2Id, organizationId);
                orgVO.setRelatedOrg2AndOrg3Id(rId23);
                orgVO.setOrganizationTwoId(org2Id);
                orgVO.setOrganizationTwoName(org2.getOrganizationName());
                orgVO.setOrganizationTwoBelong(org2.getBelong());
            }

            // 获取一级组织的信息
            Long org1Id = relatedOrg1AndOrg2.getOrganizationOneByOrganizationTwoId(org2Id);
            if (org1Id == null) {
                orgVO.setOrganizationOneName(null);
                orgVO.setOrganizationOneBelong(null);
            } else {
                BasicOrganizationEntity org1 = organizationOneService.getById(org1Id);
                Long rId12 = relatedOrg1AndOrg2.getId(org1Id, org2Id);
                orgVO.setRelatedOrg1AndOrg2Id(rId12);
                orgVO.setOrganizationOneId(org1Id);
                orgVO.setOrganizationOneName(org1.getOrganizationName());
                orgVO.setOrganizationOneBelong(org1.getBelong());
            }

            // 将构建好的组织结构信息添加到结果列表中
            list.add(orgVO);
        }

        // 根据分页参数截取结果列表
        List<OrganizationThreeVO> vos = list.subList(start, start + length);

        // 计算总记录数
        int count = vos.size();

        // 构建并返回分页结果
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
