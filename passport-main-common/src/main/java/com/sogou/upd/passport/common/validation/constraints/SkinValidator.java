package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * 检查wap皮肤是否是可支持的颜色
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-23
 * Time: 下午2:28
 * To change this template use File | Settings | File Templates.
 */
public class SkinValidator implements ConstraintValidator<Skin, String> {

    private static List SUPPORT_SKIN = Lists.newArrayList();

    //目前wap只有red一种颜色，若不传，默认为绿色.后续有其它颜色再添加
    static {
        SUPPORT_SKIN.add("red");
        SUPPORT_SKIN.add("green");
    }

    @Override
    public void initialize(Skin constraintAnnotation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        if (!SUPPORT_SKIN.contains(value)) {
            return false;
        }
        return true;
    }
}
