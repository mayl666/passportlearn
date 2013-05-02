package com.sogou.upd.passport.common.math;

import java.security.MessageDigest;

/**
 * MD5工具类
 * User: mayan
 * Date: 13-3-27
 * Time: 上午11:19
 * To change this template use File | Settings | File Templates.
 */
public class MD5Encryption   
{   
    static public String MD5(String sInput) throws Exception   
    {   
           
        String algorithm = "";
        // eliminate null input
        if(sInput.trim() == null)   
        {   
            return "null";   
        }     
           
        try {   
            algorithm = System.getProperty("MD5.algorithm","MD5");   
        } catch(SecurityException se) {   
        }   
           
        MessageDigest md = MessageDigest.getInstance(algorithm);   
           
        byte buffer[] = sInput.getBytes();   
        md.update(buffer);   
           
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd','e', 'f'};   
        byte bDigest[] = md.digest();   
           
        int j = bDigest.length;   
        char str[] = new char[j * 2];   
        int k = 0;   
        for (int i = 0; i < j; i++) {   
            byte byte0 = bDigest[i];   
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];   
            str[k++] = hexDigits[byte0 & 0xf];   
        }   
        return new String(str);   
   
    }  
}  