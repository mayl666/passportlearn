package com.sogou.upd.passport.web.problem.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.WebAddProblemParameters;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
import com.sogou.upd.passport.manager.form.WebRegisterParameters;
import com.sogou.upd.passport.manager.problem.ProblemAnswerManager;
import com.sogou.upd.passport.manager.problem.ProblemManager;
import com.sogou.upd.passport.manager.problem.ProblemTypeManager;
import com.sogou.upd.passport.manager.problem.vo.ProblemAnswerVO;
import com.sogou.upd.passport.manager.problem.vo.ProblemVO;
import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.model.problem.ProblemAnswer;
import com.sogou.upd.passport.model.problem.ProblemType;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-9
 * Time: 下午2:56
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/web")
public class ProblemAction extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ProblemAction.class);
    private static final Integer PAGE_SIZE = 10;

    @Autowired
    private ProblemManager problemManager;
    @Autowired
    private ProblemTypeManager problemTypeManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private ProblemAnswerManager problemAnswerManager;

    @RequestMapping(value = "/problem/addProblem", method = RequestMethod.GET)
    public Object login(HttpServletRequest request, Model model)
            throws Exception {
        // TODO 获取并set passportId
        // TODO 获取应用列表

        //获取问题类型列表
        List<ProblemType> typeList = problemTypeManager.getProblemTypeList();
        model.addAttribute("typeList", typeList);
        //TODO 获取并set验证码

        return "/problem/addProblem";
    }

    @RequestMapping(value = "/problem/saveProblem", method = RequestMethod.POST)
    @ResponseBody
    public Object saveProblem(HttpServletRequest request, WebAddProblemParameters addProblemParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(addProblemParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result;
        }

        //验证client_id
        int clientId = Integer.parseInt(addProblemParams.getClient_id());
        //检查client_id格式以及client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
            return result;
        }

        result = problemManager.insertProblem(addProblemParams,getIp(request));
        return result.toString();
    }

    @RequestMapping(value = "/problem/myProblem", method = RequestMethod.GET)
    public Object listMyProblem(HttpServletRequest request,int page, Model model)
            throws Exception {
        // TODO 获取并set passportId
        String  passportId = null;
        int start=1;
        int end =PAGE_SIZE;
        if (page > 1) {
            start = (page - 1) * PAGE_SIZE+1;
            end = page * PAGE_SIZE;
        }
        List<ProblemVO> problemVOList=problemManager.queryProblemListByPassportId(passportId,start,end);
        model.addAttribute("problemVOList",problemVOList);
        model.addAttribute("problemSize",problemVOList.size());
        return "/problem/myProblem";
    }

    @RequestMapping(value = "/problem/problemAnswer", method = RequestMethod.GET)
    @ResponseBody
    public Object listProblemAnswer(HttpServletRequest request,int problemId, Model model)
            throws Exception {
        // TODO 获取并set passportId
        String  passportId = null;

        return problemAnswerManager.getAnswerVOList(problemId,passportId);
    }

    @RequestMapping(value = "/problem/saveProblemAnswer", method = RequestMethod.POST)
    @ResponseBody
    public Object saveProblemAnswer(HttpServletRequest request,long problemId, String content)
            throws Exception {
        Result result = new APIResultSupport(false);
        // TODO 获取并set passportId
        String  passportId = null;

        ProblemAnswer problemAnswer = new ProblemAnswer();
        problemAnswer.setProblemId(problemId);
        problemAnswer.setAnsContent(content);
        problemAnswer.setAnsTime(new Date());
        problemAnswer.setAnsPassportId(passportId);

        result = problemAnswerManager.insertProblemAnswer(problemAnswer,getIp(request));
        return result.toString();
    }
}
