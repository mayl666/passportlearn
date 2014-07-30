package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.account.WebRoamDO;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-8-21
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
public interface TokenService {

    /**
     * 存储waptoken，有效期为5分钟
     * key:根据passportId算出的MD5值；value:passportId
     *
     * @param passportId
     * @throws ServiceException
     */
    public String saveWapToken(String passportId) throws ServiceException;

    /**
     * 存储用于漫游的token值，有效期为5分钟
     * key:根据passportId算出的MD5值；value:“v:xxxx|passportId:xxxx|status:xxxx（登录状态）|ct:xxxx(请求时间)”
     *
     * @param passportId
     * @throws ServiceException
     */
    public String saveWebRoamToken(String passportId) throws ServiceException;

    /**
     * 通过waptoken获取passportId
     *
     * @param token
     * @return
     * @throws ServiceException
     */
    public String getPassprotIdByWapToken(String token) throws ServiceException;

    /**
     * 通过waptoken获取passportId
     *
     * @param token
     * @return
     * @throws ServiceException
     */
    public WebRoamDO getWebRoamDOByToken(String token) throws ServiceException;
}
