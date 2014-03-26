package com.sogou.upd.passport.common.validation.constraints;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检查回调的url是否在sogou.com或sohu.com域下
 * User: shipengzhi
 * Date: 13-6-17
 * Time: 下午6:05
 * To change this template use File | Settings | File Templates.
 */
public class RuValidator implements ConstraintValidator<Ru, String> {

    private static List SUPPORT_DOMAIN = Lists.newArrayList();

    static {
        SUPPORT_DOMAIN.add("sogou.com");
        SUPPORT_DOMAIN.add("sohu.com");
        SUPPORT_DOMAIN.add("go2map.com");
        SUPPORT_DOMAIN.add("qq.com");
        SUPPORT_DOMAIN.add("soso.com");
    }

    @Override
    public void initialize(Ru constraintAnnotation) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Strings.isNullOrEmpty(value)) {
            return true;
        }
        String rootPath;
        try {
            Pattern p = Pattern.compile("^(https?:\\/\\/)?[\\w\\-.]+\\.(sogou|sohu|qq|soso|go2map)\\.com($|\\?|\\/|\\\\|:[\\d])", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(value);
            if(matcher.find()){
                return true;
            }else{
                return false;
            }


        } catch (Exception e) {
            return false;
        }

//        if (!SUPPORT_DOMAIN.contains(rootPath)) {
//            return false;
//        }

//        return true;
    }

    public static void main(String args[]){
        String value="http://www.qq.com";
        String rootPath;
        try {
            Pattern p = Pattern.compile("(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(value);
            matcher.find();
            rootPath = matcher.group();
            System.out.println(rootPath);
        } catch (Exception e) {
            System.out.println("error!!");

        }

    }

}
