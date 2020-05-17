/**
 * Copyright 2020 bejson.com
 */
package com.hackerda.spider.support.scheme;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Auto-generated: 2020-01-09 1:8:0
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SchemeGradeItem {

    private Id id;
    private String classNo;
    private String entryStatusCode;
    private String scoreEntryModeCode;
    private String gradePointScore;
    private String standardScore;
    private String percentileRankScore;
    private int gradeScore;
    private String planNO;
    private String courseAttributeName;
    private String courseAttributeCode;
    private String payableMoney;
    private String examTypeCode;
    private String examTime;
    private String electiveTypeCode;
    private String studyModeCode;
    private int courseScore;
    private String operator;
    private String operatingTime;
    private String makeupExaminationTypeCode;
    private String notByReasonCode;
    private String notByReasonName;
    private String remark;
    private String cycle;
    private String courseName;
    private String englishCourseName;
    private String planName;
    private String planName2;
    private String academicYearCode;
    private String termTypeCode;
    private String termTypeName;
    private Integer termCode;
    private String termName;
    private String gradeName;
    @JSONField(name = "cj")
    private String score;
    private String xkcsxdm;
    private String xkcsxmc;
    private String tdkcm;
    private String cjlrfsdm;
    private String bm;
    private String credit;
    private String tscore;
    private String substituteCourseNo;

    public String getCredit() {
        if (StringUtils.startsWith(credit, ".")) {
            return "0" + credit;
        }
        return credit;
    }

    public String getTermYear() {
        // 这里是个补偿做法 由于有些课程没有该数据  所以存储当前的学期
        return this.getId().getExecutiveEducationPlanNumber().substring(0, 9);
    }

    public String getExamTypeName() {
        if(StringUtils.isEmpty(this.getExamTypeCode())){
            return "";
        }else if("01".equals(this.getExamTypeCode())){
            return "考试";
        }else if("02".equals(this.getExamTypeCode())){
            return "考查";
        }
        return "";
    }


}