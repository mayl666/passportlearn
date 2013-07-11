package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.patchca.background.SingleColorBackgroundFactory;
import org.patchca.color.DefaultColorFactory;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.DefaultRippleFilterFactory;
import org.patchca.font.RandomFontFactory;
import org.patchca.service.Captcha;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.text.renderer.RandomYBestFitTextRenderer;
import org.patchca.word.DefaultRandomWordFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * User: mayan Date: 13-5-8 Time: 下午2:20 To change this template use File | Settings | File Templates.
 */
public class CaptchaUtils {

    private Random random = new Random();
    private String randString = "123456789ABCDEFGHIJKLMNPQRSTUVWXYZ";//随机产生的字符串
    private int width = 80;//图片宽
    private int height = 26;//图片高
    private int lineSize = 40;//干扰线数量
    private int stringNum = 5;//随机产生字符数量
  /* * 获得字体     */

    private static List<String> fontList = Lists.newLinkedList();

    private static ConfigurableCaptchaService captchaService = new ConfigurableCaptchaService();
    private static DefaultRandomWordFactory wordFactory = new DefaultRandomWordFactory("123456789ABCDEFGHIJKLMNPQRSTUVWXYZ", 5);
    private static DefaultRippleFilterFactory filterFactory = new DefaultRippleFilterFactory();
    private static RandomYBestFitTextRenderer textRenderer = new RandomYBestFitTextRenderer();
    private static RandomFontFactory fontFactory = new RandomFontFactory();
    private static SingleColorBackgroundFactory backgroundFactory = new SingleColorBackgroundFactory();
    private static DefaultColorFactory colorFactory = new DefaultColorFactory();
    static {
        filterFactory.setLineNum(0);
//        fontFactory.setRandomStyle(true);
//        fontFactory.setMinSize(28);
//        fontFactory.setMaxSize(28);
//        fontFactory.setBoldStyle(true);
        fontFactory.setMinSize(48);
        fontFactory.setMaxSize(48);
        captchaService.setWordFactory(wordFactory);
        captchaService.setColorFactory(colorFactory);
        captchaService.setBackgroundFactory(backgroundFactory);
        captchaService.setFontFactory(fontFactory);
        captchaService.setTextRenderer(textRenderer);
        captchaService.setFilterFactory(filterFactory);
        captchaService.setWidth(160);
        captchaService.setHeight(56);

        fontList.add("Verdana");
        fontList.add("Tahoma");
        fontList.add("Arial");
        fontList.add("Courier New");
        // fontList.add("sans-serif");
        // fontList.add("cursive");
    }

    private Font getFont() {
        return new Font(fontList.get(random.nextInt(fontList.size())), random.nextInt(2) == 1 ? Font.ITALIC : Font.CENTER_BASELINE, 24);
        // return new Font("Fixedsys", Font.CENTER_BASELINE, 24);
    }    /*     * 获得颜色     */

    private Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    public Map<String, Object> getRandCode() {
        Captcha captcha = captchaService.getCaptcha();
        BufferedImage image = captcha.getImage();
        String captchaCode = captcha.getChallenge();
        Map<String, Object> mapResult = Maps.newHashMap();
        mapResult.put("image", image);
        mapResult.put("captcha", captchaCode);

        return mapResult;
    }

    /**
     * 生成随机图片
     */
    public Map<String, Object> getRandcode() {
        //BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();//产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));
        g.setColor(getRandColor(110, 133));        //绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drowLine(g);
        }
//        drawRect(g);
        //绘制随机字符
        String randomString = "";
        for (int i = 1; i <= stringNum; i++) {
            randomString = drowString(g, randomString, i);
        }
        g.dispose();
        Map<String, Object> mapResult = Maps.newHashMap();
        mapResult.put("image", image);
        mapResult.put("captcha", randomString);

        return mapResult;
    }    /*     * 绘制字符串     */

    private String drowString(Graphics g, String randomString, int i) {
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random.nextInt(121)));
        String rand = String.valueOf(getRandomString(random.nextInt(randString.length())));
        randomString += rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 11 * i - (i % 2 == 0 ? 0 : random.nextInt(2)), 16 + (i % 2) * random.nextInt(3)) ;
        return randomString;
    }    /*     * 绘制干扰线     */

    private void drowLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }    /*     * 获取随机的字符     */

    private void drawRect(Graphics g) {
        for (int i=0; i<4; i++) {
            g.setColor(new Color(0, 0, 0));
            g.setXORMode(new Color(255, 255, 255));
            g.fillRect(random.nextInt(20) + i * 40, random.nextInt(20) + i*5, random.nextInt(20) + 30, random.nextInt(20) + 30);
        }
    }

    public String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }
}
