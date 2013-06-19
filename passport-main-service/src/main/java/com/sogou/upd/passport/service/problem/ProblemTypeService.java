package com.sogou.upd.passport.service.problem;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.problem.ProblemType;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-4 Time: 下午3:24 To change this template
 * use File | Settings | File Templates.
 */
public interface ProblemTypeService {

    /**
     * @param id
     * @return
     * @throws ServiceException
     */
    public ProblemType getProblemTypeById(long id) throws ServiceException;

    /**
     *获取问题烈性列表
     * @return
     * @throws ServiceException
     */
    public List<ProblemType> getProblemTypeList() throws ServiceException;

    /**
     * 插入一条反馈类型
     * @param problemType
     * @return
     * @throws ServiceException
     */
    public int  insertProblemType(ProblemType problemType) throws ServiceException;

    /**
     * 删除一条反馈类型
     * @param id
     * @return
     * @throws ServiceException
     */
    public int  deleteProblemTypeById(long id) throws ServiceException;
}
