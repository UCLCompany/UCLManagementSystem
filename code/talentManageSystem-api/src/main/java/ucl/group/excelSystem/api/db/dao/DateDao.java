package ucl.group.excelSystem.api.db.dao;

import org.apache.ibatis.annotations.Param;
import ucl.group.excelSystem.api.db.pojo.BasicDateEntity;

import java.util.List;

/**
 * @projectName: code
 * @package: ucl.group.excelSystem.api.db.dao
 * @className: DateDao
 * @author: he_jiale
 * @description: 作业管理表的日期数据
 * @date: 2024/07/16 11:15
 * @version: 1.0
 */
public interface DateDao {
    public List<BasicDateEntity> searchByMonthId(Long monthId);
    public int add(BasicDateEntity basicDateEntity);
    public BasicDateEntity searchById(Long dateId);
    public int removeByMonthId(Long monthId);

    //批量插入
    void addAll(@Param("basicDateEntities")List<BasicDateEntity> basicDateEntities);

    //批量删除
    void removeByMonthIds(@Param("longList") List<Long> longList);
}
