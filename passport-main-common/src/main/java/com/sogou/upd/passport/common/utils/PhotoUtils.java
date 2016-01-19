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
    final private String appid = "100140008";
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
                    reqEntity.addPart("sign_f1", new StringBody(picNameInURL));
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getMessage(), e);
                    return false;
                }
                break;
            case 1://网络文件上传
                try {
                    reqEntity.addPart("url1", new StringBody(webUrl));
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
            response.getEntity().getContent().close();
        } catch (IOException e) {
            logger.warn("uploadImg ioException:" + e.getMessage(), e);
            return false;
        }
        int statusCode = response.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK != statusCode) {
            logger.error(response.getStatusLine().toString());
            return false;
        }
        return true;
    }

    //返回图像名称
    public String accessURLTemplate(String imgName) {
        return imgName;
    }

    String getImgName(String url) {
        if (url.indexOf("%s") >= 0) {
            return url.substring(url.lastIndexOf('/') + 1);
        }
        return url;
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

    public Result obtainPhoto(String imageUrl, String sizes) {
        Result result = new APIResultSupport(false);
        try {
            String[] sizeArry = sizes.split(",");

            //检测是否是支持的尺寸
            if (ArrayUtils.isEmpty(sizeArry)) {
                result.setCode(ErrorUtil.ERR_CODE_ERROR_IMAGE_SIZE);
                return result;
            }

            for (String sizeString : sizeArry) {
                int size = Integer.parseInt(sizeString);
                if (size < 30 || size > 180) {
                    result.setCode(ErrorUtil.ERR_CODE_ERROR_IMAGE_SIZE);
                    return result;
                }
            }

            if (Strings.isNullOrEmpty(imageUrl)) {
                result.setCode(ErrorUtil.ERR_CODE_OBTAIN_PHOTO);
                return result;
            }

            for (String sizeString : sizeArry) {
                int size = Integer.parseInt(sizeString);
                //随机获取cdn域名
                String cdnUrl = getCdnURL();

                String photoURL = String.format("%s/v2/thumb/resize/w/%d/h/%d/t/0/?appid=%s&name=%s",
                        cdnUrl, size, size, appid, getImgName(imageUrl));
                if (!Strings.isNullOrEmpty(photoURL)) {
                    result.setDefaultModel("img_" + size, photoURL);
                }
            }
            result.setSuccess(true);
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

    public String getStorageEngineURL() {
        return storageEngineURL;
    }

    public int getTimeout() {
        return timeout;
    }
}
