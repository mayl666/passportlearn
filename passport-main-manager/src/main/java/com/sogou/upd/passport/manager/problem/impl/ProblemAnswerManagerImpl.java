package com.sogou.upd.passport.manager.problem.impl;

import com.sogou.upd.passport.manager.problem.ProblemManager;
import com.sogou.upd.passport.service.problem.ProblemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-7
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProblemAnswerManagerImpl implements ProblemManager {

    private static Logger logger = LoggerFactory.getLogger(ProblemAnswerManagerImpl.class);

    @Autowired
    private ProblemService problemService;

    @Override
    public int updateStatusById(long id, int status) throws Exception {
        return problemService.updateStatusById(id, status);
    }

}
