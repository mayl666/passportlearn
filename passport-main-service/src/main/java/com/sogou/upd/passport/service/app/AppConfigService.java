package com.sogou.upd.passport.service.app;

import com.sogou.upd.passport.common.exception.ApplicationException;
import com.sogou.upd.passport.model.app.AppConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-25
 * Time: 下午11:41
 * To change this template use File | Settings | File Templates.
 */
@Service
public interface AppConfigService {

    public long getMaxAppid();

    public AppConfig regApp(AppConfig app);

    public AppConfig getAppConfig(int appKey);

    public int getAccessTokenExpiresIn(int appKey);

    public int getRefreshTokenExpiresIn(int appKey);

}
