package com.sogou.upd.passport.common.result;

import com.google.common.base.Strings;

/**
 * Created by xieyilun on 2015/11/24.
 */
public class APIResultCallbackSupport extends APIResultSupport {
    public String callback = null;

    public APIResultCallbackSupport(boolean success) {
        super(success);
    }

    @Override
    public String toString() {
        if (Strings.isNullOrEmpty(callback)) {
            return super.toString();
        }
        return callback + "('" + super.toString() + "')";
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }
}
