package com.sogou.upd.passport.manager.problem.impl;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.form.WebAddProblemParameters;
import com.sogou.upd.passport.manager.problem.ProblemManager;
import com.sogou.upd.passport.manager.problem.vo.ProblemVO;
import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.model.problem.ProblemType;
import com.sogou.upd.passport.service.problem.ProblemService;
import com.sogou.upd.passport.service.problem.ProblemTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-7
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProblemManagerImpl implements ProblemManager {

    private static Logger logger = LoggerFactory.getLogger(ProblemManagerImpl.class);
    private static final int PROBLEM_CLOSE_STATE = 2;

    @Autowired
    private ProblemService problemService;
    @Autowired
    private ProblemTypeService problemTypeService;

    @Override
    public Result updateStatusById(long id, int status) throws Exception {
        Result result = new APIResultSupport(false);
        try {
             int row =problemService.updateStatusById(id, status);
             if(row >0){
                 result.setSuccess(true);
                 result.setMessage("更新问题状态成功");
             }else{
                 result.setCode(ErrorUtil.ERR_CODE_PROBLEM_CLOSE_FAILED);
             }
        }catch (Exception e) {
            logger.error("insertProblem fail,id:" + id, e);
            result.setCode(ErrorUtil.ERR_CODE_PROBLEM_CLOSE_FAILED);
            return result;
        }
        return result;
    }

    @Override
    public Result closeProblemById(long id) throws Exception{
        Result result = updateStatusById(id,PROBLEM_CLOSE_STATE);
        if(result.isSuccess()){
            result.setMessage("关闭问题成功");
        }
        return result;
    }

    @Override
    public Result insertProblem(WebAddProblemParameters addProblemParams, String ip) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            //TODO 提交反馈次数检查

            Problem problem = new Problem();
            problem.setPassportId(addProblemParams.getPassportId());
            problem.setClientId(Integer.parseInt(addProblemParams.getClient_id()));
            problem.setStatus(0);
            problem.setQq(addProblemParams.getQq());
            problem.setContent(addProblemParams.getContent());
            problem.setTypeId(addProblemParams.getTypeId());
            problem.setSubTime(new Date());
            int count = problemService.insertProblem(problem);
            if (count > 0) {
                result.setSuccess(true);
                result.setMessage("添加反馈成功！");
            } else {
                result.setCode(ErrorUtil.ERR_CODE_PROBLEM_INSERT_FAILED);
            }

        } catch (Exception e) {
            logger.error("insertProblem fail,passportId:" + addProblemParams.getPassportId(), e);
            result.setCode(ErrorUtil.ERR_CODE_PROBLEM_INSERT_FAILED);
            return result;
        }
        return result;
    }

    @Override
    public List<ProblemVO> queryProblemListByPassportId(String passportId, int start, int end) throws Exception {
        try {
            List<Problem> problemsList = problemService.queryProblemListByPassportId(passportId, start, end);
            List<ProblemVO> resultList = new ArrayList<ProblemVO>();
            for (Problem problem : problemsList) {
                long problemId = problem.getId();
                ProblemType problemType = problemTypeService.getProblemTypeById(problemId);
                ProblemVO problemVO = new ProblemVO(problemType.getTypeName(), problem);
                resultList.add(problemVO);
            }
            return resultList;
        } catch (Exception e) {
            logger.error("queryProblemListByPassportId fail,passportId:" + passportId, e);
            return null;
        }
    }
}
