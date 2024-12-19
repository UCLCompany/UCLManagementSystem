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
import ucl.group.excelSystem.api.db.pojo.bo.InsertOrganizationBO;
import ucl.group.excelSystem.api.db.pojo.bo.UpdateOrganizationBO;
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

    /**
     * 查询二级组织分页数据
     *
     * @param param 包含分页参数的Map，例如页码和每页长度
     * @return 分页结果对象，包含查询到的数据列表、总记录数、当前页码和每页长度
     */
    @Override
    @Transactional
    public PageUtils selectOrganizationTwoByPage(Map<String, Object> param) {

        ArrayList<HashMap> list = null;

        // 获取总记录数
        long count = organizationTwoDao.selectOrganizationTwoByPageCount();

        // 如果有记录，则查询分页数据；否则返回空列表
        if (count > 0) {
            list = organizationTwoDao.selectOrganizationTwoByPage(param);
        } else {
            list = new ArrayList<>();
        }

        // 从参数中获取当前页码
        int page = MapUtil.getInt(param, "page");
        // 从参数中获取每页长度
        int length = MapUtil.getInt(param, "length");

        // 创建并返回分页结果对象
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    /**
     * 插入一个新的组织2信息，并将其与所属组织关联。
     *
     * @param insertOrganizationBO 包含要插入的组织信息的业务对象
     */
    @Override
    @Transactional
    public void insertOrganizationTwo(InsertOrganizationBO insertOrganizationBO) {
        BasicOrganizationEntity basicOrganizationEntity = new BasicOrganizationEntity();
        basicOrganizationEntity.setOrganizationName(insertOrganizationBO.getOrganizationName());
        basicOrganizationEntity.setBelong(insertOrganizationBO.getBelong());
        basicOrganizationEntity.setOrganizationId(null);

        // 获取所属组织的 ID 值
        Long preOrganization = insertOrganizationBO.getPreOrganizationId();

        // 插入新的组织信息并获取生成的 ID
        organizationTwoDao.insertOrganizationTwo(basicOrganizationEntity);
        long id = basicOrganizationEntity.getOrganizationId();

        // 将新插入的组织与所属组织关联
        relatedOrg1AndOrg2.insertOrg1AndOrg2(preOrganization, id);
    }

    /**
     * 更新组织2的信息，并可能更新其所属的组织1。
     *
     * @param bean 包含更新组织2所需信息的业务对象
     */
    @Override
    @Transactional
    public void updateOrganizationTwo(UpdateOrganizationBO bean) {
        BasicOrganizationEntity org2 = new BasicOrganizationEntity();
        Long organizationId = bean.getOrganizationId();
        org2.setOrganizationId(organizationId);
        org2.setOrganizationName(bean.getOrganizationName());
        org2.setBelong(bean.getBelong());

        // 更新组织2的基本信息
        organizationTwoDao.updateOrganizationTwo(org2);

        Long relatedId = bean.getRelatedId();
        Long preOrganizationId = bean.getPreOrganizationId();

        // 如果 relatedId 和 preOrganizationId 都不为空，说明需要更新组织2所属的组织1
        if (relatedId != null && preOrganizationId != null) {
            // 更新组织2所属的组织1
            relatedOrg1AndOrg2.updateOrg1AndOrg2ById(relatedId, preOrganizationId, organizationId);
        }
    }

    /**
     * 删除指定的组织，并检查该组织下是否还有其他关联的组织。
     *
     * @param organizationId 要删除的组织ID，不能为空
     * @throws RuntimeException 如果该组织下还有其他关联的组织，则抛出异常
     */
    @Override
    @Transactional
    public void deleteOrganizationTwo(@NotEmpty(message = "organizationId不能为空") Long organizationId) {
        // 检查该组织下是否还有其他关联的组织
        boolean related = relatedOrg2AndOrg3.isRelatedByOrg2Id(organizationId);

        // 如果有相关联的组织，抛出异常
        if (related) {
            throw new RuntimeException("该组织下还有其他组织，无法删除");
        } else {
            // 如果没有相关联的组织，删除该组织
            organizationTwoDao.deleteOrganizationTwo(organizationId);
        }
    }

    /**
     * 根据组织ID查询组织2及其相关联的组织3。
     *
     * @param organizationId 组织ID
     * @return 包含指定组织及其相关联子组织的列表
     */
    @Override
    @Transactional
    public List<BasicOrganizationEntity> selectOrganizationTwoById(Long organizationId) {
        // 初始化结果列表
        List<BasicOrganizationEntity> list = new ArrayList<>();

        // 查询指定ID的组织
        BasicOrganizationEntity first = organizationTwoDao.selectOrganizationTwoById(organizationId);
        list.add(first);

        // 检查该组织是否与子组织相关联
        boolean related = relatedOrg2AndOrg3.isRelatedByOrg2Id(first.getOrganizationId());
        if (related) {
            // 获取与该组织相关联的所有子组织ID
            List<Long> longList = relatedOrg2AndOrg3.selectOrg2AndOrg3ByOrg2Id(first.getOrganizationId());

            // 查询这些子组织并添加到结果列表中
            List<BasicOrganizationEntity> basicOrganizationEntities = organizationThreeService.selectOrganizationThreeByIds(longList);
            list.addAll(basicOrganizationEntities);
        }

        // 返回包含指定组织及其相关联子组织的列表
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

    /**
     * 获取上级组织信息并分页
     *
     * @param param 包含分页参数的Map，包括：
     *              - "length"：每页显示的记录数
     *              - "start"：起始索引
     *              - "page"：当前页码
     * @return 分页后的组织信息列表
     */
    @Override
    @Transactional
    public PageUtils getUpper(Map<String, Object> param) {
        int length = (int) param.get("length"); // 每页显示的记录数
        int start = (int) param.get("start"); // 起始索引
        int page = (int) param.get("page"); // 当前页码

        // 获取所有二级组织的ID
        List<Long> organizationIds = organizationTwoDao.getAllId();

        // 创建一个空列表来存储组织信息
        List<OrganizationTwoVO> list = new ArrayList<>();

        // 遍历所有二级组织ID
        for (Long organizationId : organizationIds) {
            OrganizationTwoVO orgVO = new OrganizationTwoVO();

            // 获取二级组织的基本信息
            BasicOrganizationEntity org2 = getById(organizationId);
            orgVO.setOrganizationTwoId(org2.getOrganizationId());
            orgVO.setOrganizationName(org2.getOrganizationName());
            orgVO.setBelong(org2.getBelong());

            // 获取一级组织的信息
            Long id = relatedOrg1AndOrg2.getOrganizationOneByOrganizationTwoId(organizationId);
            if (id == null) {
                orgVO.setOrganizationOneName(null);
                orgVO.setOrganizationOneBelong(null);
            } else {
                BasicOrganizationEntity org1 = organizationOneService.getById(id);
                Long rId = relatedOrg1AndOrg2.getId(id, organizationId);
                orgVO.setRelatedOrg1AndOrg2Id(rId);
                orgVO.setOrganizationOneId(org1.getOrganizationId());
                orgVO.setOrganizationOneName(org1.getOrganizationName());
                orgVO.setOrganizationOneBelong(org1.getBelong());
            }

            // 将组织信息添加到列表中
            list.add(orgVO);
        }

        // 获取分页后的组织信息列表
        List<OrganizationTwoVO> vos = list.subList(start, start + length);

        // 计算总记录数
        int count = vos.size();

        // 创建并返回分页工具对象
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
