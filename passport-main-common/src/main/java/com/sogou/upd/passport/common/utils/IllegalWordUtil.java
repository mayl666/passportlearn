package com.sogou.upd.passport.common.utils;


import com.google.common.collect.Sets;
import com.sogou.upd.passport.common.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-3-11
 * Time: 上午11:18
 * To change this template use File | Settings | File Templates.
 */
public class IllegalWordUtil {
    private static final Logger logger = LoggerFactory.getLogger(IllegalWordUtil.class);
    public static final Set<String> SENSITIVE_SET;


    static {
        SENSITIVE_SET = Sets.newHashSet();
        try {
            InputStream illegalWords = IllegalWordUtil.class.getResourceAsStream("/illegalWords.dat");
            BufferedReader br = new BufferedReader(new InputStreamReader(illegalWords, CommonConstant.DEFAULT_CHARSET));
            String readValue = br.readLine();
            String word;
            while (readValue != null) {
                word = readValue;
                SENSITIVE_SET.add(word);
                readValue = br.readLine();
            }
        } catch (IOException e) {
            logger.error("get illegal sensitive word failed", e);

        }
    }
}
