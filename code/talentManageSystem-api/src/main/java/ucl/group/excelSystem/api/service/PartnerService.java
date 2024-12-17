package ucl.group.excelSystem.api.service;

import org.springframework.stereotype.Service;
import ucl.group.excelSystem.api.db.pojo.BasicPartnerEntity;
import ucl.group.talentManageSystem.api.common.PageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public interface PartnerService {
    public BasicPartnerEntity getPartnerByName(String partnerCompanyName);
    public List<BasicPartnerEntity> getAllPartners();
    public void addPartner(BasicPartnerEntity basicPartnerEntity);
    public void updatePartner(BasicPartnerEntity basicPartnerEntity);
    public void deletePartner(List<Integer> companyids);
    public PageUtils selectCompanyByPage(Map param);
    public ArrayList<HashMap> selectCompany();
}