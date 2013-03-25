package com.sogou.upd.passport.web;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.exception.ApplicationException;
import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.parameter.CommonParameters;
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
	
	protected boolean isInternalRequest(HttpServletRequest request) {
		
		String host = request.getServerName();
        String [] hosts=INTERNAL_HOST.split(";");
        int i=Arrays.binarySearch(hosts,host);
        if(i>=0){
            return true;
        }
		return false;
	}

    /**
     * 处理Controller抛出的异常
     * ApplicationException-参数异常
     * ProblemException-接口调用问题异常
     * Exception-服务器异常
     * @param e
     * @param uri 接口URI
     * @param connectName 可以为null
     * @return
     */
    public Map<String, Object> handleException(Throwable e, String connectName, String uri) {

        if (e instanceof ApplicationException) {
            if (logger.isDebugEnabled()) {
                logger.debug("[" + uri + "] Provider:" + connectName
                        + " request params error! ErrorMessage:" + e.getMessage());
            }
            return ErrorUtil.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, e.getMessage());
        } else if (e instanceof ProblemException) {
            String desc = ((ProblemException) e).getDescription();
            String error = ((ProblemException) e).getError();
            if (StringUtils.isEmpty(desc)) {
                desc = ErrorUtil.ERR_CODE_MSG_MAP.get(error);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("[" + uri + "] Provider:" + connectName + " Exception; Error:"
                        + error + ", Message:" + desc);
            }
            return ErrorUtil.buildError(error, desc);
        } else {
            logger.error("HasError!, uri:[" + uri + "]", e);
            return ErrorUtil.buildExceptionError(e.getMessage());
        }

    }

}
