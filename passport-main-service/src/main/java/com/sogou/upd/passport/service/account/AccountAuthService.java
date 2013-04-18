package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.model.account.AccountAuth;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-3-29 Time: 上午1:12 To change this template
 * use File | Settings | File Templates.
 */
public interface AccountAuthService {

  /**
   * 验证refresh_token的合法性
   *
   * @return refreshToken不存在或过期则返回null
   */
  public AccountAuth verifyRefreshToken(String refreshToken, String instanceId);

  /**
   * 验证access_token的合法性
   *
   * @return refreshToken不存在或过期则返回null
   */
  public AccountAuth verifyAccessToken(String accessToken);

  /**
   * @param userId
   * @param connectUid
   * @param accountType
   * @param clientId
   * @return
   */
  public boolean isAbleBind(long userId, String connectUid, int accountType, int clientId);

  /**
   * 初始化账号授权信息
   */
  public AccountAuth initialAccountAuth(long userId, String passportId, int clientId,
                                        String instanceId) throws Exception;

  /**
   * @param userId
   * @param passportId
   * @param clientId
   * @return
   * @throws Exception
   */
  public AccountAuth updateAccountAuth(long userId, String passportId, int clientId,
                                       String instanceId) throws Exception;

  /**
   * 删除AccountAuth（内部debug接口使用）
   */
  public int deleteAccountAuthByUserId(long user_id);

  /**
   * 异步更新某用户其它状态信息
   */
  public void asynUpdateAccountAuthBySql(final String mobile, final int clientId,
                                         final String instanceId) throws SystemException;


}
