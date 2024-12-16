package ucl.group.excelSystem.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucl.group.excelSystem.api.common.constants.ExcelSystemConstants;
import ucl.group.excelSystem.api.common.utils.TableUtils;
import ucl.group.excelSystem.api.controller.form.SaveProjectTechnicianColumn;
import ucl.group.excelSystem.api.controller.form.SaveProjectTechnicianListForm;
import ucl.group.excelSystem.api.controller.form.SaveProjectTechnicianRow;
import ucl.group.excelSystem.api.controller.form.SaveTechnicianListForm;
import ucl.group.excelSystem.api.db.dao.*;
import ucl.group.excelSystem.api.db.pojo.*;
import ucl.group.excelSystem.api.db.pojo.bo.RelatedProjectTechBO;
import ucl.group.excelSystem.api.db.pojo.vo.*;
import ucl.group.excelSystem.api.service.MonthService;
import ucl.group.excelSystem.api.service.TableService;
import ucl.group.talentManageSystem.api.common.utils.DateUtils;
import ucl.group.talentManageSystem.api.common.utils.SecurityUtils;
import ucl.group.talentManageSystem.api.common.utils.StringUtils;
import ucl.group.talentManageSystem.api.exception.ServiceException;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TableServiceImpl implements TableService {
    @Autowired
    private RelatedProjectTechnicianDao relatedProjectTechnicianDao;
    @Autowired
    private TechnicianDao technicianDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private DateDao dateDao;
    @Autowired
    private StatsDao statsDao;
    @Autowired
    private MonthDao monthDao;
    @Autowired
    private MonthService monthService;

    @Override
    @Transactional
    public List<TechnicianListVO> searchTechnicianList(LocalDate dateStart, LocalDate dateEnd) {
        //查询所有的技术者
        List<RelatedProjectTechnician> relatedProjectTechnicians = relatedProjectTechnicianDao.searchBetweenStartAndEnd(String.valueOf(dateStart), String.valueOf(dateEnd));
        //去重
        List<RelatedProjectTechnician> depulicateRelateds = TableUtils.deduplicateRelatedProTech(relatedProjectTechnicians);
        //声明返回值
        List<TechnicianListVO> technicianListVOS = new ArrayList<>();

        for (RelatedProjectTechnician tempR : depulicateRelateds) {
            TechnicianListVO technicianListVO = new TechnicianListVO();
            getTechnicianListVOForYearMonth(technicianListVO, tempR, dateStart, dateEnd);
            technicianListVOS.add(technicianListVO);
        }
        //排序
        sortTechnicianList(technicianListVOS);
        return technicianListVOS;
    }

/*    public List<TechnicianListVO> searchTechnicianList(LocalDate dateStart, LocalDate dateEnd) {
        // 查询所有的技术者
        List<RelatedProjectTechnician> relatedProjectTechnicians =
                relatedProjectTechnicianDao.searchBetweenStartAndEnd(String.valueOf(dateStart), String.valueOf(dateEnd));

        // 去重
        List<RelatedProjectTechnician> deduplicatedRelateds = TableUtils.deduplicateRelatedProTech(relatedProjectTechnicians);

        // 处理并返回结果
        return deduplicatedRelateds.stream()
                .map(tempR -> {
                    TechnicianListVO technicianListVO = new TechnicianListVO();
                    getTechnicianListVOForYearMonth(technicianListVO, tempR, dateStart, dateEnd);
                    return technicianListVO;
                })
                .sorted(this::compareTechnicianListVO)
                .collect(Collectors.toList());
    }*/

/*    private int compareTechnicianListVO(TechnicianListVO vo1, TechnicianListVO vo2) {
        // 自定义的排序逻辑
        int cmp = vo1.getCustomerName().compareTo(vo2.getCustomerName());
        if (cmp == 0) {
            cmp = vo1.getProjectName().compareTo(vo2.getProjectName());
            if (cmp == 0) {
                cmp = vo1.getBelongCompany().compareTo(vo2.getBelongCompany());
            }
        }
        return cmp;
    }*/

    @Transactional
    public void getTechnicianListVOForYearMonth(TechnicianListVO technicianListVO, RelatedProjectTechnician tempR, LocalDate dateStart, LocalDate dateEnd) {
        technicianListVO.setTechnicianId(tempR.getTechnicianId());
        BasicTechnicianEntity searchedById = technicianDao.searchById(tempR.getTechnicianId());
        BasicProjectEntity searchById = projectDao.searchById(tempR.getProjectId());
        technicianListVO.setName(Optional.ofNullable(searchedById.getName()).orElse(""));
        technicianListVO.setBelongCompany(Optional.ofNullable(searchedById.getBelongCompany()).orElse(""));
        technicianListVO.setRemark(Optional.ofNullable(tempR.getRemark()).orElse(""));
        technicianListVO.setCustomerName(Optional.ofNullable(customerDao.searchById(searchById.getCustomerId()).getCustomerName()).orElse(""));
        technicianListVO.setProjectName(Optional.ofNullable(searchById.getProjectName()).orElse(""));
        technicianListVO.setPrincipal(Optional.ofNullable(searchById.getPrincipal()).orElse(""));
        technicianListVO.setPrincipalCompany(Optional.ofNullable(searchById.getPrincipalCompany()).orElse(""));
        //确定真正的cBeginMonth和cEndMonth
        if (tempR.getCBeginMonth().isBefore(dateStart)) {
            //beginMonth在dateStart前,以dateStart为准
            technicianListVO.setcBeginMonth(DateUtils.localDateToStr(dateStart, "yyyy-MM"));
        } else {
            technicianListVO.setcBeginMonth(DateUtils.localDateToStr(tempR.getCBeginMonth(), "yyyy-MM"));
        }
        if (tempR.getCEndMonth() == null) {
            technicianListVO.setcEndMonth(DateUtils.localDateToStr(dateEnd, "yyyy-MM"));
        } else {
            if (dateEnd.isBefore(tempR.getCEndMonth())) {
                //dateEnd在cEndMonth前，以dateEnd为准
                technicianListVO.setcEndMonth(DateUtils.localDateToStr(dateEnd, "yyyy-MM"));
            } else {
                //dateEnd等于或者大于cEndMonth，以cEndMonth为准
                technicianListVO.setcEndMonth(DateUtils.localDateToStr(tempR.getCEndMonth(), "yyyy-MM"));
            }
        }
        technicianListVO.setStopMonth(tempR.getStopMonth() != null ? DateUtils.localDateToStr(tempR.getStopMonth(), "yyyy-MM") : "");
    }


    @Override
    public void sortTechnicianList(List<TechnicianListVO> technicianListVOS) {
        technicianListVOS.sort((o1, o2) -> {
            int cmp = o1.getCustomerName().compareTo(o2.getCustomerName());
            if (cmp == 0) {
                cmp = o1.getProjectName().compareTo(o2.getProjectName());
                if (cmp == 0) {
                    cmp = o1.getBelongCompany().compareTo(o2.getBelongCompany());
                }
            }
            return cmp;
        });
    }

    @Override
    @Transactional
    public void saveTechnicianList(List<SaveTechnicianListForm> forms) {
        for (SaveTechnicianListForm saveTechnicianListForm : forms) {
            try {
                saveTechnicianListForm.setUpdateBy(SecurityUtils.getUsername());
                relatedProjectTechnicianDao.saveRemark(saveTechnicianListForm);
            } catch (Exception e) {
                // 处理异常，或者重新抛出自定义异常
                //保存技术者备注失败
                throw new ServiceException("フォームに技術者の備考を保存できませんでした: " + saveTechnicianListForm, e);
            }
        }
    }

    //原版
    //@Override
/*    public List<TechnicianListStatsVO> searchTechnicianStatsList(LocalDate dateStart, LocalDate dateEnd) {
        List<RelatedProjectTechnician> relatedProjectTechnicians = relatedProjectTechnicianDao.searchBetweenStartAndEnd(String.valueOf(dateStart), String.valueOf(dateEnd));
        List<TechnicianListVO> technicianListVOS = TableUtils.changeStartAndEndMonth(relatedProjectTechnicians, dateStart, dateEnd);
        //获取一年的月份范围
        List<String> yearMonths = TableUtils.getYearMonthsBetween(dateStart, dateEnd);
        //获取一年的日期范围(2024-03-01)
        List<String> yearMonthDays = TableUtils.getYearMonthDaysBetween(dateStart, dateEnd);
        int countByYearMonth = statsDao.countByYearMonth(yearMonthDays);
        if (countByYearMonth == 12) {
            //刷新人数和比率记录
            flushStats(dateStart, dateEnd);
            //如果一年中12个月都有记录，则不新增
            return searchStats(yearMonths);
        } else if (countByYearMonth == 0) {
            //如果一年中12个月都没记录，则新增
            return addStats(yearMonths, technicianListVOS);
        } else {
            //出现这种情况，数据库绝对有问题
            //No records were found for 12 months. The database is incorrect.
            throw new ServiceException("12か月間の記録が見つかりませんでした。データベースが間違っています。");
        }
    }*/

    @Override
    @Transactional
    public List<TechnicianListStatsVO> searchTechnicianStatsList(LocalDate dateStart, LocalDate dateEnd) {
        // 优化查询，减少数据库访问次数
        List<RelatedProjectTechnician> relatedProjectTechnicians = relatedProjectTechnicianDao.searchBetweenStartAndEnd(String.valueOf(dateStart), String.valueOf(dateEnd));

        // 去重和转换日期范围
        List<TechnicianListVO> technicianListVOS = TableUtils.changeStartAndEndMonth(
                TableUtils.deduplicateRelatedProTech(relatedProjectTechnicians), dateStart, dateEnd);

        // 获取一年的月份和日期范围
        List<String> yearMonths = TableUtils.getYearMonthsBetween(dateStart, dateEnd);
        List<String> yearMonthDays = TableUtils.getYearMonthDaysBetween(dateStart, dateEnd);

        // 统计记录数量
        int countByYearMonth = statsDao.countByYearMonth(yearMonthDays);

        if (countByYearMonth == 12) {
            // 刷新记录并返回查询结果
            flushStats(dateStart, dateEnd);
            return searchStats(yearMonths);
        } else if (countByYearMonth == 0) {
            // 新增记录
            return addStats(yearMonths, technicianListVOS);
        } else {
            // 数据库出现异常情况
            throw new ServiceException("12か月間の記録が見つかりませんでした。データベースが間違っています。");
        }
    }


    @Override
    @Transactional
    public int countTechniciansByCompany(List<TechnicianListVO> technicianListVOS, String companyName) {
        if (technicianListVOS == null || companyName == null) {
            return 0;
        }
        // 不区分大小写
        String lowerCaseCompanyName = companyName.toLowerCase();

        int count = 0;

        for (TechnicianListVO technician : technicianListVOS) {
            String belongCompany = Optional.ofNullable(technicianDao.searchById(technician.getTechnicianId()).getBelongCompany()).orElse("");
            if (belongCompany.toLowerCase().equals(lowerCaseCompanyName)) {
                count++;
            }
        }
        return count;
    }


    /**
     * @param yearMonths:
     * @param technicianListVOS:
     * @return List<TechnicianListStatsVO>
     * @author he_jiale
     * @description 按12个月新增stats记录
     * @date 2024/07/09 14:10
     */
    @Transactional
    public List<TechnicianListStatsVO> addStats(List<String> yearMonths, List<TechnicianListVO> technicianListVOS) {
        List<TechnicianListStatsVO> technicianListStatsVOS = new ArrayList<>();
        //获取belongCompany
        for (TechnicianListVO tempT : technicianListVOS) {
            tempT.setBelongCompany(technicianDao.searchById(tempT.getTechnicianId()).getBelongCompany());
        }
        for (String tempYearMonth : yearMonths) {
            //查询该月有哪些UCL技术者工作
            List<TechnicianListVO> techniciansInRanges = TableUtils.getTechniciansInRange(technicianListVOS, tempYearMonth);
            TechnicianListStatsVO technicianListStatsVO = new TechnicianListStatsVO();
            int uclNum = countTechniciansByCompany(techniciansInRanges, "UCL");
            int bpNum = techniciansInRanges.size() - uclNum;
            int totalNumber = techniciansInRanges.size();
            BigDecimal bpUclRate = null;
            if (uclNum == 0) {
                //虽然不存在这种情况，碰到-1即前端显示为空
                bpUclRate = BigDecimal.valueOf(-1);
            } else {
                BigDecimal bigUclNum = BigDecimal.valueOf(uclNum);
                BigDecimal bigBpNum = BigDecimal.valueOf(bpNum);
                bpUclRate = bigBpNum.divide(bigUclNum, 2, RoundingMode.HALF_UP);
            }
            BasicStatsEntity basicStatsEntity = new BasicStatsEntity(null, BigDecimal.valueOf(uclNum),
                    BigDecimal.valueOf(bpNum), BigDecimal.valueOf(totalNumber), bpUclRate, DateUtils.toLocalDateFirstDay(tempYearMonth));
            statsDao.addStats(basicStatsEntity);
            //设置返回值
            technicianListStatsVO.setStatsId(basicStatsEntity.getStatsId());
            technicianListStatsVO.setUclMember(basicStatsEntity.getUclMember());
            technicianListStatsVO.setBpMember(basicStatsEntity.getBpMember());
            technicianListStatsVO.setTotalNumber(basicStatsEntity.getTotalNumber());
            technicianListStatsVO.setBpUclRate(basicStatsEntity.getBpUclRate());
            technicianListStatsVO.setYearMonth(tempYearMonth);
            technicianListStatsVOS.add(technicianListStatsVO);
        }
        return technicianListStatsVOS;
    }

    /**
     * @param yearMonths:
     * @return List<TechnicianListStatsVO>
     * @author he_jiale
     * @description 根据yearMonth检索12个stats
     * @date 2024/07/09 14:13
     */
    @Transactional
    public List<TechnicianListStatsVO> searchStats(List<String> yearMonths) {
        List<TechnicianListStatsVO> technicianListStatsVOS = new ArrayList<>();
        for (String tempYearMonth : yearMonths) {
            //根据年月查询记录
            BasicStatsEntity basicStatsEntity = statsDao.searchByYearMonth(DateUtils.localDateToStr(DateUtils.toLocalDateFirstDay(tempYearMonth), "yyyy-MM-dd"));
            TechnicianListStatsVO technicianListStatsVO = new TechnicianListStatsVO();
            //设置返回值
            technicianListStatsVO.setStatsId(basicStatsEntity.getStatsId());
            technicianListStatsVO.setUclMember(basicStatsEntity.getUclMember());
            technicianListStatsVO.setBpMember(basicStatsEntity.getBpMember());
            technicianListStatsVO.setTotalNumber(basicStatsEntity.getTotalNumber());
            BigDecimal bpUclRate = basicStatsEntity.getBpUclRate();
            technicianListStatsVO.setBpUclRate(bpUclRate);
            technicianListStatsVO.setYearMonth(tempYearMonth);
            technicianListStatsVOS.add(technicianListStatsVO);
        }
        return technicianListStatsVOS;
    }

    @Override
    @Transactional
    public void saveTechnicianListStats(List<TechnicianListStatsVO> technicianListStatsVOS) {
        for (TechnicianListStatsVO tempStats : technicianListStatsVOS) {
            try {
                BasicStatsEntity basicStatsEntity = new BasicStatsEntity(
                        tempStats.getStatsId(),
                        tempStats.getUclMember(),
                        tempStats.getBpMember(),
                        tempStats.getTotalNumber(),
                        tempStats.getBpUclRate(),
                        DateUtils.toLocalDateFirstDay(tempStats.getYearMonth())
                );
                statsDao.edit(basicStatsEntity);
            } catch (Exception e) {
                // 处理异常，或者重新抛出自定义异常
                //Failed to save stats for entity
                throw new ServiceException("エンティティの統計を保存できませんでした。: " + technicianListStatsVOS, e);
            }
        }
    }

    @Override
    @Transactional
    public ProjectTechnicianVO searchProjectTechnicianList(LocalDate dateStart, LocalDate dateEnd) {
        ProjectTechnicianVO projectTechnicianVO = new ProjectTechnicianVO();

        // 查询符合条件的记录
        List<RelatedProjectTechnician> relatedProjectTechnicians = relatedProjectTechnicianDao.searchBetweenStartAndEnd(String.valueOf(dateStart), String.valueOf(dateEnd));
        if (relatedProjectTechnicians.isEmpty()) {
            return projectTechnicianVO;
        }

        //todo
        
        // 转换BO列表并生成行数据
        List<ProjectTechnicianRow> projectTechnicianRows = convertToProjectTechnicianRows(relatedProjectTechnicians, dateStart, dateEnd);
        //第一次拿到column数据
        projectTechnicianVO.setColumn(monthService.getColumnData(dateStart, dateEnd));

        //todo

        // 刷新数据库
        flushMonthData(prepareSaveForm(projectTechnicianRows, projectTechnicianVO.getColumn()));

        // 重新生成行数据并排序
        List<ProjectTechnicianRow> sortedProjectTechnicianRows = convertToProjectTechnicianRows(relatedProjectTechnicians, dateStart, dateEnd);
        sortProjectTechnicianList(sortedProjectTechnicianRows);

        // 设置VO的行和列数据
        projectTechnicianVO.setRow(sortedProjectTechnicianRows);
        projectTechnicianVO.setColumn(monthService.getColumnData(dateStart, dateEnd));

        return projectTechnicianVO;
    }

    //原版
/*    private List<ProjectTechnicianRow> convertToProjectTechnicianRows(List<RelatedProjectTechnician> relatedProjectTechnicians, LocalDate dateStart, LocalDate dateEnd) {
        List<RelatedProjectTechBO> relatedProjectTechBOS = changeRelatedBOList(relatedProjectTechnicians, dateStart, dateEnd);

        List<ProjectTechnicianRow> projectTechnicianRows = new ArrayList<>();
        for (RelatedProjectTechBO tempR : relatedProjectTechBOS) {
            ProjectTechnicianRow projectTechnicianRow = new ProjectTechnicianRow();
            getProjectTechnicianRow(projectTechnicianRow, tempR, dateStart, dateEnd);
            projectTechnicianRows.add(projectTechnicianRow);
        }
        return projectTechnicianRows;
    }*/

    //并行处理
    private List<ProjectTechnicianRow> convertToProjectTechnicianRows(List<RelatedProjectTechnician> relatedProjectTechnicians, LocalDate dateStart, LocalDate dateEnd) {
        List<RelatedProjectTechBO> relatedProjectTechBOS = changeRelatedBOList(relatedProjectTechnicians, dateStart, dateEnd);

        return relatedProjectTechBOS.parallelStream()
                .map(tempR -> {
                    ProjectTechnicianRow projectTechnicianRow = new ProjectTechnicianRow();
                    getProjectTechnicianRow(projectTechnicianRow, tempR, dateStart, dateEnd);
                    return projectTechnicianRow;
                })
                .collect(Collectors.toList());
    }


    //批量处理
/*    private List<ProjectTechnicianRow> convertToProjectTechnicianRows(List<RelatedProjectTechnician> relatedProjectTechnicians, LocalDate dateStart, LocalDate dateEnd) {
        List<RelatedProjectTechBO> relatedProjectTechBOS = changeRelatedBOList(relatedProjectTechnicians, dateStart, dateEnd);

        List<ProjectTechnicianRow> projectTechnicianRows = new ArrayList<>(relatedProjectTechBOS.size());
        for (RelatedProjectTechBO tempR : relatedProjectTechBOS) {
            ProjectTechnicianRow projectTechnicianRow = new ProjectTechnicianRow();
            getProjectTechnicianRow(projectTechnicianRow, tempR, dateStart, dateEnd);
            projectTechnicianRows.add(projectTechnicianRow);
        }
        return projectTechnicianRows;
    }*/


    private SaveProjectTechnicianListForm prepareSaveForm(List<ProjectTechnicianRow> projectTechnicianRows, List<ProjectTechnicianColumn> projectTechnicianColumns) {
        SaveProjectTechnicianListForm form = new SaveProjectTechnicianListForm();

        // 转换并设置行数据
        List<SaveProjectTechnicianRow> saveProjectTechnicianRows = projectTechnicianRows.stream().map(row -> {
            SaveProjectTechnicianRow saveRow = new SaveProjectTechnicianRow();
            saveRow.setProjectTechnicianId(row.getProjectTechnicianId());
            saveRow.setMonthDataList(row.getMonthDataList());
            return saveRow;
        }).collect(Collectors.toList());
        form.setRow(saveProjectTechnicianRows);

        // 转换并设置列数据
        List<SaveProjectTechnicianColumn> saveProjectTechnicianColumns = projectTechnicianColumns.stream().map(column -> {
            SaveProjectTechnicianColumn saveColumn = new SaveProjectTechnicianColumn();
            saveColumn.setMonthDays(column.getMonthDays());
            return saveColumn;
        }).collect(Collectors.toList());
        form.setColumn(saveProjectTechnicianColumns);

        return form;
    }


    //设置行内数据
    @Transactional
    public void getProjectTechnicianRow(ProjectTechnicianRow projectTechnicianRow, RelatedProjectTechBO tempR, LocalDate dateStart, LocalDate dateEnd) {
        projectTechnicianRow.setProjectTechnicianId(tempR.getProjectTechnicianId());
        projectTechnicianRow.setCustomerName(tempR.getCustomerName());
        projectTechnicianRow.setProjectName(tempR.getProjectName());
        projectTechnicianRow.setName(tempR.getName());
        projectTechnicianRow.setStatus(tempR.getStatus());
        //if(StpUtil.hasPermission("price_search"))
        //按理说这里权限管理应该由后端做，鉴于BigDecimal的特殊性，前端无法处理空的显示，故这里的权限管理全部交由前端实现
        projectTechnicianRow.setCPrice(tempR.getCPrice());
        projectTechnicianRow.setHPrice(tempR.getHPrice());
        projectTechnicianRow.setCLowerHours(tempR.getCLowerHours());
        projectTechnicianRow.setCHigherHours(tempR.getCHigherHours());
        projectTechnicianRow.setCReductPrice(tempR.getCReductPrice());
        projectTechnicianRow.setCIncreasePrice(tempR.getCIncreasePrice());

        projectTechnicianRow.setStandardHours(tempR.getStandardHours());


        LocalDate startMonth = DateUtils.toLocalDateFirstDay(tempR.getCBeginMonth());
        projectTechnicianRow.setStartMonth(startMonth);
        LocalDate endMonth = !StringUtils.equals(tempR.getCEndMonth(), "") ? DateUtils.toLocalDateFirstDay(tempR.getCEndMonth()) : null;
        projectTechnicianRow.setEndMonth(endMonth);
        LocalDate stopMonth = !StringUtils.equals(tempR.getStopMonth(), "") ? DateUtils.toLocalDateFirstDay(tempR.getStopMonth()) : null;
        projectTechnicianRow.setStopMonth(stopMonth);
        int colorLeft = tempR.getColorLeft();
        projectTechnicianRow.setColorLeft(colorLeft);
        //用于排序字段
        projectTechnicianRow.setBelongCompany(tempR.getBelongCompany());
        projectTechnicianRow.setCreateTime(tempR.getCreateTime());
        projectTechnicianRow.setTechnicianId(tempR.getTechnicianId());
        //新增month记录
        //获取monthDataList，如果是新增的记录则生成一期的数据插入数据库中
        List<MonthDataListVO> monthDataListVOS = monthService.getMonthDataList(tempR.getProjectTechnicianId(), dateStart, dateEnd);

        //只保留在startMonthNum和endMonthNum之间的数据
        //projectTechnicianRow.setMonthDataList(filterByRange(monthDataListVOS, projectTechnicianRow.getStartMonthNum(), projectTechnicianRow.getEndMonthNum()));
        //不只保留在startMonthNum和endMonthNum之间的数据
        projectTechnicianRow.setMonthDataList(noFilterByRange(monthDataListVOS, startMonth, endMonth, stopMonth, colorLeft));

        //获取monthClassArr
        List<MonthClassArrVO> monthClassArrVOS = getMonthClassArr();
        projectTechnicianRow.setMonthClassArr(monthClassArrVOS);
    }

    public List<MonthClassArrVO> getMonthClassArr() {
        List<MonthClassArrVO> list = new ArrayList<>();
        // 创建6个MonthClassArrVO对象，并将它们的所有BigDecimal字段初始化为null
        for (int i = 0; i < 6; i++) {
            MonthClassArrVO vo = new MonthClassArrVO();
            vo.setPresumedTime("");
            vo.setExpectedPrice("");
            vo.setActualHours("");
            vo.setActualPrice("");
            vo.setFrom("");
            vo.setSubcontractPrice("");
            list.add(vo);
        }
        return list;
    }
//暂时作废
//    public List<MonthDataListVO> filterByRange(List<MonthDataListVO> monthDataListVOS, int minRange, int maxRange) {
//        return monthDataListVOS.stream()
//                .filter(data -> data.getYearMonth() >= minRange && data.getYearMonth() <= maxRange)
//                .collect(Collectors.toList());
//    }

    public List<MonthDataListVO> noFilterByRange(List<MonthDataListVO> monthDataListVOS, LocalDate startNum, LocalDate endNum, LocalDate stopNum, int colorLeft) {
        for (MonthDataListVO tempM : monthDataListVOS) {
            if (stopNum != null) {
                //判断中间值
                //对每个月的colorFlag值进行修改
                if (!tempM.getYearMonth().isBefore(startNum) && tempM.getYearMonth().isBefore(stopNum)) {
                    tempM.setColorFlag(0);
                }
                //判断右侧
                //价格变动或者结束退场
                else if (!tempM.getYearMonth().isBefore(stopNum)) {
                    if (colorLeft == 1) {
                        //价格变动，画青色
                        tempM.setColorFlag(1);
                    } else {
                        //退场黑色
                        tempM.setColorFlag(3);
                    }
                } else {
                    //左侧不论如何，都为空
                    tempM.setColorFlag(2);
                }
            } else {
                //退场月没有
                //判断中间值
                if (!tempM.getYearMonth().isBefore(startNum) && !tempM.getYearMonth().isAfter(endNum)) {
                    tempM.setColorFlag(0);
                }
                //判断左侧
                //价格变动或者延迟开始
                else if (tempM.getYearMonth().isBefore(startNum)) {
                    if (colorLeft == 1) {
                        //价格变动，青色
                        tempM.setColorFlag(1);
                    } else {
                        //还没入场，空
                        tempM.setColorFlag(2);
                    }
                } else {
                    //右侧不论如何，都为空
                    tempM.setColorFlag(2);
                }
            }
        }
        return monthDataListVOS;
    }

//作废
/*    public List<RelatedProjectTechBO> changeRelatedBOList
    (List<RelatedProjectTechnician> relatedProjectTechnicians, LocalDate dateStart, LocalDate dateEnd) {
        List<RelatedProjectTechBO> technicianListVOS = new ArrayList<>();
        for (RelatedProjectTechnician tempR : relatedProjectTechnicians) {
            RelatedProjectTechBO technicianListVO = new RelatedProjectTechBO();
            BasicProjectEntity basicProjectEntity = projectDao.searchById(tempR.getProjectId());
            BasicTechnicianEntity basicTechnicianEntity = technicianDao.searchById(tempR.getTechnicianId());

            technicianListVO.setProjectTechnicianId(tempR.getProjectTechnicianId());
            technicianListVO.setTechnicianId(tempR.getTechnicianId());
            technicianListVO.setStatus(Optional.ofNullable(tempR.getStatus()).orElse(""));
            technicianListVO.setCPrice(Optional.ofNullable(tempR.getCPrice()).orElse(BigDecimal.valueOf(0)));
            technicianListVO.setHPrice(Optional.ofNullable(tempR.getHPrice()).orElse(BigDecimal.valueOf(0)));
            technicianListVO.setCLowerHours(Optional.ofNullable(tempR.getCLowerHours()).orElse(BigDecimal.valueOf(0)));
            technicianListVO.setCHigherHours(Optional.ofNullable(tempR.getCHigherHours()).orElse(BigDecimal.valueOf(0)));
            technicianListVO.setCReductPrice(Optional.ofNullable(tempR.getCReductPrice()).orElse(BigDecimal.valueOf(0)));
            technicianListVO.setCIncreasePrice(Optional.ofNullable(tempR.getCIncreasePrice()).orElse(BigDecimal.valueOf(0)));
            technicianListVO.setStandardHours(Optional.ofNullable(tempR.getStandardHours()).orElse(BigDecimal.valueOf(0)));
            technicianListVO.setCustomerName(Optional.ofNullable(customerDao.searchById(basicProjectEntity.getCustomerId()).getCustomerName()).orElse(""));
            technicianListVO.setProjectName(Optional.ofNullable(basicProjectEntity.getProjectName()).orElse(""));
            technicianListVO.setName(Optional.ofNullable(basicTechnicianEntity.getName()).orElse(""));
            //应用于排序的字段
            technicianListVO.setBelongCompany(Optional.ofNullable(basicTechnicianEntity.getBelongCompany()).orElse(""));
            technicianListVO.setCreateTime(tempR.getCreateTime() == null ? DateUtils.getNowDate() : tempR.getCreateTime());
            technicianListVO.setTechnicianId(tempR.getTechnicianId());
            //确定真正的cBeginMonth和cEndMonth
            if (tempR.getCBeginMonth().isBefore(dateStart)) {
                //beginMonth在dateStart前,以dateStart为准
                technicianListVO.setCBeginMonth(DateUtils.localDateToStr(dateStart, "yyyy-MM"));
            } else {
                technicianListVO.setCBeginMonth(DateUtils.localDateToStr(tempR.getCBeginMonth(), "yyyy-MM"));
            }
            if (tempR.getCEndMonth() == null && tempR.getStopMonth() == null) {
                //都为空，以dateEnd为准
                technicianListVO.setCEndMonth(DateUtils.localDateToStr(dateEnd, "yyyy-MM"));
                technicianListVO.setStopMonth("");
            } else {
                if (tempR.getCEndMonth() == null && tempR.getStopMonth() != null) {
                    if (dateEnd.isBefore(tempR.getStopMonth())) {
                        //dateEnd在stop之前，为null
                        technicianListVO.setStopMonth("");
                    } else {
                        technicianListVO.setStopMonth(DateUtils.localDateToStr(tempR.getStopMonth(), "yyyy-MM"));
                    }
                    technicianListVO.setCEndMonth(DateUtils.localDateToStr(dateEnd, "yyyy-MM"));
                } else if (tempR.getCEndMonth() != null && tempR.getStopMonth() == null) {
                    if (dateEnd.isBefore(tempR.getCEndMonth())) {
                        //dateEnd在cEndMonth前，以dateEnd为准
                        technicianListVO.setCEndMonth(DateUtils.localDateToStr(dateEnd, "yyyy-MM"));
                    } else {
                        technicianListVO.setCEndMonth(DateUtils.localDateToStr(tempR.getCEndMonth(), "yyyy-MM"));
                    }
                    technicianListVO.setStopMonth("");
                }
                //tempR.getCEndMonth() != null && tempR.getStopMonth() != null
                else {
                    //只认stop
                    if (dateEnd.isBefore(tempR.getStopMonth())) {
                        //dateEnd在stop之前，为null
                        technicianListVO.setStopMonth("");
                    } else {
                        technicianListVO.setStopMonth(DateUtils.localDateToStr(tempR.getStopMonth(), "yyyy-MM"));
                    }
                    //填充end
                    if (dateEnd.isBefore(tempR.getCEndMonth())) {
                        //dateEnd在cEndMonth前，以dateEnd为准
                        technicianListVO.setCEndMonth(DateUtils.localDateToStr(dateEnd, "yyyy-MM"));
                    } else {
                        technicianListVO.setCEndMonth(DateUtils.localDateToStr(tempR.getCEndMonth(), "yyyy-MM"));
                    }
                }
            }

            if (tempR.getPriceMonth() != null) {
                //technicianListVO.setStopMonth(DateUtils.localDateToStr(tempR.getPriceMonth(), "yyyy-MM"));
                technicianListVO.setColorLeft(ExcelSystemConstants.GRAY_YES);
            } else {
                //为null则为单条记录  或者 由价格变动月变化而来

                if (tempR.getParentId() != 0) {
                    // 为价格变动月，左侧一定为灰
                    technicianListVO.setColorLeft(ExcelSystemConstants.GRAY_YES);
                } else {
                    // 单条记录，且左侧一定不为灰
                    technicianListVO.setColorLeft(ExcelSystemConstants.GRAY_NO);
                }

            }

            technicianListVOS.add(technicianListVO);
        }
        return technicianListVOS;
    }*/

    /**
     * @param relatedProjectTechnicians:
     * @param dateStart:
     * @param dateEnd:
     * @return List<TechnicianListVO>
     * @author he_jiale
     * @description 确定新的cBeginMonth、cEndMonth、stopMonth以及全体管理信息[仅用于全体项目管理表！]
     * @date 2024/07/10 15:33
     */
    @Transactional
    public List<RelatedProjectTechBO> changeRelatedBOList(
            List<RelatedProjectTechnician> relatedProjectTechnicians, LocalDate dateStart, LocalDate dateEnd) {

        List<RelatedProjectTechBO> technicianListVOS = new ArrayList<>();

        // 获取所有项目ID和技师ID
        Set<Long> projectIds = relatedProjectTechnicians.stream()
                .map(RelatedProjectTechnician::getProjectId)
                .collect(Collectors.toSet());

        Set<Long> technicianIds = relatedProjectTechnicians.stream()
                .map(RelatedProjectTechnician::getTechnicianId)
                .collect(Collectors.toSet());

//        todo
        // 批量查询项目和技师
        List<BasicProjectEntity> projects = projectDao.searchByIds(new ArrayList<>(projectIds));
        List<BasicTechnicianEntity> technicians = technicianDao.searchByIds(new ArrayList<>(technicianIds));

        // 将查询结果转化为Map
        Map<Long, BasicProjectEntity> projectMap = projects.stream()
                .collect(Collectors.toMap(BasicProjectEntity::getProjectId, p -> p));

        Map<Long, BasicTechnicianEntity> technicianMap = technicians.stream()
                .collect(Collectors.toMap(BasicTechnicianEntity::getTechnicianId, t -> t));

        // 使用parallelStream并行处理
        technicianListVOS = relatedProjectTechnicians.parallelStream()
                .map(tempR -> {
                    RelatedProjectTechBO technicianListVO = new RelatedProjectTechBO();
                    BasicProjectEntity basicProjectEntity = projectMap.get(tempR.getProjectId());
                    BasicTechnicianEntity basicTechnicianEntity = technicianMap.get(tempR.getTechnicianId());

                    technicianListVO.setProjectTechnicianId(tempR.getProjectTechnicianId());
                    technicianListVO.setTechnicianId(tempR.getTechnicianId());
                    technicianListVO.setStatus(Optional.ofNullable(tempR.getStatus()).orElse(""));
                    technicianListVO.setCPrice(Optional.ofNullable(tempR.getCPrice()).orElse(BigDecimal.valueOf(0)));
                    technicianListVO.setHPrice(Optional.ofNullable(tempR.getHPrice()).orElse(BigDecimal.valueOf(0)));
                    technicianListVO.setCLowerHours(Optional.ofNullable(tempR.getCLowerHours()).orElse(BigDecimal.valueOf(0)));
                    technicianListVO.setCHigherHours(Optional.ofNullable(tempR.getCHigherHours()).orElse(BigDecimal.valueOf(0)));
                    technicianListVO.setCReductPrice(Optional.ofNullable(tempR.getCReductPrice()).orElse(BigDecimal.valueOf(0)));
                    technicianListVO.setCIncreasePrice(Optional.ofNullable(tempR.getCIncreasePrice()).orElse(BigDecimal.valueOf(0)));
                    technicianListVO.setStandardHours(Optional.ofNullable(tempR.getStandardHours()).orElse(BigDecimal.valueOf(0)));
                    technicianListVO.setCustomerName(Optional.ofNullable(customerDao.searchById(basicProjectEntity.getCustomerId()).getCustomerName()).orElse(""));
                    technicianListVO.setProjectName(Optional.ofNullable(basicProjectEntity.getProjectName()).orElse(""));
                    technicianListVO.setName(Optional.ofNullable(basicTechnicianEntity.getName()).orElse(""));
                    technicianListVO.setBelongCompany(Optional.ofNullable(basicTechnicianEntity.getBelongCompany()).orElse(""));
                    technicianListVO.setCreateTime(tempR.getCreateTime() == null ? DateUtils.getNowDate() : tempR.getCreateTime());
                    technicianListVO.setTechnicianId(tempR.getTechnicianId());

                    // 确定真正的cBeginMonth和cEndMonth
                    if (tempR.getCBeginMonth().isBefore(dateStart)) {
                        technicianListVO.setCBeginMonth(DateUtils.localDateToStr(dateStart, "yyyy-MM"));
                    } else {
                        technicianListVO.setCBeginMonth(DateUtils.localDateToStr(tempR.getCBeginMonth(), "yyyy-MM"));
                    }
                    if (tempR.getCEndMonth() == null && tempR.getStopMonth() == null) {
                        technicianListVO.setCEndMonth(DateUtils.localDateToStr(dateEnd, "yyyy-MM"));
                        technicianListVO.setStopMonth("");
                    } else {
                        if (tempR.getCEndMonth() == null && tempR.getStopMonth() != null) {
                            if (dateEnd.isBefore(tempR.getStopMonth())) {
                                technicianListVO.setStopMonth("");
                            } else {
                                technicianListVO.setStopMonth(DateUtils.localDateToStr(tempR.getStopMonth(), "yyyy-MM"));
                            }
                            technicianListVO.setCEndMonth(DateUtils.localDateToStr(dateEnd, "yyyy-MM"));
                        } else if (tempR.getCEndMonth() != null && tempR.getStopMonth() == null) {
                            if (dateEnd.isBefore(tempR.getCEndMonth())) {
                                technicianListVO.setCEndMonth(DateUtils.localDateToStr(dateEnd, "yyyy-MM"));
                            } else {
                                technicianListVO.setCEndMonth(DateUtils.localDateToStr(tempR.getCEndMonth(), "yyyy-MM"));
                            }
                            technicianListVO.setStopMonth("");
                        } else {
                            if (dateEnd.isBefore(tempR.getStopMonth())) {
                                technicianListVO.setStopMonth("");
                            } else {
                                technicianListVO.setStopMonth(DateUtils.localDateToStr(tempR.getStopMonth(), "yyyy-MM"));
                            }
                            if (dateEnd.isBefore(tempR.getCEndMonth())) {
                                technicianListVO.setCEndMonth(DateUtils.localDateToStr(dateEnd, "yyyy-MM"));
                            } else {
                                technicianListVO.setCEndMonth(DateUtils.localDateToStr(tempR.getCEndMonth(), "yyyy-MM"));
                            }
                        }
                    }

                    if (tempR.getPriceMonth() != null) {
                        technicianListVO.setColorLeft(ExcelSystemConstants.GRAY_YES);
                    } else {
                        if (tempR.getParentId() != 0) {
                            technicianListVO.setColorLeft(ExcelSystemConstants.GRAY_YES);
                        } else {
                            technicianListVO.setColorLeft(ExcelSystemConstants.GRAY_NO);
                        }
                    }

                    return technicianListVO;
                })
                .collect(Collectors.toList());

        return technicianListVOS;
    }


    @Override
    public void sortProjectTechnicianList(List<ProjectTechnicianRow> projectTechnicianRows) {
        projectTechnicianRows.sort((o1, o2) -> {
            int cmp = o1.getCustomerName().compareTo(o2.getCustomerName());
            if (cmp == 0) {
                cmp = o1.getProjectName().compareTo(o2.getProjectName());
                if (cmp == 0) {
                    cmp = o1.getBelongCompany().compareTo(o2.getBelongCompany());
                    if (cmp == 0) {
                        cmp = o1.getTechnicianId().compareTo(o2.getTechnicianId());
                        if (cmp == 0) {
                            cmp = o1.getCreateTime().compareTo(o2.getCreateTime());
                        }
                    }
                }
            }
            return cmp;
        });
    }

    @Override
    @Transactional
    public void saveProjectTechnicianList(SaveProjectTechnicianListForm form) {
        monthService.saveAllItems(form);

    }

    @Override
    @Transactional
    public List<ProjectMonthListVO> searchProjectMonthList(LocalDate localDate, Long customerId) {
        //先通过customerId确定有哪些项目
        List<BasicProjectEntity> basicProjectEntities = projectDao.searchByCustomerId(customerId);
        if (basicProjectEntities.isEmpty()) {
            // throw new ServiceException("この顧客は今月、プロジェクトがいません。");
            return new ArrayList<>();
        }
        //并通过项目名进行排序
        basicProjectEntities.sort(Comparator.comparing(BasicProjectEntity::getProjectName));


        return getJobManageListVOS(basicProjectEntities, localDate, customerId);
    }

    /**
     * @param basicProjectEntities:
     * @param localDate:
     * @return List<JobManageListVO>
     * @author he_jiale
     * @description 生成作业管理表的返回内容
     * @date 2024/07/12 18:48
     */
    @Transactional
    public List<ProjectMonthListVO> getJobManageListVOS(List<BasicProjectEntity> basicProjectEntities, LocalDate
            localDate, Long customerId) {
        //声明返回值
        List<ProjectMonthListVO> projectMonthListVOS = new ArrayList<>();
        List<ProjectMonthListVO> tempProjectMonthListVOS = new ArrayList<>();
        List<Long> projectIds = new ArrayList<>();
        for (BasicProjectEntity basicProjectEntity : basicProjectEntities) {
            projectIds.add(basicProjectEntity.getProjectId());
        }
        int countProject = 0;
        int countCustomer = 0;
        BigDecimal orderSum = BigDecimal.ZERO;
        for (Long projectId : projectIds) {
            //通过以一个项目确定有哪些技术者关联
            List<RelatedProjectTechnician> tempRelateds = relatedProjectTechnicianDao.searchByProjectId(projectId);

            if (!tempRelateds.isEmpty()) {
                //过滤得到该月工作的不重复的技术者关联记录，以及projectTechnicianId，可以实时更新
                List<RelatedProjectTechnician> newTechnicianListVOS = getRelatedByYearMonth(tempRelateds, localDate);
                //用得到的projectTechnicianId和yearMonth去得到projectTechnicianId记录
                List<Long> projectTechnicianIds = newTechnicianListVOS.stream().map(RelatedProjectTechnician::getProjectTechnicianId)
                        .collect(Collectors.toList());
                countProject = projectTechnicianIds.size();
                countCustomer += countProject;
                Long monthId = null;
                Boolean flagProject = true;
                //得到所有的技术者信息
                for (Long projectTechId : projectTechnicianIds) {
                    //对每个项目得到technicianList
                    ProjectMonthListVO jobTechnicianListVO = new ProjectMonthListVO();
                    BasicMonthEntity basicMonthEntity = monthService.searchByProjectTechIdAndYearMonth(projectTechId, localDate);
                    if (basicMonthEntity == null) {
                        //The job management table cannot be generated because the overall management table data has not been generated!
                        throw new ServiceException("全体管理表のデータが生成されていないため、作業管理表を生成できません！");
                    }
                    jobTechnicianListVO.setMonthId(basicMonthEntity.getMonthId());
                    monthId = basicMonthEntity.getMonthId();
                    //设置基本数据
                    setMonthData(customerId, basicMonthEntity, jobTechnicianListVO);

                    //得到dateList
                    jobTechnicianListVO.setDateList(getDateList(monthId));
                    tempProjectMonthListVOS.add(jobTechnicianListVO);
                }
                //对技术者列表通过项目名进行排序
                tempProjectMonthListVOS = sortJobTechnicianListVO(tempProjectMonthListVOS);
                for (ProjectMonthListVO tempPro : tempProjectMonthListVOS) {
                    //得到countProject
                    if (flagProject) {
                        tempPro.setCountProject(countProject);
                        orderSum = orderSum.add(tempPro.getOrderAmount());
                        flagProject = Boolean.FALSE;
                    } else {
                        tempPro.setCountProject(0);
                        tempPro.setCountCustomer(0);
                    }


                }
            }
            projectMonthListVOS.addAll(tempProjectMonthListVOS);
            tempProjectMonthListVOS.clear();
        }

        if (!projectMonthListVOS.isEmpty()) {
            projectMonthListVOS.get(0).setCountCustomer(countCustomer);
            projectMonthListVOS.get(0).setOrderAmountSum(orderSum);
        }

        //循环判断，如果dateList为空，是新增的记录，找到该月已有数据的记录数，创建空内容的对象生成一个链表
        setDateListVO(projectMonthListVOS);

        return projectMonthListVOS;

    }

    public void setDateListVO(List<ProjectMonthListVO> projectMonthListVOS) {
        // Step 1: Find the maximum size of dateListVOS
        int maxDateListSize = 0;
        for (ProjectMonthListVO tempProMonth : projectMonthListVOS) {
            int currentSize = tempProMonth.getDateList().size();
            if (currentSize > maxDateListSize) {
                maxDateListSize = currentSize;
            }
        }

        // Step 2: Process each ProjectMonthListVO and set dateListVOS accordingly
        for (ProjectMonthListVO tempProMonth : projectMonthListVOS) {
            List<DateListVO> dateListVOS = tempProMonth.getDateList();

            // Step 3: If size is 0, fill with empty strings
            if (dateListVOS.size() == 0) {
                // Create a new list with the size of maxDateListSize, filled with empty strings
                //用于创建一个指定数量的元素相同的列表。这里的 n 是列表的大小，obj 是要填充到列表中的元素
                List<DateListVO> filledDateListVOS = new ArrayList<>(Collections.nCopies(maxDateListSize, new DateListVO("", "")));
                tempProMonth.setDateList(filledDateListVOS);
            }
        }
    }

    @Transactional
    public void setMonthData(Long customerId, BasicMonthEntity basicMonthEntity, ProjectMonthListVO
            jobTechnicianListVO) {
        String customerName = customerDao.searchById(customerId).getCustomerName();
        jobTechnicianListVO.setCustomerName(customerName);

        jobTechnicianListVO.setProductName(Optional.ofNullable(basicMonthEntity.getProductName()).orElse(""));
        jobTechnicianListVO.setProductNum(Optional.ofNullable(basicMonthEntity.getProductNum()).orElse(""));
        jobTechnicianListVO.setProjectTechnicianId(basicMonthEntity.getProjectTechnicianId());
        jobTechnicianListVO.setProductNum(Optional.ofNullable(basicMonthEntity.getProductNum()).orElse(""));
        jobTechnicianListVO.setCPrice(Optional.ofNullable(basicMonthEntity.getExpectedPrice()).orElse(BigDecimal.valueOf(0)));
        jobTechnicianListVO.setSummary(Optional.ofNullable(basicMonthEntity.getSummary()).orElse(""));
        jobTechnicianListVO.setOrderAmount(Optional.ofNullable(basicMonthEntity.getOrderAmount()).orElse(BigDecimal.valueOf(0)));
        jobTechnicianListVO.setRemark(Optional.ofNullable(basicMonthEntity.getRemark()).orElse(""));
        jobTechnicianListVO.setPersonMonth(Optional.ofNullable(basicMonthEntity.getPersonMonth()).orElse(BigDecimal.valueOf(0)));

        RelatedProjectTechnician relatedProjectTechnician = relatedProjectTechnicianDao.searchByProjectTechId(basicMonthEntity.getProjectTechnicianId());
        BasicTechnicianEntity basicTechnicianEntity = technicianDao.searchById(relatedProjectTechnician.getTechnicianId());
        BasicProjectEntity basicProjectEntity = projectDao.searchById(relatedProjectTechnician.getProjectId());

        //以下是用于排序
        jobTechnicianListVO.setBelongCompany(basicTechnicianEntity.getBelongCompany());
        jobTechnicianListVO.setProjectName(basicProjectEntity.getProjectName());
        //以下是两个特殊类型的字段返回
        jobTechnicianListVO.setName(basicTechnicianEntity.getName());
        jobTechnicianListVO.setPrincipal(Optional.ofNullable(basicProjectEntity.getPrincipal()).orElse(""));
    }

    @Transactional
    public List<DateListVO> getDateList(Long monthId) {
        //子属性返回值
        List<DateListVO> dateListVOS = new ArrayList<>();
        //再得到每个dateList
        //因为同一个项目的作业日期列表都是一样的，所以只取第一个monthId拿到值即可
        if (monthId != null) {
            List<BasicDateEntity> basicDateEntities = dateDao.searchByMonthId(monthId);

            for (BasicDateEntity tempD : basicDateEntities) {
                DateListVO dateListVO = new DateListVO();
                dateListVO.setName(tempD.getName());
                dateListVO.setMonthDate(tempD.getMonthDate() == null ? "" : String.valueOf(tempD.getMonthDate()));
                dateListVOS.add(dateListVO);
            }
            dateListVOS.sort(Comparator.comparing(DateListVO::getName));

        }
        return dateListVOS;
    }

    /**
     * @param relatedProjectTechnicians:
     * @param yearMonth:
     * @return List<RelatedProjectTechnician>
     * @author he_jiale
     * @description 确定yearMonth时候在工作的related记录，必不重复
     * @date 2024/07/12 15:13
     */
    public List<RelatedProjectTechnician> getRelatedByYearMonth
    (List<RelatedProjectTechnician> relatedProjectTechnicians, LocalDate yearMonth) {
        List<RelatedProjectTechnician> result = new ArrayList<>();
//        for(RelatedProjectTechnician tempR:relatedProjectTechnicians){
//            if(!localDate.isBefore(tempR.getCBeginMonth())){
//                if(tempR.getStopMonth()!=null){
//                    if(!localDate.isAfter(tempR.getStopMonth())){
//                        result.add(tempR);
//                    }
//                }else{
//                    if(tempR.getCEndMonth()!=null){
//                        if(!localDate.isAfter(tempR.getCEndMonth())){
//                            result.add(tempR);
//                        }
//                    }else{
//                        result.add(tempR);
//                    }
//                }
//            }
//        }
        //以下是优化后的代码，保留优化前的代码，逻辑更易理解
        for (RelatedProjectTechnician tempR : relatedProjectTechnicians) {
            LocalDate beginMonth = tempR.getCBeginMonth();
            //tempR.getStopMonth()要减一个月，不能算进去
            LocalDate endMonth = tempR.getStopMonth() != null ? tempR.getStopMonth().minusMonths(1) : tempR.getCEndMonth();

            if (!yearMonth.isBefore(beginMonth) && (endMonth == null || !yearMonth.isAfter(endMonth))) {
                result.add(tempR);
            }
        }
        return result;
    }

    public List<ProjectMonthListVO> sortJobTechnicianListVO(List<ProjectMonthListVO> jobTechnicianListVOS) {
        jobTechnicianListVOS.sort(new Comparator<ProjectMonthListVO>() {
            @Override
            public int compare(ProjectMonthListVO o1, ProjectMonthListVO o2) {
                int result = o1.getProjectName().compareTo(o2.getProjectName());
                if (result == 0) {
                    result = o1.getBelongCompany().compareTo(o2.getBelongCompany());
                }

                // 如果两个对象顺序被交换，则交换它们的字段
//                if (result > 0) {
//                    int tempA = o1.getCountProject();
//                    o1.setCountProject(o2.getCountProject());
//                    o2.setCountProject(tempA);
//                }

                return result;
            }
        });

        return jobTechnicianListVOS;
    }

    @Override
/*    public void saveProjectMonthList(List<ProjectMonthListVO> form) {

        //同一个项目进行保存
        if (!form.isEmpty()) {
            int countProject = form.get(0).getCountProject();
            int count = 0;
            List<BasicMonthEntity> basicMonthEntities = new ArrayList<>();
            int temp = 0;
            while (count < form.size()) {
                BasicMonthEntity monthEntity = new BasicMonthEntity();
                monthEntity.setMonthId(form.get(count).getMonthId());
                monthEntity.setProjectTechnicianId(form.get(count).getProjectTechnicianId());

                monthEntity.setRemark(form.get(count).getRemark());
                monthEntity.setPersonMonth(form.get(count).getPersonMonth());
                if (form.get(count).getCountProject() != 0) {
                    temp = count;
                    monthEntity.setProductName(form.get(count).getProductName());
                    monthEntity.setProductNum(form.get(count).getProductNum());
                    monthEntity.setSummary(form.get(count).getSummary());
                    monthEntity.setOrderAmount(form.get(count).getOrderAmount());
                }
                {
                    monthEntity.setProductName(form.get(temp).getProductName());
                    monthEntity.setProductNum(form.get(temp).getProductNum());
                    monthEntity.setSummary(form.get(temp).getSummary());
                    monthEntity.setOrderAmount(form.get(temp).getOrderAmount());
                }
                basicMonthEntities.add(monthEntity);
                if (form.get(count).getCountProject() != 0) {
                    temp = count;
                    addProjectMonth(basicMonthEntities, form.get(0).getDateList(), form.get(count).getDateList());
                }
                {
                    addProjectMonth(basicMonthEntities, form.get(0).getDateList(), form.get(temp).getDateList());
                }
                count++;
                if (count == countProject) {
                    basicMonthEntities.clear();
                    if (count >= form.size()) {
                        break;
                    }
                    countProject += form.get(count).getCountProject();

                }
            }
        }
    }*/

    public void saveProjectMonthList(List<ProjectMonthListVO> form) {
        if (form.isEmpty()) {
            return;
        }

        int countProject = form.get(0).getCountProject();
        int count = 0;
        List<BasicMonthEntity> basicMonthEntities = new ArrayList<>();
        int temp = 0;

        while (count < form.size()) {
            ProjectMonthListVO currentForm = form.get(count);
            BasicMonthEntity monthEntity = new BasicMonthEntity();
            monthEntity.setMonthId(currentForm.getMonthId());
            monthEntity.setProjectTechnicianId(currentForm.getProjectTechnicianId());
            monthEntity.setRemark(currentForm.getRemark());
            monthEntity.setPersonMonth(currentForm.getPersonMonth());

            if (currentForm.getCountProject() != 0) {
                temp = count;
                monthEntity.setProductName(currentForm.getProductName());
                monthEntity.setProductNum(currentForm.getProductNum());
                monthEntity.setSummary(currentForm.getSummary());
                monthEntity.setOrderAmount(currentForm.getOrderAmount());
            } else {
                ProjectMonthListVO tempForm = form.get(temp);
                monthEntity.setProductName(tempForm.getProductName());
                monthEntity.setProductNum(tempForm.getProductNum());
                monthEntity.setSummary(tempForm.getSummary());
                monthEntity.setOrderAmount(tempForm.getOrderAmount());
            }

            basicMonthEntities.add(monthEntity);

            if (currentForm.getCountProject() != 0) {
                temp = count;
            }

            addProjectMonth(basicMonthEntities, form.get(0).getDateList(), form.get(temp).getDateList());
            count++;

            if (count == countProject) {
                basicMonthEntities.clear();
                if (count >= form.size()) {
                    break;
                }
                countProject += form.get(count).getCountProject();
            }
        }
    }



    /**
     * @param basicMonthEntities:
     * @param firstDateListVOS:
     * @param dateListVOS:
     * @return void
     * @author he_jiale
     * @description 增加日期信息
     * @date 2024/7/22 16:46
     */


/*    public void addProjectMonth
    (List<BasicMonthEntity> basicMonthEntities, List<DateListVO> firstDateListVOS, List<DateListVO> dateListVOS) {
        try {
            //第一条记录才是对的name
            monthService.edit(basicMonthEntities);
            if (!dateListVOS.isEmpty()) {
                List<Long> longList = basicMonthEntities.stream().map(BasicMonthEntity::getMonthId).collect(Collectors.toList());
                for (int i = 0; i < longList.size(); i++) {
                    dateDao.removeByMonthId(longList.get(i));
                }
                int count = 0;
                for (Long i : longList) {
                    for (DateListVO dateListVO : dateListVOS) {
                        BasicDateEntity basicDateEntity = new BasicDateEntity();
                        basicDateEntity.setMonthId(i);
                        basicDateEntity.setName(firstDateListVOS.get(count).getName());
                        if (StringUtils.equals(dateListVO.getMonthDate(), "") || dateListVO.getMonthDate() == null) {
                            basicDateEntity.setMonthDate(null);
                        } else {
                            basicDateEntity.setMonthDate(DateUtils.toLocalDateFirstDay(dateListVO.getMonthDate()));

                        }
                        dateDao.add(basicDateEntity);
                        count++;
                    }
                    //重新计数寻找表头
                    count = 0;
                }
            } else {
                //为空则删除所有
                List<Long> longList = basicMonthEntities.stream().map(BasicMonthEntity::getMonthId).collect(Collectors.toList());
                for (int i = 0; i < longList.size(); i++) {
                    dateDao.removeByMonthId(longList.get(i));
                }
            }
        } catch (Exception e) {
            throw new ServiceException("projectMonthフォームを保存できませんでした。" + e);
        }
    }*/

    @Transactional
    public void addProjectMonth(List<BasicMonthEntity> basicMonthEntities, List<DateListVO> firstDateListVOS, List<DateListVO> dateListVOS) {
        try {
            // 第一条记录才是对的name
            monthService.edit(basicMonthEntities);

            if (!dateListVOS.isEmpty()) {
                // 批量获取MonthId
                List<Long> longList = basicMonthEntities.stream().map(BasicMonthEntity::getMonthId).collect(Collectors.toList());

                // 批量删除旧记录
                dateDao.removeByMonthIds(longList);

                // 批量添加新记录
                List<BasicDateEntity> basicDateEntities = new ArrayList<>();
                for (int i = 0; i < longList.size(); i++) {
                    Long monthId = longList.get(i);
                    for (int j = 0; j < dateListVOS.size(); j++) {
                        DateListVO dateListVO = dateListVOS.get(j);
                        BasicDateEntity basicDateEntity = new BasicDateEntity();
                        basicDateEntity.setMonthId(monthId);
                        basicDateEntity.setName(firstDateListVOS.get(i).getName());

                        if (StringUtils.isBlank(dateListVO.getMonthDate())) {
                            basicDateEntity.setMonthDate(null);
                        } else {
                            basicDateEntity.setMonthDate(DateUtils.toLocalDateFirstDay(dateListVO.getMonthDate()));
                        }
                        basicDateEntities.add(basicDateEntity);
                    }
                }
                dateDao.addAll(basicDateEntities);
            } else {
                // 为空则删除所有
                List<Long> longList = basicMonthEntities.stream().map(BasicMonthEntity::getMonthId).collect(Collectors.toList());
                dateDao.removeByMonthIds(longList);
            }
        } catch (Exception e) {
            throw new ServiceException("projectMonthフォームを保存できませんでした。" + e);
        }
    }



    @Transactional
    @Override
    public void flushStats(LocalDate dateStart, LocalDate dateEnd) {
        List<RelatedProjectTechnician> relatedProjectTechnicians = relatedProjectTechnicianDao.searchBetweenStartAndEnd(String.valueOf(dateStart), String.valueOf(dateEnd));
        List<TechnicianListVO> technicianListVOS = TableUtils.changeStartAndEndMonth(relatedProjectTechnicians, dateStart, dateEnd);
        //获取一年的月份范围
        List<String> yearMonths = TableUtils.getYearMonthsBetween(dateStart, dateEnd);
        List<TechnicianListStatsVO> technicianListStatsVOS = new ArrayList<>();
        //获取belongCompany
        for (TechnicianListVO tempT : technicianListVOS) {
            tempT.setBelongCompany(technicianDao.searchById(tempT.getTechnicianId()).getBelongCompany());
        }
        for (String tempYearMonth : yearMonths) {
            //查询该月有哪些UCL技术者工作
            List<TechnicianListVO> techniciansInRanges = TableUtils.getTechniciansInRange(technicianListVOS, tempYearMonth);
            TechnicianListStatsVO technicianListStatsVO = new TechnicianListStatsVO();
            int uclNum = countTechniciansByCompany(techniciansInRanges, "UCL");
            int bpNum = techniciansInRanges.size() - uclNum;
            int totalNumber = techniciansInRanges.size();
            BigDecimal bpUclRate = null;
            if (uclNum == 0) {
                //虽然不存在这种情况，碰到-1即前端显示为空
                bpUclRate = BigDecimal.valueOf(-1);
            } else {
                BigDecimal bigUclNum = BigDecimal.valueOf(uclNum);
                BigDecimal bigBpNum = BigDecimal.valueOf(bpNum);
                bpUclRate = bigBpNum.divide(bigUclNum, 2, RoundingMode.HALF_UP);
            }
            BasicStatsEntity basicStatsEntity = new BasicStatsEntity(null, BigDecimal.valueOf(uclNum),
                    BigDecimal.valueOf(bpNum), BigDecimal.valueOf(totalNumber), bpUclRate, DateUtils.toLocalDateFirstDay(tempYearMonth));
            statsDao.modifyStats(basicStatsEntity);
        }
    }



/*  原版
    @Override
    public void flushMonthData(SaveProjectTechnicianListForm form) {
        List<BasicMonthEntity> basicMonthEntities = new ArrayList<>();
        for (SaveProjectTechnicianRow row : form.getRow()) {
            List<MonthDataListVO> monthDataList = row.getMonthDataList();
            int i = 0;
            for (MonthDataListVO monthDataListVO : monthDataList) {
                BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
                basicMonthEntity.setMonthId(monthDataListVO.getMonthId());
                BigDecimal standardHours = Optional.ofNullable(relatedProjectTechnicianDao.searchByProjectTechId(row.getProjectTechnicianId()).getStandardHours()).orElse(BigDecimal.ZERO);
                BigDecimal newPresumedTime = standardHours.multiply(BigDecimal.valueOf(form.getColumn().get(i).getMonthDays()));
                //修改预计时间
                basicMonthEntity.setPresumedTime(newPresumedTime);
                //修改预计单价
                basicMonthEntity.setExpectedPrice(monthDataListVO.getExpectedPrice());
                //修改实际时间
                basicMonthEntity.setActualHours(monthDataListVO.getActualHours());
                basicMonthEntity.setActualPriceEdit(monthDataListVO.getActualPriceEdit());
                basicMonthEntity.setFromEdit(monthDataListVO.getFromEdit());
                //修改实际价格
                if (monthDataListVO.getActualPriceEdit()) {
                    //以人工修改的为准
                    basicMonthEntity.setActualPrice(monthDataListVO.getActualPrice());
                } else {
                    //自动计算
                    BigDecimal actNumber = getNewActualPrice(monthDataListVO, row.getProjectTechnicianId());
                    BigDecimal roundedNumber = actNumber.setScale(2, RoundingMode.HALF_UP);
                    basicMonthEntity.setActualPrice(roundedNumber);

                }
                //修改乖离
                if (monthDataListVO.getFromEdit()) {
                    //以人工修改的为准
                    basicMonthEntity.setFrom(monthDataListVO.getFrom());
                } else {
                    //自动计算
                    BigDecimal fromNumber = basicMonthEntity.getActualPrice().subtract(basicMonthEntity.getExpectedPrice());
                    BigDecimal roundedNumber = fromNumber.setScale(2, RoundingMode.HALF_UP);
                    basicMonthEntity.setFrom(roundedNumber);
                }
                basicMonthEntity.setUpdateBy(SecurityUtils.getUsername());
                basicMonthEntities.add(basicMonthEntity);
                i++;
            }
        }
        for (BasicMonthEntity basicMonthEntity : basicMonthEntities) {
            try {
                monthDao.save(basicMonthEntity);
            } catch (Exception e) {
                throw new ServiceException("フォームの basicMonthEntity を保存できませんでした。" + e);
            }
        }
    }*/


    //批量更新
    @Override
    @Transactional
    public void flushMonthData(SaveProjectTechnicianListForm form) {
        List<BasicMonthEntity> basicMonthEntities = new ArrayList<>();
        Map<Long, BigDecimal> standardHoursCache = new HashMap<>();

        // 预先查询所有的标准工时并缓存
        List<Long> projectTechnicianIds = form.getRow().stream()
                .map(SaveProjectTechnicianRow::getProjectTechnicianId)
                .collect(Collectors.toList());
//        List<ProjectTechnician> projectTechnicians = relatedProjectTechnicianDao.findAllById(projectTechnicianIds);
        List<RelatedProjectTechnician> relatedProjectTechnicians = relatedProjectTechnicianDao.findAllById(projectTechnicianIds);

        for (RelatedProjectTechnician relatedProjectTechnician : relatedProjectTechnicians) {
            standardHoursCache.put(relatedProjectTechnician.getProjectTechnicianId(), relatedProjectTechnician.getStandardHours());
        }

        for (SaveProjectTechnicianRow row : form.getRow()) {
            List<MonthDataListVO> monthDataList = row.getMonthDataList();
            int i = 0;
            BigDecimal standardHours = standardHoursCache.getOrDefault(row.getProjectTechnicianId(), BigDecimal.ZERO);
            for (MonthDataListVO monthDataListVO : monthDataList) {
                BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
                basicMonthEntity.setMonthId(monthDataListVO.getMonthId());
                BigDecimal newPresumedTime = standardHours.multiply(BigDecimal.valueOf(form.getColumn().get(i).getMonthDays()));

                // 修改预计时间
                basicMonthEntity.setPresumedTime(newPresumedTime);
                // 修改预计单价
                basicMonthEntity.setExpectedPrice(monthDataListVO.getExpectedPrice());
                // 修改实际时间
                basicMonthEntity.setActualHours(monthDataListVO.getActualHours());
                basicMonthEntity.setActualPriceEdit(monthDataListVO.getActualPriceEdit());
                basicMonthEntity.setFromEdit(monthDataListVO.getFromEdit());
                // 修改实际价格
                if (monthDataListVO.getActualPriceEdit()) {
                    // 以人工修改的为准
                    basicMonthEntity.setActualPrice(monthDataListVO.getActualPrice());
                } else {
                    // 自动计算
                    BigDecimal actNumber = getNewActualPrice(monthDataListVO, row.getProjectTechnicianId());
                    BigDecimal roundedNumber = actNumber.setScale(2, RoundingMode.HALF_UP);
                    basicMonthEntity.setActualPrice(roundedNumber);
                }
                // 修改乖离
                if (monthDataListVO.getFromEdit()) {
                    // 以人工修改的为准
                    basicMonthEntity.setFrom(monthDataListVO.getFrom());
                } else {
                    // 自动计算
                    BigDecimal fromNumber = basicMonthEntity.getActualPrice().subtract(basicMonthEntity.getExpectedPrice());
                    BigDecimal roundedNumber = fromNumber.setScale(2, RoundingMode.HALF_UP);
                    basicMonthEntity.setFrom(roundedNumber);
                }
                basicMonthEntity.setUpdateBy(SecurityUtils.getUsername());
                basicMonthEntities.add(basicMonthEntity);
                i++;
            }
        }
        // 批量保存
        batchSave(basicMonthEntities);
    }

    //批量更新
    @Transactional
    public void batchSave(List<BasicMonthEntity> basicMonthEntities) {
        try {
            monthDao.saveAll(basicMonthEntities);
        } catch (Exception e) {
            throw new ServiceException("フォームの basicMonthEntity を保存できませんでした。" + e);
        }
    }


    //计算最新的实际单价，原理同monthService的getNewActualPrice
    @Transactional
    public BigDecimal getNewActualPrice(MonthDataListVO monthDataListVO, Long projectTechnicianId) {
        //低于下限时间: 実績単価＝見込単価－減単金*（精算幅（下限）-実時間）
        BigDecimal newActualPrice;
        RelatedProjectTechnician relatedProjectTechnician = relatedProjectTechnicianDao.searchByProjectTechId(projectTechnicianId);
        if (monthDataListVO.getActualHours() == null && relatedProjectTechnician.getCLowerHours() == null) {
            monthDataListVO.setActualHours(BigDecimal.ZERO);
            relatedProjectTechnician.setCLowerHours(BigDecimal.ZERO);
        }

        if (monthDataListVO.getActualHours() == null) {
            monthDataListVO.setActualHours(BigDecimal.ZERO);
        }
        if (relatedProjectTechnician.getCLowerHours() == null) {
            relatedProjectTechnician.setCLowerHours(BigDecimal.ZERO);
        }
        if (relatedProjectTechnician.getCHigherHours() == null) {
            relatedProjectTechnician.setCHigherHours(BigDecimal.ZERO);
        }


        if (monthDataListVO.getActualHours().compareTo(relatedProjectTechnician.getCLowerHours()) < 0) {
            BigDecimal temp = relatedProjectTechnician.getCReductPrice().multiply(relatedProjectTechnician.getCLowerHours().subtract(monthDataListVO.getActualHours()));
            newActualPrice = monthDataListVO.getExpectedPrice().subtract(temp);
            return newActualPrice;
        }
        //超过上限时间：実績単価＝見込単価+増単金*（実時間-精算幅（上限））
        else if (monthDataListVO.getActualHours().compareTo(relatedProjectTechnician.getCHigherHours()) > 0) {
            BigDecimal temp = relatedProjectTechnician.getCIncreasePrice().multiply(monthDataListVO.getActualHours().subtract(relatedProjectTechnician.getCHigherHours()));
            newActualPrice = monthDataListVO.getExpectedPrice().add(temp);
            return newActualPrice;
        }
        //正常，保持想定单价相同
        else {
            return relatedProjectTechnician.getCPrice();
        }
    }


}
