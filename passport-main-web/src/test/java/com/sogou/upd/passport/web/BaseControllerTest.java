package com.sogou.upd.passport.web;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.utils.ServletUtil;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-17
 * Time: 下午2:08
 */
public class BaseControllerTest {

//    @Autowired
//    public RequestMappingHandlerAdapter handlerAdapter;
////    @Autowired
////    private JobController jobController;
//
//    private static MockHttpServletRequest request;
//
//    private static MockHttpServletResponse response;
//
//    @BeforeClass
//    public static void before() {
//        request = new MockHttpServletRequest();
//        request.setCharacterEncoding("UTF-8");
//        response = new MockHttpServletResponse();
//    }

    @Test
    public void testList() {
//        request.set;
//        request.setMethod(HttpMethod.POST.name());
//        ModelAndView mv = null;
//        try {
//            mv = handlerAdapter.handle(request, response, new HandlerMethod(jobController, "list"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void testReturnErrMsg() {
        String ru = "";
        String errorCode = "10003";
        String errorMsg = "手动阀噶生个娃气而去";
        ru = ru + "?errorCode=" + errorCode + "&errorMsg=" + Coder.encodeUTF8(errorMsg);

        String ru1 = "";
        Map paramMap = Maps.newHashMap();
        paramMap.put("errorCode", errorCode);
        paramMap.put("errorMsg", errorMsg);
        ru1 = ServletUtil.applyOAuthParametersString(ru1, paramMap);

        Assert.assertEquals(ru, ru1);


    }
}
