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
            ALL_FIELD = " id,name_sort,name,limit_sort,insert_time,duration_time,insert_server,scope ";

    /**
     * 新添用户
     */
    @SQL(
            "insert into " +
                    TABLE_NAME +
                    "(name_sort,name,limit_sort,insert_time,duration_time,insert_server,scope) "
                    + "values (:blackItem.nameSort,:blackItem.name,:blackItem.limitSort,:blackItem.insertTime,:blackItem.durationTime,:blackItem.insertServer,:blackItem.scope)")
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
            + "#if(:name_sort != null){and name_sort = :name_sort }"
            + "#if(:name != null){and name = :name }"
            + "#if(:limit_sort != null){and limit_sort = :limit_sort }"
            + "#if(:minTime != null){and duration_time >= :minTime }"
            + "#if(:maxTime != null){and duration_time <= :maxTime }"
            + "#if(:scope != null){and scope = :scope }"
            + " order by insert_time DESC "
            + " #if((:start != null)&&(:end !=null)){ limit :start,:end }"
        )
    public List<BlackItem> getBlackItemList(@SQLParam("start_date") Date startDate, @SQLParam("end_date") Date endDate,
                                            @SQLParam("name_sort") Integer name_sort,@SQLParam("name") String name,
                                            @SQLParam("limit_sort") Integer limit_sort,
                                            @SQLParam("minTime") Double minTime, @SQLParam("maxTime") Double maxTime,
                                            @SQLParam("scope") Integer scope,
                                            @SQLParam("start") Integer start,
                                            @SQLParam("end") Integer end
                                            ) throws DataAccessException;


    /**
     * 取当前的BlackItem列表
     */
    @SQL("select count(id) from  "
            + TABLE_NAME +
            " where 1=1 "
            + "#if(:start_date != null){and insert_time >= :start_date }"
            + "#if(:end_date != null){and insert_time <= :end_date }"
            + "#if(:name_sort != null){and name_sort = :name_sort }"
            + "#if(:name != null){and name = :name }"
            + "#if(:limit_sort != null){and limit_sort = :limit_sort }"
            + "#if(:minTime != null){and duration_time >= :minTime }"
            + "#if(:maxTime != null){and duration_time <= :maxTime }"
            + "#if(:scope != null){and scope = :scope }"
            + " order by insert_time DESC "
            + " #if((:start != null)&&(:end !=null)){ limit :start,:end }"
    )
    public int getBlackItemCount(@SQLParam("start_date") Date startDate, @SQLParam("end_date") Date endDate,
                                            @SQLParam("name_sort") Integer name_sort,@SQLParam("name") String name,
                                            @SQLParam("limit_sort") Integer limit_sort,
                                            @SQLParam("minTime") Double minTime, @SQLParam("maxTime") Double maxTime,
                                            @SQLParam("scope") Integer scope,
                                            @SQLParam("start") Integer start,
                                            @SQLParam("end") Integer end
    ) throws DataAccessException;

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
            " and limit_sort=:limit_sort" +
            " and name=:name ")
    public BlackItem getBlackItemByName(@SQLParam("start_date") Date startDate, @SQLParam("end_date") Date endDate,
                                        @SQLParam("limit_sort") int limit_sort,@SQLParam("name") String name) throws
            DataAccessException;
}
