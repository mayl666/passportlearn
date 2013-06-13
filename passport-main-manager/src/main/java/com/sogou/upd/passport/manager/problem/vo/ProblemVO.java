package com.sogou.upd.passport.manager.problem.vo;

import com.sogou.upd.passport.model.problem.Problem;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-4 Time: 下午4:59 To change this template
 * use File | Settings | File Templates.
 */
public class ProblemVO extends Problem {
//    private String mobile;
    private String typeName;
    private int ansNum;
    public ProblemVO(String typeName,Problem problem) {
//        this.mobile = mobile;
        this.typeName = typeName;

        this.setId(problem.getId());
        this.setContent(problem.getContent());
        this.setTypeId(problem.getTypeId());
        this.setSubTime(problem.getSubTime());
        this.setClientId(problem.getClientId());
        this.setPassportId(problem.getPassportId());
        this.setStatus(problem.getStatus());
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
