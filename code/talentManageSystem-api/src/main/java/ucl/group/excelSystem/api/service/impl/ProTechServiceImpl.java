package ucl.group.excelSystem.api.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.common.utils.TableUtils;
import ucl.group.excelSystem.api.db.dao.ProTechDao;
import ucl.group.excelSystem.api.db.dao.StatsDao;
import ucl.group.excelSystem.api.db.dao.TechnicianDao;
import ucl.group.excelSystem.api.db.pojo.BasicStatsEntity;
import ucl.group.excelSystem.api.db.pojo.BasicTechnicianEntity;
import ucl.group.excelSystem.api.db.pojo.RelatedProjectTechnician;
import ucl.group.excelSystem.api.service.MonthService;
import ucl.group.excelSystem.api.service.ProTechService;
import ucl.group.talentManageSystem.api.common.PageUtils;
import ucl.group.talentManageSystem.api.common.utils.DateUtils;
import ucl.group.talentManageSystem.api.common.utils.SecurityUtils;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProTechServiceImpl implements ProTechService {

    @Resource
    private StatsDao statsDao;

    @Resource
    private ProTechDao proTechDao;

    @Resource
    private TechnicianDao technicianDao;


//    private final RedisTemplate<Object, Object> redisTemplate;
//    private final HashOperations<Object, Object, Object> hashOperations;

//    public ProTechServiceImpl(RedisTemplate<Object, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//        this.hashOperations = redisTemplate.opsForHash();
//    }

    /**
     * 创建一条项目-技术者记录
     *
     * @param relatedProjectTechnicianS 实体类
     */
    @Override
    public void insertProTech(RelatedProjectTechnician relatedProjectTechnicianS) {
        if (relatedProjectTechnicianS.getSelected().equals("insert")) {
            addProTech(relatedProjectTechnicianS);
        } else if (relatedProjectTechnicianS.getSelected().equals("priceChange")) {
            priceChange(relatedProjectTechnicianS);
        }
    }

    /**
     * @param :
     * @return void
     * @author he_jiale
     * @description 追加
     * @date 2024/7/24 17:22
     */
    @Transactional
    public void addProTech(RelatedProjectTechnician relatedProjectTechnicianS) {
        // 如果是新增，价格变动一定为空
        relatedProjectTechnicianS.setPriceMonth(null);
        relatedProjectTechnicianS.setCreateBy(SecurityUtils.getUsername());
        relatedProjectTechnicianS.setDelFlag("0");
        relatedProjectTechnicianS.setParentId(0L);
        relatedProjectTechnicianS.setRealStopMonth(relatedProjectTechnicianS.getStopMonth());
        relatedProjectTechnicianS.setCRealBeginMonth(relatedProjectTechnicianS.getCBeginMonth());
        relatedProjectTechnicianS.setCRealEndMonth(relatedProjectTechnicianS.getCEndMonth());
        //判定null处理
        relatedProjectTechnicianS.setCLowerHours(Optional.ofNullable(relatedProjectTechnicianS.getCLowerHours()).orElse(BigDecimal.ZERO));
        relatedProjectTechnicianS.setCHigherHours(Optional.ofNullable(relatedProjectTechnicianS.getCHigherHours()).orElse(BigDecimal.ZERO));
        relatedProjectTechnicianS.setCReductPrice(Optional.ofNullable(relatedProjectTechnicianS.getCReductPrice()).orElse(BigDecimal.ZERO));
        relatedProjectTechnicianS.setCIncreasePrice(Optional.ofNullable(relatedProjectTechnicianS.getCIncreasePrice()).orElse(BigDecimal.ZERO));
        relatedProjectTechnicianS.setHLowerHours(Optional.ofNullable(relatedProjectTechnicianS.getHLowerHours()).orElse(BigDecimal.ZERO));
        relatedProjectTechnicianS.setHHigherHours(Optional.ofNullable(relatedProjectTechnicianS.getHHigherHours()).orElse(BigDecimal.ZERO));
        relatedProjectTechnicianS.setHReductPrice(Optional.ofNullable(relatedProjectTechnicianS.getHReductPrice()).orElse(BigDecimal.ZERO));
        relatedProjectTechnicianS.setHIncreasePrice(Optional.ofNullable(relatedProjectTechnicianS.getHIncreasePrice()).orElse(BigDecimal.ZERO));
        relatedProjectTechnicianS.setHPrice(Optional.ofNullable(relatedProjectTechnicianS.getHPrice()).orElse(BigDecimal.ZERO));
        relatedProjectTechnicianS.setStandardHours(Optional.ofNullable(relatedProjectTechnicianS.getStandardHours()).orElse(BigDecimal.ZERO));

        proTechDao.insertProTech(relatedProjectTechnicianS);

//        LocalDate cEndMonth = relatedProjectTechnicianS.getCEndMonth();
//        LocalDate cBeginMonth = relatedProjectTechnicianS.getCBeginMonth();
//        LocalDate endMonth = null;
//        //获取数据库中最未来的时间
//        Date statsMonthDate = statsDao.selectYearMonth();
//        // 对统计表也没有数据时进行判断
//        // 为null就不管
//        if (statsMonthDate != null) {
//            LocalDate statsMonth = statsMonthDate.toLocalDate();
//            if (cEndMonth == null) {
//                // 获取从契约开始月到下下年2月1号的数据
//                //endMonth = TableUtils.getNextNextYearFebruary(cBeginMonth);
//
//                // 获取有统计表中数据的月份
//                endMonth = statsMonth;
//            } else {
//                if (cEndMonth.isBefore(statsMonth)) endMonth = cEndMonth;
//                else endMonth = statsMonth;
//            }
//            // 获取开始到契约结束月||统计表当前月的年月数据
//            // 预期结果示例：[2024-02, 2024-03, 2024-04, 2024-05, 2024-06, 2024-07, 2024-08, 2024-09, 2024-10, 2024-11, 2024-12, 2025-01, 2025-02]
//            List<String> yearMonthsBetween = TableUtils.getYearMonthsBetween(cBeginMonth, endMonth);
//            // 查询新增任用的技术者信息
//            BasicTechnicianEntity entity = technicianDao.searchById(relatedProjectTechnicianS.getTechnicianId());
//
//            // 判断是否为UCL职员
//            Boolean isUCL = null;
//            if (entity.getBelongCompany().equalsIgnoreCase("ucl")) isUCL = true;
//            else isUCL = false;
//
//            for (String yearMonth : yearMonthsBetween) {
//                // 循环查询每个年月下，统计表的值
//                BasicStatsEntity basicStatsEntity = statsDao.searchByYearMonth(yearMonth + "-01");
//                if (basicStatsEntity != null) {
//                    BigDecimal uclMember = basicStatsEntity.getUclMember();
//                    BigDecimal bpMember = basicStatsEntity.getBpMember();
//                    BigDecimal totalNumber = basicStatsEntity.getTotalNumber();
//                    BigDecimal bpUclRate = basicStatsEntity.getBpUclRate();
//
//                    // 对UCL或者BP+1
//                    if (isUCL) basicStatsEntity.setUclMember(uclMember.add(BigDecimal.ONE));
//                    else basicStatsEntity.setBpMember(bpMember.add(BigDecimal.ONE));
//                    uclMember = basicStatsEntity.getUclMember();
//                    bpMember = basicStatsEntity.getBpMember();
//
//                    // 对总数和比率进行计算
//                    basicStatsEntity.setTotalNumber(totalNumber.add(BigDecimal.ONE));
//                    totalNumber = basicStatsEntity.getTotalNumber();
//                    if (uclMember.compareTo(BigDecimal.ZERO) == 0) {
//                        //虽然不存在这种情况，碰到-1即前端显示为空
//                        bpUclRate = BigDecimal.valueOf(-1);
//                    } else {
//                        // 2 为精确到小数点后2位
//                        // RoundingMode.HALF_UP为四舍五入的舍入模式
//                        bpUclRate = bpMember.divide(uclMember, 2, RoundingMode.HALF_UP);
//                    }
//                    basicStatsEntity.setBpUclRate(bpUclRate);
//                    // 更新至统计表中
//                    statsDao.updateStatsByYearMonth(basicStatsEntity);
//
//                    // 更新项目月份表的统计数据
//                    Map<String, Object> param = new HashMap<>();
//                    param.put("yearMonth", yearMonth + "-01");
//                    param.put("totalNumber", totalNumber);
//                    technicianDao.updateTotalByYearMonth(param);
//                }
//            }
//            // 将projectTechnicianId和endMonth存入redis中，便于退场月判断
//            saveProjectTechnicianEndMonth(String.valueOf(relatedProjectTechnicianS.getProjectTechnicianId()), endMonth);
//        }
    }

    /**
     * @param relatedProjectTechnicianS:
     * @return void
     * @author he_jiale
     * @description 价格变动处理逻辑
     * @date 2024/7/24 17:21
     */
    @Transactional
    public void priceChange(RelatedProjectTechnician relatedProjectTechnicianS) {
        HashMap mapF = proTechDao.selectProTechForUpdateOrChange(relatedProjectTechnicianS.getProjectTechnicianId());
        RelatedProjectTechnician relatedProjectTechnicianF = relatedProjectTechnicianS.clone();

        // 修改父节点信息
        relatedProjectTechnicianF.setCPrice((BigDecimal) mapF.get("cPrice"));
        relatedProjectTechnicianF.setStopMonth(relatedProjectTechnicianF.getPriceMonth());
        relatedProjectTechnicianF.setPriceMonth(relatedProjectTechnicianF.getStopMonth());

        // 修改子节点信息
        if (mapF.get("cEndMonth") != null && mapF.get("cEndMonth") != "")
            relatedProjectTechnicianS.setCEndMonth(((Date) mapF.get("cEndMonth")).toLocalDate());
        if (mapF.get("stopMonth") != null && mapF.get("stopMonth") != "")
            relatedProjectTechnicianS.setStopMonth(((Date) mapF.get("stopMonth")).toLocalDate());
        if (mapF.get("cRealBeginMonth") != null && mapF.get("cRealBeginMonth") != "")
            relatedProjectTechnicianS.setCRealBeginMonth(((Date) mapF.get("cRealBeginMonth")).toLocalDate());
        if (mapF.get("cRealEndMonth") != null && mapF.get("cRealEndMonth") != "")
            relatedProjectTechnicianS.setCRealEndMonth(((Date) mapF.get("cRealEndMonth")).toLocalDate());
        if (mapF.get("realStopMonth") != null && mapF.get("realStopMonth") != "")
            relatedProjectTechnicianS.setRealStopMonth(((Date) mapF.get("realStopMonth")).toLocalDate());
        relatedProjectTechnicianS.setCBeginMonth(relatedProjectTechnicianF.getPriceMonth());
        relatedProjectTechnicianS.setPriceMonth(null);
        relatedProjectTechnicianS.setParentId((Long) mapF.get("projectTechnicianId"));
        relatedProjectTechnicianS.setDelFlag("0");
        relatedProjectTechnicianS.setStopMonth(null);

        relatedProjectTechnicianF.setCEndMonth(DateUtils.toLocalDateBefDay(relatedProjectTechnicianF.getPriceMonth()));

        proTechDao.updateProTech(relatedProjectTechnicianF);
        proTechDao.insertProTech(relatedProjectTechnicianS);
    }

    @Override
    @Transactional
    public PageUtils selectProTechByPage(Map param) {
        ArrayList<HashMap> list = null;
        long count = proTechDao.selectProTechByPageCount(param);

        if (count > 0) {
            list = proTechDao.selectProTechByPage(param);
//           he_jiale编写，父子节点的数据相同问题，不通过前端实现，通过修改后端的返回值实现。但问题是此做法会造成前端颜色显示异常，建议使用前端实现或新增字段
//            //遍历HashMap找到父子结点
//            // 创建一个以 parentId 为 key 的映射，用于快速查找父节点
//            Map<Long, Map<String, Object>> parentMap = new HashMap<>();
//            for (Map<String, Object> node : list) {
//                Long parentId = (Long) node.get("parentId");
//                if(parentId==0L){
//                    parentMap.put((Long) node.get("projectTechnicianId"), node);
//                }
//            }
//            // 更新子节点的 cBeginMonth 和 cEndMonth
//            for (Map<String, Object> node : list) {
//                Long parentId = (Long) node.get("parentId");
//                if (parentId != 0) {
//                    Map<String, Object> parentNode = parentMap.get(parentId);
//                    if (parentNode != null) {
//                        // 从父节点获取 cBeginMonth 和 cEndMonth 并设置到当前子节点
//                        node.put("cBeginMonth", parentNode.get("cBeginMonth"));
//                        node.put("cEndMonth", parentNode.get("cEndMonth"));
//                        node.put("stopMonth", parentNode.get("stopMonth"));
//                    }
//                }
//            }
        } else {
            list = new ArrayList<>();
        }


        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public HashMap selectProTechForUpdateOrChange(long proTechId) {
        HashMap map = proTechDao.selectProTechForUpdateOrChange(proTechId);
        return map;
    }

    @Override
    @Transactional
    public void updateProTech(RelatedProjectTechnician relatedProjectTechnician) {
        if (relatedProjectTechnician.getSelected().equals("exit")) {
            // 新增任用节点月
//            LocalDate endMonth = null;
//            String str = getEndMonthByProjectTechnicianId(relatedProjectTechnician.getProjectTechnicianId());
//            if (str != null) {
//                endMonth = LocalDate.parse(str);
//            }
            // 退场月
            LocalDate exitMonth = relatedProjectTechnician.getStopMonth();
//            if (endMonth == null) {
//                //对于已有的记录，之前生成的记录不做改变！
//            } else {
//                addProTechRedis(relatedProjectTechnician, endMonth, exitMonth);
//            }
            relatedProjectTechnician.setRealStopMonth(exitMonth);

            Long proTechId = relatedProjectTechnician.getParentId();
            // 实现对子节点退场时，同步修改父节点的实际退场月(realStopMonth)
            if (proTechId != null)
                stopMonthChange(proTechId, relatedProjectTechnician.getStopMonth());
        }

        RelatedProjectTechnician relatedProjectTechnician1 = proTechDao.selectProTechForStopMonth(relatedProjectTechnician.getProjectTechnicianId());
        // 如果数据库查询的值和前端传入的值不想等，说明用户改变了该值。将该值赋值给cRealBeginMonth
        if (relatedProjectTechnician1.getCBeginMonth() != relatedProjectTechnician.getCBeginMonth())
            relatedProjectTechnician.setCRealBeginMonth(relatedProjectTechnician1.getCBeginMonth());
        if (relatedProjectTechnician1.getCEndMonth() != relatedProjectTechnician.getCEndMonth())
            relatedProjectTechnician.setCRealEndMonth(relatedProjectTechnician1.getCEndMonth());
        relatedProjectTechnician.setUpdateBy(SecurityUtils.getUsername());
        relatedProjectTechnician.setDelFlag("0");
        proTechDao.updateProTech(relatedProjectTechnician);
    }

//    /**
//     * @param relatedProjectTechnician:
//     * @param endMonth:
//     * @param exitMonth:
//     * @return void
//     * @author he_jiale
//     * @description 说明redis中有该记录，是表格已经生成后再新增的职员，需要重新计算stats表和全体管理表
//     * @date 2024/7/24 17:18
//     */
//    public void addProTechRedis(RelatedProjectTechnician relatedProjectTechnician, LocalDate endMonth, LocalDate exitMonth) {
//
//        // 查询新增任用的技术者信息
//        BasicTechnicianEntity entity = technicianDao.searchById(relatedProjectTechnician.getTechnicianId());
//
//        // 判断是否为UCL职员
//        Boolean isUCL = null;
//        if (entity.getBelongCompany().equalsIgnoreCase("ucl")) isUCL = true;
//        else isUCL = false;
//
//        if (endMonth.isBefore(exitMonth)) {
//            // 使用 DateTimeFormatter 来格式化 endMonth 为 "yyyy-MM" 字符串格式
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
//            String endMonthStr = endMonth.format(formatter);
//            // 获取节点月到退场月的年月时间
//            // 预期结果示例：[2024-02, 2024-03, 2024-04, 2024-05, 2024-06, 2024-07, 2024-08, 2024-09, 2024-10, 2024-11, 2024-12, 2025-01, 2025-02]
//            List<String> yearMonthsBetween = TableUtils.getYearMonthsBetween(endMonth, exitMonth);
//            // 使用 Stream API 来过滤掉与 endMonth 相同的月份
//            List<String> filteredYearMonths = yearMonthsBetween.stream()
//                    .filter(month -> !month.equals(endMonthStr))
//                    .collect(Collectors.toList());
//            for (String yearMonth : yearMonthsBetween) {
//                // 循环查询每个年月下，统计表的值
//                BasicStatsEntity basicStatsEntity = statsDao.searchByYearMonth(yearMonth + "-01");
//                // 例如：退场月在2025-05-01，而统计表月份只到了2025-02，没有的数据不进行增减
//                if (basicStatsEntity != null) {
//                    BigDecimal uclMember = basicStatsEntity.getUclMember();
//                    BigDecimal bpMember = basicStatsEntity.getBpMember();
//                    BigDecimal totalNumber = basicStatsEntity.getTotalNumber();
//                    BigDecimal bpUclRate = basicStatsEntity.getBpUclRate();
//
//                    // 对UCL或者BP+1
//                    if (isUCL) basicStatsEntity.setUclMember(uclMember.add(BigDecimal.ONE));
//                    else basicStatsEntity.setBpMember(bpMember.add(BigDecimal.ONE));
//                    uclMember = basicStatsEntity.getUclMember();
//                    bpMember = basicStatsEntity.getBpMember();
//
//                    // 对总数和比率进行计算
//                    basicStatsEntity.setTotalNumber(totalNumber.add(BigDecimal.ONE));
//                    totalNumber = basicStatsEntity.getTotalNumber();
//                    if (uclMember.compareTo(BigDecimal.ZERO) == 0) {
//                        //虽然不存在这种情况，碰到-1即前端显示为空
//                        bpUclRate = BigDecimal.valueOf(-1);
//                    } else {
//                        // 2 为精确到小数点后2位
//                        // RoundingMode.HALF_UP为四舍五入的舍入模式
//                        bpUclRate = bpMember.divide(uclMember, 2, RoundingMode.HALF_UP);
//                    }
//                    basicStatsEntity.setBpUclRate(bpUclRate);
//                    // 更新至统计表中
//                    statsDao.updateStatsByYearMonth(basicStatsEntity);
//
//                    // 更新项目月份表的统计数据
//                    Map<String, Object> param = new HashMap<>();
//                    param.put("yearMonth", yearMonth + "-01");
//                    param.put("totalNumber", totalNumber);
//                    technicianDao.updateTotalByYearMonth(param);
//                }
//            }
//        } else {
//            // 使用 DateTimeFormatter 来格式化 endMonth 为 "yyyy-MM" 字符串格式
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
//            String endMonthStr = endMonth.format(formatter);
//
//            List<String> yearMonthsBetween = TableUtils.getYearMonthsBetween(exitMonth, endMonth);
//            // 使用 Stream API 来过滤掉与 endMonth 相同的月份
//            List<String> filteredYearMonths = yearMonthsBetween.stream()
//                    .filter(month -> !month.equals(endMonthStr))
//                    .collect(Collectors.toList());
//            for (String yearMonth : yearMonthsBetween) {
//                // 循环查询每个年月下，统计表的值
//                BasicStatsEntity basicStatsEntity = statsDao.searchByYearMonth(yearMonth + "-01");
//                if (basicStatsEntity != null) {
//                    BigDecimal uclMember = basicStatsEntity.getUclMember();
//                    BigDecimal bpMember = basicStatsEntity.getBpMember();
//                    BigDecimal totalNumber = basicStatsEntity.getTotalNumber();
//                    BigDecimal bpUclRate = basicStatsEntity.getBpUclRate();
//
//                    // 对UCL或者BP+1
//                    if (isUCL) basicStatsEntity.setUclMember(uclMember.subtract(BigDecimal.ONE));
//                    else basicStatsEntity.setBpMember(bpMember.subtract(BigDecimal.ONE));
//                    uclMember = basicStatsEntity.getUclMember();
//                    bpMember = basicStatsEntity.getBpMember();
//
//                    // 对总数和比率进行计算
//                    basicStatsEntity.setTotalNumber(totalNumber.subtract(BigDecimal.ONE));
//                    totalNumber = basicStatsEntity.getTotalNumber();
//                    if (uclMember.compareTo(BigDecimal.ZERO) == 0) {
//                        //虽然不存在这种情况，碰到-1即前端显示为空
//                        bpUclRate = BigDecimal.valueOf(-1);
//                    } else {
//                        // 2 为精确到小数点后2位
//                        // RoundingMode.HALF_UP为四舍五入的舍入模式
//                        bpUclRate = bpMember.divide(uclMember, 2, RoundingMode.HALF_UP);
//                    }
//                    basicStatsEntity.setBpUclRate(bpUclRate);
//                    // 更新至统计表中
//                    statsDao.updateStatsByYearMonth(basicStatsEntity);
//
//                    // 更新项目月份表的统计数据
//                    Map<String, Object> param = new HashMap<>();
//                    param.put("yearMonth", yearMonth + "-01");
//                    param.put("totalNumber", totalNumber);
//                    technicianDao.updateTotalByYearMonth(param);
//                }
//            }
//        }
//        deleteEndMonthByProjectTechnicianId(String.valueOf(relatedProjectTechnician.getProjectTechnicianId()));
//    }

    @Override
    @Transactional
    public void deleteProTech(Long[] ids) {
        proTechDao.deleteProTech(ids);
    }

    @Override
    @Transactional
    public ArrayList<HashMap> selectTechnician() {
        ArrayList<HashMap> list = proTechDao.selectTechnician();
        return list;
    }

    @Override
    @Transactional
    public ArrayList<HashMap> selectProject(Long customerId) {
        ArrayList<HashMap> list = proTechDao.selectProject(customerId);
        return list;
    }

    @Override
    @Transactional
    public ArrayList<HashMap> selectCustomer() {
        ArrayList<HashMap> list = proTechDao.selectCustomer();
        return list;
    }


//    /**
//     * 用于持久化新增任用时计算的年月时间，便于退场月的计算判断
//     *
//     * @param projectTechnicianId 任用管理的Id
//     * @param endMonth            新增任用时，统计表重新计算的年月节点
//     */
//    private void saveProjectTechnicianEndMonth(String projectTechnicianId, LocalDate endMonth) {
//        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String endMonthStr = formatter.format(endMonth);
//        String key = "projectTechnicianId:" + projectTechnicianId;
//
//        hashOperations.put(key, "endMonth:", endMonthStr);
//    }

//    /**
//     * @param projectTechnicianId
//     * @return 返回新增任用时，统计表重新计算的年月节点
//     */
//    private String getEndMonthByProjectTechnicianId(Long projectTechnicianId) {
//        String key = "projectTechnicianId:" + projectTechnicianId;
//        return (String) hashOperations.get(key, "endMonth:");
//    }

//    /**
//     * @param projectTechnicianId 对退场月的逻辑处理完毕后，删除endMonth数据
//     */
//    private void deleteEndMonthByProjectTechnicianId(String projectTechnicianId) {
//        String key = "projectTechnicianId:" + projectTechnicianId;
//        hashOperations.delete(key, "endMonth:");
//    }

    /**
     *
     */
    @Transactional
    private void stopMonthChange(Long projectTechnicianId, LocalDate stopMonth) {
        // 根据projectTechnicianId查询到父节点的值
        RelatedProjectTechnician relatedProjectTechnician = proTechDao.selectProTechForStopMonth(projectTechnicianId);
        // 如果能查到父节点，则执行
        if (relatedProjectTechnician != null) {
            // 如果该父节点仍然有parentId，说明此节点为中间节点，继续递归修改其父节点的stopMonth
            if (relatedProjectTechnician.getParentId() != null)
                stopMonthChange(relatedProjectTechnician.getParentId(), stopMonth);
            // 修改stopMonth和realStopMonth的值
            relatedProjectTechnician.setRealStopMonth(stopMonth);
            relatedProjectTechnician.setStopMonth(stopMonth);
            // 更新数据
            proTechDao.updateProTech(relatedProjectTechnician);
        }
    }

}
