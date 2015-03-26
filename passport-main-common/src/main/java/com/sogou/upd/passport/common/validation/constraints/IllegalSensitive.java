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
 * User: nahongxu
 * Date: 15-3-10
 * Time: 下午7:22
 * To change this template use File | Settings | File Templates.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = IllegalSensitiveValidator.class)
@Documented
public @interface IllegalSensitive {
    String message() default "包含敏感词汇";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
