package com.sogou.upd.passport.manager.api.connect.impl.qq;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.connect.AbstractConnectProxyResultStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;

/**
 * mail平台统一结果的实现类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午3:09
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MailConnectProxyResultStrategy extends AbstractConnectProxyResultStrategy {

    @Override
    public Result buildCommonResultByPlatform(HashMap<String, Object> maps) {
        Result result = new APIResultSupport(false);
        if (maps.containsKey("ret") && !ErrorUtil.SUCCESS.equals(maps.get("ret"))) {
            result.setCode(ErrorUtil.ERR_CODE_CONNECT_FAILED);
            result.setMessage((String) maps.get("msg"));
        } else {
            if (maps.containsKey("result")) {
                HashMap<String, Object> resultMap = (HashMap<String, Object>) maps.get("result");
                if (!CollectionUtils.isEmpty(resultMap)) {
                    HashMap<String, Object> dataMail = new HashMap<>();
                    int size = Integer.parseInt(resultMap.get("Count").toString());
                    dataMail.put("count", size);
                    List<Object> emailList = (List<Object>) resultMap.get("UnreadMailCountData");
                    HashMap<String, Object>[] mapArray = new HashMap[size];
                    HashMap<String, Object> item;
                    for (int i = 0; i < size; i++) {
                        if (!CollectionUtils.isEmpty(emailList)) {
                            HashMap<String, Object> mail = (HashMap<String, Object>) emailList.get(i);
                            if (!CollectionUtils.isEmpty(mail)) {
                                result.setCode("0");
                                result.setSuccess(true);
                                result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.SUCCESS));
                                item = super.convertToFormatMap(mail);
                                mapArray[i] = item;
                            }
                        }
                    }
                    dataMail.put("unreadMailCountData", mapArray);
                    result.setModels(dataMail);
                }
            }
        }
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
