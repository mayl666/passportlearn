package com.sogou.upd.passport.web.account.action.wap;

import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.WapLoginManager;
import com.sogou.upd.passport.web.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-9-11
 * Time: 下午6:22
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class WapV2LoginAction extends BaseController {

    @Autowired
    private LoginManager loginManager;
    @Autowired
    private WapLoginManager wapLoginManager;
    @Autowired
    private AccountInfoManager accountInfoManager;

    private static final Logger logger = LoggerFactory.getLogger(WapV2LoginAction.class);
}
