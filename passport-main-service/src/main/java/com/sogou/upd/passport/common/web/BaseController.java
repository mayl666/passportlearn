package com.sogou.upd.passport.common.web;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonParameters;
import com.sogou.upd.passport.common.utils.AESencrp;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class BaseController {

	public static final String INTERNAL_HOST = "api.id.sogou.com.z.sogou-op.org;dev01.id.sogou.com;test01.id.sogou.com";
	
	protected static Logger logger = LoggerFactory.getLogger(BaseController.class);

	/**
	 * 判断是否是服务端签名
	 * @param client_signature
	 * @param signature
	 * @return
	 */
	protected boolean isServerSig(String client_signature, String signature) {
		if (StringUtils.isEmpty(signature)) { return false; }
		return true;
	}

	/**
	 * 验证参数是否有空参数
	 * @param args
	 * @return
	 */
	protected boolean hasEmpty(String... args) {

		if (args == null) { return false; }

		Object[] argArray = getArguments(args);
		for (Object obj : argArray) {
			if (obj instanceof String && StringUtils.isEmpty((String) obj)) return true;
		}
		return false;
	}

	private Object[] getArguments(Object[] varArgs) {
		if (varArgs.length == 1 && varArgs[0] instanceof Object[]) {
			return (Object[]) varArgs[0];
		} else {
			return varArgs;
		}
	}

	protected Map<String, Object> buildSuccess(String msg, Map<String, Object> data) {
		Map<String, Object> retMap = Maps.newHashMap();
		retMap.put(CommonParameters.RESPONSE_STATUS, "0");
		retMap.put(CommonParameters.RESPONSE_STATUS_TEXT, msg);
		retMap.put(CommonParameters.RESPONSE_DATA, data == null ? Collections.emptyMap() : data);
		return retMap;
	}

	/**
	 * 构造statusText为空的成功结果json
	 * @param data
	 * @return
	 */
	protected Map<String, Object> buildSuccess(Map<String, Object> data) {
		return buildSuccess("", data);
	}

	/**
	 * 构造statusText为空，data为空的成功结果json
	 * @return
	 */
	protected Map<String, Object> buildSuccess() {
		return buildSuccess("", null);
	}

	/**
	 * 验证签名合法性，十分钟之内就是有效的访问签名
	 * @param secret
	 * @param signature
	 * @param appid
	 * @return
	 */
	protected boolean checkSignature(String secret, String signature, String appid) {
		boolean valid = AESencrp.checkSignature(appid, secret, signature);
		return valid;
	}

	protected String generateAccessToken(String base, String appSecret) throws Exception {
		String accessToken = AESencrp.encrypt(appSecret, base);
		return accessToken;
	}

	protected String composeBaseAccessToken(String appid, String openid, long time, String version) {
		String base = appid + "|" + openid + "|" + time + "|" + version;
		return base;
	}

	protected static String getIp(HttpServletRequest request) {
		String sff = request.getHeader("X-Forwarded-For");// 根据nginx的配置，获取相应的ip
		if(sff == null)
			return "";
		String[] ips = sff.split(",");
		String realip = ips[0];
		return realip;
	}
	
	protected Object checkRequiredValidParam(String access_token, String openid,
			String client_signature, String signature) {
		if (hasEmpty(signature) && hasEmpty(client_signature)) { return ErrorUtil
				.buildError(ErrorUtil.ERR_CODE_COM_REQURIE); }
		if (!hasEmpty(client_signature) && hasEmpty(access_token)) { return ErrorUtil
				.buildError(ErrorUtil.ERR_CODE_COM_REQURIE); }
		if (!hasEmpty(signature) && hasEmpty(openid)) { return ErrorUtil
				.buildError(ErrorUtil.ERR_CODE_COM_REQURIE); }
		return null;
	}

	protected boolean isInternalRequest(HttpServletRequest request) {
		
		String host = request.getServerName();
        String [] hosts=INTERNAL_HOST.split(";");
        int i=Arrays.binarySearch(hosts,host);
        if(i>=0){
            return true;
        }
		return false;
	}

}
