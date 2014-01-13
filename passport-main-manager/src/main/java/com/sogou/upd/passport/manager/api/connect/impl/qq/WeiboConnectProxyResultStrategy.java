package com.sogou.upd.passport.manager.api.connect.impl.qq;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.connect.AbstractConnectProxyResultStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;

/**
 * weibo平台统一结果的实现类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午3:08
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WeiboConnectProxyResultStrategy extends AbstractConnectProxyResultStrategy {

    @Override
    public Result buildCommonResultByPlatform(HashMap<String, Object> maps) {
        Result result = new APIResultSupport(false);
        String ret = maps.get("ret").toString();
        if (ret.equals(ErrorUtil.SUCCESS)) {
            if (maps.containsKey("data")) {
                //封装QQ返回请求正确的结果，返回结果中不包含ret或者包含ret且ret值为0的结果封装
                HashMap<String, Object> data;
                HashMap<String, Object> mapsWeibo = (HashMap<String, Object>) maps.get("data");
                if (!CollectionUtils.isEmpty(mapsWeibo)) {
                    result.setSuccess(true);
                    result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.SUCCESS));
                    data = super.convertToFormatMap(mapsWeibo);
                    result.setModels(data);
                }
            }
        }
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
