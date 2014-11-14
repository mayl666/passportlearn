package com.sogou.upd.passport.zk;

import com.google.common.base.Strings;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.netflix.curator.framework.recipes.cache.NodeCache;
import com.netflix.curator.framework.recipes.cache.NodeCacheListener;
import com.sogou.upd.passport.common.utils.JsonUtil;
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
@Component
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
                LOGGER.warn(" data source node current data :" + nodeData);
                Map jsonMap = JsonUtil.jsonToBean(nodeData, Map.class);
                String masterJdbcUrl = (String) jsonMap.get(DataSourceConstant.masterJdbcUrl);
                String slaveJdbcUrl = (String) jsonMap.get(DataSourceConstant.slaveJdbcUrl);

                LOGGER.warn("refresh  before. masterJdbcUrl:{},master-acquireIncrement:{},master-checkoutTimeout:{},master-maxPoolSize:{}," +
                        "slaveJdbcUrl:{},slave-acquireIncrement:{},slave-checkoutTimeout:{},slave-maxPoolSize:{}",
                        new Object[]{masterDataSource.getJdbcUrl(), masterDataSource.getAcquireIncrement(), masterDataSource.getCheckoutTimeout(), masterDataSource.getMaxPoolSize(),
                                slaveDataSource.getJdbcUrl(), slaveDataSource.getAcquireIncrement(), slaveDataSource.getCheckoutTimeout(), slaveDataSource.getMaxPoolSize()
                        });

                if (!Strings.isNullOrEmpty(masterJdbcUrl)) {
                    if (masterJdbcUrl.equals(masterDataSource.getJdbcUrl())) {
                        LOGGER.warn("DBZkSwitchMonitor refresh node data.masterJdbcUrl no changed.");
                        return;
                    }
                } else {
                    LOGGER.warn("DBZkSwitchMonitor refresh node changed.masterJdbcUrl is NULL.");
                    return;
                }

                if (!Strings.isNullOrEmpty(slaveJdbcUrl)) {
                    if (slaveJdbcUrl.equals(slaveDataSource.getJdbcUrl())) {
                        LOGGER.warn("DBZkSwitchMonitor refresh node data.slaveJdbcUrl no changed.");
                        return;
                    }
                } else {
                    LOGGER.warn("DBZkSwitchMonitor refresh node changed.slaveJdbcUrl is NULL.");
                    return;
                }

                if (masterDataSource != null && slaveDataSource != null) {

                    //重新构建 masterDataSource jdbc url
                    masterDataSource.setJdbcUrl(masterJdbcUrl);

                    slaveDataSource.setJdbcUrl(slaveJdbcUrl);

                    /*if (jsonMap.containsKey(DataSourceConstant.acquireIncrement) && jsonMap.get(DataSourceConstant.acquireIncrement) != null) {
                        masterDataSource.setAcquireIncrement((Integer) jsonMap.get(DataSourceConstant.acquireIncrement));
                        slaveDataSource.setAcquireIncrement((Integer) jsonMap.get(DataSourceConstant.acquireIncrement));
                    } else {
                        masterDataSource.setAcquireIncrement(DataSourceConstant.acquireIncrement_value);
                        slaveDataSource.setAcquireIncrement(DataSourceConstant.acquireIncrement_value);
                    }

                    if (jsonMap.containsKey(DataSourceConstant.acquireRetryAttempts) && jsonMap.get(DataSourceConstant.acquireRetryAttempts) != null) {
                        masterDataSource.setAcquireRetryAttempts((Integer) jsonMap.get(DataSourceConstant.acquireRetryAttempts));
                        slaveDataSource.setAcquireRetryAttempts((Integer) jsonMap.get(DataSourceConstant.acquireRetryAttempts));
                    } else {
                        masterDataSource.setAcquireRetryAttempts(DataSourceConstant.acquireRetryAttempts_value);
                        slaveDataSource.setAcquireRetryAttempts(DataSourceConstant.acquireRetryAttempts_value);
                    }

                    if (jsonMap.containsKey(DataSourceConstant.idleConnectionTestPeriod) && jsonMap.get(DataSourceConstant.idleConnectionTestPeriod) != null) {
                        masterDataSource.setIdleConnectionTestPeriod((Integer) jsonMap.get(DataSourceConstant.idleConnectionTestPeriod));
                        slaveDataSource.setIdleConnectionTestPeriod((Integer) jsonMap.get(DataSourceConstant.idleConnectionTestPeriod));
                    } else {
                        masterDataSource.setIdleConnectionTestPeriod(DataSourceConstant.idleConnectionTestPeriod_value);
                        slaveDataSource.setIdleConnectionTestPeriod(DataSourceConstant.idleConnectionTestPeriod_value);
                    }

                    if (jsonMap.containsKey(DataSourceConstant.checkoutTimeout) && jsonMap.get(DataSourceConstant.checkoutTimeout) != null) {
                        masterDataSource.setCheckoutTimeout((Integer) jsonMap.get(DataSourceConstant.checkoutTimeout));
                        slaveDataSource.setCheckoutTimeout((Integer) jsonMap.get(DataSourceConstant.checkoutTimeout));
                    } else {
                        masterDataSource.setCheckoutTimeout(DataSourceConstant.checkoutTimeout_value);
                        slaveDataSource.setCheckoutTimeout(DataSourceConstant.checkoutTimeout_value);
                    }

                    if (jsonMap.containsKey(DataSourceConstant.maxPoolSize) && jsonMap.get(DataSourceConstant.maxPoolSize) != null) {
                        masterDataSource.setMaxPoolSize((Integer) jsonMap.get(DataSourceConstant.maxPoolSize));
                        slaveDataSource.setMaxPoolSize((Integer) jsonMap.get(DataSourceConstant.maxPoolSize));
                    } else {
                        masterDataSource.setMaxPoolSize(DataSourceConstant.maxPoolSize_value);
                        slaveDataSource.setMaxPoolSize(DataSourceConstant.maxPoolSize_value);
                    }

                    if (jsonMap.containsKey(DataSourceConstant.initialPoolSize) && jsonMap.get(DataSourceConstant.initialPoolSize) != null) {
                        masterDataSource.setMinPoolSize((Integer) jsonMap.get(DataSourceConstant.minPoolSize));
                        slaveDataSource.setMinPoolSize((Integer) jsonMap.get(DataSourceConstant.minPoolSize));
                    } else {
                        masterDataSource.setMinPoolSize(DataSourceConstant.minPoolSize_value);
                        slaveDataSource.setMinPoolSize(DataSourceConstant.minPoolSize_value);
                    }

                    if (jsonMap.containsKey(DataSourceConstant.initialPoolSize) && jsonMap.get(DataSourceConstant.initialPoolSize) != null) {
                        masterDataSource.setInitialPoolSize((Integer) jsonMap.get(DataSourceConstant.initialPoolSize));
                        slaveDataSource.setInitialPoolSize((Integer) jsonMap.get(DataSourceConstant.initialPoolSize));
                    } else {
                        masterDataSource.setInitialPoolSize(DataSourceConstant.initialPoolSize_value);
                        slaveDataSource.setInitialPoolSize(DataSourceConstant.initialPoolSize_value);
                    }

                    if (jsonMap.containsKey(DataSourceConstant.maxStatements) && jsonMap.get(DataSourceConstant.maxStatements) != null) {
                        masterDataSource.setMaxStatements((Integer) jsonMap.get(DataSourceConstant.maxStatements));
                        slaveDataSource.setMaxStatements((Integer) jsonMap.get(DataSourceConstant.maxStatements));
                    }

                    if (jsonMap.containsKey(DataSourceConstant.maxIdleTime) && jsonMap.get(DataSourceConstant.maxIdleTime) != null) {
                        masterDataSource.setMaxIdleTime((Integer) jsonMap.get(DataSourceConstant.maxIdleTime));
                        slaveDataSource.setMaxIdleTime((Integer) jsonMap.get(DataSourceConstant.maxIdleTime));
                    } else {
                        masterDataSource.setMaxIdleTime(DataSourceConstant.maxIdleTime_value);
                        slaveDataSource.setMaxIdleTime(DataSourceConstant.maxIdleTime_value);
                    }

                    if (jsonMap.containsKey((DataSourceConstant.numHelperThreads)) && jsonMap.get(DataSourceConstant.numHelperThreads) != null) {
                        masterDataSource.setNumHelperThreads((Integer) jsonMap.get(DataSourceConstant.numHelperThreads));
                        slaveDataSource.setNumHelperThreads((Integer) jsonMap.get(DataSourceConstant.numHelperThreads));
                    } else {
                        masterDataSource.setNumHelperThreads(DataSourceConstant.numHelperThreads_value);
                        slaveDataSource.setNumHelperThreads(DataSourceConstant.numHelperThreads_value);
                    }

                    if (jsonMap.containsKey(DataSourceConstant.breakAfterAcquireFailure) && jsonMap.get(DataSourceConstant.breakAfterAcquireFailure) != null) {
                        masterDataSource.setBreakAfterAcquireFailure((Boolean) jsonMap.get(DataSourceConstant.breakAfterAcquireFailure));
                        slaveDataSource.setBreakAfterAcquireFailure((Boolean) jsonMap.get(DataSourceConstant.breakAfterAcquireFailure));
                    } else {
                        masterDataSource.setBreakAfterAcquireFailure(DataSourceConstant.breakAfterAcquireFailure_value);
                        slaveDataSource.setBreakAfterAcquireFailure(DataSourceConstant.breakAfterAcquireFailure_value);
                    }

                    if (jsonMap.containsKey(DataSourceConstant.testConnectionOnCheckout) && jsonMap.get(DataSourceConstant.testConnectionOnCheckout) != null) {
                        masterDataSource.setTestConnectionOnCheckout((Boolean) jsonMap.get(DataSourceConstant.testConnectionOnCheckout));
                        slaveDataSource.setTestConnectionOnCheckout((Boolean) jsonMap.get(DataSourceConstant.testConnectionOnCheckout));
                    } else {
                        masterDataSource.setTestConnectionOnCheckout(DataSourceConstant.testConnectionOnCheckout_value);
                        slaveDataSource.setTestConnectionOnCheckout(DataSourceConstant.testConnectionOnCheckout_value);
                    }
*/
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
