package com.sogou.upd.passport.common.exception;

/**
 * User: mayan Date: 13-4-15 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class MailException extends RuntimeException {

  private static final long serialVersionUID = 7226094841756356370L;

  public MailException() {
    super();
  }

  public MailException(String msg) {
    super(msg);
  }

  public MailException(Throwable cause) {
    super(cause);
  }

  public MailException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
