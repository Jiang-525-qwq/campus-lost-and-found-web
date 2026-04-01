package com.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] b = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte value : b) {
                int temp = value & 0xff;
                if (temp < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(temp));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}