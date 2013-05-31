package com.sogou.upd.passport.common;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.model.ActiveEmail;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-21
 * Time: 下午5:46
 * To change this template use File | Settings | File Templates.
 */
public class JacksonTest extends TestCase {

    public void testWriteValueAsString() {
        try {
            ActiveEmail activeEmail = buildJsonObject();
            String jsonString = new ObjectMapper().writeValueAsString(activeEmail);
            System.out.println("Object write Json String:" + jsonString);
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testReadValue() {
        try {
            String jsonString = "{\"map\":{\"nickname\":\"spz\"},\"toEmail\":\"shipengzhi@sogou-inc.com\",\"subject\":\"Send Active Email\",\"category\":\"aaa\",\"activeUrl\":\"http://www.sogou.com\",\"templateFile\":null}";
            ActiveEmail activeEmail = new ObjectMapper().readValue(jsonString, ActiveEmail.class);
            System.out.println("Read Object:" + activeEmail);
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(true);
        }
    }

    /*
     * 测试JSON转换List<Object>
     */
    @Test
    public void testListToJson() {
        try {
            ActiveEmail activeEmail = buildJsonObject();
            List list = new ArrayList<>();
            list.add(activeEmail);
            list.add(activeEmail);
            String jsonResult = new ObjectMapper().writeValueAsString(list);
            List<ActiveEmail> transferList = new ObjectMapper().readValue(jsonResult, new TypeReference<List<ActiveEmail>>() { });
            ActiveEmail activeEmailJson = transferList.get(0);
            System.out.println("Old object: " + activeEmail.getMap() + activeEmail.getActiveUrl());
            System.out.println("From Json to object: " + activeEmailJson.getMap() + activeEmailJson.getActiveUrl());
            Assert.assertTrue(activeEmail.getSubject().equals(activeEmailJson.getSubject()));
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(true);
        }
    }

    private ActiveEmail buildJsonObject() {
        ActiveEmail jsonObject = new ActiveEmail();
        jsonObject.setActiveUrl("http://www.sogou.com");
        jsonObject.setCategory("aaa");
        jsonObject.setSubject("Send Active Email");
        Map map = Maps.newHashMap();
        map.put("nickname", "spz");
        jsonObject.setMap(map);
        jsonObject.setToEmail("shipengzhi@sogou-inc.com");
        return jsonObject;
    }

}
