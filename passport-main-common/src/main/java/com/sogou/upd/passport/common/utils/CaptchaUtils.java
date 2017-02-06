package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Maps;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.patchca.background.SingleColorBackgroundFactory;
import org.patchca.color.ColorFactory;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.patchca.font.FontFactory;
import org.patchca.service.Captcha;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.text.renderer.BestFitTextRenderer;
import org.patchca.text.renderer.TextCharacter;
import org.patchca.text.renderer.TextRenderer;
import org.patchca.text.renderer.TextString;
import org.patchca.word.WordFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * 验证码工具类
 */
public class CaptchaUtils {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaUtils.class);

    /** 提示文字颜色 */
    private static final Color hintColor = new Color(255, 255, 255);
    /** 背景颜色 */
    private static final Color backgroundColor = new Color(14, 131, 230);
    /** 干扰线颜色 */
    private static final Color filterColor = new Color(0, 71, 167);

    private static HintCaptchaService captchaService = new HintCaptchaService();
    private static OperationWordFactory wordFactory = new OperationWordFactory();
    private static CurvesRippleFilterFactory filterFactory = new CurvesRippleFilterFactory();
    private static BestFitTextRenderer contextTextRenderer = new BestFitTextRenderer();
    private static HintTextRenderer hintTextRenderer = new HintTextRenderer();
    private static ContextFontFactory contextFontFactory = new ContextFontFactory();
    private static HintFontFactory hintFontFactory = new HintFontFactory();
    private static SingleColorBackgroundFactory backgroundFactory = new SingleColorBackgroundFactory(backgroundColor);
    private static SingleColorFactory hintColorFactory = new SingleColorFactory(hintColor);
    private static IllegibilityFactory contextColorFactory = new IllegibilityFactory();
    private static SingleColorFactory filterColorFactory = new SingleColorFactory(filterColor);


    static {
        // 最少5条干扰线
        filterFactory.setColorFactory(filterColorFactory);
        filterFactory.setStrokeMin(5);

        // 正文距顶部距离
        contextTextRenderer.setTopMargin(22);

        captchaService.setWordFactory(wordFactory);
        captchaService.setColorFactory(contextColorFactory);
        captchaService.setBackgroundFactory(backgroundFactory);
        captchaService.setFontFactory(contextFontFactory);
        captchaService.setTextRenderer(contextTextRenderer);
        captchaService.setFilterFactory(filterFactory);
        captchaService.setHintColorFactory(hintColorFactory);
        captchaService.setHintFontFactory(hintFontFactory);
        captchaService.setHintTextRenderer(hintTextRenderer);

        // 验证码尺寸
        captchaService.setWidth(160);
        captchaService.setHeight(56);
    }

    /**
     * 获取验证码
     * @return 返回 map。image：图片；captcha：验证码内容
     */
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
     * 带提示文字的验证码服务类
     */
    private static class HintCaptchaService extends ConfigurableCaptchaService {
        private HintFontFactory hintFontFactory;
        private ColorFactory hintColorFactory;
        private TextRenderer hintTextRenderer;

        @Override
        public Captcha getCaptcha() {
            // 新建图片
            BufferedImage bufImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
            // 设置背景色
            this.backgroundFactory.fillBackground(bufImage);
            // 获取下一个验证码
            String word = this.wordFactory.getNextWord();
            // 格式：“值|文字”，如“2|五减三”
            String[] wordArr = StringUtils.split(word, "|");
            // 渲染文字
            this.textRenderer.draw(wordArr[1], bufImage, this.fontFactory, this.colorFactory);
            // 干扰
            bufImage = this.filterFactory.applyFilters(bufImage);
            // 在干扰后的图片上添加提示文字，保证提示文字不被干扰
            hintTextRenderer.draw("请输入以下结果", bufImage, hintFontFactory, hintColorFactory);
            return new Captcha(wordArr[0], bufImage);
        }

        private void setHintFontFactory(HintFontFactory hintFontFactory) {
            this.hintFontFactory = hintFontFactory;
        }

        private void setHintColorFactory(ColorFactory hintColorFactory) {
            this.hintColorFactory = hintColorFactory;
        }

        private void setHintTextRenderer(TextRenderer hintTextRenderer) {
            this.hintTextRenderer = hintTextRenderer;
        }
    }

    /**
     * 提示文字渲染器
     */
    private static class HintTextRenderer extends BestFitTextRenderer {

        /**
         * 绘制提示文字
         * @param text
         * @param canvas
         * @param fontFactory
         * @param colorFactory
         */
        @Override
        public void draw(String text, BufferedImage canvas, FontFactory fontFactory, ColorFactory colorFactory) {
            Graphics2D graphics = (Graphics2D)canvas.getGraphics();

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int step = 0;

            // 渲染提示
            TextString hintTextString = this.convertToCharacters(text, graphics, fontFactory, colorFactory);
            this.arrangeHintCharacters(hintTextString);
            for(Iterator i$ = hintTextString.getCharacters().iterator(); i$.hasNext(); step ^= 1) {
                TextCharacter hintTextCharacter = (TextCharacter)i$.next();
                graphics.setColor(hintTextCharacter.getColor());
                graphics.drawString(hintTextCharacter.iterator(), (float)hintTextCharacter.getX() - step, (float)hintTextCharacter.getY());
            }
        }

        /**
         * 设置文字位置
         * @param textString
         */
        private void arrangeHintCharacters(TextString textString) {
            double x = 0;
            TextCharacter textCharacter;
            for(Iterator i$ = textString.getCharacters().iterator(); i$.hasNext(); x += textCharacter.getWidth()) {
                textCharacter = (TextCharacter)i$.next();
                textCharacter.setX(x);
                double y = textCharacter.getAscent();
                textCharacter.setY(y);
            }
        }
    }

    /**
     * 运算字符生成工厂
     */
    private static class OperationWordFactory implements WordFactory {
        private static String[] numberArr = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

        private static String[] operArr = {"加", "乘"};

        private static int MIN = 4;
        private static int MAX = 10;

        @Override
        public String getNextWord() {
            Random rnd = new Random();

            // 运算符
            int operArrIndex = rnd.nextInt(operArr.length);

            // 第一项
            int first = rnd.nextInt(MAX - MIN) + MIN;
            // 第二项
            int second = rnd.nextInt(MAX - MIN) + MIN;

            // 运算结果
            int result;

            if(operArrIndex == 0) { // 加法
                result = first + second;
            } else { // 乘法
                result = first * second;
            }

            // 验证码文字
            StringBuilder captchaSb = new StringBuilder();
            captchaSb.append(result).append("|");

            // 第一项字符
            String firstStr = String.valueOf(first);
            for (int i = 0; i < firstStr.length(); i++) {
                char c = firstStr.charAt(i);
                captchaSb.append(numberArr[Integer.parseInt(String.valueOf(c))]);
            }
            // 操作符
            captchaSb.append(operArr[operArrIndex]);
            // 第二项字符
            String secondStr = String.valueOf(second);
            for (int i = 0; i < secondStr.length(); i++) {
                char c = secondStr.charAt(i);
                captchaSb.append(numberArr[Integer.parseInt(String.valueOf(c))]);
            }

            return captchaSb.toString();
        }
    }

    /**
     * 不可识别颜色 生成工厂
     */
    private static class IllegibilityFactory implements ColorFactory {
        private static final Color[] colorArr = {
                // 这个颜色灰度值差异稍大，暂且保留
//                 new Color(0, 71, 167),
                new Color(40, 205, 102),
                new Color(53, 195, 190),
                new Color(190, 53, 69),
                new Color(224, 197, 18)
        };

        @Override
        public Color getColor(int i) {
            return colorArr[RandomUtils.nextInt(colorArr.length)];
        }
    }

    /**
     * 提示字体工厂
     */
    private static class HintFontFactory implements FontFactory {
        /** 定义验证码中字体 */
        private static final Font font;

        static {
            InputStream is = null;
            try {
                is = CaptchaUtils.class.getClassLoader().getResourceAsStream("font/宋体.ttc");
                Font tempFont = Font.createFont(Font.TRUETYPE_FONT, is);
                font = tempFont.deriveFont(Font.BOLD, 20F);
            } catch (Exception e) {
                throw new RuntimeException("load font error.", e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        @Override
        public Font getFont(int i) {
            return font;
        }
    }

    /**
     * 内容字体工厂
     */
    private static class ContextFontFactory implements FontFactory {

        /** 定义验证码中字体 */
        private static final Font[] fontArr;

        static {
            InputStream is = null;

            URL url = CaptchaUtils.class.getClassLoader().getResource("font");
            logger.info("font folder url path : " + url);
            if(url == null) {
                throw new RuntimeException("font folder url path is null.");
            }

            File fontFolderFile = new File(url.getPath());
            File[] fileArr = fontFolderFile.listFiles();

            if(ArrayUtils.isEmpty(fileArr)) {
                throw new RuntimeException("no font resource.");
            }
            logger.info("find " + fileArr.length + " font resource.");

            fontArr = new Font[fileArr.length];

            for (int i = 0; i < fileArr.length; i++) {
                File fontFile = fileArr[i];
                logger.info("load font : " + fontFile.getName());
                try {
                    is = FileUtils.openInputStream(fontFile);
                    fontArr[i] = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 46F);
                } catch (Exception e) {
                    throw new RuntimeException("load font error.", e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        @Override
        public Font getFont(int i) {
            return fontArr[RandomUtils.nextInt(fontArr.length)];
        }
    }
}
