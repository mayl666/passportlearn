package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 下午9:51
 * To change this template use File | Settings | File Templates.
 */
public interface SnamePassportMappingService {

    /**
     * 根据 snameOrPhone来查询passportId
     * @param snameOrPhone
     * @return
     * @throws ServiceException
     */
    public String queryPassportIdBySnameOrPhone(String snameOrPhone) throws ServiceException;
    /**
     * 根据sname获取passportId
     *
     * @param sname
     * @return 获取不到则抛出异常
     * @throws com.sogou.upd.passport.exception.ServiceException
     *
     */
    public String queryPassportIdBySname(String sname) throws ServiceException;

    /**
     * 根据mobile获取passportId
     *
     * @param mobile
     * @return
     * @throws ServiceException
     */
    public String queryPassportIdByMobile(String mobile) throws ServiceException;

    /**
     * 更新sname和passportId的映射关系
     *
     * @param sname
     * @param passportId
     * @return
     * @throws com.sogou.upd.passport.exception.ServiceException
     *
     */
    public boolean updateSnamePassportMapping(String sname, String passportId) throws ServiceException;

    /**
     * 删除sname和passportId的映射关系
     *
     * @param sname
     * @return
     * @throws com.sogou.upd.passport.exception.ServiceException
     *
     */
    public boolean deleteSnamePassportMapping(String sname) throws ServiceException;

    /**
     *插入一条映射关系
     * @param sid
     * @param sname
     * @param passportId
     * @param mobile
     * @return
     * @throws ServiceException
     */
    public boolean insertSnamePassportMapping(String sid,String sname, String passportId,String mobile) throws ServiceException;
}
