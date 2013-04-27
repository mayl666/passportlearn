package com.sogou.upd.passport.exception;

/**
 * Service层异常
 */
public class ServiceException extends RuntimeException {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 7226094841756356370L;

  public ServiceException() {
    super();
  }

  public ServiceException(String msg) {
    super(msg);
  }

  public ServiceException(Throwable cause) {
    super(cause);
  }

  public ServiceException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
