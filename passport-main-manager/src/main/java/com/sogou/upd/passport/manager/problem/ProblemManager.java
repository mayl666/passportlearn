package com.sogou.upd.passport.manager.problem;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WebAddProblemParams;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-7
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
public interface ProblemManager {


    public Result insertProblem(WebAddProblemParams addProblemParams,String ip)throws Exception;
}
