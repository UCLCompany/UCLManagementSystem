package ucl.group.excelSystem.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.web.bind.annotation.*;
import ucl.group.excelSystem.api.controller.request.*;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.excelSystem.api.db.pojo.bo.InsertOrganizationBO;
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

    /**
     * 获取组织3信息分页数据
     *
     * @param request 分页请求参数，包含页码、每页长度等
     * @return 返回包含分页数据的结果对象
     */
    @GetMapping("/getAllByPage")
    //@SaCheckLogin
    public R selectOrganizationThree(@Valid OrganizationPageRequest request) {
        // 将请求参数转换为Map对象
        Map<String, Object> param = BeanUtil.beanToMap(request);

        // 计算分页起始位置
        int page = request.getPage();
        int length = request.getLength();
        int start = (page - 1) * length;
        param.put("start", start);

        // 调用服务方法获取分页数据
        PageUtils pageUtils = organizationThreeService.selectOrganizationThreeByPage(param);

        // 返回包含分页数据的结果对象
        return R.ok().put("result", pageUtils);
    }

    /**
     * 插入新的组织信息到组织表3中。
     *
     * @param request 包含组织信息的请求对象，包括前一个组织ID、组织名称和所属关系
     * @return 操作结果，成功则返回R.ok()
     */
    @PostMapping("/insertOrganizationThree")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.INSERT)
    //@SaCheckLogin
    public R insertOrganizationThree(@RequestBody @Valid InsertOrganizationRequest request) {
        // 创建一个新的插入组织业务对象
        InsertOrganizationBO insertOrganizationBO = new InsertOrganizationBO();

        // 设置前一个组织ID
        insertOrganizationBO.setPreOrganizationId(request.getPreOrganizationId());

        // 设置组织名称
        insertOrganizationBO.setOrganizationName(request.getOrganizationName());

        // 设置所属关系
        insertOrganizationBO.setBelong(request.getBelong());

        // 调用服务层方法插入新的组织信息
        organizationThreeService.insertOrganizationThree(insertOrganizationBO);

        // 返回操作成功的结果
        return R.ok();
    }

    /**
     * 更新组织表3的信息
     *
     * @param request 包含更新组织表1所需信息的请求对象
     * @return 操作结果，成功返回R.ok()
     */
    @PutMapping("/updateOrganizationThree")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.UPDATE)
    //@SaCheckLogin
    public R updateOrganizationThree(@RequestBody @Valid UpdateOrganizationRequest request) {
        // 将请求对象转换为业务对象并调用服务层方法更新组织表1信息
        organizationThreeService.updateOrganizationThree(BeanUtil.toBean(request, UpdateOrganizationBO.class));
        return R.ok();
    }

    /**
     * 删除指定ID的组织3信息。
     *
     * @param request 包含要删除的组织ID的请求对象
     * @return 操作结果，成功则返回R.ok()
     */
    @DeleteMapping("/deleteOrganizationThree")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.DELETE)
    //@SaCheckLogin
    public R deleteOrganizationThree(@RequestBody @Valid DeleteOrganizationRequest request) {
        // 调用服务层方法删除指定ID的组织信息
        organizationThreeService.deleteOrganizationThree(request.getOrganizationId());
        // 返回操作结果
        return R.ok();
    }

    /**
     * 处理 GET 请求，用于查询所有组织3信息。
     *
     * @return 返回一个封装了查询结果的响应对象 R，其中 "result" 键对应查询到的所有组织信息列表。
     */
    @GetMapping("/selectAll")
    public R selectAll() {
        // 调用 service 层的 selectAll 方法获取所有组织信息，并将结果封装到响应对象中
        return R.ok().put("result", organizationThreeService.selectAll());
    }

    /**
     * 根据ID查询组织3信息
     *
     * @param request 包含组织ID的请求对象，通过 {@link SelectOrganizationRequest} 类型的参数传递
     * @return 返回一个 {@link R} 对象，其中包含查询结果
     */
    @GetMapping("/getById")
    public R selectOrganizationThreeById(@Valid SelectOrganizationRequest request) {
        // 调用服务层方法查询组织信息
        return R.ok().put("result", organizationThreeService.selectOrganizationThreeById(request.getOrganizationId()));
    }

    /**
     * 获取上级组织信息
     *
     * @param request 组织分页请求对象，包含分页信息和其他查询条件
     * @return 返回包含上级组织信息的结果对象
     */
    @GetMapping("/getUpper")
    public R getUpper(@Valid OrganizationPageRequest request) {
        // 将请求对象转换为Map，方便后续处理
        Map<String, Object> param = BeanUtil.beanToMap(request);

        // 计算分页起始位置
        int page = request.getPage();
        int length = request.getLength();
        int start = (page - 1) * length;
        param.put("start", start);

        // 调用服务层方法获取上级组织信息
        return R.ok().put("result", organizationThreeService.getUpper(param));
    }
}
