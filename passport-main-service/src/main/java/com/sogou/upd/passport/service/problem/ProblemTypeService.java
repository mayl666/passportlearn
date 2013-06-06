package com.sogou.upd.passport.service.problem;

import com.sogou.upd.passport.exception.ServiceException;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-4 Time: 下午3:24 To change this template
 * use File | Settings | File Templates.
 */
public interface ProblemTypeService {

  /**
   *
   * @param id
   * @return
   * @throws ServiceException
   */
  public String getTypeNameById(long id) throws ServiceException;
}
