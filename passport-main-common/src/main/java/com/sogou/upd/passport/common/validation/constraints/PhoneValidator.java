package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhoneUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.IDN;
import java.util.regex.Matcher;

/**
 * 验证手机号码、电话号码是否有效
 * 新联通 （中国联通+中国网通）手机号码开头数字 130、131、132、145、155、156、185、186 　　
 * 新移动 （中国移动+中国铁通）手机号码开头数字 134、135、136、137、138、139、147、150、151、152、157、158、159、182、183、187、188
 * 新电信 （中国电信 <http://baike.baidu.com/view/3214.htm>+中国卫通）手机号码开头数字 133、153、189、180、181
 *
 * User: shipengzhi
 * Date: 13-6-17
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    @Override
    public void initialize(Phone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintContext) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        return value.matches(PhoneUtil.PHONE_FORMAT);
    }
}
