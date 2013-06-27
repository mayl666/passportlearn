package com.sogou.upd.passport.web.inteceptor;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-27
 * Time: 下午2:52
 */
@Component
public class MetricInitListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(MetricInitListener.class);

    @Autowired
    private MetricRegistry metrics;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final JmxReporter reporter = JmxReporter.forRegistry(metrics).build();
        reporter.start();
        log.info("MetricRegistry JmxReporter start ok!!!");
    }
}
