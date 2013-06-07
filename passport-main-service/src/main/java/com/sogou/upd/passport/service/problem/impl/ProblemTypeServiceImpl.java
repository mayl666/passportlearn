package com.sogou.upd.passport.service.problem.impl;

import com.sogou.upd.passport.dao.problem.ProblemDAO;
import com.sogou.upd.passport.dao.problem.ProblemTypeDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.service.problem.ProblemService;
import com.sogou.upd.passport.service.problem.ProblemTypeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-4 Time: 下午3:25 To change this template
 * use File | Settings | File Templates.
 */
@Service
public class ProblemTypeServiceImpl implements ProblemTypeService {

  private static final Logger logger = LoggerFactory.getLogger(ProblemTypeServiceImpl.class);

  @Autowired
  private ProblemTypeDAO problemTypeDAO;

  @Override
  public String getTypeNameById(long id) throws ServiceException {
    return problemTypeDAO.getTypeNameById(id);
  }
}
