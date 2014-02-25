package com.sogou.upd.passport.web.internal.connect.file;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.ConnectTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 根据sohu导出的文件从sogou库中读取记录，依次与sohu线上库对比，记录不同错误类型。
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-2-23
 * Time: 下午7:38
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class TestCheckSohuDataController {

    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private ConnectApiManager proxyConnectApiManager;
    @Autowired
    private ConnectAuthService connectAuthService;

    private static Logger logger = LoggerFactory.getLogger(TestCheckSohuDataController.class);

    private static ExecutorService service = Executors.newFixedThreadPool(300);

    /**
     * 验证sohu导出的数据与sohu线上是否一致
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/check")
    @ResponseBody
    public Object move()
            throws Exception {
        long time = System.currentTimeMillis();
        String fileRoot = "D:\\db\\";
//        String[] fileNames = {"open_token_1.txt", "open_token_2.txt", "open_token_3.txt", "open_token_4.txt", "open_token_5.txt", "open_token_6.txt", "open_token_7.txt", "open_token_8.txt", "open_token_9.txt", "open_token_10.txt"};
        String[] fileNames = {"open_token_1.txt", "open_token_2.txt"};

        int size = fileNames.length;

        CountDownLatch latch = new CountDownLatch(size);

        for (int i = 0; i < size; i++) {
            String fileName = fileRoot + fileNames[i];
            service.execute(new MoveCacheThread(latch, fileName, connectTokenService, proxyConnectApiManager));
        }
        latch.await();
        System.out.println("总执行时间：" + (System.currentTimeMillis() - time));
        return buildSuccess("", null);
    }

    /**
     * 线下方式调用第三方API补全第三方用户信息
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/add/connect/info")
    @ResponseBody
    public Object addConnectUserInfo(UserOpenApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //1.调用第三方API获取第三方用户信息
            int provider = AccountTypeEnum.getAccountType(params.getUserid()).getValue();
            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            String appKey = ConnectTypeEnum.getAppKey(provider);
            ConnectConfig connectConfig = new ConnectConfig();
            connectConfig.setAppKey(appKey);
            ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainConnectUserInfo(provider, connectConfig, params.getOpenid(), params.getAccessToken(), oAuthConsumer);
            if (connectUserInfoVO == null) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_GET_USERINFO_ERROR);
                return result;
            }
            connectAuthService.initialOrUpdateConnectUserInfo(params.getUserid(), connectUserInfoVO);
            //2.获取第三方头像、昵称信息
            ConnectToken connectToken = new ConnectToken();
            connectToken.setConnectUniqname(StringUtil.strToUTF8(connectUserInfoVO.getNickname())); //第三方用户昵称
            connectToken.setAvatarSmall(connectUserInfoVO.getAvatarSmall());  //第三方用户小头像
            connectToken.setAvatarMiddle(connectUserInfoVO.getAvatarMiddle()); //第三方用户中头像
            connectToken.setAvatarLarge(connectUserInfoVO.getAvatarLarge());  //第三方用户大头像
            connectToken.setPassportId(params.getUserid());
            connectToken.setGender(String.valueOf(connectUserInfoVO.getGender()));//第三方用户性别
            connectToken.setUpdateTime(params.getUpdateTime()); //connectToken表的更新时间还保持原来表中的时间
            boolean isUpdateSuccess = connectTokenService.insertOrUpdateConnectToken(connectToken);
            if (!isUpdateSuccess) {
                //todo 记录出错的passportId
            }

        } catch (Exception e) {
            logger.error("getUserInfo:Get User For Internal Is Failed,Userid is " + params.getOpenid(), e);
        }

        return result.toString();
    }

    private Map<String, Object> buildSuccess(String msg, Map<String, Object> data) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("0", "完成");
        return retMap;
    }

}
