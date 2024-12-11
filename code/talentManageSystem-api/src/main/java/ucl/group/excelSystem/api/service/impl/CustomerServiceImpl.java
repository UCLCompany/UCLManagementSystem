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
    @Override
    @Transactional
    public void insertCustomer(BasicCustomerEntity basicCustomerEntity){
        basicCustomerEntity.setCreateBy(SecurityUtils.getUsername());
        basicCustomerEntity.setDelFlag("0");
        customerDao.insertCustomer(basicCustomerEntity);
    }

    @Override
    @Transactional
    public void updateCustomer(BasicCustomerEntity basicCustomerEntity) {
        basicCustomerEntity.setUpdateBy(SecurityUtils.getUsername());
        customerDao.updateCustomer(basicCustomerEntity);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long[] customerIds) {
        customerDao.deleteCustomer(customerIds);
    }

    @Override
    @Transactional
    public PageUtils selectCustomerByPage(Map param) {
        ArrayList<HashMap> list = null;
        long count = customerDao.selectCustomerByPageCount();

        if (count > 0) list = customerDao.selectCustomerByPage(param);
        else list = new ArrayList<>();

        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }
}
