
package com.android.shortvideo.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5操作�?
 */
public class Md5Util
{
    
    /**
     * 获取MD5，统�?��回大写字母格�?
     * @param key
     * @return
     */
    public static String md5(String key) {
        try {
            char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            MessageDigest md;
            md = MessageDigest.getInstance("MD5");
            byte[] buf = key.getBytes();
            md.update(buf, 0, buf.length);
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder(32);
            for (byte b : bytes) {
                sb.append(hex[((b >> 4) & 0xF)]).append(hex[((b >> 0) & 0xF)]);
            }
            key = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        key = key.toUpperCase(); // PS: 统一用大�?
        return key;
    }
    
}
