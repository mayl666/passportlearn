package com.sogou.upd.passport.model.problem;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-3 Time: 下午3:00 To change this template
 * use File | Settings | File Templates.
 */
public class ProblemType {
  private long id;
  private String typeName;

  public ProblemType() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }
}
