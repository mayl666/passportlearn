package com.sogou.upd.passport.manager.problem.vo;

import com.sogou.upd.passport.model.problem.Problem;

import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-4 Time: 下午4:59 To change this template
 * use File | Settings | File Templates.
 */
public class ProblemAnswerVO  {
//    private final String USER_REPLY_PREFIX = "我的留言: ";
//    private final String ADMIN_REPLY_PREFIX = "客服回复: ";
    private int  type;//0表示用户又添加的反馈回答；1表示管理员对反馈的回复
    private String content;
    private Date date;
    public ProblemAnswerVO(String content,int type) {
        this.type = type;
        this.content = content;
        this.date = new Date();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
