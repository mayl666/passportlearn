/**
 * Created by rongbin on 2017/3/21.
 */
import static lib.BDD.*;

import javax.annotation.PostConstruct;

CONFIG(
	server:"http://127.0.0.1"
)

def random = new Random()
def now = System.currentTimeMillis()

def udid, token, rc;
def client_id = "1120"
def ct = "1484623418453"
def code = "596109892be8a20eb3f0ede8284649ad"
def userid = "BA61FF202EDBFC5F8AB11E8052815AE7@qq.sohu.com"
def fields = "birthday,uid,avatarurl"
def original = "1"
// for userinfoBySgid
def sgid = "AViasFURibev2sxZBUp8pAr48"
def code_sgid = "fe5b5f9b057b43684aadacdaeff56b9f"
def ct_sgid = "1487672679460"
def fields_sgid = "uid,uniqname,avatarurl"
// for getuserinfo
def getuserinfo_sgid = "AVhiaOlic9M7YzGmqk0giayXyM"
def getuserinfo_ct = "1483424311656"
def getuserinfo_code = "21bea33747813a7d96d4a308bc6486f0"
def getuserinfo_fields = "uid"

for (i in 0 .. 4) {
	//query parameters /internal/account/userinfo
	def userinfo_query = [client_id:client_id, ct:ct, code:code, userid:userid, fields:fields]
	//query parameters /internal/account/connect/users/info
	def info_query = [original:original,client_id:client_id, ct:ct, code:code, userid:userid, fields:fields]
    //query parameters /internal/account/userinfoBySgid
    def sgid_query = [client_id:client_id, ct:ct_sgid, code: code_sgid, sgid:sgid, fields: fields_sgid]
    //query parameters /mapp/userinfo/getuserinfo
    def getuserinfo_query = [sgid:getuserinfo_sgid, client_id:client_id, ct:getuserinfo_ct, code:getuserinfo_code, fields:getuserinfo_fields]

    POST("/internal/account/userinfo") {
    	r.query = userinfo_query
    }
    EXPECT {
    	json.status = "0"
    }
    GET("/internal/account/userinfo") {
    	r.query = userinfo_query
    }
    EXPECT {
    	json.status = "0"
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
    	json.status = "20263"
    }
    GET("/internal/account/userinfoBySgid") {
    	r.query = sgid_query
    }
    EXPECT {
    	json.status = "20263"
    }

    POST("/mapp/userinfo/getuserinfo") {
//    	r.server = "http://m.account.sogou.com"
    	r.query = getuserinfo_query
    }
    EXPECT {
    	json.status = null
    }
    GET("/mapp/userinfo/getuserinfo") {
//    	r.server = "http://m.account.sogou.com"
    	r.query = getuserinfo_query
    }
    EXPECT {
    	json.status = null
    }
}