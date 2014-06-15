package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.utils.UniqNameUtil;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import java.io.UnsupportedEncodingException;

/**
 * 检查昵称是否存在
 * User: mayan
 * Date: 13-8-8 Time: 下午2:18
 */
public class CheckNickNameParams {

    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;

    @NotBlank(message = "用户昵称不能为空")
    private String nickname;


    @AssertTrue(message = "用户昵称格式不正确!")
    private boolean isCheckUinqName() {
        UniqNameUtil uniqNameUtil = new UniqNameUtil();
        if (!uniqNameUtil.checkUniqNameIsCorrect(nickname)) {
            return false;
        } else {
            return true;
        }
    }

    public CheckNickNameParams() {
    }

    public CheckNickNameParams(String nickname,String ClientId) {
        this.client_id=ClientId;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

}
