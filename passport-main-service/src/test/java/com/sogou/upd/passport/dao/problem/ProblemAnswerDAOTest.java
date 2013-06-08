package com.sogou.upd.passport.dao.problem;

import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.model.problem.ProblemAnswer;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
public class ProblemAnswerDAOTest extends BaseDAOTest {

    @Autowired
    private ProblemAnswerDAO problemAnswerDAO;

    @Before
    public void init() {
      ProblemAnswer problemAnswer = new ProblemAnswer();
      problemAnswer.setAnsPassportId(PASSPORT_ID);
      problemAnswer.setProblemId(281l);
      problemAnswer.setAnsTime(new Date());
      problemAnswer.setAnsContent("尊敬的玩家您好，活动已经结束请您等待下次开启，请注意查看精彩活动。感谢您对搜狗游戏的支持");
      int row = problemAnswerDAO.insertProblemAnswer(problemAnswer);
      Assert.assertTrue(row != 0);
    }

    @After
    public void end() {
    }

    /**
     * 测试单条记录查询
     */
    @Test
    public void testGetProblemAnswerList() {
      List<ProblemAnswer> list= problemAnswerDAO.getAnswerListByProblemId(281l);
        Assert.assertTrue(list.size() >0);
    }



}
