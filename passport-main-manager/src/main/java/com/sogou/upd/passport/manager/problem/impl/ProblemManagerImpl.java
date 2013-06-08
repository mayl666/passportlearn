package com.sogou.upd.passport.manager.problem.impl;

import com.sogou.upd.passport.manager.problem.ProblemAnswerManager;
import com.sogou.upd.passport.model.problem.ProblemAnswer;
import com.sogou.upd.passport.service.problem.ProblemAnswerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-7
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProblemManagerImpl implements ProblemAnswerManager {

    private static Logger logger = LoggerFactory.getLogger(ProblemManagerImpl.class);

    @Autowired
    private ProblemAnswerService problemAnswerService;

    @Override
    public int insertProblemAnswer(ProblemAnswer problemAnswer) throws Exception {
        return problemAnswerService.insertProblemAnswer(problemAnswer);
    }
    @Override
    public List<ProblemAnswer> getAnswerListByProblemId(long id) throws Exception {
        return problemAnswerService.getAnswerListByProblemId(id);
    }
}
