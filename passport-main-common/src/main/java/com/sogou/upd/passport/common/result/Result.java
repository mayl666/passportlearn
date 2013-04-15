package com.sogou.upd.passport.common.result;

import java.util.*;

/**
 * Service返回值对象
 * User: mayan
 * Date: 13-4-11
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */
public class Result {
    private boolean success;
    private Object obj;
    private List list;
    private String message;

    public Result() {
    }

    public Result(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}