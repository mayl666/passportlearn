package com.sogou.upd.passport.web;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-17
 * Time: 下午2:22
 */
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletResponse;

import com.sogou.upd.passport.manager.ManagerHelper;
import org.apache.struts.mock.MockServletContext;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

public class JUnitActionBase {

    protected static final String passportId = "upd_test@sogou.com";

    protected static final String password = "testtest1";

    public static final String APP_KEY = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";

    protected static final int clientId = 1120;

    protected static final String modifyIp = "10.1.164.160";

    private static HandlerMapping handlerMapping;
    private static HandlerAdapter handlerAdapter;


    @BeforeClass
    public static void setUp() throws UnsupportedEncodingException {
        if (handlerMapping == null) {

//            ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-config-test.xml");


            String[] configs = { "classpath:spring-config-test.xml" };
            XmlWebApplicationContext context = new XmlWebApplicationContext();
            context.setConfigLocations(configs);
            MockServletContext msc = new MockServletContext();
            context.setServletContext(msc);
            context.refresh();
            msc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);

            handlerMapping = (HandlerMapping) context
                    .getBean(DefaultAnnotationHandlerMapping.class);
            handlerAdapter = (HandlerAdapter) context.getBean(context.getBeanNamesForType(AnnotationMethodHandlerAdapter.class)[0]);
        }
    }


    public ModelAndView excuteAction(MockHttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.excuteAction(request, response,request.getParameter("userid"));
    }

    public ModelAndView  excuteAction(MockHttpServletRequest request, HttpServletResponse response,String signVariableStr) throws Exception {
        long ct=System.currentTimeMillis();
        String code= ManagerHelper.generatorCode(signVariableStr,clientId,APP_KEY,ct);
        request.addParameter("code",code);
        request.addParameter("client_id",String.valueOf(clientId));
        request.addParameter("ct",String.valueOf(ct));
        request.setAttribute(HandlerMapping.INTROSPECT_TYPE_LEVEL_MAPPING, true);
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        final ModelAndView model = handlerAdapter.handle(request, response, chain.getHandler());
        return model;
    }
}
