package com.sogou.upd.passport.web.internal.debug;

import com.sogou.upd.passport.common.HystrixConstant;
import com.sogou.upd.passport.common.hystrix.HystrixConfigFactory;
import com.sogou.upd.passport.web.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-6-5
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class SimulateQqDelay extends BaseController {
    private static final Logger logger= LoggerFactory.getLogger(SimulateQqDelay.class);

    @RequestMapping(value = "/user/get_qzoneupdates", method = RequestMethod.POST)
    @ResponseBody
    public String simulateGetQzone(HttpServletRequest request, HttpServletResponse response) {
        int delayProperty=Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_DELAY));

            try {
                Thread.sleep(delayProperty);
            } catch (InterruptedException e) {
                logger.error("simulate qq delay failed",e);
            }

           return "{\"ret\":0,\"msg\":\"ok\",\"friupdatecount\":0,\"relatecount\":0,\"visitorcount\":1}";


    }

    @RequestMapping(value = "/v3/user/get_qqclub_face", method = RequestMethod.POST)
    @ResponseBody
    public String simulateClubFace(HttpServletRequest request, HttpServletResponse response) {
        int delayProperty=Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_DELAY));

        try {
            Thread.sleep(delayProperty);
        } catch (InterruptedException e) {
            logger.error("simulate qq delay failed",e);
        }

        return "{\"crdata\":[],\"data\":[],\"is_lost\":0,\"msg\":\"操作成功\",\"openid\":\"4FB03E78F76727E05F1DA23B95561483\",\"ret\":0}";


    }

    @RequestMapping(value = "/hystrix/setdelay", method = RequestMethod.POST)
    @ResponseBody
    public String setDelay(HttpServletRequest request, HttpServletResponse response, String delay) {
        int delayProperty=Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_DELAY));
        logger.warn("before setDelay,delay is:"+delayProperty);

        HystrixConfigFactory.modifyProperty(HystrixConstant.PROPERTY_QQ_DELAY, delay);
        delayProperty=Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_DELAY));
        logger.warn("after setDelay,delay is:"+delayProperty);

      return "setDelay success";
    }

    @RequestMapping(value = "/oauth2.0/token", method = RequestMethod.POST)
    @ResponseBody
    public String simulateAuthToken(HttpServletRequest request, HttpServletResponse response) {
        int delayProperty=Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_DELAY));

        try {
            Thread.sleep(delayProperty);
        } catch (InterruptedException e) {
            logger.error("simulate qq delay failed",e);
        }

        return "{\"ret\":0,\"msg\":\"\",\"access_token\":\"77173AA3B1793AD01D62F8B210291190\",\"expires_in\":7776000,\"refresh_token\":\"B7A87A6A4B7464E6C83CB3274CAEAD2F\",\"openid\":\"E74BEC2F5729AB12495986504FA64826\",\"userinfo\":{\"nickname\":\"Dolphin\",\"gender\":\"female\",\"faceurl40\":\"http://q.qlogo.cn/qqapp/100294784/E74BEC2F5729AB12495986504FA64826/40\",\"faceurl100\":\"http://q.qlogo.cn/qqapp/100294784/E74BEC2F5729AB12495986504FA64826/100\"}}";

//        return "{\"msg\":\"invalid change the token password\",\"ret\":\"100045\"}}";


    }




}
