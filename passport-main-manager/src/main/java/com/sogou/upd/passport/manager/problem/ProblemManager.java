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

    /** 通过用户反馈id，来更新反馈状态
     *
     * @param id
     * @param status
     * @return
     * @throws Exception
     */
    public Result updateStatusById(long id, int status) throws Exception;

    /**
     * 关闭反馈
     * @param id
     * @return
     * @throws Exception
     */
    public Result closeProblemById(long id) throws Exception;

    public Result insertProblem(WebAddProblemParameters addProblemParams,String ip)throws Exception;

    public int getProblemCount(Integer status,Integer clientId,Integer typeId,Date startDate,Date endDate,String title,  String content);
}
