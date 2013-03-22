package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.service.account.AccountService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Inject
    private AccountMapper accountMapper;
}
