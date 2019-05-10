package com.dace.textreader.util;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用于数据加密/解密
 * Created by 70391 on 2017/9/4.
 */

public class DataEncryption {

    /**
     * 加密
     *
     * @param data 要加密的数据
     * @return 加密后的数据
     */
    public static String encode(String data,String key) {
        String result;

        result = Base64.encodeToString(data.getBytes(), Base64.DEFAULT);

//        result = "C85A4c8d2G" + result;
        result = key + result;

        result = Base64.encodeToString(result.getBytes(), Base64.DEFAULT);

        return result;
    }


    /**
     * 音频加密
     *
     * @param data
     * @return
     */
    public static String audioEncode(String data) {
        String encodeResult = data;
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            String filename = data.substring(data.lastIndexOf("/") + 1, data.lastIndexOf("."));
            String text = filename + "18d9cf6";
            byte[] result = digest.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0" + hex);
                } else {
                    sb.append(hex);
                }
            }
            encodeResult = encodeResult + "?from=" + sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodeResult;
    }

    /**
     * 纠错数据加密
     *
     * @param key       //公钥
     * @param code      //随机字符串
     * @param studentId
     * @return
     */
    public static String errorCorrectionEncode(String key, String code, long studentId) {
        String result;

        String str1 = key + code;

        result = Base64.encodeToString(str1.getBytes(), Base64.DEFAULT);

        if (result.contains("\n")) {
            result = result.replace("\n", "");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("md5");

            String str2 = result + String.valueOf(studentId);

            byte[] bytes = digest.digest(str2.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0" + hex);
                } else {
                    sb.append(hex);
                }
            }
            result = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取指定位数的随机字符串(包含小写字母、大写字母、数字,0<length)
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        //随机字符串的随机字符库
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }



}
