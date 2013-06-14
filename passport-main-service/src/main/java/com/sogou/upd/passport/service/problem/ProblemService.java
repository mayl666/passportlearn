package com.sogou.upd.passport.service.problem;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.problem.Problem;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-4 Time: 下午3:24 To change this template
 * use File | Settings | File Templates.
 */
public interface ProblemService {

    /**
     * @param status
     * @param clientId
     * @param typeId
     * @param startDate
     * @param endDate
     * @param content
     * @param start
     * @param end
     * @return
     * @throws ServiceException
     */
    public List<Problem> queryProblemList(Integer status, Integer clientId, Integer typeId, Date startDate,
                                          Date endDate,String title,  String content, Integer start, Integer end) throws
            ServiceException;

    /**
     *
     * @param id
     * @param status
     * @return
     * @throws ServiceException
     */
    public int updateStatusById(long id, int status) throws ServiceException;

    public int insertProblem(Problem problem) throws ServiceException;
}
