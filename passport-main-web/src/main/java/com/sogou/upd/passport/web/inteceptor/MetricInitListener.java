package com.sogou.upd.passport.web.inteceptor;

import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-27
 * Time: 下午2:52
 */
public class MetricInitListener implements ApplicationListener<ContextStartedEvent> {

    @Autowired
    private MetricRegistry metrics;

    @Override
    public void onApplicationEvent(ContextStartedEvent event) {



    }
}
