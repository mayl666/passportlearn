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
 * User: liuling
 * Date: 14-6-26
 * Time: 下午8:41
 * To change this template use File | Settings | File Templates.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FindPwdTypeValidator.class)
@Documented
public @interface FindPwdType {
    String message() default "不支持该找回密码方式";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

