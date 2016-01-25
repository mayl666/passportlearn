package com.sogou.upd.passport.zk;

import com.google.common.base.Strings;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.netflix.curator.framework.recipes.cache.NodeCache;
import com.netflix.curator.framework.recipes.cache.NodeCacheListener;
import com.sogou.upd.passport.common.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 数据库datasource 切换监控
 * User: chengang
 * Date: 14-11-6
 * Time: 下午7:43
 */
//@Component
public class DBZkSwitchMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBZkSwitchMonitor.class);


    /**
     * data source jdbc zkNode
     */
    private NodeCache dataSourceNodeCache;

    /**
     * store jdbc zk path
     */
    private String dataSourceZkPath;

    /**
     * master 数据源
     */
    private ComboPooledDataSource masterDataSource;

    /**
     * slave 数据源
     */
    private ComboPooledDataSource slaveDataSource;


    /**
     * db monitor
     */
    private DBMonitor dbMonitor;

    public DBZkSwitchMonitor() {

    }


    public DBZkSwitchMonitor(DBMonitor dbMonitor, String dataSourceZkPath, ComboPooledDataSource masterDataSource, ComboPooledDataSource slaveDataSource) {
        this.dbMonitor = dbMonitor;
        this.dataSourceZkPath = dataSourceZkPath;
        this.masterDataSource = masterDataSource;
        this.slaveDataSource = slaveDataSource;
        dataSourceNodeCache = this.addListener(dataSourceZkPath, new DataSourceListenerImpl());
    }


    private NodeCache addListener(String path, NodeCacheListener nodeCacheListener) {
        NodeCache nodeCache = new NodeCache(dbMonitor.getCuratorFramework(), path, true);
        try {
            nodeCache.start();
            nodeCache.getListenable().addListener(nodeCacheListener);
        } catch (Exception e) {
            LOGGER.error("DBZkSwitchMonitor start error", e);
        }
        return nodeCache;
    }


    private class DataSourceListenerImpl implements NodeCacheListener {

        @Override
        public void nodeChanged() throws Exception {
            LOGGER.warn("data source node changed");
            //refresh
            refresh(dataSourceNodeCache, masterDataSource, slaveDataSource);
        }
    }

    /**
     * 动态刷新构建 data source
     *
     * @param nodeCache
     * @param masterDataSource
     * @param slaveDataSource
     */
    private void refresh(NodeCache nodeCache, ComboPooledDataSource masterDataSource, ComboPooledDataSource slaveDataSource) {
        try {
            if (nodeCache.getCurrentData() != null && nodeCache.getCurrentData().getData() != null) {
                String nodeData = new String(nodeCache.getCurrentData().getData());
                LOGGER.warn(" node current data :" + nodeData);

                if (Strings.isNullOrEmpty(nodeData) || !JsonUtil.mayBeJSONObject(nodeData)) {
                    return;
                }

                Map jsonMap = JsonUtil.jsonToBean(nodeData, Map.class);
                if (!jsonMap.containsKey(DataSourceConstant.masterJdbcUrl) || !jsonMap.containsKey(DataSourceConstant.slaveJdbcUrl)) {
                    return;
                }

                String masterJdbcUrl = (String) jsonMap.get(DataSourceConstant.masterJdbcUrl);
                String slaveJdbcUrl = (String) jsonMap.get(DataSourceConstant.slaveJdbcUrl);

                LOGGER.warn("refresh  before. masterJdbcUrl:{},master-acquireIncrement:{},master-checkoutTimeout:{},master-maxPoolSize:{}," +
                        "slaveJdbcUrl:{},slave-acquireIncrement:{},slave-checkoutTimeout:{},slave-maxPoolSize:{}",
                        new Object[]{masterDataSource.getJdbcUrl(), masterDataSource.getAcquireIncrement(), masterDataSource.getCheckoutTimeout(), masterDataSource.getMaxPoolSize(),
                                slaveDataSource.getJdbcUrl(), slaveDataSource.getAcquireIncrement(), slaveDataSource.getCheckoutTimeout(), slaveDataSource.getMaxPoolSize()
                        });

                if (!Strings.isNullOrEmpty(masterJdbcUrl)) {
                    if (masterJdbcUrl.equals(masterDataSource.getJdbcUrl())) {
                        LOGGER.warn("DBZkSwitchMonitor refresh. masterJdbcUrl no changed.");
                        return;
                    }
                } else {
                    LOGGER.warn("DBZkSwitchMonitor refresh. masterJdbcUrl is NULL.");
                    return;
                }

                if (!Strings.isNullOrEmpty(slaveJdbcUrl)) {
                    if (slaveJdbcUrl.equals(slaveDataSource.getJdbcUrl())) {
                        LOGGER.warn("DBZkSwitchMonitor refresh. slaveJdbcUrl no changed.");
                        return;
                    }
                } else {
                    LOGGER.warn("DBZkSwitchMonitor refresh. slaveJdbcUrl is NULL.");
                    return;
                }

                if (masterDataSource != null && slaveDataSource != null) {

                    //重新构建 masterDataSource jdbc url
                    masterDataSource.setJdbcUrl(StringUtils.trim(masterJdbcUrl));
                    //重新构建 slaveDataSource jdbc url
                    slaveDataSource.setJdbcUrl(StringUtils.trim(slaveJdbcUrl));

                    //reset masterDataSource poolManager
                    masterDataSource.resetPoolManager(true);

                    //reset slaveDataSource poolManager
                    slaveDataSource.resetPoolManager(true);

                }

                LOGGER.warn("refresh after. masterJdbcUrl:{},master-acquireIncrement:{},master-checkoutTimeout:{},master-maxPoolSize:{}," +
                        "slaveJdbcUrl:{},slave-acquireIncrement:{},slave-checkoutTimeout:{},slave-maxPoolSize:{}",
                        new Object[]{masterDataSource.getJdbcUrl(), masterDataSource.getAcquireIncrement(), masterDataSource.getCheckoutTimeout(), masterDataSource.getMaxPoolSize(),
                                slaveDataSource.getJdbcUrl(), slaveDataSource.getAcquireIncrement(), slaveDataSource.getCheckoutTimeout(), slaveDataSource.getMaxPoolSize()
                        });
            } else {
                LOGGER.warn("DBZkSwitchMonitor refresh node changed is NULL. Zk path: " + nodeCache.getCurrentData().getPath());
                return;
            }
        } catch (Exception e) {
            LOGGER.error("DBZkSwitchMonitor refresh error.", e);
        }

    }


    /**
     * spring销毁Monitor时，关闭对zookeeper的监听
     */
    public void destroy() {
        try {
            if (dataSourceNodeCache != null) {
                dataSourceNodeCache.close();
            }
        } catch (Exception e) {
            LOGGER.error("destroy PathChildrenCache in Observer", e);
        }
    }


}
