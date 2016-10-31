package com.sogou.upd.passport.web.session.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 校验 sgid 参数
 * Created by wanghuaqing on 2016/10/28.
 */
public class VerifySgidParam extends BaseApiParams {
  @NotBlank(message = "密码不允许为空")
  private String sgid;
  
  public String getSgid() {
    return sgid;
  }
  
  public void setSgid(String sgid) {
    this.sgid = sgid;
  }
  
  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("sgid", sgid)
      .toString();
  }
}
