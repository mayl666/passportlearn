package com.sogou.upd.passport.web.account.captcha;

import com.sogou.upd.passport.common.utils.RedisUtils;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: mayan Date: 13-5-7 Time: 下午6:22 To change this template use
 * File | Settings | File Templates.
 */
public class CaptchaServlet  extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws
                                                                              ServletException,
                                                                              IOException {
    // 获取spring的context
    ServletContext application = getServletContext();
    WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application);

    RedisUtils redisUtils= (RedisUtils) wac.getBean("redisUtils");

    //生成验证码
    RandomValidateCode randomValidateCode = new RandomValidateCode();

    response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
    response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expire", 0);

    try {
      randomValidateCode.getRandcode(request, response);//输出图片方法
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }
}

