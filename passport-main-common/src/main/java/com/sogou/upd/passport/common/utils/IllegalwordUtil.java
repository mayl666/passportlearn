package com.sogou.upd.passport.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-3-11
 * Time: 上午11:18
 * To change this template use File | Settings | File Templates.
 */
public class IllegalwordUtil {
    private static final Logger logger = LoggerFactory.getLogger(IllegalwordUtil.class);
    public static final Set<String> SENSITIVE_SET;


    static {
        SENSITIVE_SET = new HashSet<String>();
        try {
            InputStream illegalwords = IllegalwordUtil.class.getResourceAsStream("/illegalwords.dat");
            BufferedReader br = new BufferedReader(new InputStreamReader(illegalwords, "UTF-8"));
            String readValue = br.readLine();
            String word;
            while (readValue != null) {
                word = readValue;
                SENSITIVE_SET.add(word);
                readValue = br.readLine();
            }
        } catch (IOException e) {
            logger.error("get ilegal sensitive word failed", e);

        }
    }
}
