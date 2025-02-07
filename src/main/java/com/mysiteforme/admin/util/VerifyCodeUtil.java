package com.mysiteforme.admin.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;


/**
 * 验证码生成器
 * 可生成数字、大写、小写字母及三者混合类型的验证码
 * 支持自定义验证码字符数量,支持自定义验证码图片的大小,支持自定义需排除的特殊字符,支持自定义干扰线的数量,支持自定义验证码图文颜色
 * 另外,给Shiro加入验证码有多种方式,也可以通过继承修改FormAuthenticationFilter类,通过Shiro去验证验证码
 * 而这里既然使用了SpringMVC,也为了简化操作,就使用此工具生成验证码,并在Controller中处理验证码的校验
 * &#064;create  Sep 29, 2013 4:23:13 PM
 * @author 玄玉
 */
public class VerifyCodeUtil {
    /**
     * 验证码类型为仅数字,即0~9
     */
    public static final int TYPE_NUM_ONLY = 0;

    /**
     * 验证码类型为仅字母,即大小写字母混合
     */
    public static final int TYPE_LETTER_ONLY = 1;

    /**
     * 验证码类型为数字和大小写字母混合
     */
    public static final int TYPE_ALL_MIXED = 2;

    /**
     * 验证码类型为数字和大写字母混合
     */
    public static final int TYPE_NUM_UPPER = 3;

    /**
     * 验证码类型为数字和小写字母混合
     */
    public static final int TYPE_NUM_LOWER = 4;

    /**
     * 验证码类型为仅大写字母
     */
    public static final int TYPE_UPPER_ONLY = 5;

    /**
     * 验证码类型为仅小写字母
     */
    public static final int TYPE_LOWER_ONLY = 6;

    private VerifyCodeUtil(){}

    /**
     * 生成随机颜色
     */
    private static Color generateRandomColor() {
        Random random = new Random();
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }


    /**
     * 生成图片验证码
     * @param type           验证码类型,参见本类的静态属性
     * @param length         验证码字符长度,要求大于0的整数
     * @param excludeString  需排除的特殊字符
     * @param width          图片宽度(注意此宽度若过小,容易造成验证码文本显示不全,如4个字符的文本可使用85到90的宽度)
     * @param height         图片高度
     * @param interLine      图片中干扰线的条数
     * @param randomLocation 每个字符的高低位置是否随机
     * @param backColor      图片颜色,若为null则表示采用随机颜色
     * @param foreColor      字体颜色,若为null则表示采用随机颜色
     * @param lineColor      干扰线颜色,若为null则表示采用随机颜色
     * @return 图片缓存对象
     */
    public static BufferedImage generateImageCode(int type, int length, String excludeString, int width, int height, int interLine, boolean randomLocation, Color backColor, Color foreColor, Color lineColor){
        String textCode = generateTextCode(type, length, excludeString);
        return generateImageCode(textCode, width, height, interLine, randomLocation, backColor, foreColor, lineColor);
    }


    /**
     * 生成验证码字符串
     * @param type          验证码类型,参见本类的静态属性
     * @param length        验证码长度,要求大于0的整数
     * @param excludeString 需排除的特殊字符（无需排除则为null）
     * @return 验证码字符串
     */
    public static String generateTextCode(int type, int length, String excludeString){
        if(length <= 0){
            return "";
        }
        StringBuilder verifyCode = new StringBuilder();
        int i = 0;
        Random random = new Random();
        switch(type){
            case TYPE_NUM_ONLY:
                while(i < length){
                    int t = random.nextInt(10);
                    //排除特殊字符
                    if(null==excludeString || !excludeString.contains(t + "")) {
                        verifyCode.append(t);
                        i++;
                    }
                }
                break;
            case TYPE_LETTER_ONLY:
                while(i < length){
                    int t = random.nextInt(123);
                    if((t>=97 || (t>=65&&t<=90)) && (null==excludeString||excludeString.indexOf((char)t)<0)){
                        verifyCode.append((char)t);
                        i++;
                    }
                }
                break;
            case TYPE_ALL_MIXED:
                while(i < length){
                    int t = random.nextInt(123);
                    if((t>=97 || (t>=65&&t<=90) || (t>=48&&t<=57)) && (null==excludeString||excludeString.indexOf((char)t)<0)){
                        verifyCode.append((char)t);
                        i++;
                    }
                }
                break;
            case TYPE_NUM_UPPER:
                while(i < length){
                    int t = random.nextInt(91);
                    if((t>=65 || (t>=48&&t<=57)) && (null==excludeString || excludeString.indexOf((char)t)<0)){
                        verifyCode.append((char)t);
                        i++;
                    }
                }
                break;
            case TYPE_NUM_LOWER:
                while(i < length){
                    int t = random.nextInt(123);
                    if((t>=97 || (t>=48&&t<=57)) && (null==excludeString || excludeString.indexOf((char)t)<0)){
                        verifyCode.append((char)t);
                        i++;
                    }
                }
                break;
            case TYPE_UPPER_ONLY:
                while(i < length){
                    int t = random.nextInt(91);
                    if((t >= 65) && (null==excludeString||excludeString.indexOf((char)t)<0)){
                        verifyCode.append((char)t);
                        i++;
                    }
                }
                break;
            case TYPE_LOWER_ONLY:
                while(i < length){
                    int t = random.nextInt(123);
                    if((t>=97) && (null==excludeString||excludeString.indexOf((char)t)<0)){
                        verifyCode.append((char)t);
                        i++;
                    }
                }
                break;
        }
        return verifyCode.toString();
    }

    /**
     * 已有验证码,生成验证码图片
     * @param textCode       文本验证码
     * @param width          图片宽度(注意此宽度若过小,容易造成验证码文本显示不全,如4个字符的文本可使用85到90的宽度)
     * @param height         图片高度
     * @param interLine      图片中干扰线的条数
     * @param randomLocation 每个字符的高低位置是否随机
     * @param backColor      图片颜色,若为null则表示采用随机颜色
     * @param foreColor      字体颜色,若为null则表示采用随机颜色
     * @param lineColor      干扰线颜色,若为null则表示采用随机颜色
     * @return 图片缓存对象
     */
    public static BufferedImage generateImageCode(String textCode, int width, int height, int interLine, boolean randomLocation, Color backColor, Color foreColor, Color lineColor){
        //创建内存图像
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //获取图形上下文
        Graphics graphics = bufferedImage.getGraphics();
        //画背景图
        graphics.setColor(null==backColor ? generateRandomColor() : backColor);
        graphics.fillRect(0, 0, width, height);
        //画干扰线
        Random random = new Random();
        if(interLine > 0){
            int x = 0, y, y1 = 0;
            for(int i=0; i<interLine; i++){
                graphics.setColor(null==lineColor ? generateRandomColor() : lineColor);
                y = random.nextInt(height);
                y1 = random.nextInt(height);
                graphics.drawLine(x, y, width, y1);
            }
        }
        //字体大小为图片高度的80%
        int size = (int)(height * 0.8);
        int fx = height - size;
        int fy = size;
        //设定字体
        graphics.setFont(new Font("微软雅黑", Font.BOLD, size));
        //写验证码字符
        for(int i=0; i<textCode.length(); i++){
            fy = randomLocation ? (int)((Math.random()*0.3+0.6)*height) : fy;
            graphics.setColor(null==foreColor ? generateRandomColor() : foreColor);
            //将验证码字符显示到图象中
            graphics.drawString(textCode.charAt(i)+"", fx, fy);
            fx += (int) (size * 0.9);
        }
        graphics.dispose();
        return bufferedImage;
    }
}
