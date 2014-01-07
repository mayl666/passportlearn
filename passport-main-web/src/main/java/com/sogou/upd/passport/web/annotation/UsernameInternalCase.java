package com.sogou.upd.passport.web.annotation;

import java.lang.annotation.*;

/**
 * 忽略username大小写的注解
 * 标记用在Controller里的所有接口
 * User: shipengzhi@sogou-inc.com
 * Date: 13-6-18
 * Time: 下午22:53
 */


@Target( { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface UsernameInternalCase {
}
