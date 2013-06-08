package com.sogou.upd.passport.service.problem.impl;

import com.sogou.upd.passport.dao.problem.ProblemAnswerDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.problem.ProblemAnswer;
import com.sogou.upd.passport.service.problem.ProblemAnswerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-7
 * Time: 下午12:29
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProblemAnswerServiceImpl implements ProblemAnswerService {
    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceImpl.class);

    @Autowired
    private ProblemAnswerDAO problemAnswerDAO;

    public int insertProblemAnswer(ProblemAnswer problemAnswer)throws ServiceException{
           return problemAnswerDAO.insertProblemAnswer(problemAnswer);
    }
    public List<ProblemAnswer> getAnswerListByProblemId(long id) throws ServiceException{
          return  problemAnswerDAO.getAnswerListByProblemId(id);
    }

}
