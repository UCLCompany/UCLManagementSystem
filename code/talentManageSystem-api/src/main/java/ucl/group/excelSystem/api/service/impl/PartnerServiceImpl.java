package ucl.group.excelSystem.api.service.impl;

import cn.hutool.core.map.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.db.dao.PartnerDao;
import ucl.group.excelSystem.api.db.pojo.BasicPartnerEntity;
import ucl.group.excelSystem.api.service.PartnerService;
import ucl.group.talentManageSystem.api.common.PageUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PartnerServiceImpl implements PartnerService {
    @Resource
    private PartnerDao partnerDao;

    @Override
    @Transactional
    public void addPartner(BasicPartnerEntity basicPartnerEntity) {
        partnerDao.addPartner(basicPartnerEntity);
    }

    @Override
    @Transactional
    public void updatePartner(BasicPartnerEntity basicPartnerEntity) {
        partnerDao.updatePartner(basicPartnerEntity);
    }

    @Override
    @Transactional
    public void deletePartner(List<Integer> companyids) {
        if (companyids == null || companyids.isEmpty()) {
            throw new IllegalArgumentException("IDs list cannot be null or empty");
        }
        partnerDao.deletePartnerById(companyids);
    }

    @Override
    @Transactional
    public PageUtils selectCompanyByPage(Map param) {
        ArrayList<HashMap> list = null;
        long count = partnerDao.selectCompanyByPageCount();
        if (count > 0) list = (ArrayList<HashMap>) partnerDao.selectCompanyByPage(param);
        else list = new ArrayList<>();
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public ArrayList<HashMap> selectCompany() {
        ArrayList<HashMap> list = partnerDao.selectCompany();
        return list;
    }
}
