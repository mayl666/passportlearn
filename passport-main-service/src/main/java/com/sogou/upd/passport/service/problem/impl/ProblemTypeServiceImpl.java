package com.sogou.upd.passport.service.problem.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.problem.ProblemTypeDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.problem.ProblemType;
import com.sogou.upd.passport.service.problem.ProblemTypeService;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-4 Time: 下午3:25 To change this template
 * use File | Settings | File Templates.
 */
@Service
public class ProblemTypeServiceImpl implements ProblemTypeService {

    private static final Logger logger = LoggerFactory.getLogger(ProblemTypeServiceImpl.class);
    private static final String CACHE_PREFIX_ID_PROBLEMTYPE = CacheConstant.CACHE_PREFIX_ID_PROBLEMTYPE;
    private static final String CACHE_PROBLEM_LIST = "PROBLEMTYPELIST";
    @Autowired
    private ProblemTypeDAO problemTypeDAO;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public ProblemType getProblemTypeById(long id) throws ServiceException {
        try {
            List<ProblemType> list =  getProblemTypeList();
            if (!CollectionUtils.isEmpty(list)) {
                for(ProblemType problemType:list){
                    if(problemType.getId() == id){
                        return problemType;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new ServiceException();
        }
    }

    @Override
    public List<ProblemType> getProblemTypeList() throws ServiceException {
        List<ProblemType> list = null;
        try {
            String cacheKey = buildProblemTypeListKey(CACHE_PROBLEM_LIST);
            String listStr = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(listStr)) {
                list = problemTypeDAO.getProblemTypeList();
                if(!CollectionUtils.isEmpty(list)) {
                    String jsonResult = new ObjectMapper().writeValueAsString(list);
                    redisUtils.set(cacheKey, jsonResult);
                }
            } else {
                list = new ObjectMapper().readValue(listStr, new TypeReference<List<ProblemType>>() {
                });
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return list;
    }

    @Override
    public int  insertProblemType(ProblemType problemType) throws ServiceException {
        try {
            int row = problemTypeDAO.insertProblemType(problemType);
            if(row > 0) {
                String cacheKey = buildProblemTypeListKey(CACHE_PROBLEM_LIST);
                redisUtils.delete(cacheKey);
            }
            return row;
        } catch (Exception e) {
            throw new ServiceException();

        }
    }

    @Override
    public int  deleteProblemTypeByName(String name) throws ServiceException {
        try {
            int row = problemTypeDAO.deleteProblemTypeByName(name);
            if(row > 0) {
                String cacheKey = buildProblemTypeListKey(CACHE_PROBLEM_LIST);
                redisUtils.delete(cacheKey);
            }
            return row;
        } catch (Exception e) {
            throw new ServiceException();

        }
    }

    private String buildProblemTypeListKey(String id) {
        return CACHE_PREFIX_ID_PROBLEMTYPE + id;
    }
}
