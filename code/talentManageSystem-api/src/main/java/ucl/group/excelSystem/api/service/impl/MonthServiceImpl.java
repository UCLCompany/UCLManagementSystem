package ucl.group.excelSystem.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import ucl.group.excelSystem.api.common.constants.ExcelSystemConstants;
import ucl.group.excelSystem.api.common.utils.TableUtils;
import ucl.group.excelSystem.api.controller.form.SaveProjectTechnicianColumn;
import ucl.group.excelSystem.api.controller.form.SaveProjectTechnicianListForm;
import ucl.group.excelSystem.api.controller.form.SaveProjectTechnicianRow;
import ucl.group.excelSystem.api.db.dao.MonthDao;
import ucl.group.excelSystem.api.db.dao.RelatedProjectTechnicianDao;
import ucl.group.excelSystem.api.db.pojo.BasicDateEntity;
import ucl.group.excelSystem.api.db.pojo.BasicMonthEntity;
import ucl.group.excelSystem.api.db.pojo.RelatedProjectTechnician;
import ucl.group.excelSystem.api.db.pojo.vo.MonthDataListVO;
import ucl.group.excelSystem.api.db.pojo.vo.ProjectTechnicianColumn;
import ucl.group.excelSystem.api.db.pojo.vo.TechnicianListStatsVO;
import ucl.group.excelSystem.api.db.pojo.vo.TechnicianListVO;
import ucl.group.excelSystem.api.service.MonthService;
import ucl.group.excelSystem.api.service.TableService;
import ucl.group.talentManageSystem.api.common.utils.DateUtils;
import ucl.group.talentManageSystem.api.common.utils.SecurityUtils;
import ucl.group.talentManageSystem.api.exception.ServiceException;

import javax.sound.sampled.Port;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @projectName: code
 * @package: ucl.group.excelSystem.api.service.impl
 * @className: MonthServiceImpl
 * @author: he_jiale
 * @description: TODO
 * @date: 2024/07/10 16:09
 * @version: 1.0
 */
@Service
public class MonthServiceImpl implements MonthService {
    @Autowired
    private MonthDao monthDao;
    @Autowired
    private RelatedProjectTechnicianDao relatedProjectTechnicianDao;

    @Override
    @Transactional
    public List<MonthDataListVO> getMonthDataList(Long projectTechnicianId, LocalDate dateStart, LocalDate dateEnd) {
        List<String> yearMonthDaysBetween = TableUtils.getYearMonthDaysBetween(dateStart, dateEnd);
        if (yearMonthDaysBetween.size() == 12) {
            //dateStart and dateEnd is incorrect,they should six of months
            throw new ServiceException("開始日と終了日が正しくありません。6か月であるべきです。");
        }
        List<BasicMonthEntity> basicMonthEntities = monthDao.searchByProjectTechIdAndYearMonths(projectTechnicianId, yearMonthDaysBetween);
        List<MonthDataListVO> monthDataListVOS = new ArrayList<>();
        if (basicMonthEntities.size() == 6) {
            for (BasicMonthEntity tempB : basicMonthEntities) {
                MonthDataListVO monthDataListVO = new MonthDataListVO();
                monthDataListVO.setMonthId(tempB.getMonthId());
                monthDataListVO.setPresumedTime(Optional.ofNullable(tempB.getPresumedTime()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setExpectedPrice(Optional.ofNullable(tempB.getExpectedPrice()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setActualHours(Optional.ofNullable(tempB.getActualHours()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setActualPrice(Optional.ofNullable(tempB.getActualPrice()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setActualPriceEdit(tempB.getActualPriceEdit());
                monthDataListVO.setFrom(Optional.ofNullable(tempB.getFrom()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setFromEdit(tempB.getFromEdit());
                monthDataListVO.setSubcontractPrice(Optional.ofNullable(tempB.getSubcontractPrice()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setYearMonth(tempB.getYearMonth());
                monthDataListVO.setColorFlag(0);
                monthDataListVOS.add(monthDataListVO);
            }

        } else if (basicMonthEntities.isEmpty()) {
            //存在新的任用者，或者因为价格变动产生一条新的任用信息，插入数据库
            addMonthDataList(projectTechnicianId, yearMonthDaysBetween, dateStart, dateEnd);
            basicMonthEntities = monthDao.searchByProjectTechIdAndYearMonths(projectTechnicianId, yearMonthDaysBetween);
            for (BasicMonthEntity tempB : basicMonthEntities) {
                MonthDataListVO monthDataListVO = new MonthDataListVO();
                monthDataListVO.setMonthId(tempB.getMonthId());
                monthDataListVO.setPresumedTime(Optional.ofNullable(tempB.getPresumedTime()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setExpectedPrice(Optional.ofNullable(tempB.getExpectedPrice()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setActualHours(Optional.ofNullable(tempB.getActualHours()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setActualPrice(Optional.ofNullable(tempB.getActualPrice()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setActualPriceEdit(Optional.ofNullable(tempB.getActualPriceEdit()).orElse(Boolean.FALSE));
                monthDataListVO.setFrom(Optional.ofNullable(tempB.getFrom()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setFromEdit(Optional.ofNullable(tempB.getFromEdit()).orElse(Boolean.FALSE));
                monthDataListVO.setSubcontractPrice(Optional.ofNullable(tempB.getSubcontractPrice()).orElse(BigDecimal.valueOf(0)));
                monthDataListVO.setYearMonth(tempB.getYearMonth());
                monthDataListVOS.add(monthDataListVO);
            }

        } else {
            //说明数据不是一期，数据库有误！
            throw new ServiceException("Failed to read six of monthData for one people,dataBase is incorrect");
        }
        return monthDataListVOS;
    }

    @Override
    @Transactional
    public void addMonthData(List<BasicMonthEntity> basicMonthEntities) {
        for (BasicMonthEntity basicMonthEntity : basicMonthEntities) {
            try {
                monthDao.add(basicMonthEntity);
            } catch (Exception e) {
                throw new ServiceException("フォームに月を追加できませんでした。" + basicMonthEntity, e);
            }
        }
    }

    @Override
    @Transactional
    public List<ProjectTechnicianColumn> getColumnData(LocalDate dateStart, LocalDate dateEnd) {
        //刷新人数，代替之前的redis逻辑
        flushTotalNumber(dateStart, dateEnd);
        List<String> yearMonthDaysBetween = TableUtils.getYearMonthDaysBetween(dateStart, dateEnd);
        List<ProjectTechnicianColumn> projectTechnicianColumns = new ArrayList<>();
        for (String str : yearMonthDaysBetween) {
            List<BasicMonthEntity> basicMonthEntities = monthDao.searchByYearMonth(DateUtils.toLocalDateFirstDay(str));
            ProjectTechnicianColumn projectTechnicianColumn = new ProjectTechnicianColumn();
            projectTechnicianColumn.setTotalNumber(basicMonthEntities.get(0).getTotalNumber());
            projectTechnicianColumn.setMonthDays(basicMonthEntities.get(0).getMonthDays());
            projectTechnicianColumn.setMonthNum(TableUtils.judgeMonthNum(basicMonthEntities.get(0).getYearMonth()));
            projectTechnicianColumns.add(projectTechnicianColumn);

        }
        return projectTechnicianColumns;
    }

    /**
     * @param projectTechnicianId:
     * @param yearMonthDays:
     * @param dateStart:
     * @param dateEnd:
     * @return void
     * @author he_jiale
     * @description 新增某技术者的6个月的月份价格信息，用于全体项目管理表，注意价格变动月之前和之后的信息
     * @date 2024/07/11 11:12
     */
    @Transactional
    public void addMonthDataList(Long projectTechnicianId, List<String> yearMonthDays, LocalDate dateStart, LocalDate dateEnd) {
        for (String tempYearMonth : yearMonthDays) {
            BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
            RelatedProjectTechnician relatedProjectTechnician = relatedProjectTechnicianDao.searchByProjectTechId(projectTechnicianId);
            Integer monthDays = monthDao.searchMonthDays(DateUtils.toLocalDateFirstDay(tempYearMonth));
            // 如果monthDao.searchMonthDays返回null，则使用默认值0
            if (monthDays == null) {
                monthDays = 0;
            }
            basicMonthEntity.setMonthDays(monthDays);
            basicMonthEntity.setProjectTechnicianId(relatedProjectTechnician.getProjectTechnicianId());
            //想定時＝1日標準稼働時間*稼働日であること
            BigDecimal standardHours = relatedProjectTechnician.getStandardHours();
            if (standardHours != null) {
                basicMonthEntity.setPresumedTime(standardHours.multiply(BigDecimal.valueOf(monthDays)));
            } else {
                basicMonthEntity.setPresumedTime(BigDecimal.valueOf(0));
            }
            //预计单价
            basicMonthEntity.setExpectedPrice(relatedProjectTechnician.getCPrice());
            //实时间
            basicMonthEntity.setActualHours(BigDecimal.valueOf(0));
            //修改实际价格
            MonthDataListVO monthDataListVO = new MonthDataListVO();
            monthDataListVO.setActualHours(BigDecimal.ZERO);
            monthDataListVO.setExpectedPrice(basicMonthEntity.getExpectedPrice());
            BigDecimal actNumber = getNewActualPrice(monthDataListVO, basicMonthEntity.getProjectTechnicianId());
            BigDecimal roundedNumber = actNumber.setScale(2, RoundingMode.HALF_UP);
            basicMonthEntity.setActualPrice(roundedNumber);
            //修改乖离
            BigDecimal fromNumber = basicMonthEntity.getActualPrice().subtract(basicMonthEntity.getExpectedPrice());
            BigDecimal roundedNumber2 = fromNumber.setScale(2, RoundingMode.HALF_UP);
            basicMonthEntity.setFrom(roundedNumber2);
            //修改下请单价
            basicMonthEntity.setSubcontractPrice(relatedProjectTechnician.getHPrice());
            basicMonthEntity.setYearMonth(DateUtils.toLocalDateFirstDay(tempYearMonth));
            basicMonthEntity.setDelFlag(ExcelSystemConstants.DEL_NO);
            basicMonthEntity.setCreateBy(SecurityUtils.getUsername());
            monthDao.add(basicMonthEntity);
        }
    }

    @Override
    @Transactional
    public void saveMonthData(SaveProjectTechnicianListForm form) {
        List<BasicMonthEntity> basicMonthEntities = new ArrayList<>();
        if (!form.getRow().isEmpty()) {
            for (SaveProjectTechnicianRow row : form.getRow()) {
                List<MonthDataListVO> monthDataList = row.getMonthDataList();
                for (MonthDataListVO monthDataListVO : monthDataList) {
                    BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
                    basicMonthEntity.setMonthId(monthDataListVO.getMonthId());
                    basicMonthEntity.setPresumedTime(monthDataListVO.getPresumedTime());
                    basicMonthEntity.setExpectedPrice(monthDataListVO.getExpectedPrice());
                    basicMonthEntity.setActualHours(monthDataListVO.getActualHours());
                    basicMonthEntity.setActualPrice(monthDataListVO.getActualPrice());
                    basicMonthEntity.setFrom(monthDataListVO.getFrom());
                    basicMonthEntity.setSubcontractPrice(monthDataListVO.getSubcontractPrice());
                    basicMonthEntity.setUpdateBy(SecurityUtils.getUsername());
                    basicMonthEntities.add(basicMonthEntity);
                }
            }
            for (BasicMonthEntity basicMonthEntity : basicMonthEntities) {
                try {
                    monthDao.save(basicMonthEntity);
                } catch (Exception e) {
                    throw new ServiceException("フォームの basicMonthEntity を保存できませんでした。" + e);
                }
            }
        }
        if (!form.getColumn().isEmpty()) {
            //处理列数据
            for (SaveProjectTechnicianColumn column : form.getColumn()) {
                //修改所有yearMonth等于这个值的
                //更新所有这个月的数据
                try {
                    BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
                    //basicMonthEntity.setTotalNumber(column.getTotalNumber());
                    basicMonthEntity.setMonthDays(column.getMonthDays());
                    basicMonthEntity.setYearMonth(DateUtils.toLocalDateFirstDay(column.getYearMonth()));
                    basicMonthEntity.setUpdateBy(SecurityUtils.getUsername());
                    monthDao.editTotalAndDays(basicMonthEntity);
                } catch (Exception e) {
                    //Failed to update monthDays and totalNumber basicMonthEntity;
                    throw new ServiceException("basicMonthEntityの月日数と合計数を更新できませんでした。" + e);
                }
            }
        }

    }

    @Override
    @Transactional
    public void saveAllItems(SaveProjectTechnicianListForm form) {
        List<BasicMonthEntity> basicMonthEntities = new ArrayList<>();
        if (!form.getColumn().isEmpty()) {
            //处理列数据
            for (SaveProjectTechnicianColumn column : form.getColumn()) {
                //修改所有yearMonth等于这个值的
                //更新所有这个月的数据
                try {
                    BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
                    //修改每个月的工作人数这个功能
                    basicMonthEntity.setTotalNumber(column.getTotalNumber());
                    basicMonthEntity.setMonthDays(column.getMonthDays());
                    basicMonthEntity.setYearMonth(DateUtils.toLocalDateFirstDay(column.getYearMonth()));
                    basicMonthEntity.setUpdateBy(SecurityUtils.getUsername());
                    monthDao.editTotalAndDays(basicMonthEntity);
                } catch (Exception e) {
                    //Failed to update all basicMonthEntity;
                    throw new ServiceException("basicMonthEntityを更新できませんでした。" + e);
                }
            }
        }
        if (!form.getRow().isEmpty()) {
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
        }

        //存入数据库中
        for (BasicMonthEntity basicMonthEntity : basicMonthEntities) {
            try {
                monthDao.save(basicMonthEntity);
            } catch (Exception e) {
                throw new ServiceException("フォームの basicMonthEntity を保存できませんでした。" + e);
            }
        }
    }

    @Override
    @Transactional
    public void saveMonthDays(SaveProjectTechnicianListForm form) {
        List<BasicMonthEntity> basicMonthEntities = new ArrayList<>();
        if (!form.getColumn().isEmpty()) {
            //处理列数据
            for (SaveProjectTechnicianColumn column : form.getColumn()) {
                //修改所有yearMonth等于这个值的
                //更新所有这个月的数据
                try {
                    BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
                    //修改每个月的工作人数这个功能
                    basicMonthEntity.setTotalNumber(column.getTotalNumber());
                    basicMonthEntity.setMonthDays(column.getMonthDays());
                    basicMonthEntity.setYearMonth(DateUtils.toLocalDateFirstDay(column.getYearMonth()));
                    basicMonthEntity.setUpdateBy(SecurityUtils.getUsername());
                    monthDao.editTotalAndDays(basicMonthEntity);
                } catch (Exception e) {
                    //Failed to update monthDays and totalNumber basicMonthEntity;
                    throw new ServiceException("basicMonthEntityの月日数と合計数を更新できませんでした。" + e);
                }
            }
        }
        if (!form.getRow().isEmpty()) {
            for (SaveProjectTechnicianRow row : form.getRow()) {
                List<MonthDataListVO> monthDataList = row.getMonthDataList();
                int i = 0;
                for (MonthDataListVO monthDataListVO : monthDataList) {
                    BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
                    basicMonthEntity.setMonthId(monthDataListVO.getMonthId());
                    BigDecimal standardHours = Optional.ofNullable(relatedProjectTechnicianDao.searchByProjectTechId(row.getProjectTechnicianId()).getStandardHours()).orElse(BigDecimal.ZERO);
                    BigDecimal newPresumedTime = standardHours.multiply(BigDecimal.valueOf(form.getColumn().get(i).getMonthDays()));
                    basicMonthEntity.setPresumedTime(newPresumedTime);
                    basicMonthEntity.setUpdateBy(SecurityUtils.getUsername());
                    basicMonthEntities.add(basicMonthEntity);
                    i++;
                }
            }
            for (BasicMonthEntity basicMonthEntity : basicMonthEntities) {
                try {
                    monthDao.savePresumedTime(basicMonthEntity);
                } catch (Exception e) {
                    throw new ServiceException("フォームの basicMonthEntity を保存できませんでした。" + e);
                }
            }

        }
    }

    @Override
    @Transactional
    public void saveExpectedPrice(SaveProjectTechnicianListForm form) {
        List<BasicMonthEntity> basicMonthEntities = new ArrayList<>();
        if (!form.getRow().isEmpty()) {
            for (SaveProjectTechnicianRow row : form.getRow()) {
                List<MonthDataListVO> monthDataList = row.getMonthDataList();
                int i = 0;
                for (MonthDataListVO monthDataListVO : monthDataList) {
                    BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
                    basicMonthEntity.setMonthId(monthDataListVO.getMonthId());
                    basicMonthEntity.setExpectedPrice(monthDataListVO.getExpectedPrice());
                    basicMonthEntity.setUpdateBy(SecurityUtils.getUsername());
                    basicMonthEntities.add(basicMonthEntity);
                    i++;
                }
            }
            for (BasicMonthEntity basicMonthEntity : basicMonthEntities) {
                try {
                    monthDao.saveExpectedPrice(basicMonthEntity);
                } catch (Exception e) {
                    throw new ServiceException("フォームの basicMonthEntity を保存できませんでした。" + e);
                }
            }
        }
    }

    @Override
    @Transactional
    public void saveActualHours(SaveProjectTechnicianListForm form) {
        List<BasicMonthEntity> basicMonthEntities = new ArrayList<>();
        if (!form.getRow().isEmpty()) {
            for (SaveProjectTechnicianRow row : form.getRow()) {
                List<MonthDataListVO> monthDataList = row.getMonthDataList();
                int i = 0;
                for (MonthDataListVO monthDataListVO : monthDataList) {
                    BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
                    basicMonthEntity.setMonthId(monthDataListVO.getMonthId());
                    basicMonthEntity.setExpectedPrice(monthDataListVO.getExpectedPrice());
                    basicMonthEntity.setActualHours(monthDataListVO.getActualHours());
                    basicMonthEntity.setActualPriceEdit(monthDataListVO.getActualPriceEdit());
                    basicMonthEntity.setFromEdit(monthDataListVO.getFromEdit());
                    if (monthDataListVO.getActualPriceEdit()) {
                        //以人工修改的为准
                        basicMonthEntity.setActualPrice(monthDataListVO.getActualPrice());
                    } else {
                        //自动计算
                        BigDecimal actNumber = getNewActualPrice(monthDataListVO, row.getProjectTechnicianId());
                        BigDecimal roundedNumber = actNumber.setScale(2, RoundingMode.HALF_UP);
                        basicMonthEntity.setActualPrice(roundedNumber);

                    }
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
                    monthDao.saveActualHours(basicMonthEntity);
                } catch (Exception e) {
                    throw new ServiceException("フォームの basicMonthEntity を保存できませんでした。" + e);
                }
            }
        }
    }

    @Override
    @Transactional
    public void saveFrom(SaveProjectTechnicianListForm form) {
        List<BasicMonthEntity> basicMonthEntities = new ArrayList<>();
        if (!form.getRow().isEmpty()) {
            for (SaveProjectTechnicianRow row : form.getRow()) {
                List<MonthDataListVO> monthDataList = row.getMonthDataList();
                int i = 0;
                for (MonthDataListVO monthDataListVO : monthDataList) {
                    BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
                    basicMonthEntity.setMonthId(monthDataListVO.getMonthId());
                    basicMonthEntity.setExpectedPrice(monthDataListVO.getExpectedPrice());
                    basicMonthEntity.setActualPrice(monthDataListVO.getActualPrice());
                    basicMonthEntity.setActualPriceEdit(monthDataListVO.getActualPriceEdit());
                    basicMonthEntity.setFromEdit(monthDataListVO.getFromEdit());
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
                    monthDao.saveFrom(basicMonthEntity);
                } catch (Exception e) {
                    throw new ServiceException("フォームの basicMonthEntity を保存できませんでした。" + e);
                }
            }
        }
    }

    /**
     * 保存实际价格信息
     *
     * 该方法主要用于保存项目技术人员的实际价格数据它通过遍历表单中的每一行数据，
     * 对于每一行中的月度数据，创建相应的实体对象，并根据条件设置实体对象的属性值
     * 如果实际价格是人工编辑的，则直接使用编辑后的价格，否则需要计算得出
     * 同样，如果来源是人工编辑的，则直接使用编辑后的来源，否则需要计算得出
     * 最后，将这些实体对象保存到数据库中
     *
     * @param form 包含项目技术人员实际价格信息的表单
     */
    @Override
    @Transactional
    public void saveActualPrice(SaveProjectTechnicianListForm form) {
        List<BasicMonthEntity> basicMonthEntities = new ArrayList<>();
        if (!form.getRow().isEmpty()) {
            for (SaveProjectTechnicianRow row : form.getRow()) {
                List<MonthDataListVO> monthDataList = row.getMonthDataList();
                int i = 0;
                for (MonthDataListVO monthDataListVO : monthDataList) {
                    BasicMonthEntity basicMonthEntity = new BasicMonthEntity();
                    basicMonthEntity.setMonthId(monthDataListVO.getMonthId());
                    basicMonthEntity.setExpectedPrice(monthDataListVO.getExpectedPrice());
                    basicMonthEntity.setActualPrice(monthDataListVO.getActualPrice());
                    basicMonthEntity.setActualPriceEdit(monthDataListVO.getActualPriceEdit());
                    basicMonthEntity.setFromEdit(monthDataListVO.getFromEdit());
                    if (monthDataListVO.getActualPriceEdit()) {
                        //以人工修改的为准
                        basicMonthEntity.setActualPrice(monthDataListVO.getActualPrice());
                    } else {
                        //自动计算
                        BigDecimal actNumber = getNewActualPrice(monthDataListVO, row.getProjectTechnicianId());
                        BigDecimal roundedNumber = actNumber.setScale(2, RoundingMode.HALF_UP);
                        basicMonthEntity.setActualPrice(roundedNumber);
                    }
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
                for (BasicMonthEntity basicMonthEntity : basicMonthEntities) {
                    try {
                        monthDao.saveActualPrice(basicMonthEntity);
                    } catch (Exception e) {
                        throw new ServiceException("フォームの basicMonthEntity を保存できませんでした。" + e);
                    }
                }
            }
        }
    }

    /**
     * @Description: [monthDataListVO, projectTechnicianId] 自动计算获取实际价格
     * @Param: [monthDataListVO, projectTechnicianId]
     * @return: java.math.BigDecimal
     * @Author: he_jiale
     * @Date: 12:21 2024/08/20
     */
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


    /**
     * @param tempYearMonth:yyyy-MM
     * @param dateStart:
     * @param dateEnd:
     * @return int
     * @author he_jiale
     * @description 计算这个时间范围内的总工作人数, 用于全体项目管理表
     * @date 2024/07/11 11:15
     */
    @Override
    @Transactional
    public int getTotalNumByMonth(String tempYearMonth, LocalDate dateStart, LocalDate dateEnd) {
        List<RelatedProjectTechnician> relatedProjectTechnicians = relatedProjectTechnicianDao.searchBetweenStartAndEnd(String.valueOf(dateStart), String.valueOf(dateEnd));
        List<TechnicianListVO> technicianListVOS = TableUtils.changeStartAndEndMonth(relatedProjectTechnicians, dateStart, dateEnd);
        List<TechnicianListVO> techniciansInRanges = TableUtils.getTechniciansInRange(technicianListVOS, tempYearMonth);
        return techniciansInRanges.size();
    }

    @Override
    @Transactional
    public BasicMonthEntity searchByProjectTechIdAndYearMonth(Long projectTechnicianId, LocalDate localDate) {
        return monthDao.searchByProjectTechIdAndYearMonth(projectTechnicianId, localDate);
    }

    @Override
    @Transactional
    public List<BasicMonthEntity> searchByProjectTechId(Long projectTechnicianId) {
        return monthDao.searchByProjectTechId(projectTechnicianId);
    }

    @Override
    @Transactional
    public void edit(List<BasicMonthEntity> basicMonthEntities) {
        for (BasicMonthEntity basicMonthEntity : basicMonthEntities) {
            try {
                basicMonthEntity.setUpdateBy(SecurityUtils.getUsername());
                monthDao.save(basicMonthEntity);
            } catch (Exception e) {
                throw new ServiceException("フォームの月を保存できませんでした。" + basicMonthEntity, e);
            }
        }
    }

    @Override
    @Transactional
    public void flushTotalNumber(LocalDate dateStart, LocalDate dateEnd) {
        List<RelatedProjectTechnician> relatedProjectTechnicians = relatedProjectTechnicianDao.searchBetweenStartAndEnd(String.valueOf(dateStart), String.valueOf(dateEnd));
        List<TechnicianListVO> technicianListVOS = TableUtils.changeStartAndEndMonth(relatedProjectTechnicians, dateStart, dateEnd);
        //获取半年的月份范围
        List<String> yearMonths = TableUtils.getYearMonthsBetween(dateStart, dateEnd);
        List<TechnicianListStatsVO> technicianListStatsVOS = new ArrayList<>();
        for (String tempYearMonth : yearMonths) {
            //查询该月有哪些技术者工作
            List<TechnicianListVO> techniciansInRanges = TableUtils.getTechniciansInRange(technicianListVOS, tempYearMonth);
            TechnicianListStatsVO technicianListStatsVO = new TechnicianListStatsVO();
            int totalNumber = techniciansInRanges.size();
            monthDao.modifyTotalNumber(totalNumber, DateUtils.toLocalDateFirstDay(tempYearMonth));
        }
    }


}
