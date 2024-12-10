package ucl.group.excelSystem.api.db.dao;

import org.apache.ibatis.annotations.Param;
import ucl.group.excelSystem.api.db.pojo.BasicMonthEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * @projectName: code
 * @package: ucl.group.excelSystem.api.db.dao
 * @className: MonthDao
 * @author: he_jiale
 * @description: TODO
 * @date: 2024/07/10 16:03
 * @version: 1.0
 */
public interface MonthDao {
    public List<BasicMonthEntity> searchByProjectTechIdAndYearMonths(@Param("projectTechnicianId") Long projectTechnicianId,@Param("yearMonths") List<String> yearMonths);
    public int add(BasicMonthEntity basicMonthEntity);
    public int save(BasicMonthEntity basicMonthEntity);
    public int savePresumedTime(BasicMonthEntity basicMonthEntity);
    public int saveExpectedPrice(BasicMonthEntity basicMonthEntity);
    public int saveActualHours(BasicMonthEntity basicMonthEntity);
    public int saveFrom(BasicMonthEntity basicMonthEntity);
    public int saveActualPrice(BasicMonthEntity basicMonthEntity);
    public List<BasicMonthEntity> searchByYearMonth(LocalDate yearMonth);
    public BasicMonthEntity searchById(Long monthId);
    public int editTotalAndDays(BasicMonthEntity basicMonthEntity);
    public BasicMonthEntity searchByProjectTechIdAndYearMonth(@Param("projectTechnicianId") Long projectTechnicianId,@Param("localDate") LocalDate localDate);
    public List<BasicMonthEntity> searchByProjectTechId(Long projectTechnianId);
    public Integer searchMonthDays(@Param("localDate") LocalDate localDate);
    public int remove(Long[] ids);
    public int modifyTotalNumber(@Param("totalNumber") int totalNumber,@Param("localDate") LocalDate localDate);

    //批量更新
    void saveAll(@Param("basicMonthEntities") List<BasicMonthEntity> basicMonthEntities);
}
