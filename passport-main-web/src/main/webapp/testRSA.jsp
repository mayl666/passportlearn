<%@ page import="com.sogou.upd.passport.common.math.RSA" %>
<%@ page import="com.sogou.upd.passport.service.account.generator.TokenGenerator" %>
<%@ page import="com.sogou.upd.passport.common.math.Base64Coder" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%--
  Created by IntelliJ IDEA.
  User: denghua
  Date: 14-6-10
  Time: 上午10:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<form action="/testRSA.jsp" method="post">
<table>
    <tr>
        <th>公钥加密</th>
        <td>
            <textarea name="content" cols="20" rows="5"></textarea>
        </td>
        <td><input type="hidden" name="type" value="en">
            <input type="submit"></td>
    </tr>


</table>

</form>

<form action="/testRSA.jsp" method="post">
    <table>

        <tr>
            <th>私钥解密</th>
            <td>
                <textarea name="content" cols="20" rows="5"></textarea>
            </td>
            <td><input type="hidden" name="type" value="de">
                <input type="submit"></td>
        </tr>

    </table>

</form>

<textarea readonly="readonly"><%
        String type=request.getParameter("type");
        String content=request.getParameter("content");
        if(content!=null){
            byte[] contentByte=content.getBytes();
            if("de".equals(type)){
                //解密
                out.print(RSA.decryptByPrivateKey(Base64Coder.decode(URLDecoder.decode(content,"UTF-8")),TokenGenerator.PRIVATE_KEY));
            };
            if("en".equals(type)){
                //加密
                out.print(URLEncoder.encode(new String(Base64Coder.encode(RSA.encryptByPublicKey(contentByte, TokenGenerator.PUBLIC_KEY))),"UTF-8"));
            }

        }

    %></textarea>
</body>
</html>
