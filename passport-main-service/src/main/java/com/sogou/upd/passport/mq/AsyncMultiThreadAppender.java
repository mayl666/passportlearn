package com.sogou.upd.passport.mq;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AsyncAppenderBase;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-9-4 Time: 上午10:46 To change this template use File | Settings | File Templates.
 */
public class AsyncMultiThreadAppender extends AsyncMultiThreadAppenderBase<ILoggingEvent> {

    boolean includeCallerData = false;

    /**
     * Events of level TRACE, DEBUG (and INFO) are deemed to be discardable.
     * @param event
     * @return true if the event is of level TRACE, DEBUG (or INFO) false otherwise.
     * @Note: INFO is kept
     */
    protected boolean isDiscardable(ILoggingEvent event) {
        Level level = event.getLevel();
        return level.toInt() < Level.INFO_INT;
    }

    protected void preprocess(ILoggingEvent eventObject) {
        eventObject.prepareForDeferredProcessing();
        if(includeCallerData)
            eventObject.getCallerData();
    }

    public boolean isIncludeCallerData() {
        return includeCallerData;
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }
}
