package ucl.group.excelSystem.api.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.db.dao.ProjectDao;
import ucl.group.excelSystem.api.db.pojo.BasicProjectEntity;
import ucl.group.excelSystem.api.service.ProjectV2Service;
import ucl.group.talentManageSystem.api.common.PageUtils;
import ucl.group.talentManageSystem.api.common.utils.SecurityUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ProjectV2ServiceImpl implements ProjectV2Service {
    @Resource
    private ProjectDao projectDao;
    @Override
    @Transactional
    public void insertProject(BasicProjectEntity basicProjectEntity) {
        basicProjectEntity.setCreateBy(SecurityUtils.getUsername());
        basicProjectEntity.setDelFlag("0");
        projectDao.insertProject(basicProjectEntity);
    }

    @Override
    @Transactional
    public void updateProject(BasicProjectEntity basicProjectEntity) {
        basicProjectEntity.setUpdateBy(SecurityUtils.getUsername());
        projectDao.updateProject(basicProjectEntity);
    }

    @Override
    @Transactional
    public void deleteProject(Long[] projectIds) {
        projectDao.deleteProject(projectIds);
    }

    @Override
    @Transactional
    public PageUtils selectProjectByPage(Map param) {
        ArrayList<HashMap> list = null;
        long count = projectDao.selectProjectByPageCount();

        if (count > 0) list = projectDao.selectProjectByPage(param);
        else list = new ArrayList<>();

        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }
}
