package com.sogou.upd.passport.model.problem;

import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-3 Time: 下午3:03 To change this template
 * use File | Settings | File Templates.
 */
public class ProblemAnswer {
  private long id;
  private long problemId;
  private String ansPassportId;
  private String ansContent;
  private Date ansTime;

  public ProblemAnswer() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Date getAnsTime() {
    return ansTime;
  }

  public void setAnsTime(Date ansTime) {
    this.ansTime = ansTime;
  }

  public String getAnsContent() {
    return ansContent;
  }

  public void setAnsContent(String ansContent) {
    this.ansContent = ansContent;
  }

  public String getAnsPassportId() {
    return ansPassportId;
  }

  public void setAnsPassportId(String ansPassportId) {
    this.ansPassportId = ansPassportId;
  }

  public long getProblemId() {
    return problemId;
  }

  public void setProblemId(long problemId) {
    this.problemId = problemId;
  }
}
