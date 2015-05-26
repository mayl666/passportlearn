package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.JVMRandom;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 图片相关
 * User: mayan
 * Date: 13-8-5
 * Time: 下午4:22
 */
public class PhotoUtils {

    private static final HttpClient httpClient = SGHttpClient.httpClient;

    private String storageEngineURL;
    private int timeout = 5000;               // timeout毫秒数
    private String appid;
    private Map<String, String> sizeToAppIdMap = null;
    private List<String> listCDN = null;

    //图片名生成规则
    private static Random random = new JVMRandom();
    private static char[] chs = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    static final Logger logger = LoggerFactory.getLogger(PhotoUtils.class);

    public void init() {
        /**
         * 30x30     100140006
         * 50x50     100140007
         * 180x180   100140008
         */
        sizeToAppIdMap = new HashMap<String, String>();
        sizeToAppIdMap.put("30", "100140006");
        sizeToAppIdMap.put("50", "100140007");
        sizeToAppIdMap.put("180", "100140008");

        //初始化cdn列表
        listCDN = new ArrayList<String>();
        listCDN.add("http://img01.sogoucdn.com");
        listCDN.add("http://img02.sogoucdn.com");
        listCDN.add("http://img03.sogoucdn.com");
        listCDN.add("http://img04.sogoucdn.com");
    }

    //图片名生成
    public String generalFileName() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(chs[random.nextInt(62)]);
        }
        sb.append("_");
        sb.append(System.currentTimeMillis());
        return sb.toString();
    }

    //获取size对应的appId
    public String getAppIdBySize(String size) {
        return sizeToAppIdMap.get(size);
    }

    //获取所有图片尺寸
    public String[] getAllImageSize() {
        Map<String, String> map = getAllAppId();

        String[] imgSize = map.keySet().toArray(new String[map.size()]);

        return imgSize;
    }

    //获取所有appId
    public Map<String, String> getAllAppId() {
        return sizeToAppIdMap;
    }

    //随机获取cdn域名
    public String getCdnURL() {
        return listCDN.get(RandomUtils.nextInt(listCDN.size()));
    }

    public boolean uploadImg(String picNameInURL, byte[] picBytes, String webUrl, String uploadType) {

        MultipartEntity reqEntity = new MultipartEntity();

        switch (Integer.parseInt(uploadType)) {
            case 0://本地文件上传
                ByteArrayBody byteArrayBody = new ByteArrayBody(picBytes, picNameInURL);
                reqEntity.addPart("f1", byteArrayBody);
                try {
                    reqEntity.addPart("appid", new StringBody(appid));
                    reqEntity.addPart("sign_f1", new StringBody(picNameInURL));
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getMessage(), e);
                    return false;
                }
                break;
            case 1://网络文件上传
                try {
                    reqEntity.addPart("url1", new StringBody(webUrl));
                    reqEntity.addPart("appid", new StringBody(appid));
                    reqEntity.addPart("sign_url1", new StringBody(picNameInURL));
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getMessage(), e);
                    return false;
                }
                break;
        }
        HttpPost httpPost = new HttpPost(storageEngineURL);
        httpPost.setEntity(reqEntity);

        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            logger.warn("uploadImg ioException:" + e.getMessage(), e);
            return false;
        }
        int statusCode = response.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK != statusCode) {
            logger.error(response.getStatusLine().toString());
            return false;
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                String json = EntityUtils.toString(entity);
                logger.info("OP-IMG-PLATFORM-RESPONSE:\n" + json);
                JSONArray arr = JSONArray.fromObject(json);
                if (arr.size() != appid.split(",").length) {
                    return false;
                }

                for (int i = 0; i < arr.size(); i++) {
                    JSONObject single = arr.getJSONObject(i);
                    String status = single.getString("status");
                    if (!"0".equals(status)) {
                        return false;
                    }
                }
            } catch (IOException e) {
                logger.error("uploadImg ioException:" + e.getMessage(), e);
            } finally {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    //拼接url
    public String accessURLTemplate(String picNameInURL) {
        return new StringBuilder("%s").append("/app/a/%s").append("/").append(picNameInURL).toString();
    }

    //判断后缀
    public static boolean checkPhotoExt(byte[] buf) {
        String str = bytesToHexString(buf);

        String type = str.toUpperCase();
        if (type.contains("FFD8FF")) {  //jpg
            return true;
        } else if (type.contains("89504E47")) {   //png
            return true;
        } else if (type.contains("47494638")) {   //gif
            return true;
        } else if (type.contains("424D")) {  //bmp
            return true;
        }
        return false;
    }

    public Result obtainPhoto(String imageUrl, String size) {
        Result result = new APIResultSupport(false);
        try {
            String[] sizeArry = null;
            //获取size对应的appId
            if (!Strings.isNullOrEmpty(size)) {
                //检测是否是支持的尺寸
                sizeArry = size.split(",");

                if (ArrayUtils.isNotEmpty(sizeArry)) {
                    for (int i = 0; i < sizeArry.length; i++) {
                        if (Strings.isNullOrEmpty(getAppIdBySize(sizeArry[i]))) {
                            result.setCode(ErrorUtil.ERR_CODE_ERROR_IMAGE_SIZE);
                            return result;
                        }
                    }
                } else {
                    //为空获取所有的尺寸
                    sizeArry = getAllImageSize();
                }
                if (!Strings.isNullOrEmpty(imageUrl) && ArrayUtils.isNotEmpty(sizeArry)) {
                    result.setSuccess(true);
                    for (int i = 0; i < sizeArry.length; i++) {
                        //随机获取cdn域名
                        String cdnUrl = getCdnURL();
                        //获取图片尺寸
                        String clientId = getAppIdBySize(sizeArry[i]);

                        String photoURL = String.format(imageUrl, cdnUrl, clientId);
                        if (!Strings.isNullOrEmpty(photoURL)) {
                            result.setDefaultModel("img_" + sizeArry[i], photoURL);
                        }
                    }
                    return result;
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_OBTAIN_PHOTO);
                    return result;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setCode(ErrorUtil.ERR_CODE_OBTAIN_PHOTO);
            return result;
        }
        return result;
    }

    /**
     * byte数组转换成16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public void setStorageEngineURL(String storageEngineURL) {
        this.storageEngineURL = storageEngineURL;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getStorageEngineURL() {
        return storageEngineURL;
    }

    public int getTimeout() {
        return timeout;
    }
}
