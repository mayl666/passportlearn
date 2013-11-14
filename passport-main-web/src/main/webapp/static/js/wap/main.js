/**
 * getById
 * @param  {string} id ID
 * @return {HTMLElement}    元素
 */
function $$(id) {
    return document.getElementById(id);
}

/**
 * 去除字符串两端空白
 * @param  {string} str 字符串
 * @return {string}     去除空白后的字符串
 */
function trim(str) {
    return str.replace(/^\s+|\s+$/g, '');
}
/**
 * 判断一个元素是否可见
 * @param  {HTMLElement}  element 元素
 * @return {Boolean}         
 */
function isVisible(element) {
    var rect = element.getBoundingClientRect();
    return !!(rect.bottom - rect.top);
}

var captImg = $$('CaptImg');
/**
 * 更新验证码
 */
function updateCaptcha() {
    var d = +new Date();
    var src = captImg.src;
    if (src.indexOf('?') !== -1) {
        src = src.split('?')[0];
    }
    captImg.src = src + '?t=' + d;
}

/**
 * 表单验证
 */
$$('LoginForm').addEventListener('submit', function(e) {
    var errMsg = '请填写帐号、密码';
    if (isVisible($$('CaptchaWrp'))) {
        errMsg += '和验证码'
    }
    if (!trim($$('ID').value) ||
        !trim($$('Pass').value) ||
        !trim($$('Captcha').value)) {

        $$('Error').innerHTML = errMsg;
        e.preventDefault(); // 阻止提交表单
    }
}, false);

$$('ReloadCapt').addEventListener('click', function(e) {
    e.preventDefault();
    updateCaptcha();
}, false);

captImg.addEventListener('click', function(e) {
    e.preventDefault();
    updateCaptcha();
}, false);