package com.sogou.upd.passport.common.validation.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-3-23
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FindPwdTypeValidator.class)
@Documented
public @interface FindPwdType {
    String message() default "找回密码方式不支持";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

