package com.sogou.upd.passport.manager.problem;

import com.sogou.upd.passport.model.problem.ProblemAnswer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-7
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
public interface ProblemAnswerManager {

    /**
     * @param problemAnswer
     * @return
     * @throws Exception
     */
    public int insertProblemAnswer(ProblemAnswer problemAnswer) throws Exception;

    /**
     * @param id
     * @return
     * @throws Exception
     */
    public List<ProblemAnswer> getAnswerListByProblemId(long id) throws Exception;

}
