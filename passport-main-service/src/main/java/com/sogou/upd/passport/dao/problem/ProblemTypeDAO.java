package com.sogou.upd.passport.dao.problem;

import com.sogou.upd.passport.model.problem.ProblemType;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午3:55 To change this template
 * use File | Settings | File Templates.
 */
@DAO
public interface ProblemTypeDAO {

    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " problem_type ";

    /**
     * 所有字段列表
     */
    String
            ALL_FIELD =
            " id, type_name ";

    /**
     * 除了id之外所有字段列表
     */
    String
            ALL_FIELD_EXCEPTID =
            " type_name ";
    /**
     * 值列表
     */
    String
            VALUE_FIELD_EXCEPTID =
            " :problemType.typeName ";


    /**
     * 插入一条用户反馈类型
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(" + ALL_FIELD_EXCEPTID + ") "
                    + "values (" + VALUE_FIELD_EXCEPTID + ")")
    public int insertProblemType(@SQLParam("problemType") ProblemType problemType) throws DataAccessException;

    @SQL("delete from" +
            TABLE_NAME +
            " where id=:id")
    public int deleteProblemTypeById(@SQLParam("id") long id) throws
            DataAccessException;

    @SQL("delete from" +
            TABLE_NAME +
            " where type_name=:typeName")
    public int deleteProblemTypeByName(@SQLParam("typeName") String typeName) throws
            DataAccessException;
    /**
     * 根据id获取Problem类型
     */
    @SQL("select "+ALL_FIELD+ " from "
            + TABLE_NAME +
            " where id=:id")
    public ProblemType getProblemTypeById(@SQLParam("id") long id) throws DataAccessException;

    /**
     * 获取Problem类型列表
     */
    @SQL("select "+ALL_FIELD+ " from "
            + TABLE_NAME)
    public List<ProblemType> getProblemTypeList() throws DataAccessException;


}
