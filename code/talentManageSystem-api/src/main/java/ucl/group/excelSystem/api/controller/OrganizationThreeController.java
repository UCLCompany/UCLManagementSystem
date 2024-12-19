package ucl.group.excelSystem.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.web.bind.annotation.*;
import ucl.group.excelSystem.api.controller.request.*;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.excelSystem.api.db.pojo.bo.UpdateOrganizationBO;
import ucl.group.excelSystem.api.service.OrganizationThreeService;
import ucl.group.talentManageSystem.api.common.PageUtils;
import ucl.group.talentManageSystem.api.common.R;
import ucl.group.talentManageSystem.api.common.annotation.Log;
import ucl.group.talentManageSystem.api.common.enums.BusinessType;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/organizationThree")
public class OrganizationThreeController {

    @Resource
    private OrganizationThreeService organizationThreeService;

    @GetMapping("/getAllByPage")
    //@SaCheckLogin
    public R selectOrganizationThree(@Valid OrganizationPageRequest request) {
        Map<String, Object> param = BeanUtil.beanToMap(request);
        int page = request.getPage();
        int length = request.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = organizationThreeService.selectOrganizationThreeByPage(param);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/insertOrganizationThree")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.INSERT)
    //@SaCheckLogin
    public R insertOrganizationThree(@RequestBody @Valid InsertOrganizationRequest request){
        BasicOrganizationEntity basicOrganizationEntity = BeanUtil.toBean(request, BasicOrganizationEntity.class);
        organizationThreeService.insertOrganizationThree(basicOrganizationEntity);
        return R.ok();
    }
    @PutMapping("/updateOrganizationThree")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.UPDATE)
    //@SaCheckLogin
    public R updateOrganizationThree(@RequestBody @Valid UpdateOrganizationRequest request){
        organizationThreeService.updateOrganizationThree(BeanUtil.toBean(request, UpdateOrganizationBO.class));
        return R.ok();
    }
    @DeleteMapping("/deleteOrganizationThree")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.DELETE)
    //@SaCheckLogin
    public R deleteOrganizationThree(@RequestBody @Valid DeleteOrganizationRequest request){
        organizationThreeService.deleteOrganizationThree(request.getOrganizationId());
        return R.ok();
    }

    @GetMapping("/selectAll")
    public R selectAll(){
        return R.ok().put("result",organizationThreeService.selectAll());
    }

    //查询 id
    @GetMapping("/getById")
    public R selectOrganizationThreeById(@Valid SelectOrganizationRequest request){
        return R.ok().put("result",organizationThreeService.selectOrganizationThreeById(request.getOrganizationId()));
    }

    //往上查
    @GetMapping("/getUpper")
    public R getUpper(@Valid OrganizationPageRequest request){
        Map<String, Object> param = BeanUtil.beanToMap(request);
        int page = request.getPage();
        int length = request.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        return R.ok().put("result", organizationThreeService.getUpper(param));
    }
}
