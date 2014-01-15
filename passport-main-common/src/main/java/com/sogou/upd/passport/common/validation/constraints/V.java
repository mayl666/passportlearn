package com.sogou.upd.passport.common.validation.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: mayan
 * Date: 14-1-15
 * Time: 下午2:47
 * To change this template use File | Settings | File Templates.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = VValidator.class)
@Documented
public @interface V {

    String message() default "版本错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
