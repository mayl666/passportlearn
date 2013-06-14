package com.sogou.upd.passport.dao.problem;

import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.dao.problem.ProblemDAO;
import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;

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
public class ProblemDAOTest extends BaseDAOTest {

    @Autowired
    private ProblemDAO problemDAO;

    @Before
    public void init() {
      Problem problem = new Problem();
      problem.setPassportId("18612532596@sohu.com");
      problem.setStatus(0);
      problem.setClientId(CLIENT_ID);
      problem.setSubTime(new Date());
      problem.setTypeId(1);
        problem.setTitle("标题");
      problem.setContent("我的幼儿园");
        problem.setEmail("jiamengchen@126.com");
       // problem.setQq("1037113048");
      int row = problemDAO.insertProblem(problem);
      Assert.assertTrue(row != 0);
    }

    @After
    public void end() {
    }

    /**
     * 测试单条记录查询
     */
    @Test
    public void testUpdateStatusById() {
        int row = problemDAO.updateStatusById(1, 2);
        Assert.assertTrue(row >0);
    }

  /**
   * 测试单条记录查询
   */
  @Test
  public void testGetProblemBySqlStr() {
//    String sqlStr = " status = 0 and sub_time > '2013-06-04' AND sub_time < '2013-06-05' and client_id = 1001 and type_id = 1 and content = '%游戏%'";
    Integer  status = 0;
    Integer  clientId = 1001;
    Integer  typeId = 1;
    Date startDate = DateUtil.parse("2013-06-03",DateUtil.DATE_FMT_3) ;
    Date endDate = DateUtil.parse("2013-06-18",DateUtil.DATE_FMT_3) ;
    String content = null;
      String title ="标题";
    Integer  start = 1;
    Integer  end = 100;
    List<Problem> list
            = problemDAO.queryProblemList(status,clientId,typeId,startDate,endDate,title,content,start,end);
    System.out.println("list.size():"+list.size());
    Assert.assertTrue(list.size() >0);
  }

  @Test
  public void testGetProblemCount() {
//    String sqlStr = " status = 0 and sub_time > '2013-06-04' AND sub_time < '2013-06-05' and client_id = 1001 and type_id = 1 and content = '%游戏%'";
    Integer  status = 1;
    Integer  clientId = 1001;
    Integer  typeId = 1;
    Date startDate = DateUtil.parse("2013-06-03",DateUtil.DATE_FMT_3) ;
    Date endDate = DateUtil.parse("2013-06-05",DateUtil.DATE_FMT_3) ;
    String content = "游戏";
    int count = problemDAO.getProblemCount(status,clientId,typeId,startDate,endDate,content);
    System.out.println("count:"+count);
//    Assert.assertTrue(list.size() >0);
  }

    /**
     * 测试单条记录查询
     */
    @Test
    public void testQueryProblemListByPassportId() {
        List<Problem> list = problemDAO.queryProblemListByPassportId("18612532596@sohu.com",1,2);
        System.out.println("size:"+list.size());
    }


}
