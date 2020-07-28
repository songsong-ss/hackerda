package com.hackerda.platform.controller.api;


import com.hackerda.platform.pojo.WebResponse;
import com.hackerda.platform.pojo.constant.ErrorCode;
import com.hackerda.platform.pojo.vo.CourseTimetableOverviewVO;
import com.hackerda.platform.pojo.vo.GradeResultVo;
import com.hackerda.platform.service.CourseTimeTableService;
import com.hackerda.platform.service.GradeService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
public class SchoolCommonController {

    @Resource
    private CourseTimeTableService courseTimeTableService;
    @Autowired
    private GradeService gradeService;

    @RequiresAuthentication
    @RequestMapping(value = "/grade")
    public WebResponse getNowGradeV2() {

        GradeResultVo grade = gradeService.getGrade();

        if (grade.getErrorCode() == ErrorCode.ACCOUNT_OR_PASSWORD_INVALID.getErrorCode()){
            return WebResponse.fail(ErrorCode.ACCOUNT_OR_PASSWORD_INVALID.getErrorCode(), "账号或密码错误");
        }

        return WebResponse.success(grade);
    }

    @RequiresAuthentication
    @RequestMapping(value = "/timetable")
    public WebResponse getTimeTableV2() {

        CourseTimetableOverviewVO vo = courseTimeTableService.getCurrentTermCourseTimeTableByStudent();
        if (vo.getErrorCode() == ErrorCode.ACCOUNT_OR_PASSWORD_INVALID.getErrorCode()){
            return WebResponse.fail(ErrorCode.ACCOUNT_OR_PASSWORD_INVALID.getErrorCode(), "账号或密码错误");
        }
        return WebResponse.success(vo.getCourseTimetableVOList());
    }

    @RequiresAuthentication
    @RequestMapping(value = "/unbind")
    public WebResponse appUnbind(@RequestParam(value = "appId") String appId,
                                 @RequestParam(value = "code") String code) {

        String account = SecurityUtils.getSubject().getPrincipal().toString();
        CourseTimetableOverviewVO vo = courseTimeTableService.getCurrentTermCourseTimeTableByStudent(Integer.parseInt(account));
        if (vo.getErrorCode() == ErrorCode.ACCOUNT_OR_PASSWORD_INVALID.getErrorCode()){
            return WebResponse.fail(ErrorCode.ACCOUNT_OR_PASSWORD_INVALID.getErrorCode(), "账号或密码错误");
        }
        return WebResponse.success(vo.getCourseTimetableVOList());
    }
}
