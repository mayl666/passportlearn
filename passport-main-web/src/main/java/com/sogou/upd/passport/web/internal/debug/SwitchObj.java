package com.sogou.upd.passport.web.internal.debug;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-28
 * Time: 下午8:17
 * To change this template use File | Settings | File Templates.
 */
public class SwitchObj {
    private int id;
    private String name;
    private int open=1; //0关闭，1打开

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }
}
