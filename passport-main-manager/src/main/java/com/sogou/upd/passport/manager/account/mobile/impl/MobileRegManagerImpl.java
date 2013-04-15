package com.sogou.upd.passport.manager.account.mobile.impl;

import com.sogou.upd.passport.manager.account.mobile.MobileRegManager;
import com.sogou.upd.passport.service.account.AccountAuthService;
import com.sogou.upd.passport.service.account.AccountService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * User: mayan
 * Date: 13-4-15
 * Time: 上午11:50
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MobileRegManagerImpl implements MobileRegManager {

    @Inject
    private AccountService accountService;

    @Inject
    private AccountAuthService accountAuthService;
}
