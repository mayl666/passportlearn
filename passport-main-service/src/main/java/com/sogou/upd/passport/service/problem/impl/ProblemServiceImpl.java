package com.sogou.upd.passport.service.problem.impl;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.dao.problem.ProblemDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.service.problem.ProblemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-4 Time: 下午3:25 To change this template
 * use File | Settings | File Templates.
 */
@Service
public class ProblemServiceImpl implements ProblemService {

    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceImpl.class);
    private static final String CACHE_PREFIX_PASSPORTID_PROBLEMList = CacheConstant.CACHE_PREFIX_PASSPORTID_PROBLEMLIST;
    @Autowired
    private ProblemDAO problemDAO;

    @Override
    public List<Problem> queryProblemList(Integer status, Integer clientId, Integer typeId, Date startDate,
                                          Date endDate, String title, String content, Integer start, Integer end) throws ServiceException {
        return problemDAO.queryProblemList(status, clientId, typeId, startDate, endDate, title,content, start,
                end);
    }
    @Override
    public int getProblemCount(Integer status,Integer clientId,Integer typeId,Date startDate,Date endDate,String title,  String content)throws ServiceException {
        return problemDAO.getProblemCount(status,clientId,typeId,startDate,endDate,title,content);
    }
    @Override
    public int updateStatusById(long id, int status) throws ServiceException {
        try {
            int result = problemDAO.updateStatusById(id, status);
            return  result;
        }catch (Exception e) {
            throw new ServiceException();
        }
    }

    @Override
    public int insertProblem(Problem problem) throws ServiceException {
        try {
            int result =  problemDAO.insertProblem(problem);
            return result;
        } catch (Exception e) {
            throw new ServiceException();
        }
    }
}
