require(['app/accountV2'], function(Account) {
	var classIds = {
		i_loginForm : "login_form",  //登录form
		i_commonRegForm   :  "common_reg" ,//普通注册form
		i_phoneRegForm : "phone_reg",//手机注册form
		c_loginLeft :"login-wrap",//登录左侧区域
		c_loginRight :"outside-login",//登录右侧区域
		c_regBottom : "dl-bottom",//登录底部区域
		c_commonReg : "reg-wrap", //普通注册
		c_phoneReg :"reg-phone", //手机号注册
		c_mainWrap : "dl-wrap"

	};
    new Account().init(classIds); 
});
