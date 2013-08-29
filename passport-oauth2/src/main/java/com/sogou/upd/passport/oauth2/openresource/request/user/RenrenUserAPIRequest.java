package com.sogou.upd.passport.oauth2.openresource.request.user;

import com.sogou.upd.passport.oauth2.openresource.parameters.RenrenOAuth;
import com.sogou.upd.passport.oauth2.openresource.request.RenrenAbstractAPIRequest;

public class RenrenUserAPIRequest extends RenrenAbstractAPIRequest {

    public RenrenUserAPIRequest(String url) {
        super(url);
    }

    /**
     * Renren用户类API调用的请求参数
     */
    public static class RenrenUserAPIBuilder extends RenrenAbstractAPIRequest.RenrenCommonParamsBuilder {

        public RenrenUserAPIBuilder(String url) {
            super(url);
        }

        /**
         * 需要查询的用户uids,可批量(最好填)
         * users.getInfo接口使用
         */
        public RenrenUserAPIBuilder setUserId(String userId) {
            this.parameters.put(RenrenOAuth.USERID, userId == null ? null : userId);
            return this;
        }

        /**
         * 需要查询的用户uids,可批量(最好填)
         * users.getProfileInfo接口使用
         */
        public RenrenUserAPIBuilder setUid(String uid) {
            this.parameters.put(RenrenOAuth.UIDS, uid == null ? null : uid);
            return this;
        }

        /**
         * 返回的字段列表
         * uid,name,sex,star,zidou,vip,birthday,tinyurl,headurl,mainurl,hometown_location,work_history,university_history。
         * 如果不传递此参数默认返回uid,name,tinyurl,headhurl,zidou,star。
         */
        public RenrenUserAPIBuilder setFields(String fields) {
            this.parameters.put(RenrenOAuth.FIELDS, fields == null ? null : fields);
            return this;
        }

    }

}
