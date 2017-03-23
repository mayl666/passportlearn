/**
 * Created by rongbin on 2017/3/21.
 *
 * test code for internal
 */
@GrabConfig(systemClassLoader = true)
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')

import static lib.BDD.*;
import org.apache.http.Header

CONFIG(
	server:"http://127.0.0.1"
//	server:"http://account.sogou"
)

def random = new Random()
def now = System.currentTimeMillis()

// login the sogou account as normal /web/login
def web_client_id = "1120"
def username = "codetest1"
def password = "111111"
def autologin = "1"
def login_token = "a4e7d404214dc43bce5a1edaddf41a13"
def fresh_sgid = ""
def fresh_ppinf = ""
def fresh_pprdig = ""
def header_setCookies = "Set-Cookie"
def name_pprdig = "pprdig"
def name_sgid = "sgid"
def name_ppinf = "ppinf"

// login the web page with different account
def login_query = [token:login_token,username:username, password:password, autoLogin:autologin, client_id:web_client_id]
POST ("/web/login") {
//	r.server = "http://account.sogou.com"
    r.server = "http://127.0.0.1"
    r.query = login_query
}

//println("PRINT_RESPONSE COOKIES")
def resp_header = bdd.resp.getAllHeaders()
//println(resp_header.size())

// Get the temp token for every time login
resp_header.each{it->
  if (it instanceof Header) {
  	if (it.name == header_setCookies) {
  		def tmp = it.value.split(";")[0].split("=")
  		if (tmp[0] == name_ppinf) {
  			fresh_ppinf = tmp[1]
  		}
  		if (tmp[0] == name_sgid) {
  			fresh_sgid = tmp[1]
  		}
  		if (tmp[0] == name_pprdig) {
  			fresh_pprdig = tmp[1]
  		}
  	}
  }
}

println("PPINF  : ${fresh_ppinf}")
println("SGID   : ${fresh_sgid}")
println("PPRDIG : ${fresh_pprdig}")

// the basic information for internal interface
def client_id = web_client_id
def app_securty = "4xoG%9>2Z67iL5]OdtBq\$l#>DfW@TY"
def ct = "1484623418453"
def tmp = username + client_id + app_securty + ct
def code = MD5_HEX(tmp)

// user id for third part authentication
def userid = "BA61FF202EDBFC5F8AB11E8052815AE7@qq.sohu.com"
def fields = "birthday,uid,avatarurl"
def original = "1"

// user/info
def info_code = "596109892be8a20eb3f0ede8284649ad"
def info_fields = "birthday,uid"
// for userinfoBySgid
def sgid_sgid = "AViasFURibev2sxZBUp8pAr48"
def code_sgid = "fe5b5f9b057b43684aadacdaeff56b9f"
def ct_sgid = "1487672679460"
def fields_sgid = "uid,uniqname,avatarurl"

// verify the interface
for (i in 0 .. 0) {
	//query parameters /internal/account/userinfo
	def userinfo_query = [client_id:client_id, ct:ct, code:code, userid:username, fields:fields]
	//query parameters /internal/account/connect/users/info
	//must use the third part account
	def info_query = [original:original,client_id:client_id, ct:ct, code:info_code, userid:userid, fields:info_fields]
    //query parameters /internal/account/userinfoBySgid
    def sgid_query = [client_id:client_id, ct:ct, code: code, userid:username, sgid:fresh_sgid, fields: fields_sgid]

    POST("/internal/account/userinfo") {
    	r.query = userinfo_query
    }
    EXPECT {
    	json.status = "0"
        json.'data' = NotEmpty
        json.'data.userid' = username.concat('@sogou.com')
    }
    GET("/internal/account/userinfo") {
    	r.query = userinfo_query
    }
    EXPECT {
    	json.status = "0"
        json.'data' = NotEmpty
        json.'data.userid' = username.concat('@sogou.com')
    }

    POST("/internal/connect/users/info") {
    	r.query = info_query
    }
    EXPECT {
    	json.status = "30021"
    }
    GET("/internal/connect/users/info") {
    	r.query = info_query
    }
    EXPECT {
    	json.status = "30021"
    }

    POST("/internal/account/userinfoBySgid") {
    	r.query = sgid_query
    }
    EXPECT {
    	json.status = "0"
        json.'data' = NotEmpty
        json.'data.userid' = username.concat('@sogou.com')
    }
    GET("/internal/account/userinfoBySgid") {
    	r.query = sgid_query
    }
    EXPECT {
    	json.status = "0"
        json.'data' = NotEmpty
        json.'data.userid' = username.concat('@sogou.com')
    }
}