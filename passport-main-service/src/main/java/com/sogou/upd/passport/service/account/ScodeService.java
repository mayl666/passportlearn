package com.sogou.upd.passport.service.account;

/**
 * Created by xieyilun on 2016/1/26.
 */
public interface ScodeService {
    /**
     * 生成安全码
     * 如果有未验证的安全码，将被覆盖
     * @param passportId
     * @param appid
     * @return
     */
    public String generate(final String passportId, final int appid);

    /**
     * 验证安全码
     * 如果验证通过，安全码立即失效
     * @param passportId
     * @param appid
     * @param scode
     * @return
     */
    public boolean verify(final String passportId, final int appid, final String scode);
}
