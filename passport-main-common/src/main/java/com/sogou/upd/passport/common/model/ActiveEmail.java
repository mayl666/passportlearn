package com.sogou.upd.passport.common.model;

import java.util.Map;

/**
 * User: mayan Date: 13-4-15 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class ActiveEmail {

  private String activeUrl;
  private String templateFile;
  private Map<String,Object> map;
  private String subject;
  private String category;
  private String toEmail;

  public String getActiveUrl() {
    return activeUrl;
  }

  public void setActiveUrl(String activeUrl) {
    this.activeUrl = activeUrl;
  }

  public String getTemplateFile() {
    return templateFile;
  }

  public void setTemplateFile(String templateFile) {
    this.templateFile = templateFile;
  }

  public Map<String, Object> getMap() {
    return map;
  }

  public void setMap(Map<String, Object> map) {
    this.map = map;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getToEmail() {
    return toEmail;
  }

  public void setToEmail(String toEmail) {
    this.toEmail = toEmail;
  }
}
