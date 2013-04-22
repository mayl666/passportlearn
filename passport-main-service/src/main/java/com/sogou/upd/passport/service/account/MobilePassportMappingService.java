package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.exception.ServiceException;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 下午9:51
 * To change this template use File | Settings | File Templates.
 */
public interface MobilePassportMappingService {

    /**
     * 根据手机号码获取passportId
     *
     * @param mobile
     * @return 获取不到则抛出异常
     * @throws ServiceException
     */
    public String queryPassportIdByMobile(String mobile) throws ServiceException;

    /**
     * 根据username获取passport，手机号则查映射表，邮箱账号直接返回
     * @param username
     * @return
     * @throws ServiceException
     */
    public String queryPassportIdByUsername(String username) throws ServiceException;

    /**
     * 插入一条mobile和passportId的映射关系
     *
     * @param mobile
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public boolean initialMobilePassportMapping(String mobile, String passportId) throws ServiceException;

    /**
     * 更新mobile和passportId的映射关系
     *
     * @param mobile
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public boolean updateMobilePassportMapping(String mobile, String passportId) throws ServiceException;

    /**
     * 删除mobile和passportId的映射关系
     *
     * @param mobile
     * @return
     * @throws ServiceException
     */
    public boolean deleteMobilePassportMapping(String mobile) throws ServiceException;
}
