package com.sogou.upd.passport.web.annotation;

import java.lang.annotation.*;

/**
 * 根据appid检查接口调用是否超限
 * User: mayan
 * Date: 13-10-31
 * Time: 下午4:46
 */
@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface InterfaceLimited {
    ResponseResultType resultType() default ResponseResultType.json;
}
