package com.sogou.upd.passport.common.validation.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The string has to be a well-formed phone.
 *
 * User: shipengzhi
 * Date: 13-6-17
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = SafeInputValidator.class)
@Documented
public @interface SafeInput {
    String message() default "输入内容包含非法字符";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
