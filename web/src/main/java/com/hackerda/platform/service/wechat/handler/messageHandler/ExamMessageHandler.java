package com.hackerda.platform.service.wechat.handler.messageHandler;

import com.hackerda.platform.builder.TextBuilder;
import com.hackerda.platform.pojo.StudentUser;
import com.hackerda.platform.service.NewUrpSpiderService;
import com.hackerda.platform.service.OpenIdService;
import com.hackerda.spider.support.UrpExamTime;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author JR Chan
 * @date 2018/6/12 15:16
 */
@Slf4j
@Component
public class ExamMessageHandler implements WxMpMessageHandler {

    @Resource
    private TextBuilder textBuilder;
    @Resource
    private OpenIdService openIdService;
    @Resource
    private NewUrpSpiderService newUrpSpiderService;

    private static final String DATA_NOT_FOUND = "还没有你的考试时间，可以过段时间再来查询";

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        String appId = wxMpService.getWxMpConfigStorage().getAppId();
        StudentUser student = openIdService.getStudentByOpenId(wxMpXmlMessage.getFromUser(), appId);
        List<UrpExamTime> examTime = newUrpSpiderService.getExamTime(student);

        return textBuilder.build(examListToText(examTime), wxMpXmlMessage, wxMpService);
    }


    private String examListToText(List<UrpExamTime> examTimeList) {

        if(CollectionUtils.isEmpty(examTimeList)){
            return DATA_NOT_FOUND;
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (UrpExamTime examTime : examTimeList) {
            stringBuffer.append(examTime.getCourseName()).append("\n");
            stringBuffer.append(examTime.getExamName()).append("\n");
            if(StringUtils.isEmpty(examTime.getDate())){
                stringBuffer.append("考试时间联系具体任课老师\n\n");
            }else {
                stringBuffer.append(examTime.getDate()).append(" ").append(examTime.getExamTime()).append("\n");
                stringBuffer.append("第").append(examTime.getWeekOfTerm()).append(" ").append(examTime.getWeek()).append("\n");
                stringBuffer.append(examTime.getLocation()).append("\n\n");
            }

        }

        return new String(stringBuffer);
    }

}
