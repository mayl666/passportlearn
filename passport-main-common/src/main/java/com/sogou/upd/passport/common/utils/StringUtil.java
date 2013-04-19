package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.parameter.CommonParameters;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StringUtil {

  static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

  public static boolean isEmpty(String str) {
    return StringUtils.isEmpty(str) || str.equalsIgnoreCase("null");
  }

  /**
   * 如果字符串是<code>null</code>或空字符串<code>""</code>，则返回指定默认字符串，否则返回字符串本身。
   * <pre>
   * StringUtil.defaultIfEmpty(null, "default")  = "default"
   * StringUtil.defaultIfEmpty("", "default")    = "default"
   * StringUtil.defaultIfEmpty("  ", "default")  = "  "
   * StringUtil.defaultIfEmpty("bat", "default") = "bat"
   * </pre>
   *
   * @param str        要转换的字符串
   * @param defaultStr 默认字符串
   * @return 字符串本身或指定的默认字符串
   */
  public static String defaultIfEmpty(String str, String defaultStr) {
    return ((str == null) || (str.length() == 0)) ? defaultStr : str;
  }

  /**
   * 字符串数组用分隔符拼接成字符串
   */
  public static String joinStrArray(String[] array, String split) {
    String tmp = "";
    for (String ss : array) {
      tmp += ss + split;
    }
    String str = tmp.substring(0, tmp.length() - split.length());
    return str;
  }

  /**
   * 多个字符串用分隔符拼接成字符串
   */
  public static String joinStrings(String split, String... strs) {
    List<String> strList = Arrays.asList(strs);
    String tmp = "";
    for (String ss : strList) {
      tmp += ss + split;
    }
    String str = tmp.substring(0, tmp.length() - split.length());
    return str;
  }

  /**
   * 多个字符串用分隔符拼接成字符串
   */
  public static <T> String joinCollection(Collection<T> collection, String split) {
    StringBuffer tmp = new StringBuffer();
    for (T obj : collection) {
      tmp.append(obj).append(split);
    }
    String str = tmp.deleteCharAt(tmp.length() - 1).toString();
    return str;
  }


  /**
   * 将参数URLEncode为UTF-8
   */
  public static String urlEncodeUTF8(String params) throws UnsupportedEncodingException {
    String en = URLEncoder.encode(params, CommonParameters.DEFAULT_CONTENT_CHARSET);
    en = en.replace("+", "%20");
    en = en.replace("*", "%2A");
    return en;
  }

  /**
   * 将参数URLEncode，默认为UTF-8
   */
  public static String encode(String params, String encoding) {
    try {
      String
          en =
          URLEncoder.encode(params,
                            encoding != null ? encoding : CommonParameters.DEFAULT_CONTENT_CHARSET);
      en = en.replace("+", "%20");
      en = en.replace("*", "%2A");
      return en;
    } catch (UnsupportedEncodingException problem) {
      throw new IllegalArgumentException(problem);
    }
  }

  /**
   * 输入为map 输出为：appid=xxx&openid=xxx&...
   */
  public static String formRequestParam(Map<String, String> params) {
    String requestParam = params.toString();
    requestParam = requestParam.substring(1, requestParam.length() - 1);
    requestParam = requestParam.replace(", ", "&");

    return requestParam;
  }

  /**
   * 将Html和XML的转义字符解码
   */
  public static String unescapeHtmlAndXML(String str) {
    String origin = str;
    str = StringEscapeUtils.unescapeHtml4(str);
    str = StringEscapeUtils.unescapeXml(str);
    if (!str.equals(origin) && logger.isInfoEnabled()) {
      logger.info("[Nickname]Unescape-Html-And-XML, Origin: " + origin + ", Unescape: " + str);
    }
    return str;
  }

  /**
   * 字符串转换为utf8
   */
  public static String strToUTF8(String str) throws UnsupportedEncodingException {
    String s = "";
    if (!StringUtils.isEmpty(str)) {
      byte[] bytes = str.getBytes(CommonParameters.DEFAULT_CONTENT_CHARSET);
      s = new String(bytes, CommonParameters.DEFAULT_CONTENT_CHARSET);
    }
    return s;
  }
}
