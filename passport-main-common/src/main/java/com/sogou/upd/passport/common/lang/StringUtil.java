package com.sogou.upd.passport.common.lang;

import com.sogou.upd.passport.common.CommonConstant;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

  static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

  /**
   * 空字符串。
   */
  public static final String EMPTY_STRING = "";

  /**
   * 检查字符串是否为数字
   */
  public static boolean checkIsDigit(String str) {
    return str.matches("[0-9]*");
  }

  /*
   *检查密码格式是否是数字与字母组合
   */
  public static boolean checkPwdFormat(String pwd) {
    Pattern pattern = Pattern.compile("[a-zA-Z]+[0-9]+");
    Matcher resultMatcher = pattern.matcher(pwd);
    if (resultMatcher.matches()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * <p>Checks if a CharSequence is empty ("") or null.</p>
   *
   * <pre>
   * StringUtil.isEmpty(null)      = true
   * StringUtil.isEmpty("null")    = true
   * StringUtil.isEmpty("")        = true
   * StringUtil.isEmpty(" ")       = false
   * StringUtil.isEmpty("bob")     = false
   * StringUtil.isEmpty("  bob  ") = false
   * </pre>
   *
   * @param str the CharSequence to check, may be null
   * @return {@code true} if the CharSequence is empty or null
   */
  public static boolean isEmpty(String str) {
    return StringUtils.isEmpty(str) || str.equalsIgnoreCase("null");
  }

  /**
   * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
   *
   * <pre>
   * StringUtil.isBlank(null)      = true
   * StringUtil.isBlank("null")     = true
   * StringUtil.isBlank("")        = true
   * StringUtil.isBlank(" ")       = true
   * StringUtil.isBlank("bob")     = false
   * StringUtil.isBlank("  bob  ") = false
   * </pre>
   *
   * @param str the CharSequence to check, may be null
   * @return {@code true} if the CharSequence is null, empty or whitespace
   */
  public static boolean isBlank(String str) {
    return StringUtils.isBlank(str) || str.equalsIgnoreCase("null");
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
      logger.info(
          "[Nickname]Unescape-Html-And-XML, Origin: " + origin + ", Unescape: " + str);
    }
    return str;
  }

  /**
   * 字符串转换为utf8
   */
  public static String strToUTF8(String str) throws UnsupportedEncodingException {
    String s = "";
    if (!StringUtils.isEmpty(str)) {
      byte[] bytes = str.getBytes(CommonConstant.DEFAULT_CONTENT_CHARSET);
      s = new String(bytes, CommonConstant.DEFAULT_CONTENT_CHARSET);
    }
    return s;
  }

  /**
   * 过滤特殊字符，只保留 中文、英文大小写字母、空格、-、_
   */
  public static String filterSpecialChar(String str) {
    StringBuilder sb = new StringBuilder();
    if (StringUtils.isNotBlank(str)) {
      str = str.trim();

      for (int i = 0; i < str.length(); i++) {
        String ch = String.valueOf(str.charAt(i));

        boolean isDigest = Pattern.matches("[a-zA-Z0-9_\\-\\s]", ch);
        if (!isDigest) {
          boolean isChinese = Pattern.matches("[\\u4e00-\\u9fa5]", ch);
          if (isChinese) {
            sb.append(ch);
          }
        } else {
          sb.append(ch);
        }
      }
    }

    return sb.toString();
  }

  /**
   * 检测多个字符串参数中是否含有null或空值串，有则返回false，无则返回true。 不传入参数，则返回false
   */
  public static boolean checkExistNullOrEmpty(String... args) {
    for (String str : args) {
      if (str == null || str.length() == 0) {
        return true;
      }
    }
    return false;
  }
}
