package com.sogou.upd.passport.service.app.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.dao.app.PackageNameSignDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.app.PackageNameSign;
import com.sogou.upd.passport.service.app.PackageNameSignService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-2-16
 * Time: 下午2:18
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PackageNameSignServiceImpl implements PackageNameSignService {


    private Logger logger = LoggerFactory.getLogger(PackageNameSignServiceImpl.class);
    private static final String CACHE_PREFIX_PACKAGE_KEY = CacheConstant.CACHE_PREFIX_PACKAGENAME_PACKAGEINFO;
    private static LoadingCache<String, PackageNameSign> packageLocalCache = null;

    @Autowired
    private PackageNameSignDAO packageNameSignDAO;

    @Inject
    private RedisUtils redisUtils;

    public PackageNameSignServiceImpl() {
        packageLocalCache = CacheBuilder.newBuilder()
                .refreshAfterWrite(CacheConstant.CACHE_REFRESH_INTERVAL, TimeUnit.MINUTES)
                .build(new CacheLoader<String, PackageNameSign>() {
                    @Override
                    public PackageNameSign load(String key) throws Exception {
                        return loadPackageInfo(key);
                    }
                });
    }

    public PackageNameSign loadPackageInfo(String packageKey) throws ServiceException {
        PackageNameSign packageInfo;
        try {
            String packageName = StringUtils.substringAfter(packageKey, CACHE_PREFIX_PACKAGE_KEY);
            packageInfo = redisUtils.getObject(packageKey, PackageNameSign.class);
            if (null == packageInfo) {
                packageInfo = packageNameSignDAO.getPackageNameSignByname(packageName);
                if (null != packageInfo) {
                    addNameMapPackageinfoToCache(packageName, packageInfo);
                }
            }

        } catch (Exception e) {
            logger.warn("[App] service method loadPackageInfo error.{}", e);
            throw new ServiceException(e);
        }
        return packageInfo;
    }

    @Override
    public PackageNameSign queryPackageInfoByName(String packageName) throws ServiceException {
        PackageNameSign packageInfo = null;
        String cacheKey = CACHE_PREFIX_PACKAGE_KEY + packageName;

        if (packageLocalCache != null) {
            try {
                packageInfo = packageLocalCache.get(cacheKey);
            } catch (Exception e) {
                logger.warn("[App] queryAppConfigByClientId.{}", e);
                return null;
            }
        } else {
            logger.error("packageLocalCache initial,failed");
            packageInfo = loadPackageInfo(cacheKey);
        }
        return packageInfo;
    }

    private boolean addNameMapPackageinfoToCache(String packageName, PackageNameSign packageInfo) {
        boolean flag = true;
        try {
            String cacheKey = CACHE_PREFIX_PACKAGE_KEY + packageName;
            redisUtils.setWithinSeconds(cacheKey, packageInfo, DateAndNumTimesConstant.ONE_MONTH);
        } catch (Exception e) {
            flag = false;
            logger.error("[App] service method addNameMapPackageinfoToCache error.{}", e);
        }
        return flag;
    }
}
