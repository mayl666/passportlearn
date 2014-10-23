package com.sogou.upd.passport.manager.api.connect.impl.qq;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.connect.AbstractConnectProxyResultStrategy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * qzone平台统一结果的实现类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午3:07
 * To change this template use File | Settings | File Templates.
 */
@Component
public class QzoneConnectProxyResultStrategy extends AbstractConnectProxyResultStrategy {

    private static final Logger logger = LoggerFactory.getLogger(QzoneConnectProxyResultStrategy.class);

    /**
     * 腾讯接口返回的ret
     * <p/>
     * 10001 err_get_result
     * #10002 err_file_found
     * 0     err_file_found
     * 10003 err_proxyfail
     * 10004 err_parameter
     * 10005 err_5mlimited
     *
     * @param maps 腾讯接口返回结果
     * @return
     */
    @Override
    public Result buildCommonResultByPlatform(HashMap<String, Object> maps) {
        Result result = new APIResultSupport(false);
        String ret = String.valueOf(maps.get("ret"));
        String msg = StringUtils.EMPTY;
        if (maps.containsKey("ret") && !ErrorUtil.SUCCESS.equals(ret)) {
            //腾讯会针对输入法用户词库大于5M的做单独处理，返回10005表示此种情况，在此做特殊处理
            if ("10005".equals(ret)) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_USER_DICTIONARY_LARGE_THAN_5M);
            } else {
                if (maps.containsKey("msg") && maps.get("msg") != null) {
                    msg = String.valueOf(maps.get("msg"));
                }
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
                result.setMessage(msg);
                logger.error("qqResult return. ret [" + ret + "]" + " and msg [" + msg + "]");
            }
        } else {
            //封装QQ返回请求正确的结果，返回结果中不包含ret或者包含ret且ret值为0的结果封装
            HashMap<String, Object> data;
            //QQ空间未读数结果封装
            if (ErrorUtil.SUCCESS.equals(ret)) {
                result.setCode("0");
                result.setSuccess(true);
                result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.SUCCESS));
                data = convertToFormatMap(maps);
                result.setModels(data);
            }
        }
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HashMap<String, Object> convertToFormatMap(HashMap<String, Object> formatMaps) {
        HashMap<String, Object> data;
        data = super.convertToFormatMap(formatMaps);
        data.remove("ret");
        data.remove("msg");
        return data;  //To change body of implemented methods use File | Settings | File Templates.
    }
}


