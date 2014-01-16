package com.sogou.upd.passport.common.exception;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-8
 * Time: 上午11:34
 * To change this template use File | Settings | File Templates.
 */
public class ConnectException extends RuntimeException {

    public ConnectException() {
        super();
    }

    public ConnectException(String msg) {
        super(msg);
    }

    public ConnectException(Throwable cause) {
        super(cause);
    }

    public ConnectException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
