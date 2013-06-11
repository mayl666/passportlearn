package com.sogou.upd.passport.service.problem.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.model.ActiveEmail;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.problem.ProblemDAO;
import com.sogou.upd.passport.dao.problem.ProblemTypeDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.problem.Problem;
import com.sogou.upd.passport.model.problem.ProblemType;
import com.sogou.upd.passport.service.problem.ProblemService;
import com.sogou.upd.passport.service.problem.ProblemTypeService;

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
        ProblemType problemType = null;
        try {
            String cacheKey = buildProblemTypeListKey(String.valueOf(id));

            problemType = redisUtils.getObject(cacheKey, ProblemType.class);
            if (problemType == null) {
                problemType = problemTypeDAO.getProblemTypeById(id);
                if (problemType != null) {
                    redisUtils.set(cacheKey, problemType);
                }
            }
        } catch (Exception e) {
            throw new ServiceException();
        }
        return problemType;
    }

    @Override
    public List<ProblemType> getProblemTypeList() throws ServiceException {
        List<ProblemType> list = null;
        try {
            String cacheKey = buildProblemTypeListKey(CACHE_PROBLEM_LIST);
            String listStr = redisUtils.get(cacheKey);
            if (Strings.isNullOrEmpty(listStr)) {
                list = problemTypeDAO.getProblemTypeList();
                if (list != null) {
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

    private String buildProblemTypeListKey(String id) {
        return CACHE_PREFIX_ID_PROBLEMTYPE + id;
    }
}
