package com.sogou.upd.passport.dao.problem;

import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.model.problem.ProblemType;

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
public class ProblemTypeDAOTest extends BaseDAOTest {

    @Autowired
    private ProblemTypeDAO problemTypeDAO;

    @Before
    public void init() {
      ProblemType problemType = new ProblemType();
      problemType.setTypeName("注册问题");
      int row = problemTypeDAO.insertProblemType(problemType);
      Assert.assertTrue(row != 0);
    }

    @After
    public void end() {
    }

    /**
     * 测试单条记录查询
     */
    @Test
    public void testGetProblemTypeById() {
//      String problemType = problemTypeDAO.getProblemTypeById(262l);
//      System.out.println("problemType:"+problemType);
//      Assert.assertTrue(problemType != null);
    }

}
