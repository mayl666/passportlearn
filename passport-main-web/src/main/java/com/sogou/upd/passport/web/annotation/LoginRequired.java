package com.sogou.upd.passport.web.annotation;

import com.sogou.upd.passport.common.utils.ErrorUtil;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记一个方法需要登录，如修改密码
 * 标记也可以用在Controller上用于表示其所有方法都需要登录
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午8:55
 */


@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LoginRequired {

    //改方法是否需要登录，默认需要登录
    boolean value() default true;

    //返回给前端的信息
    String message() default "账号未登录，请先登录!";

    //返回前端信息时采用的格式
    ResponseResultType resultType() default ResponseResultType.json;

    //当检测失败的时候跳转的页面
    String url() default "/web/login";
}
