package com.sogou.upd.passport.web;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

  public static final
  String
      INTERNAL_HOST =
      "api.id.sogou.com.z.sogou-op.org;dev01.id.sogou.com;test01.id.sogou.com";

  protected static Logger logger = LoggerFactory.getLogger(BaseController.class);

  /**
   * 判断是否是服务端签名
   */
  protected boolean isServerSig(String client_signature, String signature) {
    if (StringUtils.isEmpty(signature)) {
      return false;
    }
    return true;
  }

  /**
   * 验证参数是否有空参数
   */
  protected boolean hasEmpty(String... args) {

    if (args == null) {
      return false;
    }

    Object[] argArray = getArguments(args);
    for (Object obj : argArray) {
      if (obj instanceof String && StringUtils.isEmpty((String) obj)) {
        return true;
      }
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

  protected static String getIp(HttpServletRequest request) {
    String sff = request.getHeader("X-Forwarded-For");// 根据nginx的配置，获取相应的ip
    if (sff == null) {
      return "";
    }
    String[] ips = sff.split(",");
    String realip = ips[0];
    return realip;
  }

  protected boolean isInternalRequest(HttpServletRequest request) {

    String host = request.getServerName();
    String[] hosts = INTERNAL_HOST.split(";");
    int i = Arrays.binarySearch(hosts, host);
    if (i >= 0) {
      return true;
    }
    return false;
  }

}
