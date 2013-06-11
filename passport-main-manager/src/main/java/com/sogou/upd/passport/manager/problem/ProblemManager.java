package com.sogou.upd.passport.manager.problem;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WebAddProblemParameters;
import com.sogou.upd.passport.manager.problem.vo.ProblemVO;
import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.model.problem.ProblemAnswer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-7
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
public interface ProblemManager {

    /** 通过用户反馈id，来更新反馈状态
     *
     * @param id
     * @param status
     * @return
     * @throws Exception
     */
    public int updateStatusById(long id, int status) throws Exception;

    public Result insertProblem(WebAddProblemParameters addProblemParams,String ip)throws Exception;

    public List<ProblemVO> queryProblemListByPassportId(String passportId,int start,int end) throws Exception;
}
