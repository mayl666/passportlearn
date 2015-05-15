package com.sogou.upd.passport.service.app;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.app.PackageNameSign;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-2-16
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
public interface PackageNameSignService {
    PackageNameSign queryPackageInfoByName(String packageName) throws ServiceException;

}
