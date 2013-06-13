package com.sogou.upd.passport.service.problem;

import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.model.problem.ProblemType;
import com.sogou.upd.passport.service.account.AccountInfoService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-6 Time: 下午6:49 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class ProblemTypeServiceTest extends AbstractJUnit4SpringContextTests {
    private static final String PASSPORT_ID = "13552848876@sohu.com";
    private static final String EMAIL = "Binding123@163.com";
    private static final String NEW_EMAIL = "NewBinding123@163.com";
    private static final String QUESTION = "Secure question";
    private static final String NEW_QUESTION = "New secure question";
    private static final String ANSWER = "Secure answer";
    private static final String NEW_ANSWER = "New secure answer";

    @Autowired
    private ProblemTypeService problemTypeService;

    /**
     * 测试查询是否成功
     */
    @Test
    public void testgetProblemTypeList() {
        List<ProblemType> list = problemTypeService.getProblemTypeList();
        if (list  != null) {
            System.out.println("查询成功！");
            for(ProblemType problemType:list){
                System.out.println(problemType.getId()+","+problemType.getTypeName());
            }
        } else {
            System.out.println("查询失败！");
        }
    }
}
