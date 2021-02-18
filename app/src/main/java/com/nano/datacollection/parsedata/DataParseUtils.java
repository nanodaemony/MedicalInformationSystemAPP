package com.nano.datacollection.parsedata;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;


/**
 * 解析数据工具类
 * @author cz
 */
public class DataParseUtils {

    // 保留三位小数
    private static final DecimalFormat DF = new DecimalFormat("#.000");

    /**
     * 通过十六进制字符串计算其浮点值
     *
     * @param str 十六进制字符串
     * @return 对应的浮点值
     */
    public static double getDoubleValueByHexString(String str) {
        if (Float.isNaN(Float.intBitsToFloat(new BigInteger(str, 16).intValue()))) {
            return -1000;
        }
        return Double.parseDouble(DF.format(Float.intBitsToFloat(new BigInteger(str, 16).intValue())));
    }


    /**
     * 裁剪多于的0
     *
     * @param data 数据
     * @return 裁剪后的结果
     */
    public static String trimZero(String data) {
        int index = data.indexOf("0000000000");
        if (index != -1) {
            return data.substring(0, index);
        }
        return data;
    }

    /**
     * 将16进制的byte数组转换成字符串
     *
     * @param raw byte数组
     * @return 返回16进制字符串
     */
    public static String getBufHexStr(byte[] raw) {
        String HEXES = "0123456789ABCDEF";
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }


    /**
     * 打印出字符串列表
     *
     * @param stringList 字符串列表
     */
    public static void printStringList(List<String> stringList) {
        for (String s : stringList) {
            System.out.println(s);
        }
    }


    /**
     * 打印出字符串列表
     *
     * @param stringList 字符串列表
     */
    public static void printStringList(String[] stringList) {
        for (String s : stringList) {
            System.out.println(s);
        }
    }


    /**
     *  * 16进制直接转换成为字符串(无需Unicode解码)
     *  * @param hexStr
     *  * @return
     *  
     */
    public static String hexStringToString(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }


    /**
     * 将16进制的字符串转成字符数组
     *
     * @param str 待转换字符串
     * @return 返回byte数组
     */
    public static byte[] getHexBytes(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

}