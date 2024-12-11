package ucl.group.excelSystem.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ucl.group.excelSystem.api.controller.form.*;
import ucl.group.excelSystem.api.db.pojo.vo.*;
import ucl.group.excelSystem.api.service.TableService;
import ucl.group.talentManageSystem.api.common.R;
import ucl.group.talentManageSystem.api.common.annotation.Log;
import ucl.group.talentManageSystem.api.common.enums.BusinessType;
import ucl.group.talentManageSystem.api.common.utils.DateUtils;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v2/table")
public class TableController {
    @Autowired
    private TableService tableService;
    /**
     * @param form:
     * @return R
     * @author he_jiale
     * @description 技术者一览表查询
     * 若开始退场是3月-12月，实际工作时间是3月-11月
     * 若开始结束是3月-12月，实际工作时间是3月-12月
     * @date 2024/07/17 11:04
     */
    @GetMapping("/technicianList")
    @SaCheckLogin
    public R technicianList( @Validated DateForm form) {
        List<TechnicianListVO> technicianListVOS= tableService.searchTechnicianList(
                DateUtils.toLocalDateFirstDay(form.getDateStart()),
                DateUtils.toLocalDateFirstDay(form.getDateEnd())
        );
        return R.ok().put("result", technicianListVOS);
    }
    /**
     * @param forms:
     * @return R
     * @author he_jiale
     * @description 保存技术者一览表
     * @date 2024/07/17 11:05
     */
    @Log(title = "管理表格系统v2-表格管理", businessType = BusinessType.UPDATE)
    @PutMapping("/technicianList")
    @SaCheckLogin
    @SaCheckPermission(value = {"excel_edit"}, mode = SaMode.AND)
    public R saveTechnicianList(@RequestBody List<SaveTechnicianListForm> forms) {
        tableService.saveTechnicianList(forms);
        return R.ok();
    }
    /**
     * @param form:
     * @return R
     * @author he_jiale
     * @description 查询技术者一览统计表
     * 任用管理若新增或者删除  ，会变动数据库的数据
     * @date 2024/07/17 11:05
     */
    @GetMapping("/technicianListStats")
    @SaCheckLogin
    public R technicianListStats( @Valid DateForm form) {
        List<TechnicianListStatsVO> technicianListVOS= tableService.searchTechnicianStatsList(
                DateUtils.toLocalDateFirstDay(form.getDateStart()),
                DateUtils.toLocalDateFirstDay(form.getDateEnd()));
        return R.ok().put("result", technicianListVOS);
    }
    /**
     * @param forms:
     * @return R
     * @author he_jiale
     * @description 保存技术者一览统计表
     * @date 2024/07/17 11:05
     */
    @Log(title = "管理表格系统v2-表格管理", businessType = BusinessType.UPDATE)
    @PutMapping("/technicianListStats")
    @SaCheckLogin
    @SaCheckPermission(value = {"excel_edit"}, mode = SaMode.AND)
    public R saveTechnicianListStats(@RequestBody List<TechnicianListStatsVO> forms) {
        tableService.saveTechnicianListStats(forms);
        return R.ok();
    }
    /**
     * @param form:
     * @return R
     * @author he_jiale
     * @description 查询全体管理表
     * 退场则干活的时间是【开始月，退场月）
     * 契约结束则【开始月，退场月】
     * @date 2024/07/17 11:06
     */
    @GetMapping("/projectTechnicianList")
    @SaCheckLogin
    @SaCheckPermission(value = {"price_search"}, mode = SaMode.AND)
    public R projectTechnicianList( @Valid DateForm form) {
        ProjectTechnicianVO technicianListVOS= tableService.searchProjectTechnicianList(
                DateUtils.toLocalDateFirstDay(form.getDateStart()),
                DateUtils.toLocalDateFirstDay(form.getDateEnd())
        );
        return R.ok().put("result", technicianListVOS);
    }
    /**
     * @param form:
     * @return R
     * @author he_jiale
     * @description 保存全体管理表
     * @date 2024/07/17 11:06
     */
    @Log(title = "管理表格系统v2-表格管理", businessType = BusinessType.UPDATE)
    @PutMapping("/projectTechnicianList")
    @SaCheckLogin
    @SaCheckPermission(value = {"excel_edit"}, mode = SaMode.AND)
    public R saveProjectTechnicianList(@RequestBody @Valid SaveProjectTechnicianListForm form) {
       tableService.saveProjectTechnicianList(form);
        return R.ok();
    }
    /**
     * @param form:
     * @return R
     * @author he_jiale
     * @description 查询作业管理表
     * @date 2024/07/17 11:06
     */
    @GetMapping("/projectMonthList")
    @SaCheckLogin
    @SaCheckPermission(value = {"price_search"}, mode = SaMode.AND)
    public R projectMonthList(@Valid ProjectMonthListForm form){
        List<ProjectMonthListVO> jobManageListVOS=tableService.searchProjectMonthList(DateUtils.toLocalDateFirstDay(form.getDateMonth()),form.getCustomerId());
        return R.ok().put("result", jobManageListVOS);
    }
    /**
     * @param form:
     * @return R
     * @author he_jiale
     * @description 保存作业管理表
     * @date 2024/07/17 11:06
     */
    @Log(title = "管理表格系统v2-表格管理", businessType = BusinessType.UPDATE)
    @PutMapping("/projectMonthList")
    @SaCheckLogin
    @SaCheckPermission(value = {"excel_edit"}, mode = SaMode.AND)
    public R saveProjectMonthList(@RequestBody  @Valid List<ProjectMonthListVO> form){
        tableService.saveProjectMonthList(form);
        return R.ok();
    }
}
