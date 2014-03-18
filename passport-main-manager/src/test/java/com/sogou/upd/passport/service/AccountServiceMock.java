package com.sogou.upd.passport.service;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-3-17
 * Time: 下午5:13
 * To change this template use File | Settings | File Templates.
 */
public class AccountServiceMock implements AccountService {


    @Override
    public Account initialWebAccount(String username, String ip) throws ServiceException {
        Account returnAccount=new Account();
        returnAccount.setId(12121);

        return returnAccount;  //To change body of implemented methods use File | Settings | File Templates.

    }

    @Override
    public Account initialAccount(String username, String password, boolean needMD5, String ip, int provider) throws ServiceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Account initialConnectAccount(String passportId, String ip, int provider) throws ServiceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Account queryAccountByPassportId(String passportId) throws ServiceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean checkLimitResetPwd(String passportId) throws ServiceException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Account queryNormalAccount(String passportId) throws ServiceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result verifyUserPwdVaild(String passportId, String password, boolean needMD5) throws ServiceException {
//        if(passportId== pass=
//                )

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean deleteAccountByPassportId(String passportId) throws ServiceException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean resetPassword(Account account, String password, boolean needMD5) throws ServiceException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isInAccountBlackListByIp(String passportId, String ip) throws ServiceException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean sendActiveEmail(String username, String passpord, int clientId, String ip, String ru) throws Exception {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean activeEmail(String username, String token, int clientId) throws Exception {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean setCookie() throws Exception {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> getCaptchaCode(String code) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean checkCaptchaCodeIsVaild(String token, String captchaCode) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean modifyMobile(Account account, String newMobile) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean updateState(Account account, int newState) throws ServiceException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean checkCaptchaCode(String token, String captchaCode) throws Exception {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean updateUniqName(Account account, String nickname) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean updateAvatar(Account account, String avatar) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean removeUniqName(String nickname) throws ServiceException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String checkUniqName(String nickname) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
