package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: mayan
 * Date: 13-8-6
 * Time: 下午3:37
 */
@Component("accountInfoManager")
public class AccountInfoManagerImpl implements AccountInfoManager {
    private static final Logger logger = LoggerFactory.getLogger(AccountInfoManagerImpl.class);

    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private RedisUtils redisUtils;

    public Result uploadImg(byte[] byteArr,String passportId,String type) {
        Result result = new APIResultSupport(false);
        try {
            //判断后缀是否符合要求
            if (!PhotoUtils.checkPhotoExt(byteArr)) {
                result.setCode(ErrorUtil.ERR_CODE_PHOTO_EXT);
                return result;
            }
            //获取图片名
            String imgName = photoUtils.generalFileName();
            // 上传到OP图片平台
            if (photoUtils.uploadImg(imgName, byteArr,null,type)) {
                String imgURL = photoUtils.accessURLTemplate(imgName);
                //更新缓存记录 临时方案 暂时这里写缓存，数据迁移后以 搜狗分支为主（更新库更新缓存）
                String cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_AVATARURL + passportId;
                redisUtils.set(cacheKey, imgURL);
                //更新图片映射
                cacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_AVATARURL_MAPPING + passportId;

                Map<String, String> map = redisUtils.hGetAll(cacheKey);
                redisUtils.hPut(cacheKey,"sgImg",imgURL);


                result.setSuccess(true);
                result.setDefaultModel("image",imgURL);
                result.setMessage("头像设置成功");
                return result;
            } else {
                result.setCode(ErrorUtil.ERR_CODE_UPLOAD_PHOTO);
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setCode(ErrorUtil.ERR_CODE_UPLOAD_PHOTO);
            return result;
        }
    }

    @Override
    public Result uploadDefaultImg(String webUrl,String clientId) {
        Result result = new APIResultSupport(false);
        try {
            //获取图片名
            String imgName = clientId+"_"+System.currentTimeMillis();
            // 上传到OP图片平台
            if (photoUtils.uploadImg(imgName, null,webUrl,"1")) {
                String imgURL = photoUtils.accessURLTemplate(imgName);
                //更新缓存记录 临时方案 暂时这里写缓存，数据迁移后以 搜狗分支为主（更新库更新缓存）
                String cacheKey="SP.PASSPORTID:IMAGE_"+clientId;
                redisUtils.set(cacheKey,imgURL);

                result.setSuccess(true);
                result.setDefaultModel("image",imgURL);
                result.setMessage("头像设置成功");
                return result;
            } else {
                result.setCode(ErrorUtil.ERR_CODE_UPLOAD_PHOTO);
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setCode(ErrorUtil.ERR_CODE_UPLOAD_PHOTO);
            return result;
        }
    }

    @Override
    public Result obtainPhoto(String passportId, String size) {
        Result result = new APIResultSupport(false);
        try {
            String []sizeArry=null;
            //获取size对应的appId
            if(!Strings.isNullOrEmpty(size)){
                //检测是否是支持的尺寸
                sizeArry=size.split(",");

                if(ArrayUtils.isNotEmpty(sizeArry)){
                    for(int i=0;i<sizeArry.length;i++){
                        if(Strings.isNullOrEmpty(photoUtils.getAppIdBySize(sizeArry[i]))){
                            result.setCode(ErrorUtil.ERR_CODE_ERROR_IMAGE_SIZE);
                            return result;
                        }
                    }
                } else {
                    //为空获取所有的尺寸
                   sizeArry=photoUtils.getAllImageSize();
                }

                String cacheKey="SP.PASSPORTID:IMAGE_"+passportId;
                String image=redisUtils.get(cacheKey);

                if(!Strings.isNullOrEmpty(image) && ArrayUtils.isNotEmpty(sizeArry)){
                    result.setSuccess(true);
                    for (int i=0;i<sizeArry.length;i++){
                        //随机获取cdn域名
                        String cdnUrl=photoUtils.getCdnURL();
                        //获取图片尺寸
                        String clientId=photoUtils.getAppIdBySize(sizeArry[i]);

                        String photoURL =String.format(image, cdnUrl, clientId);
                        if(!Strings.isNullOrEmpty(photoURL)){
                            result.setDefaultModel("img_"+sizeArry[i],photoURL);
                        }
                    }
                    return result;
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_OBTAIN_PHOTO);
                    return result;
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            result.setCode(ErrorUtil.ERR_CODE_OBTAIN_PHOTO);
            return result;
        }
        return result;
    }
}
