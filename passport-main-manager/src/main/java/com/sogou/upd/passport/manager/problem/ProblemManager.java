package com.sogou.upd.passport.manager.problem;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WebAddProblemParameters;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-7
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
public interface ProblemManager {


    public Result insertProblem(WebAddProblemParameters addProblemParams,String ip)throws Exception;
}
