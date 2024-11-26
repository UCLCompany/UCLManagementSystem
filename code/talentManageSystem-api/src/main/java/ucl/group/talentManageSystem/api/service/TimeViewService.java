package ucl.group.talentManageSystem.api.service;

import org.springframework.stereotype.Service;
import ucl.group.talentManageSystem.api.controller.form.TimeViewForm;
import ucl.group.talentManageSystem.api.db.pojo.TimeViewEntity;
import ucl.group.talentManageSystem.api.db.pojo.bo.MonthAndInterviewer;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface TimeViewService {

    Integer countInterviewedNumByDate(String starDate,String  endDate) ;
    Integer countHiredNumByDate(String starDate,String  endDate) ;

    //根据日期获取每个面试官信息
    List<MonthAndInterviewer> getDateRanges(String start, String end) throws ParseException;
}
