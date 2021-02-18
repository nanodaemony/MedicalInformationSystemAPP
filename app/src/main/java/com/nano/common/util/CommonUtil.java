package com.nano.common.util;

import android.content.Context;
import android.media.MediaPlayer;


import com.nano.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * 公共的工具类
 * @author cz
 */
public class CommonUtil {



    /**
     * 标记的时间格式
     */
    private static SimpleDateFormat markTimeFormat;

    static
    {
        // 设置日期格式
        markTimeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    }

    /**
     *  获取标记的时间
     */
    public static String getMarkTimeForLocal(){
        return markTimeFormat.format(System.currentTimeMillis());
    }

    /**
     * 将16进制的byte数组转换成字符串
     * @param raw byte数组
     * @return 返回16进制字符串
     */
    public static String getBufHexStr(byte[] raw){
        String HEXES = "0123456789ABCDEF";
        if ( raw == null ) {
            return null;
        }
        final StringBuilder hex = new StringBuilder( 2 * raw.length );
        for ( final byte b : raw ) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }


    /**
     * 将16进制的字符串转成字符数组
     * @param str 待转换字符串
     * @return 返回byte数组
     */
    public static byte[] getHexBytes(String str){
        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    /**
     * 读取SD卡中的文件
     * @param fileName 文件路径
     * @return String 文件内容字符串
     * @throws Exception Exception
     */
    public String readFileContent(Context context, String fileName) throws Exception{
        FileInputStream fileInputStream = context.openFileInput(fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = fileInputStream.read(buffer)) != -1){
            outputStream.write(buffer,0,len);
        }
        return new String(outputStream.toByteArray());
    }


    /**
     * 所有ID的集合
     */
    private static List<Integer> idNumberList = new ArrayList<>();

    private static Random random = new Random();


    /**
     * 生成唯一的ID值
     * @return 整型ID值
     */
    public static int generateIdNumber(){
        int idNumber = random.nextInt(100000);
        if (isIdUnique(idNumber)){
            return idNumber;
        } else {
            generateIdNumber();
        }
        return 0;
    }


    /**
     * 判断生成的ID是否是唯一的
     * @return 是否是唯一的ID
     */
    private static boolean isIdUnique(int newId){
        for (Integer id : idNumberList){
            if (id == newId){
                return false;
            }
        }
        return true;
    }

    /**
     * 获取设备的MAC地址
     *
     * @return: MAC String
     */
    public static String getDeviceMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")){
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }


    /**
     * 播放报警提示音
     *
     * 上下文
     * void
     */
    public static void playWarningSound(Context context){
        MediaPlayer mMediaPlayer = MediaPlayer.create(context, R.raw.mention);
        mMediaPlayer.start();
    }

    /**
     * 从SD卡中读取信息进行显示
     * 也需要权限，但在写入的时候已经给了权限，读取的时不需要分配权限
     * @param path File path
     */
    public String readLogInfoFromSdcard(String path) {
        // 文件输入流
        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        try {
            is = new FileInputStream(path);
            // 使用缓冲来读
            // 每次读取1K的数据
            byte[] buf = new byte[1024 * 500 * 3];
            // 当还存在数据的时候就一直读取
            while (is.read(buf) != -1) {
                sb.append(new String(buf).trim());
            }

            String logInfo = sb.toString();

            if (logInfo.length() > 2048){
                return logInfo.substring(logInfo.length() / 2);
            }else {
                return logInfo;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    // 关闭输入流
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }


}
