package com.jonbore.groovy.util;


import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对字符处理的公共类
 *
 * @author hudaowan
 */
public class StringUtils {

    public static String GBKToUTF(String str) {
        String utfStr = null;
        try {
            utfStr = new String(str.getBytes("GBK"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return utfStr;
    }

    public static String UTFToGBK(String str) {
        String utfStr = null;
        try {
            utfStr = new String(str.getBytes(StandardCharsets.UTF_8), "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return utfStr;
    }

    public static String ISOToGBK(String str) {
        String utfStr = null;
        try {
            utfStr = new String(str.getBytes("ISO8859_1"), "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return utfStr;
    }

    /**
     * 将字符转换成map
     * {row:20000,pageno:1,startdate:2008-9-10,enddate:2008-10-1}
     * 注意：要求在字符串的key和value中不可以包含“,”、“:”，否则会出现分割错误
     *
     * @return
     * @author hudaowan
     * @date Nov 3, 2008 10:03:26 AM
     */
    public static Map<String, String> strToMap(String str) {
        Map<String, String> map = new HashMap<>();
        if (!isBlank(str)) {
            String strObj = str.substring(1, str.length() - 1);
            String[] obj = strObj.split(",");
            for (String value : obj) {
                String[] key = value.split(":");
                map.put(key[0], key[1]);
            }
        }
        return map;
    }

    /**
     * 将map转换成字符
     *
     * @param map
     * @return
     * @author hudaowan
     * @date Nov 3, 2008 10:21:31 AM
     */
    public static String mapToStr(Map<String, String> map) {
        StringBuilder str = new StringBuilder();
        if (map != null) {
            for (Map.Entry<String, String> obj : map.entrySet()) {
                String key = obj.getKey();
                String value = obj.getValue();
                str.append(key).append(":").append(value).append(",");
            }
        }
        if (str.length() > 0) {
            str = new StringBuilder("{" + str.substring(0, str.length() - 1) + "}");
        }
        return str.toString();
    }

    /**
     * @param str
     * @return 如果<tt>str</tt>为<tt>null</tt>或空白字符串返回<tt>true</tt>
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isNotBlank(String... strs) {
        boolean isNotNull = true;
        if (strs == null || strs.length == 0) {
            return false;
        }

        for (String str : strs) {
            if (StringUtils.isBlank(str)) {
                isNotNull = false;
                break;
            }
        }
        return isNotNull;
    }

    /**
     * 用<tt>separator</tt>连接字符串,例如将数组{"a","b","c"}使用';'连接,得到"a;b;c",忽略<tt>null<tt>和空白字符串
     *
     * @param s         需要连接的字符串数组
     * @param separator 分隔符
     * @return 连接好的字符串或""
     * @throws NullPointerException 如果<tt>s</tt>或<tt>separator</tt>为<tt>null</tt>
     * @see #join(String[], String, boolean, boolean)
     */
    public static String join(String[] s, String separator) {
        return join(s, separator, true, true);
    }

    /**
     * 用<tt>separator</tt>连接字符串,例如将数组{"a","b","c"}使用';'连接，得到"a;b;c"
     *
     * @param strList     需要连接的字符串数组
     * @param separator   分隔符
     * @param ignoreBlank 是否忽略空字符串,通过<tt>String.trim().length() == 0</tt>判断空字符串
     * @param ignoreNull  是否忽略<tt>null</tt>
     * @return 连接好的字符串或""
     * @throws NullPointerException 如果<tt>strList</tt>或<tt>separator</tt>为<tt>null</tt>
     */
    public static String join(String[] strList, String separator,
                              boolean ignoreBlank, boolean ignoreNull) {
        if (strList == null || separator == null) {
            throw new NullPointerException();
        }
        StringBuilder result = new StringBuilder(256);
        for (String str : strList) {
            if (ignoreNull && str == null) {
                continue;
            } else if (ignoreBlank && str.trim().length() == 0) {
                continue;
            }
            result.append(str);
            result.append(separator);
        }
        int i = result.length();
        if (i > 0) {
            return result.substring(0, i - separator.length());
        } else {
            return "";
        }
    }

    /**
     * 将CamelCase转换成大写字母，以“_”为间隔，例如abcFoo转换成ABC_FOO
     *
     * @param s
     * @return
     */
    public static String camelToCapital(String s) {
        final String pattern = "[A-Z]*[a-z0-9]+|[A-Z0-9]+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        String r = null;
        while (m.find()) {
            if (r != null) {
                r = r + "_" + m.group().toUpperCase();
            } else {
                r = m.group().toUpperCase();
            }
        }
        return r;
    }

    /**
     * 将大写字母转换成CamelCase，以“_”为间隔，例如ABC_FOO转换成abcFoo
     *
     * @param s
     * @return
     */
    public static String capitalToCamel(String s) {
        String[] tokens = s.split("_");
        StringBuilder r = new StringBuilder(tokens[0].toLowerCase());
        for (int i = 1; i < tokens.length; i++) {
            r.append(tokens[i].substring(0, 1)).append(tokens[i].substring(1)
                    .toLowerCase());
        }
        return r.toString();
    }

    /**
     * 截取字符串，按照系统默认的字符集，截取后的后缀为“...”
     *
     * @param target   被截取的原字符串，此方法执行前会先<tt>trim</tt>
     * @param maxBytes 截取后字符串的最大<tt>byte</tt>数，包括截取后的字符串的后缀
     * @return
     * @see #substring(String, String, int, String)
     */
    public static String substring(String target, int maxBytes) {
        return substring(target.trim(), Charset.defaultCharset().name(),
                maxBytes, "...");
    }

    /**
     * 截取字符串
     *
     * @param target   被截取的原字符串
     * @param charset  字符串的字符集
     * @param maxBytes 截取后字符串的最大<tt>byte</tt>数，包括截取后的字符串的后缀
     * @param append   字符串被截去后的后缀
     * @return
     */
    public static String substring(String target, String charset, int maxBytes,
                                   String append) {
        try {
            int count = getBytes(target, charset).length;
            if (count <= maxBytes) {
                return target;
            } else {
                int bytesCount = 0;
                char[] replace = new char[getBytes(append, charset).length];
                int j = 0;
                int bound = maxBytes - getBytes(append, charset).length;
                for (int i = 0; i < target.length(); i++) {
                    char c = target.charAt(i);
                    bytesCount = c > 255 ? bytesCount + 2 : bytesCount + 1;
                    if (bytesCount > maxBytes) {
                        return target.substring(0, i - j).concat(append);
                    }
                    if (bytesCount > bound) {
                        replace[j++] = c;
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Unreachable!");
    }

    private static byte[] getBytes(String s, String charset)
            throws UnsupportedEncodingException {
        return s.getBytes(charset);
    }

    public static String substringAfterLast(String str, String separator) {
        if (!isNotBlank(str, separator)) {
            return "";
        } else {
            int pos = str.lastIndexOf(separator);
            return pos != -1 && pos != str.length() - separator.length() ? str.substring(pos + separator.length()) : "";
        }
    }

    public static String trim(String str) {
        str = str.replace('　', ' ');
        return str.trim();
    }

    public static boolean containsIgnoreCase(String orig, String targ) {
        return orig.toLowerCase().contains(targ.toLowerCase());
    }

    public static boolean equalsIgnoreCase(String str, String tar) {
        return isNotBlank(str, tar) && str.toLowerCase().equals(tar.toLowerCase());
    }

    public static String joinPath(String str1, String str2) {
        if (str1.endsWith("/") && str2.startsWith("/")) {
            str2 = str1 + str2.substring(1);
        } else if (str1.endsWith("/") || str2.startsWith("/")) {
            str2 = str1 + str2;
        } else {
            str2 = str1 + "/" + str2;
        }
        return str2;
    }

}
