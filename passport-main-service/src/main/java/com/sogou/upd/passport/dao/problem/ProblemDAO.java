package com.sogou.upd.passport.dao.problem;

import com.sogou.upd.passport.model.problem.Problem;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

import org.springframework.dao.DataAccessException;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午3:55 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface ProblemDAO {

  /**
   * 对应数据库表名称
   */
  String TABLE_NAME = " problem ";

  /**
   * 所有字段列表
   */
  String
      ALL_FIELD =
      " id, passport_id, client_id, sub_time, status, type_id, content,qq ";

  /**
   * 除了id之外所有字段列表
   */
  String
          ALL_FIELD_EXCEPTID =
          " passport_id, client_id, sub_time, status, type_id, content,qq ";
  /**
   * 值列表
   */
  String
      VALUE_FIELD_EXCEPTID =
      " :problem.passportId, :problem.clientId, :problem.subTime, :problem.status, :problem.typeId, :problem.content, :problem.qq ";

  /**
   * 修改字段列表
   */
  String
      UPDATE_FIELD =
      " passport_id = :problem.passportId, client_id = :problem.clientId, sub_time = :problem.subTime, status = :problem.status, type_id = :problem.typeId, content = :problem.content, qq = :problem.qq ";


  /**
   * 修改反馈状态
   */
  @SQL("update " +
       TABLE_NAME +
       " set status=:status where id=:id")
  public int updateStatusById(@SQLParam("id") long id,@SQLParam("status") int status ) throws DataAccessException;

  /**
   * 插入一条用户反馈
   */
  @SQL(
      "insert into " +
              TABLE_NAME +
              "("+ALL_FIELD_EXCEPTID+") "
      + "values ("+ VALUE_FIELD_EXCEPTID +")")
  public int insertProblem(@SQLParam("problem") Problem problem) throws DataAccessException;

  /**
   * 根据id获取Problem
   */
  @SQL("select " +
       ALL_FIELD +
       "from" +
       TABLE_NAME +
       " where id=:id")
  public Problem getProblemById(@SQLParam("id") long id) throws DataAccessException;

  /**
   * 根据拼接的字符串获取Problem
   */
  @SQL("select " +
       ALL_FIELD +
       " from " +
       TABLE_NAME +
       " where 1=1 "
       + "#if(:status != null){and status = :status }" //根据状态筛选
       +  "#if(:client_id != null){and client_id = :client_id }" //根据应用ID筛选
       +  "#if(:type_id != null){and type_id = :type_id }" //根据反馈类型ID筛选
       +  "#if(:start_date != null){and sub_time >= :start_date }" //根据开始和结束时间筛选
       +  "#if(:end_date != null){and sub_time <= :end_date }" //
       +  "#if(:content != null){AND UPPER(content) LIKE BINARY CONCAT('%',UPPER(:content),'%')}"//根据反馈内容模糊匹配
       + " order by sub_time DESC "
       + " #if((:start != null)&&(:end !=null)){ limit :start,:end }" )
  public List<Problem> queryProblemList(@SQLParam("status") Integer status,
                                      @SQLParam("client_id") Integer clientId,
                                      @SQLParam("type_id") Integer typeId,
                                      @SQLParam("start_date") Date startDate,
                                      @SQLParam("end_date") Date endDate,
                                      @SQLParam("content") String content,
                                      @SQLParam("start") Integer start,
                                      @SQLParam("end") Integer end)
          throws DataAccessException;

  /**
   * 根据passportId获取问题列表
   */
  @SQL("select count(id) from " +
       TABLE_NAME +
       " where 1=1 "
       + "#if(:status != null){and status = :status }" //根据状态筛选
       +  "#if(:client_id != null){and client_id = :client_id }" //根据应用ID筛选
       +  "#if(:type_id != null){and type_id = :type_id }" //根据反馈类型ID筛选
       +  "#if(:start_date != null){and sub_time >= :start_date }" //根据开始和结束时间筛选
       +  "#if(:end_date != null){and sub_time <= :end_date }" //
       +  "#if(:content != null){AND UPPER(content) LIKE BINARY CONCAT('%',UPPER(:content),'%')}")//根据反馈内容模糊匹配
  public int getProblemCount(@SQLParam("status") Integer status,
                                      @SQLParam("client_id") Integer clientId,
                                      @SQLParam("type_id") Integer typeId,
                                      @SQLParam("start_date") Date startDate,
                                      @SQLParam("end_date") Date endDate,
                                      @SQLParam("content") String content)
          throws DataAccessException;

    /**
     * 根据拼接的字符串获取Problem
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where passport_Id = :passportId"
            + " order by sub_time DESC "
            + " #if((:start != null)&&(:end !=null)){ limit :start,:end }" )
    public List<Problem> queryProblemListByPassportId(@SQLParam("passportId") String  passportId,
                                          @SQLParam("start") Integer start,
                                          @SQLParam("end") Integer end)
            throws DataAccessException;

}
