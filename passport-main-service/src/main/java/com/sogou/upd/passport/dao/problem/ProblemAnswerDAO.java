package com.sogou.upd.passport.dao.problem;

import com.sogou.upd.passport.model.problem.ProblemAnswer;

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
public interface ProblemAnswerDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " problem_answer ";

    /**
     * 所有字段列表
     */
    String
            ALL_FIELD =
            " id, problem_id, ans_passport_id, ans_content, ans_time ";

    /**
     * 除了id之外所有字段列表
     */
    String
            ALL_FIELD_EXCEPTID =
            " problem_id, ans_passport_id, ans_content, ans_time ";
    /**
     * 值列表
     */
    String
            VALUE_FIELD_EXCEPTID =
            " :problem_answer.problemId, :problem_answer.ansPassportId, :problem_answer.ansContent, :problem_answer.ansTime ";


    /**
     * 插入一条反馈回答
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(" + ALL_FIELD_EXCEPTID + ") "
                    + "values (" + VALUE_FIELD_EXCEPTID + ")")
    public int insertProblemAnswer(@SQLParam("problem_answer") ProblemAnswer problemAnswer) throws DataAccessException;

    /**
     * 根据id获取Problem
     */
    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where id=:id")
    public ProblemAnswer getProblemAnswerById(@SQLParam("id") long id) throws DataAccessException;

    /**
     * 根据反馈ID取回答列表
     */
    @SQL("select" +
            ALL_FIELD +
            "from" +
            TABLE_NAME +
            " where  problem_id = :problem_id order by ans_time limit 0,10" )
    public List<ProblemAnswer> getAnswerListByProblemId(@SQLParam("problem_id") long problem_id)
            throws DataAccessException;

    /**
     * 根据反馈ID取回答数量
     */
    @SQL("select count(id) " +
            " from " +
            TABLE_NAME +
            " where  problem_id = :problem_id ")//
    public int getAnswerSizeByProblemId(@SQLParam("problem_id") long problem_id)
            throws DataAccessException;

}
