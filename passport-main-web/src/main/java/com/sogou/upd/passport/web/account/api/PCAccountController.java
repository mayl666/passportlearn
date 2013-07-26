package com.sogou.upd.passport.web.account.api;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-26
 * Time: 下午7:07
 * To change this template use File | Settings | File Templates.
 */
public class PCAccountController {

    @RequestMapping(value = "/act/getpairtoken")
    @ResponseBody
    public Object getPairToken(HttpServletRequest request) throws Exception {
        Result result = new APIResultSupport(false);


        // 检查client_id和client_secret是否有效


        return result.toString();
    }
}
