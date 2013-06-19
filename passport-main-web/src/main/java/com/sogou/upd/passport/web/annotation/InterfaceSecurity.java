package com.sogou.upd.passport.web.annotation;

import java.lang.annotation.*;

/**
 * 校验内部接口方法签名正确性和接口有效期
 * 标记用在Controller里的所有内部接口
 * User: shipengzhi@sogou-inc.com
 * Date: 13-6-18
 * Time: 下午22:53
 */


@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface InterfaceSecurity {

    //返回给前端的信息
//    String message() default "内部接口code签名错误或请求超时！";

    //返回前端信息时采用的格式
    ResponseResultType resultType() default ResponseResultType.json;

}
