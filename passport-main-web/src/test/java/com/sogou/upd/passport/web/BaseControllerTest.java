//package com.sogou.upd.passport.web;
//
//import org.apache.struts.mock.MockHttpServletRequest;
//import org.apache.struts.mock.MockHttpServletResponse;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpMethod;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
//
///**
// * User: ligang201716@sogou-inc.com
// * Date: 13-6-17
// * Time: 下午2:08
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"file:/resources/spring-config.xml"})
//public class BaseControllerTest  {
//
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
//
//    @Test
//    public void testList() {
////        request.set;
////        request.setMethod(HttpMethod.POST.name());
////        ModelAndView mv = null;
////        try {
////            mv = handlerAdapter.handle(request, response, new HandlerMethod(jobController, "list"));
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }
//}
