package com.sogou.upd.passport.web.connect;

import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 该类为交换QQ的tKey  参见：http://svn.sogou-inc.com/svn/userplatform/updoc/passport/概要设计方案/2014Q2/搜狗输入法拉取QQ好友/输入法拉取好友列表-概要设计-passport.docx
 * Created by denghua on 14-5-12.
 */
@Controller
@RequestMapping("/connect/")
public class ConnectExchangeKey {

    @ResponseBody
    @RequestMapping(value = "/t_key")
    public String tKey(HttpServletRequest req, HttpServletResponse res, ConnectLoginParams connectLoginParams) throws IOException {
        

        return "empty";
    }
}
