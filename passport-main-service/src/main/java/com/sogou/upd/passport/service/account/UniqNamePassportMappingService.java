package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.exception.ServiceException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-11-28
 * Time: 上午2:14
 * To change this template use File | Settings | File Templates.
 */
public interface UniqNamePassportMappingService {

    /*
     * 检查昵称是否存在
     */
    public String checkUniqName(String uniqname) throws ServiceException;

    /**
     * 检查昵称唯一性，并且插入昵称映射表
     * 如果昵称不唯一，则返回false，不插入表中
     * 反之返回true插入表中
     * @param passportId
     * @param uniqname
     * @return
     * @throws ServiceException
     */
    public boolean checkAndInsertUniqName(String passportId, String uniqname) throws ServiceException;

    /*
     * 插入昵称映射表
     */
    public boolean insertUniqName(String passportId, String uniqname) throws ServiceException;

    /*
     * 更新个人信息
     * 先删除原映射关系，然后插入新映射关系
     */
    public boolean updateUniqName(/*Account account,*/String passportId, String oldNickName, String nickname);

    /*
     * 删除昵称映射关系
     */
    public boolean removeUniqName(String uniqname) throws ServiceException;

}
