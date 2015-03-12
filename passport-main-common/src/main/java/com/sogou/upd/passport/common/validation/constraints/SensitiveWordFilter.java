package com.sogou.upd.passport.common.validation.constraints;

import com.sogou.upd.passport.common.utils.IllegalWordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SensitiveWordFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveWordFilter.class);

    public static Map sensitiveWordMap = new HashMap<String,String>();
    public static Set<String> keyWordSet = new HashSet<String>();
    public static int minMatchTYpe = 1;

    static {
        initKeyWord();
    }

    public static void initKeyWord() {
        try {
            keyWordSet = IllegalWordUtil.SENSITIVE_SET;
            addSensitiveWordToHashMap();

        } catch (Exception e) {
            logger.error("SensitiveWord filter initial sensitive word error", e);
        }
    }

    public static void addSensitiveWordToHashMap() {
        sensitiveWordMap = new HashMap(keyWordSet.size());
        String key;
        Map nowMap;
        Map<String, String> newWorMap;
        Iterator<String> iterator = keyWordSet.iterator();
        while (iterator.hasNext()) {
            key = iterator.next();
            nowMap = sensitiveWordMap;
            for (int i = 0; i < key.length(); i++) {
                char keyChar = key.charAt(i);
                Object wordMap = nowMap.get(keyChar);

                if (wordMap != null) {
                    nowMap = (Map) wordMap;
                } else {
                    newWorMap = new HashMap();
                    newWorMap.put("isEnd", "0");
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if (i == key.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
    }

    public static Set<String> getSensitiveWord(String txt, int matchType) {
        Set<String> sensitiveWordList = new HashSet<String>();

        for (int i = 0; i < txt.length(); i++) {
            int length = checkSensitiveWord(txt, i, matchType);
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                i = i + length - 1;
            }
        }

        return sensitiveWordList;
    }

    public static int checkSensitiveWord(String txt, int beginIndex, int matchType) {
        boolean flag = false;
        int matchFlag = 0;
        char word = 0;
        Map nowMap = sensitiveWordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);
            if (nowMap != null) {
                matchFlag++;
                if ("1".equals(nowMap.get("isEnd"))) {
                    flag = true;
                    if (SensitiveWordFilter.minMatchTYpe == matchType) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
        if (matchFlag < 1 || !flag) {
            matchFlag = 0;
        }
        return matchFlag;
    }

}