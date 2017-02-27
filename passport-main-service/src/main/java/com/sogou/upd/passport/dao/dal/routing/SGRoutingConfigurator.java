package com.sogou.upd.passport.dao.dal.routing;

import com.xiaomi.common.service.dal.routing.Router;
import com.xiaomi.common.service.dal.routing.RoutingConfigurator;
import com.xiaomi.common.service.dal.routing.RoutingDescriptor;
import com.xiaomi.common.service.dal.routing.RoutingDescriptorImpl;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 分表路由配置
 * User: shipengzhi
 * Date: 14-2-8
 * Time: 上午1:52
 * To change this template use File | Settings | File Templates.
 */
public class SGRoutingConfigurator extends RoutingConfigurator {
    static Logger logger = LoggerFactory.getLogger(SGRoutingConfigurator.class);

    protected ConcurrentHashMap<String, RoutingDescriptor> map = new ConcurrentHashMap<String, RoutingDescriptor>();

    private List<String> partitions;

    // 加锁保护配置信息
    protected ReadWriteLock rwLock = new ReentrantReadWriteLock();

    boolean inited = false;

    // 分表路由的名称
    public static final String SG_STRING_HASH = "sg-str-hash";

    public RoutingDescriptor getDescriptor(String name) {
        if (!inited) {
            initPartitions();
        }

        String keyword = name;

        // 加锁保护配置信息的完整性
        Lock lock = rwLock.readLock();

        try {
            lock.lock();
            RoutingDescriptor descriptor = map.get(keyword);
            return descriptor;
        } finally {
            lock.unlock();
        }
    }

    private void initPartitions() {
        List<String> partitions = getPartitions();
        for (int i = 0; i < partitions.size(); i++) {
            String[] conf = partitions.get(i).split(":");
            map.put(conf[1], new RoutingDescriptorImpl().setPartitionRouter(createRouter(conf)));
        }
        inited = true;
    }

    public void setPartitions(List<String> partitions) {
        this.partitions = partitions;
    }

    public List<String> getPartitions() {
        return partitions;
    }

    private Router createRouter(String[] conf) {
        if (SG_STRING_HASH.equalsIgnoreCase(conf[0])) {
            RouterFactory factory = new RouterFactory(SG_STRING_HASH) {
                @Override
                public Router onCreateRouter(String column, String pattern, int partitions) {
                    return new SGStringHashRouter(column, pattern, partitions);
                }
            };
            return factory.setColumn(conf[2]).setPattern(conf[3]).setPartition(conf[4]).createRouter();
        }
        return null;
    }

    public static abstract class RouterFactory {
        private String name;
        private Map<String, String> values = new HashMap<String, String>();

        public static final String KEY_COLUMN = "by-column";
        public static final String KEY_PATTERN = "target-pattern";
        public static final String KEY_PARTITION = "partitions";

        public RouterFactory(String name) {
            this.name = name;
        }

        public RouterFactory add(String key, String value) {
            values.put(key, value);
            return this;
        }

        public RouterFactory setColumn(String column) {
            return add(KEY_COLUMN, column);
        }

        public RouterFactory setPattern(String pattern) {
            return add(KEY_PATTERN, pattern);
        }

        public RouterFactory setPartition(String partition) {
            return add(KEY_PARTITION, partition);
        }

        public Router createRouter() {
            for (Map.Entry<String, String> entry : this.values.entrySet()) {
                if (entry.getValue() == null) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Router '%s' must have '%s' property.", name, entry.getKey()));
                    }
                    return null;
                }
            }

            String column = values.get(KEY_COLUMN);
            String pattern = values.get(KEY_PATTERN);
            String partition = values.get(KEY_PARTITION);
            int count = 0;
            if (partition != null) {
                try {
                    count = NumberUtils.toInt(partition.trim());
                } catch (NumberFormatException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Router '%s' property '%s' must be number.", name, KEY_PARTITION));
                    }
                    return null;
                }
            }

            // 输出日志
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Creating router '%s' [ %s ]", name, values));
            }

            return onCreateRouter(column, pattern, count);
        }

        public abstract Router onCreateRouter(String column, String pattern, int partitions);
    }
}

