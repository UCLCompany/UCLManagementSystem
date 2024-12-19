package ucl.group.excelSystem.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.web.bind.annotation.*;
import ucl.group.excelSystem.api.controller.form.*;
import ucl.group.excelSystem.api.db.pojo.RelatedProjectTechnician;
import ucl.group.excelSystem.api.service.ProTechService;
import ucl.group.talentManageSystem.api.common.PageUtils;
import ucl.group.talentManageSystem.api.common.R;
import ucl.group.talentManageSystem.api.common.annotation.Log;
import ucl.group.talentManageSystem.api.common.enums.BusinessType;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/protech")
public class ProTechController {

	@Resource
	private ProTechService proTechService;

	@Log(title = "管理表格系统v2-项目技术者关联管理", businessType = BusinessType.INSERT)
	@PostMapping
	@SaCheckLogin
	public R insertProTech(@RequestBody @Valid InsertProTechForm form){
		RelatedProjectTechnician relatedProjectTechnician = BeanUtil.toBean(form, RelatedProjectTechnician.class);
		proTechService.insertProTech(relatedProjectTechnician);
		return R.ok();
	}

	@GetMapping
	@SaCheckLogin
	public R selectProTechByPage(@Valid SelectProTechByPageForm form) {
		Map param = BeanUtil.beanToMap(form);
		int page = form.getPage();
		int length = form.getLength();
		int start = (page - 1) * length;
		param.put("start", start);
		PageUtils pageUtils = proTechService.selectProTechByPage(param);

		return R.ok().put("result", pageUtils);
	}

	@GetMapping("/change_price")
	@SaCheckLogin
	public R selectProTechForUpdateOrChange(@RequestParam @Valid long proTechId) {
		HashMap map = proTechService.selectProTechForUpdateOrChange(proTechId);
		if (map == null) return R.ok().put("msg", "未查询到该技术者的信息");
		return R.ok().put("result", map);
	}

	@Log(title = "管理表格系统v2-项目技术者关联管理", businessType = BusinessType.UPDATE)
	@PutMapping
	@SaCheckLogin
	public R updateProTech(@RequestBody @Valid UpdateProTechForm form) {
		proTechService.updateProTech(BeanUtil.toBean(form, RelatedProjectTechnician.class));
		return R.ok();
	}

	@Log(title = "管理表格系统v2-项目技术者关联管理", businessType = BusinessType.DELETE)
	@DeleteMapping
	@SaCheckLogin
	public R deleteProTech(@RequestBody @Valid DeleteProTechForm form) {
		proTechService.deleteProTech(form.getIds());
		return R.ok();
	}

	@Log(title = "管理表格系统v2-项目技术者关联管理", businessType = BusinessType.INSERT)
	@PostMapping("/price")
	@SaCheckLogin
	public R insertProTechChangePrice(@RequestBody @Valid InsertProTechChangePriceForm form) {
		RelatedProjectTechnician relatedProjectTechnician = BeanUtil.toBean(form, RelatedProjectTechnician.class);
		proTechService.insertProTech(relatedProjectTechnician);
		return R.ok();
	}

	@GetMapping("/technician")
	@SaCheckLogin
	public R selectTechnician() {
		ArrayList<HashMap> list = proTechService.selectTechnician();
		return R.ok().put("result", list);
	}

	@GetMapping("/project")
	@SaCheckLogin
	public R selectProject(@RequestParam(required = false) @Valid Long customerId) {
		ArrayList<HashMap> list = proTechService.selectProject(customerId);
		return R.ok().put("result", list);
	}

	@GetMapping("/customer")
	@SaCheckLogin
	public R selectCustomer() {
		ArrayList<HashMap> list = proTechService.selectCustomer();
		return R.ok().put("result", list);
	}

}