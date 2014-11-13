package com.sogou.upd.passport.zk;

import com.google.common.base.Strings;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.netflix.curator.framework.recipes.cache.NodeCache;
import com.netflix.curator.framework.recipes.cache.NodeCacheListener;
import com.sogou.upd.passport.common.utils.JsonUtil;
import net.paoding.rose.jade.dataaccess.datasource.MasterSlaveDataSourceFactory;
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
     * rose master slave 工厂
     */
//    private MasterSlaveDataSourceFactory masterSlaveDataSourceFactory;

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
                String masterJdbcUrl = (String) jsonMap.get("masterJdbcUrl");
                String slaveJdbcUrl = (String) jsonMap.get("slaveJdbcUrl");

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


                //DataSource property changeListener
//                masterDataSource.addPropertyChangeListener();

                if (masterDataSource != null && slaveDataSource != null) {
                    masterDataSource.close();
                    slaveDataSource.close();
                }

//                ComboPooledDataSource newMasterDataSource = new ComboPooledDataSource();
//                newMasterDataSource.setJdbcUrl(masterJdbcUrl);

//                ComboPooledDataSource newSlaveDataSource = new ComboPooledDataSource();
//                newSlaveDataSource.setJdbcUrl(slaveJdbcUrl);

//                List<DataSource> slaveDataSources = Lists.newArrayList();
//                slaveDataSources.add(newSlaveDataSource);

//                masterDataSource.resetPoolManager(true);
//                slaveDataSource.resetPoolManager(true);

//                masterSlaveDataSourceFactory = new MasterSlaveDataSourceFactory(newMasterDataSource, slaveDataSources, true);

                masterDataSource = new ComboPooledDataSource();
                masterDataSource.setJdbcUrl(masterJdbcUrl);

                slaveDataSource = new ComboPooledDataSource();
                slaveDataSource.setJdbcUrl(slaveJdbcUrl);

                LOGGER.warn("Data Source Properties. masterJdbcUrl:{},master-acquireIncrement:{},master-checkoutTimeout:{},master-maxPoolSize:{}" +
                        "slaveJdbcUrl:{},slave-acquireIncrement:{},slave-checkoutTimeout:{},slave-maxPoolSize:{}",
                        new Object[]{masterJdbcUrl, masterDataSource.getAcquireIncrement(), masterDataSource.getCheckoutTimeout(), masterDataSource.getMaxPoolSize(),
                                slaveJdbcUrl, slaveDataSource.getAcquireIncrement(), slaveDataSource.getCheckoutTimeout(), slaveDataSource.getMaxPoolSize()
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
