package com.sogou.upd.passport.web.Debug;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.dao.account.AccountAuthMapper;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;

/**
 * 账号相关的内部调试接口
 * User: shipengzhi
 * Date: 13-4-1
 * Time: 下午5:30
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class AccoutDebugController {

	private static final Logger logger = LoggerFactory.getLogger(AccoutDebugController.class);

	@Inject
	private AccountMapper accountMapper;
	@Inject
	private AccountAuthMapper accountAuthMapper;
	@Inject
	private RedisTemplate redisTemplate;

	/**
	 * 手机账号获取，重发手机验证码接口
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/internal/debug/deleteAccount", method = RequestMethod.GET)
	@ResponseBody
	public Object deleteAccount(@RequestParam(defaultValue = "") String mobile) throws Exception {
		if (PhoneUtil.verifyPhoneNumberFormat(mobile)) {
			Account account = accountMapper.getAccountByMobile(mobile);
			if (account != null) {
				accountMapper.deleteAccountByPassportId(account.getPassportId());
				accountAuthMapper.deleteAccountAuthByUserId(account.getId());
				String cacheKey = "PASSPORT:ACCOUNT_PASSPORTID_" + mobile + "@sohu.com";
				redisTemplate.delete(cacheKey);
				return "delete success!";
			} else {
				return "accout is not exist, not require delete";
			}
		} else {
			return "is not phone number! ";
		}
	}
}
