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

    /**
     * 获取组织信息分页数据
     *
     * @param request 分页请求参数，包含页码、每页长度等信息
     * @return 包含分页结果的响应对象
     */
    @GetMapping("/getAllByPage")
    //@SaCheckLogin
    public R selectOrganizationOne(@Valid OrganizationPageRequest request) {
        // 将请求参数转换为Map对象
        Map<String, Object> param = BeanUtil.beanToMap(request);

        // 获取页码和每页长度
        int page = request.getPage();
        int length = request.getLength();

        // 计算起始位置
        int start = (page - 1) * length;

        // 将起始位置添加到参数Map中
        param.put("start", start);

        // 调用服务方法获取分页数据
        PageUtils pageUtils = organizationOneService.selectOrganizationOneByPage(param);

        // 返回包含分页结果的响应对象
        return R.ok().put("result", pageUtils);
    }

    /**
     * 插入一个新的组织1信息到数据库。
     *
     * @param request 包含组织信息的请求对象，通过 {@link RequestBody} 注解接收 JSON 格式的请求体，并使用 {@link Valid} 注解进行校验。
     * @return 返回一个 {@link R} 对象，表示操作结果。如果插入成功，返回 {@link R#ok()} 表示成功。
     */
    @PostMapping("/insertOrganizationOne")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.INSERT)
    //@SaCheckLogin
    public R insertOrganizationOne(@RequestBody @Valid InsertOrganizationRequest request) {
        // 将请求对象转换为实体对象
        BasicOrganizationEntity basicOrganizationEntity = BeanUtil.toBean(request, BasicOrganizationEntity.class);

        // 调用服务层方法插入组织信息
        organizationOneService.insertOrganizationOne(basicOrganizationEntity);

        // 返回操作结果
        return R.ok();
    }

    /**
     * 更新组织表1的信息。
     *
     * @param request 包含更新组织表1所需信息的请求对象，通过HTTP请求体传递并进行校验。
     * @return 返回一个结果对象，表示操作是否成功。
     */
    @PutMapping("/updateOrganizationOne")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.UPDATE)
    //@SaCheckLogin
    public R updateOrganizationOne(@RequestBody @Valid UpdateOrganizationRequest request) {
        // 将请求对象转换为BasicOrganizationEntity对象，并调用服务层方法更新组织表1的信息
        organizationOneService.updateOrganizationOne(BeanUtil.toBean(request, BasicOrganizationEntity.class));
        // 返回操作成功的结果
        return R.ok();
    }

    /**
     * 删除指定ID的组织1信息。
     *
     * @param request 包含要删除的组织ID的请求对象
     * @return 操作结果，成功返回R.ok()
     */
    @DeleteMapping("/deleteOrganizationOne")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.DELETE)
    //@SaCheckLogin
    public R deleteOrganizationOne(@RequestBody @Valid DeleteOrganizationRequest request) {
        // 调用服务层方法删除指定ID的组织信息
        organizationOneService.deleteOrganizationOne(request.getOrganizationId());
        // 返回操作成功的响应
        return R.ok();
    }

    /**
     * 查询指定 ID 下的所有组织
     *
     * @param request 包含组织 ID 的请求对象，用于验证和传递参数
     * @return 返回一个包含查询结果的响应对象，其中 "result" 字段包含了查询到的组织信息
     */
    @GetMapping("/selectById")
    public R selectById(@Valid SelectOrganizationRequest request) {
        // 调用服务层方法查询指定 ID 的组织信息
        return R.ok().put("result", organizationOneService.selectOrganizationOneById(request.getOrganizationId()));
    }

    /**
     * 处理 GET 请求，用于查询所有组织信息。
     *
     * @return 返回一个封装了查询结果的响应对象 R。
     * - 响应对象中包含一个键为 "result" 的属性，值为从 organizationOneService 中查询到的所有组织信息列表。
     */
    @GetMapping("/selectAll")
    public R selectAll() {
        // 调用 service 层的 selectAll 方法获取所有组织信息，并将结果封装到响应对象中
        return R.ok().put("result", organizationOneService.selectAll());
    }

    /**
     * 根据组织ID获取组织信息
     *
     * @param request 包含组织ID的请求对象，用于验证和传递参数
     * @return 返回一个封装了结果的响应对象，其中包含查询到的组织信息
     */
    @GetMapping("/getById")
    public R selectOrganizationOneById(@Valid SelectOrganizationRequest request) {
        // 调用服务层方法，根据组织ID获取组织信息
        return R.ok().put("result", organizationOneService.getById(request.getOrganizationId()));
    }
}
