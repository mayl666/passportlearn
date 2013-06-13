package com.sogou.upd.passport.service.problem.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.problem.ProblemAnswerDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.problem.ProblemAnswer;
import com.sogou.upd.passport.service.problem.ProblemAnswerService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-6-7
 * Time: 下午12:29
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProblemAnswerServiceImpl implements ProblemAnswerService {
    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceImpl.class);
    private static final String CACHE_PREFIX_PROBLEMID_PROBLEMANSWERSIZE = CacheConstant.CACHE_PREFIX_PROBLEMID_PROBLEMANSWERSIZE;
    private static final String CACHE_PREFIX_PROBLEMID_PROBLEMANSWERLIST = CacheConstant.CACHE_PREFIX_PROBLEMID_PROBLEMANSWERLIST;
    @Autowired
    private ProblemAnswerDAO problemAnswerDAO;
    @Autowired
    private RedisUtils redisUtils;


    @Override
    public int insertProblemAnswer(ProblemAnswer problemAnswer) throws ServiceException {
        try {
            int row = problemAnswerDAO.insertProblemAnswer(problemAnswer);
            if (row > 0) {
                //删除redis缓存
                long problemId = problemAnswer.getProblemId();
                String sizeCacheKey = CACHE_PREFIX_PROBLEMID_PROBLEMANSWERLIST + String.valueOf(problemId);
                redisUtils.delete(sizeCacheKey);

                String listCacheKey = CACHE_PREFIX_PROBLEMID_PROBLEMANSWERLIST+String.valueOf(problemId);
                redisUtils.delete(listCacheKey);
            }
            return row;
        } catch (Exception e) {
            throw new ServiceException();
        }
    }

    @Override
    public List<ProblemAnswer> getAnswerListByProblemId(long id) throws ServiceException{
        List<ProblemAnswer> list = null;
        try {
            String cacheKey = CACHE_PREFIX_PROBLEMID_PROBLEMANSWERLIST+String.valueOf(id);
            String listStr = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(listStr)) {
                list = problemAnswerDAO.getAnswerListByProblemId(id);
                if (list != null) {
                    String jsonResult = new ObjectMapper().writeValueAsString(list);
                    redisUtils.set(cacheKey, jsonResult);
                }
            } else {
                list = new ObjectMapper().readValue(listStr, new TypeReference<List<ProblemAnswer>>() {
                });
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return list;
    }

    @Override
    public int getAnswerSizeByProblemId(long problem_id)throws ServiceException{
        int count =0;
        try {
            String cacheKey = CACHE_PREFIX_PROBLEMID_PROBLEMANSWERSIZE+String.valueOf(problem_id);
            String sizeStr = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(sizeStr)) {
                count = problemAnswerDAO.getAnswerSizeByProblemId(problem_id);
                if (count != 0) {
                    String jsonResult = new ObjectMapper().writeValueAsString(count);
                    redisUtils.set(cacheKey, jsonResult);
                }
            } else {
                count = Integer.parseInt(sizeStr);
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return count;
    }

}
