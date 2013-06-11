package com.sogou.upd.passport.service.problem.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.problem.ProblemDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.model.problem.ProblemType;
import com.sogou.upd.passport.service.problem.ProblemService;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<Problem> queryProblemList(Integer status, Integer clientId, Integer typeId, Date startDate,
                                          Date endDate, String content, Integer start, Integer end) throws ServiceException {
        return problemDAO.queryProblemList(status, clientId, typeId, startDate, endDate, content, start,
                end);
    }

    @Override
    public List<Problem> queryProblemListByPassportId(String passportId, Integer start, Integer end) throws ServiceException {
        List<Problem> list = null;
        try {
            String cacheKey = buildProblemListKey(passportId);
            String listStr = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(listStr)) {
                list = problemDAO.queryProblemListByPassportId(passportId, start, end);
                if (list != null) {
                    String jsonResult = new ObjectMapper().writeValueAsString(list);
                    redisUtils.set(cacheKey, jsonResult);
                }
            } else {
                list = new ObjectMapper().readValue(listStr, new TypeReference<List<Problem>>() {
                });
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return list;
    }

    private String buildProblemListKey(String id) {
        return CACHE_PREFIX_PASSPORTID_PROBLEMList + id;
    }

    @Override
    public int updateStatusById(long id, int status) throws ServiceException {
        try {
            int result = problemDAO.updateStatusById(id, status);
            if(result >0){
                Problem problem = problemDAO.getProblemById(id);
                //删除该用户的c反馈缓存
                String cacheKey = buildProblemListKey(problem.getPassportId());
                redisUtils.delete(cacheKey);
            }
            return  result;
        }catch (Exception e) {
            throw new ServiceException();
        }
    }

    @Override
    public int insertProblem(Problem problem) throws ServiceException {
        try {
            int result =  problemDAO.insertProblem(problem);
            if(result >0){
                //删除该用户的c反馈缓存
                String cacheKey = buildProblemListKey(problem.getPassportId());
                redisUtils.delete(cacheKey);
            }
            return result;
        } catch (Exception e) {
            throw new ServiceException();
        }
    }
}
