/**
 * Created by rongbin on 2017/3/23.
 *
 * test code for wap
 */
@GrabConfig(systemClassLoader = true)
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')

import static lib.BDD.*;
import org.apache.http.Header

CONFIG(
    server:"http://127.0.0.1"
//    server:"http://10.152.70.143"
)

def random = new Random()
def now = System.currentTimeMillis()

// login the sogou account as narmal http://account.sogou.com/wap/login
def client_id = '1115'
def username = 'codetest1'
def password = '111111'
def login_token = 'a4e7d404214dc43bce5a1edaddf41a13'
def name_sgid = 'sgid'
def header_setCookies = "Set-Cookie"

// the values from server
def fresh_sgid = ''

// get the values from server side after login successful
def login_query = [token:login_token,username:username, password:password, client_id:client_id, v:'1']
POST ('/wap/login') {
    r.server = 'http://127.0.0.1'
//    r.server = 'http://10.152.70.143'
    r.query = login_query
    r.headers = [host: "account.sogou.com"]
}

def resp_header = bdd.resp.getAllHeaders()

resp_header.each{it->
    if (it instanceof Header) {
        if (it.name == header_setCookies) {
            def tmp = it.value.split(";")[0].split("=")
            if (tmp[0] == name_sgid) {
                fresh_sgid = tmp[1]
            }
        }
    }
}
// get the fresh sgid
println(fresh_sgid)

// test code parameters
def getuserinfo_client = client_id
def getuserinfo_sgid = fresh_sgid
def getuserinfo_ct = '1483424311656'
def getuserinfo_cdid = '0000016f4b6def08e1bafcdfe4795d94'
def getuserinfo_cinfo = 'udid=0000016f4b6def08e1bafcdfe4795d94'
//def getuserinfo_code = '21bea33747813a7d96d4a308bc6486f0'
def app_security = '96a288d174e98fb4a687569ea593c8c8'
def getuserinfo_fields = 'birthday,province,city'
// in the server side the udid is empty
def tmp =  getuserinfo_cdid + getuserinfo_client + app_security + getuserinfo_ct
def getuserinfo_code = MD5_HEX(tmp)

for(i in 0 .. 0) {
    // https://m.account.sogou.com/mapp/userinfo/getuserinfo
    def getuserinfo_query = [sgid: getuserinfo_sgid, client_id: getuserinfo_client, ct: getuserinfo_ct, code: getuserinfo_code, fields: getuserinfo_fields]
    def getuserinfo_headers = [cinfo: getuserinfo_cinfo, host: "m.account.sogou.com"]
    POST ('/mapp/userinfo/getuserinfo') {
        r.query = getuserinfo_query
        r.headers = getuserinfo_headers
    }
    EXPECT {
        json.status = "0"
        json.'data.userid' = username.concat('@sogou.com')
    }

    GET ('/mapp/userinfo/getuserinfo') {
        r.query = getuserinfo_query
        r.headers = getuserinfo_headers
    }
    EXPECT {
        json.status = "0"
        json.'data.userid' = username.concat('@sogou.com')
    }
}