package com.sogou.upd.passport.web.internal.account;

import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.api.account.BindApiManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Web登录的内部接口
 * User: shipengzhi
 * Date: 13-6-6
 * Time: 下午2:40
 */
@Controller
@RequestMapping("/internal")
public class BindMobileApiController {

    @Autowired
    private ConfigureManager configureManager;

    @Autowired
    private BindApiManager proxyBindApiManager;

}
