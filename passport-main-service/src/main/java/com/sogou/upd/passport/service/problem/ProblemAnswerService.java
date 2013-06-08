package com.sogou.upd.passport.service.problem;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.problem.ProblemAnswer;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-4 Time: 下午3:24 To change this template
 * use File | Settings | File Templates.
 */
public interface ProblemAnswerService {
    /**
     *
     * @param problemAnswer
     * @return
     * @throws ServiceException
     */
    public int insertProblemAnswer(ProblemAnswer problemAnswer) throws ServiceException;

    /**
     *
     * @param id
     * @return
     * @throws ServiceException
     */
    public List<ProblemAnswer> getAnswerListByProblemId(long id) throws ServiceException;
}
