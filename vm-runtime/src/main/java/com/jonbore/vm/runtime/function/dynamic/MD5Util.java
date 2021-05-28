package com.jonbore.vm.runtime.function.dynamic;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    private static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String md5(String str) {
        byte[] encodeBytes = DigestUtils.md5(str);
        if (encodeBytes != null) {
            char[] encodeStr = new char[encodeBytes.length * 2];
            int k = 0;
            for (int i = 0; i < encodeBytes.length; i++) {
                byte encodeByte = encodeBytes[i];
                encodeStr[k++] = hexDigits[encodeByte >> 4 & 0xf];
                encodeStr[k++] = hexDigits[encodeByte & 0xf];
            }
            return new String(encodeStr);
        }
        return null;
    }

}
