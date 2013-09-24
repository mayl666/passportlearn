package com.sogou.upd.passport.manager.problem;

import com.sogou.upd.passport.model.problem.ProblemType;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-7
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
public interface ProblemTypeManager {

    /**
     *获取反馈类型列表
     * @return
     * @throws Exception
     */
    public List<ProblemType> getProblemTypeList() throws Exception;
}
