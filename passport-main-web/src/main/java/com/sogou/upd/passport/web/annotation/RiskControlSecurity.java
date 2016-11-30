package com.sogou.upd.passport.web.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: lzy_clement
 * Date: 15-3-24
 * Time: 上午11:26
 * To change this template use File | Settings | File Templates.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RiskControlSecurity {

    ResponseResultType resultType() default ResponseResultType.json;

}
