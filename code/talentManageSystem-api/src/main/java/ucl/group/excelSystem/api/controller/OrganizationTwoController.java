package ucl.group.excelSystem.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.web.bind.annotation.*;
import ucl.group.excelSystem.api.controller.request.*;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.excelSystem.api.db.pojo.bo.UpdateOrganizationBO;
import ucl.group.excelSystem.api.service.OrganizationTwoService;
import ucl.group.excelSystem.api.service.RelatedOrg1AndOrg2;
import ucl.group.talentManageSystem.api.common.PageUtils;
import ucl.group.talentManageSystem.api.common.R;
import ucl.group.talentManageSystem.api.common.annotation.Log;
import ucl.group.talentManageSystem.api.common.enums.BusinessType;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/organizationTwo")
public class OrganizationTwoController {


    @Resource
    private OrganizationTwoService organizationTwoService;
    
    @GetMapping("/getAllByPage")
    //@SaCheckLogin
    public R selectOrganizationTwo(@Valid OrganizationPageRequest request) {
        Map<String, Object> param = BeanUtil.beanToMap(request);
        int page = request.getPage();
        int length = request.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = organizationTwoService.selectOrganizationTwoByPage(param);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/insertOrganizationTwo")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.INSERT)
    //@SaCheckLogin
    public R insertOrganizationTwo(@RequestBody @Valid InsertOrganizationRequest request){
        BasicOrganizationEntity basicOrganizationEntity = BeanUtil.toBean(request, BasicOrganizationEntity.class);
        organizationTwoService.insertOrganizationTwo(basicOrganizationEntity);
        return R.ok();
    }
    @PutMapping("/updateOrganizationTwo")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.UPDATE)
    //@SaCheckLogin
    public R updateOrganizationTwo(@RequestBody @Valid UpdateOrganizationRequest request){
        organizationTwoService.updateOrganizationTwo(BeanUtil.toBean(request, UpdateOrganizationBO.class));
        return R.ok();
    }
    @DeleteMapping("/deleteOrganizationTwo")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.DELETE)
    //@SaCheckLogin
    public R deleteOrganizationTwo(@RequestBody @Valid DeleteOrganizationRequest request){
        organizationTwoService.deleteOrganizationTwo(request.getOrganizationId());
        return R.ok();
    }

    @GetMapping("/selectAll")
    public R selectAll(){
        return R.ok().put("result",organizationTwoService.selectAll());
    }

    //查询该 id 下的所有组织
    @GetMapping("/selectById")
    public R selectById(@Valid SelectOrganizationRequest request){
        return R.ok().put("result",organizationTwoService.selectOrganizationTwoById(request.getOrganizationId()));
    }

    //查询组织 2 所属的组织 1
    @GetMapping("/getUpper")
    public R getUpper(@Valid OrganizationPageRequest request){
        Map<String, Object> param = BeanUtil.beanToMap(request);
        int page = request.getPage();
        int length = request.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        return R.ok().put("result", organizationTwoService.getUpper(param));
    }
}
