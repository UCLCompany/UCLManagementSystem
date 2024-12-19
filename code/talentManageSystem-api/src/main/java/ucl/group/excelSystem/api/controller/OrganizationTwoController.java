package ucl.group.excelSystem.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.web.bind.annotation.*;
import ucl.group.excelSystem.api.controller.request.*;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.db.pojo.BasicOrganizationEntity;
import ucl.group.excelSystem.api.db.pojo.bo.InsertOrganizationBO;
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

    /**
     * 处理获取组织2信息分页请求
     *
     * @param request 组织分页请求对象，包含分页参数和查询条件
     * @return 返回包含分页结果的响应对象
     */
    @GetMapping("/getAllByPage")
    //@SaCheckLogin
    public R selectOrganizationTwo(@Valid OrganizationPageRequest request) {
        // 将请求对象转换为Map，方便后续处理
        Map<String, Object> param = BeanUtil.beanToMap(request);

        // 获取分页参数
        int page = request.getPage();
        int length = request.getLength();

        // 计算分页起始位置
        int start = (page - 1) * length;

        // 将起始位置添加到参数Map中
        param.put("start", start);

        // 调用服务层方法，获取分页结果
        PageUtils pageUtils = organizationTwoService.selectOrganizationTwoByPage(param);

        // 构建并返回响应对象
        return R.ok().put("result", pageUtils);
    }

    /**
     * 插入新的组织信息到组织表2。
     *
     * @param request 包含组织信息的请求对象，包括组织名称、所属部门和上级组织ID
     * @return 操作结果，成功返回R.ok()
     */
    @PostMapping("/insertOrganizationTwo")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.INSERT)
    //@SaCheckLogin
    public R insertOrganizationTwo(@RequestBody @Valid InsertOrganizationRequest request) {
        // 创建一个新的插入组织业务对象
        InsertOrganizationBO insertOrganizationBO = new InsertOrganizationBO();

        // 设置组织名称
        insertOrganizationBO.setOrganizationName(request.getOrganizationName());

        // 设置所属部门
        insertOrganizationBO.setBelong(request.getBelong());

        // 设置上级组织ID
        insertOrganizationBO.setPreOrganizationId(request.getPreOrganizationId());

        // 调用服务层方法插入组织信息
        organizationTwoService.insertOrganizationTwo(insertOrganizationBO);

        // 返回操作成功的响应
        return R.ok();
    }

    /**
     * 更新组织信息的接口。
     * <p>
     * 该接口用于更新组织表中的数据。请求体中需要传递一个有效的 {@link UpdateOrganizationRequest} 对象，
     * 该对象将被转换为 {@link UpdateOrganizationBO} 并调用 {@link OrganizationTwoService#updateOrganizationTwo} 方法进行更新。
     * 成功更新后，返回一个表示操作成功的响应。
     *
     * @param request 包含更新组织信息的请求对象，必须是有效的 {@link UpdateOrganizationRequest}。
     * @return 操作成功时返回 {@link R#ok()}，表示更新成功。
     */
    @PutMapping("/updateOrganizationTwo")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.UPDATE)
    //@SaCheckLogin
    public R updateOrganizationTwo(@RequestBody @Valid UpdateOrganizationRequest request) {
        // 将请求对象转换为业务对象并调用服务层方法进行更新
        organizationTwoService.updateOrganizationTwo(BeanUtil.toBean(request, UpdateOrganizationBO.class));
        return R.ok();
    }

    /**
     * 删除指定ID的组织信息。
     *
     * @param request 包含要删除的组织ID的请求对象
     * @return 操作结果，成功则返回R.ok()
     */
    @DeleteMapping("/deleteOrganizationTwo")
    @Log(title = "管理表格系统v2-组织表1管理", businessType = BusinessType.DELETE)
    //@SaCheckLogin
    public R deleteOrganizationTwo(@RequestBody @Valid DeleteOrganizationRequest request) {
        // 调用服务层方法删除指定ID的组织信息
        organizationTwoService.deleteOrganizationTwo(request.getOrganizationId());

        // 返回操作成功的结果
        return R.ok();
    }

    /**
     * 处理 GET 请求，用于查询所有组织2信息。
     *
     * @return 返回一个封装了查询结果的响应对象 R，其中 "result" 键对应的值是查询到的所有组织信息列表。
     */
    @GetMapping("/selectAll")
    public R selectAll() {
        // 调用 service 层的 selectAll 方法获取所有组织信息，并将结果封装到响应对象中
        return R.ok().put("result", organizationTwoService.selectAll());
    }

    /**
     * 查询指定 ID 下的所有组织
     *
     * @param request 包含组织 ID 的请求对象
     * @return 包含查询结果的响应对象
     */
    @GetMapping("/selectById")
    public R selectById(@Valid SelectOrganizationRequest request) {
        // 调用服务方法查询指定 ID 下的所有组织，并将结果封装到响应对象中
        return R.ok().put("result", organizationTwoService.selectOrganizationTwoById(request.getOrganizationId()));
    }

    /**
     * 查询组织 2 所属的组织 1
     *
     * @param request 组织分页请求对象，包含分页信息和其他查询条件
     * @return 返回一个包含查询结果的响应对象
     */
    @GetMapping("/getUpper")
    public R getUpper(@Valid OrganizationPageRequest request) {
        // 将请求对象转换为 Map，方便后续查询使用
        Map<String, Object> param = BeanUtil.beanToMap(request);

        // 获取分页信息
        int page = request.getPage();
        int length = request.getLength();

        // 计算查询的起始位置
        int start = (page - 1) * length;
        param.put("start", start);

        // 调用服务方法获取上级组织信息，并封装到响应对象中返回
        return R.ok().put("result", organizationTwoService.getUpper(param));
    }
}
