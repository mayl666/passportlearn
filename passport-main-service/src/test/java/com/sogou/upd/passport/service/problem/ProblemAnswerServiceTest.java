package com.sogou.upd.passport.service.problem;

import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.model.problem.ProblemAnswer;
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
public class ProblemAnswerServiceTest extends AbstractJUnit4SpringContextTests {
    private static final String PASSPORT_ID = "18612532596@sohu.com";

    @Autowired
    private ProblemAnswerService problemAnswerService;

    /**
     * 测试查询是否成功
     */
    @Test
    public void testQueryProblemListByPassportId() {
       int count = problemAnswerService.getAnswerSizeByProblemId(281);
        if (count  >0 ) {
            System.out.println("查询成功！count="+count);
        } else {
            System.out.println("查询失败！");
        }
    }

    @Test
    public void testgetAnswerListByProblemId() {
        List<ProblemAnswer> list= problemAnswerService.getAnswerListByProblemId(281);
        if (list.size()  >0 ) {
            System.out.println("查询成功!");
            for (ProblemAnswer problemAnswer:list){
                System.out.println(problemAnswer.getId()+","+problemAnswer.getAnsContent());
            }
        } else {
            System.out.println("查询失败！");
        }
    }
}
