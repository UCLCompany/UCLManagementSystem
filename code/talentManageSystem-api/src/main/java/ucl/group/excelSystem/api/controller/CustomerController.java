package ucl.group.excelSystem.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.web.bind.annotation.*;
import ucl.group.excelSystem.api.controller.form.DeleteCustomerForm;
import ucl.group.excelSystem.api.controller.form.InsertCustomerForm;
import ucl.group.excelSystem.api.controller.form.SelectCustomerByPageForm;
import ucl.group.excelSystem.api.controller.form.UpdateCustomerForm;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.service.CustomerService;
import ucl.group.talentManageSystem.api.common.PageUtils;
import ucl.group.talentManageSystem.api.common.R;
import ucl.group.talentManageSystem.api.common.annotation.Log;
import ucl.group.talentManageSystem.api.common.enums.BusinessType;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/customer")
public class CustomerController {
    @Resource
    private CustomerService customerService;
    @PostMapping
    @Log(title = "管理表格系统v2-顾客管理", businessType = BusinessType.INSERT)
    @SaCheckLogin
    public R insertCustomer(@RequestBody @Valid InsertCustomerForm form){
        BasicCustomerEntity basicCustomerEntity=BeanUtil.toBean(form, BasicCustomerEntity.class);
        customerService.insertCustomer(basicCustomerEntity);
        return R.ok();
    }
    @PutMapping
    @Log(title = "管理表格系统v2-顾客管理", businessType = BusinessType.UPDATE)
    @SaCheckLogin
    public R updateCustomer(@RequestBody @Valid UpdateCustomerForm form){
        customerService.updateCustomer(BeanUtil.toBean(form,BasicCustomerEntity.class));
        return R.ok();
    }
    @DeleteMapping
    @Log(title = "管理表格系统v2-顾客管理", businessType = BusinessType.DELETE)
    @SaCheckLogin
    public R deleteCustomer(@RequestBody @Valid DeleteCustomerForm form){
        customerService.deleteCustomer(form.getCustomerIds());
        return R.ok();
    }
    @GetMapping
    @SaCheckLogin
    public R selectCustomerByPage(@Valid SelectCustomerByPageForm form){
        Map param=BeanUtil.beanToMap(form);
        int page=form.getPage();
        int length=form.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = customerService.selectCustomerByPage(param);

        return R.ok().put("result", pageUtils);
    }

}
