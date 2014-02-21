package com.sogou.upd.passport.dao.black;

import com.sogou.upd.passport.model.black.BlackItem;
import com.sogou.upd.passport.model.problem.Problem;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;
import org.springframework.dao.DataAccessException;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-2-17
 * Time: 下午4:23
 * To change this template use File | Settings | File Templates.
 */
@DAO
public interface BlackItemDAO {
    /**
     * 对应数据库表名称
     */
    String TABLE_NAME = " black_item ";

    /**
     * 所有字段列表
     */
    String
            ALL_FIELD = " id,sort,name,flag_success_limit,insert_time,duration_time,insert_server,scope ";

    /**
     * 新添用户
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(sort,name,flag_success_limit,insert_time,duration_time,insert_server,scope) "
                    + "values (:blackItem.sort,:blackItem.name,:blackItem.flagSuccessLimit,:blackItem.insertTime,:blackItem.durationTime,:blackItem.insertServer,:blackItem.scope)")
    public int insertBlackItem(@SQLParam("blackItem") BlackItem blackItem) throws DataAccessException;


    /**
     * 取当前的BlackItem列表
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where 1=1 "
            + "#if(:start_date != null){and insert_time >= :start_date }"
            + "#if(:end_date != null){and insert_time <= :end_date }"
            + "#if(:sort != null){and sort = :sort }"
            + "#if(:name != null){and name = :name }"
            + "#if(:flag_success_limit != null){and flag_success_limit = :flag_success_limit }"
            + "#if(:minTime != null){and duration_time >= :minTime }"
            + "#if(:maxTime != null){and duration_time <= :maxTime }"
            + "#if(:scope != null){and scope = :scope }"
        )
    public List<BlackItem> getBlackItemList(@SQLParam("start_date") Date startDate, @SQLParam("end_date") Date endDate,
                                            @SQLParam("sort") Integer sort,@SQLParam("name") String name,
                                            @SQLParam("flag_success_limit") Integer flag_success_limit,
                                            @SQLParam("minTime") Double minTime, @SQLParam("maxTime") Double maxTime,
                                            @SQLParam("scope") Integer scope) throws
            DataAccessException;

    /**
     * 根据id删除黑名单项
     * @param id
     * @return
     * @throws DataAccessException
     */
    @SQL("delete from" +
            TABLE_NAME +
            " where id=:id")
    public int delBlackItemById(@SQLParam("id") long id) throws
            DataAccessException;


    /**
     * 取当前的BlackItem列表
     */
    @SQL("select " +
            ALL_FIELD +
            " from " +
            TABLE_NAME +
            " where insert_time >= :start_date " +
            " and insert_time <= :end_date " +
            " and sort=:sort" +
            " and name=:name ")
    public BlackItem getBlackItemByName(@SQLParam("start_date") Date startDate, @SQLParam("end_date") Date endDate,
                                        @SQLParam("sort") int sort,@SQLParam("name") String name) throws
            DataAccessException;
}
