package com.sogou.upd.passport.manager.problem.impl;

import com.sogou.upd.passport.manager.problem.ProblemTypeManager;
import com.sogou.upd.passport.model.problem.ProblemType;
import com.sogou.upd.passport.service.problem.ProblemTypeService;
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
public class ProblemTypeManagerImpl implements ProblemTypeManager {

    private static Logger logger = LoggerFactory.getLogger(ProblemTypeManagerImpl.class);

    @Autowired
    private ProblemTypeService problemTypeService;

    @Override
    public List<ProblemType> getProblemTypeList() throws Exception{
         return  problemTypeService.getProblemTypeList();
    }
}
