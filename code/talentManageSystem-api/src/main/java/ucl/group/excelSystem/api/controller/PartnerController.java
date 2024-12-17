package ucl.group.excelSystem.api.controller;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ucl.group.excelSystem.api.controller.form.SelectCompanyByPageForm;
import ucl.group.excelSystem.api.db.dao.PartnerDao;
import ucl.group.excelSystem.api.db.pojo.BasicPartnerEntity;
import ucl.group.excelSystem.api.service.PartnerService;
import ucl.group.talentManageSystem.api.common.PageUtils;
import ucl.group.talentManageSystem.api.common.R;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/company")
public class PartnerController {
    @Autowired
    private PartnerService partnerService;

//    @GetMapping("/getPartnerByName")
//    public R getPartnerByName(@RequestParam("partnerCompanyName") String partnerCompanyName) {
//        BasicPartnerEntity basicPartnerEntity=partnerService.getPartnerByName(partnerCompanyName);
//        return R.ok().put("result", basicPartnerEntity);
//    }
    @GetMapping("/getAllPartner")
    public R getAllPartner() {
        List<BasicPartnerEntity> basicPartnerEntity=partnerService.getAllPartners();
        return R.ok().put("result",basicPartnerEntity);
    }
    @PostMapping("/Partner")
    public R addPartner(@RequestBody BasicPartnerEntity basicPartnerEntity) {
        partnerService.addPartner(basicPartnerEntity);
        return R.ok();
    }
    @PutMapping("/Partner")
    public R updatePartner(@RequestBody BasicPartnerEntity basicPartnerEntity) {
        partnerService.updatePartner(basicPartnerEntity);
        return R.ok();
    }
    @DeleteMapping("/Delete")
    public R deletePartner(@RequestBody Map<String, List<Integer>> requestBody) {
        List<Integer> companyIds = requestBody.get("ids");
        if (companyIds == null || companyIds.isEmpty()) {
            return R.error("companyIds 参数不能为空");
        }
        partnerService.deletePartner(companyIds);
        return R.ok();
    }
    @GetMapping("/getpartnerById")
    public R selectCompanyByPage(@Valid SelectCompanyByPageForm selectCompanyByPageForm) {
        Map param= BeanUtil.beanToMap(selectCompanyByPageForm);
        int page=selectCompanyByPageForm.getPage();
        int length=selectCompanyByPageForm.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = partnerService.selectCompanyByPage(param);

        return R.ok().put("result", pageUtils);
    }
    @GetMapping("/company")
    public R selectCustomer() {
        ArrayList<HashMap> list = partnerService.selectCompany();
        return R.ok().put("result", list);
    }

}
