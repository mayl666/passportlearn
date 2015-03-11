package com.sogou.upd.passport.common.validation.constraints;

import com.sogou.upd.passport.common.utils.IllegalwordUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class SensitiveWordInit {
    public static HashMap sensitiveWordMap;

    public static Map initKeyWord() {
        try {

            Set<String> keyWordSet = IllegalwordUtil.SENSITIVE_SET;

            addSensitiveWordToHashMap(keyWordSet);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sensitiveWordMap;
    }


    public static void addSensitiveWordToHashMap(Set<String> keyWordSet) {
        sensitiveWordMap = new HashMap(keyWordSet.size());
        String key = null;
        Map nowMap = null;
        Map<String, String> newWorMap = null;
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
                    newWorMap = new HashMap<String, String>();
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


}
