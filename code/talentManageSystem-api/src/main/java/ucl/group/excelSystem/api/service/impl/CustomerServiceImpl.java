package ucl.group.excelSystem.api.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.db.dao.CustomerDao;
import ucl.group.excelSystem.api.db.pojo.BasicCustomerEntity;
import ucl.group.excelSystem.api.service.CustomerService;
import ucl.group.talentManageSystem.api.common.PageUtils;
import ucl.group.talentManageSystem.api.common.utils.SecurityUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private CustomerDao customerDao;

    /**
     * 插入客户信息
     *
     * 该方法用于向数据库中插入一个新的客户记录它首先设置创建者的用户名和删除标志，
     * 然后调用DAO层的方法进行实际的数据库插入操作此方法标注了@Transactional，表示
     * 此操作作为一个事务执行，确保数据的一致性
     *
     * @param basicCustomerEntity 待插入的客户实体，包含客户的基本信息
     */
    @Override
    @Transactional
    public void insertCustomer(BasicCustomerEntity basicCustomerEntity) {
        // 设置创建者的用户名，利用SecurityUtils获取当前系统的用户名
        basicCustomerEntity.setCreateBy(SecurityUtils.getUsername());
        // 设置删除标志为"0"，表示该记录未被删除
        basicCustomerEntity.setDelFlag("0");
        // 调用DAO层的方法，执行客户信息的数据库插入操作
        customerDao.insertCustomer(basicCustomerEntity);
    }

    /**
     * 更新客户信息
     *
     * 该方法用于更新客户的基本信息它首先记录当前操作者的用户名，
     * 然后将更新任务委托给customerDao执行此方法特别标注为事务性操作，
     * 意味着其要么完全执行，要么在遇到异常时回滚所有更改，以保持数据一致性
     *
     * @param basicCustomerEntity 包含更新后客户信息的实体对象
     */
    @Override
    @Transactional
    public void updateCustomer(BasicCustomerEntity basicCustomerEntity) {
        // 设置更新者为当前登录用户名，用于追踪修改记录
        basicCustomerEntity.setUpdateBy(SecurityUtils.getUsername());
        // 调用Dao层方法，执行客户信息更新操作
        customerDao.updateCustomer(basicCustomerEntity);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long[] customerIds) {
        customerDao.deleteCustomer(customerIds);
    }

    /**
     * 根据页面参数选择客户信息
     * 此方法使用Spring的@Transactional注解，确保数据一致性
     *
     * @param param 包含分页信息的参数映射，如页码和每页长度
     * @return 返回一个PageUtils对象，其中包含客户信息列表和分页细节
     */
    @Override
    @Transactional
    public PageUtils selectCustomerByPage(Map param) {
        // 初始化客户信息列表为null
        ArrayList<HashMap> list = null;
        // 获取客户信息的总数
        long count = customerDao.selectCustomerByPageCount();

        // 如果存在客户信息，则根据参数分页查询
        if (count > 0) list = customerDao.selectCustomerByPage(param);
        // 否则，创建一个空的列表
        else list = new ArrayList<>();

        // 从参数中获取当前页码
        int page = MapUtil.getInt(param, "page");
        // 从参数中获取每页长度
        int length = MapUtil.getInt(param, "length");
        // 创建并返回一个包含分页信息和客户信息列表的PageUtils对象
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }
}
