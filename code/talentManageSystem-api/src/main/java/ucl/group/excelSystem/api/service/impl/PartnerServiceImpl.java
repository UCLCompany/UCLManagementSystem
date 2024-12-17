package ucl.group.excelSystem.api.service.impl;

import cn.hutool.core.map.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public BasicPartnerEntity getPartnerByName(String partnerCompanyName) {
        return partnerDao.getPartnerByName(partnerCompanyName);
    }

    @Override
    public void addPartner(BasicPartnerEntity basicPartnerEntity) {
        partnerDao.addPartner(basicPartnerEntity);
    }

    @Override
    public List<BasicPartnerEntity> getAllPartners() {
        return partnerDao.getAllPartners();
    }
    @Override
    public void updatePartner(BasicPartnerEntity basicPartnerEntity) {
        partnerDao.updatePartner(basicPartnerEntity);
    }

    @Override
    public void deletePartner(List<Integer> companyids) {
        if (companyids == null || companyids.isEmpty()) {
            throw new IllegalArgumentException("IDs list cannot be null or empty");
        }
        partnerDao.deletePartnerById(companyids);
    }

    @Override
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
    public ArrayList<HashMap> selectCompany() {
        ArrayList<HashMap> list = partnerDao.selectCompany();
        return list;
    }
}
