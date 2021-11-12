package com.jonbore.groovy.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @author daowan.hu
 */
public class IDGenerator {


    private static int COUNT = 0;

    public static String getID() {
        UUID uid = UUID.randomUUID();
        String id = uid.toString();
        id = id.toUpperCase();
        id = id.replace("-", "");
        return id;
    }

    public static String get12UUID() {
        UUID uid = UUID.randomUUID();
        String id = uid.toString();
        id = id.toUpperCase();
        id = id.replace("-", "");
        return id.substring(0, 12);
    }

    public static void main(String[] args) {
    }

    /**
     * 生成随机字母
     */
    public static String getRandomChar() {
        int randomInt;
        int pwdLength = 0;
        char[] str = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z'};
        final int maxNum = str.length;

        StringBuilder pwd = new StringBuilder();
        Random rand = new Random();
        while (pwdLength < 32) {
            randomInt = Math.abs(rand.nextInt(maxNum));
            if (randomInt >= 0 && randomInt < str.length) {
                pwd.append(str[randomInt]);
                pwdLength++;
            }
        }
        return pwd.toString();
    }

    /**
     * 获取26位唯一字符串,前15位距离19700101的时间毫秒数，后11单独计数值
     *
     * @return
     */
    public static synchronized String getUniqueID() {
        long millis = System.currentTimeMillis();
        return num2Str(millis, 15) + num2Str(++COUNT, 11);
    }

    /**
     * 数字按照指定长度
     *
     * @param number
     * @param width
     * @return
     */
    private static String num2Str(long number, int width) {
        String numStr = String.valueOf(number);

        int len = numStr.length() - width;
        if (len > 0) {
            numStr = numStr.substring(len);
        }

        width -= numStr.length();
        StringBuilder zeroBuff = new StringBuilder();
        while (zeroBuff.length() < width) {
            zeroBuff.append("0");
        }
        return zeroBuff.toString() + numStr;
    }

    public static synchronized String getDateUniqueID() {
        return num2Str(17) + num2Str(++COUNT, 2);
    }

    /**
     * 当前时间制定长度
     *
     * @param width
     * @return
     */
    public static String num2Str(int width) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmssS");
        String numStr = sdf.format(new Date());
        width -= numStr.length();
        StringBuilder zeroBuff = new StringBuilder();
        while (zeroBuff.length() < width) {
            zeroBuff.append("0");
        }
        return zeroBuff.toString() + numStr;
    }

    /**
     * 生成流程id，克隆时不能用getID，部署不成功
     */
    public static String getRandomWorkflowId() {
        int randomInt;
        int pwdLength = 0;
        char[] str = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z'};
        char[] numStr = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        final int maxNum = str.length;
        final int num = numStr.length;
        StringBuilder pwd = new StringBuilder();
        Random rand = new Random();
        while (pwdLength < 10) {
            randomInt = Math.abs(rand.nextInt(maxNum));
            if (randomInt >= 0 && randomInt < str.length) {
                pwd.append(str[randomInt]);
                pwdLength++;
            }
        }
        while (pwdLength < 20) {
            randomInt = Math.abs(rand.nextInt(num));
            if (randomInt >= 0 && randomInt < numStr.length) {
                pwd.append(numStr[randomInt]);
                pwdLength++;
            }
        }
        return pwd.toString();
    }
}
