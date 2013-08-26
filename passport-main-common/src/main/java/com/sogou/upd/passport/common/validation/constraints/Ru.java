package com.sogou.upd.passport.common.validation.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The string has to be a real rootPath.
 *
 * User: shipengzhi
 * Date: 13-6-17
 * Time: 下午6:04
 * To change this template use File | Settings | File Templates.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = RuValidator.class)
@Documented
public @interface Ru {

    String message() default "域名不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
