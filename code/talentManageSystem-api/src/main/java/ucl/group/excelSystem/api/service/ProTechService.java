package ucl.group.excelSystem.api.service;

import ucl.group.excelSystem.api.db.pojo.RelatedProjectTechnician;
import ucl.group.talentManageSystem.api.common.PageUtils;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface ProTechService {
	/**
	* @Description: [relatedProjectTechnician] 处理价格变动和退场
	* 假设价格变动为C月
	 * 旧记录 开始月为A，结束月B 退场月  价格变动月
	 * 价格变动后
	 * 新的旧记录 开始月为A，结束月C-1 退场月C  价格变动月C
	 * 产生新记录 开始月为A，结束月B   退场月   价格变动月
	* @Param: [relatedProjectTechnician]
	* @return: void
	* @Author: he_jiale
	* @Date: 15:05 2024/09/13
	*/
	public void insertProTech(RelatedProjectTechnician relatedProjectTechnician);

	public PageUtils selectProTechByPage(Map param);

	public HashMap selectProTechForUpdateOrChange(long proTechId);

	public void updateProTech(RelatedProjectTechnician relatedProjectTechnician);

	public void deleteProTech(Long[] ids);

	public ArrayList<HashMap> selectTechnician();
	public ArrayList<HashMap> selectProject(Long customerId);
	public ArrayList<HashMap> selectCustomer();

}
