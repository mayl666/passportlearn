package com.sogou.upd.passport.web.problem.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.WebAddProblemParameters;
import com.sogou.upd.passport.manager.problem.ProblemManager;
import com.sogou.upd.passport.manager.problem.ProblemTypeManager;
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


    @RequestMapping(value = "/problem/addProblem", method = RequestMethod.GET)
    public Object addProblem(HttpServletRequest request, Model model)
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

        result = problemManager.insertProblem(addProblemParams,getIp(request));
        return result.toString();
    }
}
