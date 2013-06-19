package com.sogou.upd.passport.manager.problem.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.form.WebAddProblemParameters;
import com.sogou.upd.passport.manager.problem.ProblemManager;
import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.problem.ProblemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

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
    private OperateTimesService operateTimesService;
    @Autowired
    private AccountService accountService;

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
            if(!checkCaptcha(addProblemParams.getCaptcha(),addProblemParams.getToken())){
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE);
                return result;
            }
            //提交反馈次数检查
            if(operateTimesService.checkAddProblemInBlackList(addProblemParams.getPassportId(),ip)){
                result.setCode(ErrorUtil.ERR_CODE_PROBLEM_ADDTIMES_LIMITED);
                return result;
            }
            Problem problem = new Problem();
            problem.setPassportId(addProblemParams.getPassportId());
            if (!Strings.isNullOrEmpty(addProblemParams.getClientId())){
                problem.setClientId(Integer.parseInt(addProblemParams.getClientId()));
            }
            problem.setTitle(addProblemParams.getTitle());
            problem.setEmail(addProblemParams.getEmail());
            problem.setContent(addProblemParams.getContent());
            problem.setTypeId(Integer.parseInt(addProblemParams.getTypeId()));
            problem.setSubTime(new Date());
            int count = problemService.insertProblem(problem);
            if (count > 0) {
                result.setSuccess(true);
                result.setMessage("添加反馈成功！");
            } else {
                result.setCode(ErrorUtil.ERR_CODE_PROBLEM_INSERT_FAILED);
            }
            //记录提交反馈次数，包括成功和失败
            operateTimesService.incAddProblemTimes(addProblemParams.getPassportId(),ip);
        } catch (Exception e) {
            //记录提交反馈次数，包括成功和失败
            operateTimesService.incAddProblemTimes(addProblemParams.getPassportId(),ip);
            logger.error("insertProblem fail,passportId:" + addProblemParams.getPassportId(), e);
            result.setCode(ErrorUtil.ERR_CODE_PROBLEM_INSERT_FAILED);
            return result;
        }

        return result;
    }
    private boolean checkCaptcha(String captcha, String token) {
        //校验验证码
        if (!accountService.checkCaptchaCodeIsVaild(token, captcha)) {
            return false;
        }
        return true;
    }
}
