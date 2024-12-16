package ucl.group.excelSystem.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.web.bind.annotation.*;
import ucl.group.excelSystem.api.controller.request.*;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.excelSystem.api.service.OrganizationOneService;
import ucl.group.talentManageSystem.api.common.PageUtils;
import ucl.group.talentManageSystem.api.common.R;
import ucl.group.talentManageSystem.api.common.annotation.Log;
import ucl.group.talentManageSystem.api.common.enums.BusinessType;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/organizationOne")
public class OrganizationOneController {

    @Resource
    private OrganizationOneService organizationOneService;

    @GetMapping("/getAllByPage")
    //@SaCheckLogin
    public R selectOrganizationOne(@Valid OrganizationPageRequest request) {
        Map<String, Object> param = BeanUtil.beanToMap(request);
        int page = request.getPage();
        int length = request.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = organizationOneService.selectOrganizationOneByPage(param);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/insertOrganizationOne")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.INSERT)
    //@SaCheckLogin
    public R insertOrganizationOne(@RequestBody @Valid InsertOrganizationRequest request) {
        BasicOrganizationEntity basicOrganizationEntity = BeanUtil.toBean(request, BasicOrganizationEntity.class);
        organizationOneService.insertOrganizationOne(basicOrganizationEntity);
        return R.ok();
    }

    @PutMapping("/updateOrganizationOne")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.UPDATE)
    //@SaCheckLogin
    public R updateOrganizationOne(@RequestBody @Valid UpdateOrganizationRequest request) {
        organizationOneService.updateOrganizationOne(BeanUtil.toBean(request, BasicOrganizationEntity.class));
        return R.ok();
    }

    @DeleteMapping("/deleteOrganizationOne")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.DELETE)
    //@SaCheckLogin
    public R deleteOrganizationOne(@RequestBody @Valid DeleteOrganizationRequest request) {
        organizationOneService.deleteOrganizationOne(request.getOrganizationId());
        return R.ok();
    }

    //查询该 id 下的所有组织
    @GetMapping("/selectById")
    public R selectById(@Valid SelectOrganizationRequest request) {
        return R.ok().put("result", organizationOneService.selectOrganizationOneById(request.getOrganizationId()));
    }

    @GetMapping("/selectAll")
    public R selectAll() {
        return R.ok().put("result", organizationOneService.selectAll());
    }

}
