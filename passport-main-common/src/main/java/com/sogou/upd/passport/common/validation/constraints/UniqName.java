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
 * Date: 13-7-23
 * Time: 上午11:08
 * To change this template use File | Settings | File Templates.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = UniqNameValidator.class)
@Documented
public @interface UniqName {
    String message() default "昵称格式不正确,请输入昵称长度在2~12字符之间，" +
            "可以使用中文，字母，数字和下划线的组合，但不能以下划线开头和结尾组合，" +
            "且不能包含'搜狗'，'sogou','sougou'字样";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
