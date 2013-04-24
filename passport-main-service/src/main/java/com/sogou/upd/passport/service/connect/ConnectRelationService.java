package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.common.exception.ServiceException;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.model.connect.ConnectToken;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午11:36
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectRelationService {

    /**
     * 获取指定的单条AccountConnect对象
     * 因为缓存的原因，它和listConnectRelation()使用的是一个DAO方法
     *
     * @return
     */
    public ConnectRelation querySpecifyConnectRelation(String openid, int provider, String appKey) throws ServiceException;

    /**
     * 根据query查询AccountConnect对象
     * key:appKey  value:ConnectRelation
     *
     * @return
     */
    public Map<String, ConnectRelation> queryAppKeyMapping(String openid, int provider) throws ServiceException;

    /**
     * 初始化connect_relation，openid反查表
     *
     * @param connectRelation
     * @return
     */
    public boolean initialConnectRelation(ConnectRelation connectRelation) throws ServiceException;
}
