package ucl.group.excelSystem.api.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.db.dao.MonthDao;
import ucl.group.excelSystem.api.db.dao.ProTechDao;
import ucl.group.excelSystem.api.db.dao.RelatedProjectTechnicianDao;
import ucl.group.excelSystem.api.db.dao.TechnicianDao;
import ucl.group.excelSystem.api.db.pojo.BasicMonthEntity;
import ucl.group.excelSystem.api.db.pojo.BasicTechnicianEntity;
import ucl.group.excelSystem.api.db.pojo.RelatedProjectTechnician;
import ucl.group.excelSystem.api.service.TechnicianService;
import ucl.group.talentManageSystem.api.common.PageUtils;
import ucl.group.talentManageSystem.api.common.utils.SecurityUtils;

import javax.annotation.Resource;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TechnicianServiceImpl implements TechnicianService {

    @Resource
    private TechnicianDao technicianDao;
    @Resource
    private ProTechDao proTechDao;
    @Resource
    private RelatedProjectTechnicianDao relatedProjectTechnicianDao;
    @Resource
    private MonthDao monthDao;

    @Override
    @Transactional
    public void insertTech(BasicTechnicianEntity basicTechnicianEntity) {
        basicTechnicianEntity.setCreateBy(SecurityUtils.getUsername());
        basicTechnicianEntity.setDelFlag("0");
        technicianDao.insertTech(basicTechnicianEntity);
    }

    @Override
    @Transactional
    public PageUtils selectTechByPage(Map param) {
        ArrayList<HashMap> list = null;
        long count = technicianDao.selectTechByPageCount(param);

        if (count > 0) list = technicianDao.selectTechByPage(param);
        else list = new ArrayList<>();

        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public void updateTech(BasicTechnicianEntity basicTechnicianEntity) {
        basicTechnicianEntity.setUpdateBy(SecurityUtils.getUsername());
        technicianDao.updateTech(basicTechnicianEntity);
    }

    @Override
    @Transactional
    public void deleteTech(Long[] ids) {
        technicianDao.deleteTech(ids);
        List<Long> proTechIdsList = new ArrayList<>();
        List<Long> monthIdsList = new ArrayList<>();
        //删除任用表
        for (Long id : ids) {
            List<RelatedProjectTechnician> relatedProjectTechnicians = relatedProjectTechnicianDao.searchByTechnicianId(id);
            // 创建一个 Set 来存储唯一的 TechnicianId
            Set<Long> uniqueTechnicianIds = relatedProjectTechnicians.stream()
                    .map(RelatedProjectTechnician::getProjectTechnicianId)
                    .collect(Collectors.toSet());
            // 将 uniqueTechnicianIds 添加到 relIdsList 中，避免重复
            proTechIdsList.addAll(uniqueTechnicianIds);

        }
        if (proTechIdsList.size() > 0) {
            proTechDao.deleteProTech(proTechIdsList.toArray(new Long[0]));
            //删除month表
            for (Long id : proTechIdsList) {
                List<BasicMonthEntity> basicMonthEntity = monthDao.searchByProjectTechId(id);
                monthIdsList.addAll(basicMonthEntity.stream().map(BasicMonthEntity::getMonthId).collect(Collectors.toList()));
                monthDao.remove(monthIdsList.toArray(new Long[0]));
                monthIdsList.clear();
            }
            proTechIdsList.clear();
        }
    }

}
