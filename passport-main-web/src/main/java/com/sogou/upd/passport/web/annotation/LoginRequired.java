package com.sogou.upd.passport.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-14
 * Time: 下午8:55
 */


@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LoginRequired {

    boolean value() default true;

    String message() default "请先登录，再进行操作！";

    LoginRequiredResultType resultType() default LoginRequiredResultType.json;
}
