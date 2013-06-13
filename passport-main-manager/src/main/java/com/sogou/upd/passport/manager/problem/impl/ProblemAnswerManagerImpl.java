package com.sogou.upd.passport.manager.problem.impl;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.problem.ProblemAnswerManager;
import com.sogou.upd.passport.manager.problem.ProblemManager;
import com.sogou.upd.passport.manager.problem.vo.ProblemAnswerVO;
import com.sogou.upd.passport.model.problem.ProblemAnswer;
import com.sogou.upd.passport.service.problem.ProblemAnswerService;
import com.sogou.upd.passport.service.problem.ProblemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-7
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProblemAnswerManagerImpl implements ProblemAnswerManager {

    private static Logger logger = LoggerFactory.getLogger(ProblemAnswerManagerImpl.class);
    private static final int USER_ANSWER_TYPE = 0;
    private static final int ADMIN_ANSWER_TYPE = 1;
    @Autowired
    private ProblemAnswerService problemAnswerService;


    @Override
    public Result insertProblemAnswer(ProblemAnswer problemAnswer,String ip) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            //TODO 插入反馈评论次数限制
            //TODO IP passport检查限制
            int row = problemAnswerService.insertProblemAnswer(problemAnswer);
            if(row >0){
                result.setMessage("添加成功!");
                result.setSuccess(true);
            }else {
                result.setCode(ErrorUtil.ERR_CODE_PROBLEMANSWER_INSERT_FAILED);
            }
            return result;
        } catch (Exception e) {
            logger.error("insertProblemAnswer fail,problemtId:" + problemAnswer.getProblemId(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }

    }
    @Override
    public List<ProblemAnswer> getAnswerListByProblemId(long id) throws Exception {
        return problemAnswerService.getAnswerListByProblemId(id);
    }

    @Override
    public Result getAnswerVOList(long id, String passportId) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            List<ProblemAnswerVO> resultList = new ArrayList<ProblemAnswerVO>();
            List<ProblemAnswer> list = getAnswerListByProblemId(id);
            for (ProblemAnswer problemAnswer : list) {
                ProblemAnswerVO problemAnswerVO = null;
                if (passportId.equals(problemAnswer.getAnsPassportId())) {
                    problemAnswerVO = new ProblemAnswerVO(problemAnswer.getAnsContent(), USER_ANSWER_TYPE);
                } else {
                    problemAnswerVO = new ProblemAnswerVO(problemAnswer.getAnsContent(), ADMIN_ANSWER_TYPE);
                }
                resultList.add(problemAnswerVO);
            }
            Map<String, Object> mapResult = Maps.newHashMap();
            mapResult.put("problemAnswerVOList", resultList);
            result.setSuccess(true);
            result.setModels(mapResult);
            return result;
        } catch (Exception e) {
            logger.error("queryProblemListByPassportId fail,passportId:" + passportId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

}
