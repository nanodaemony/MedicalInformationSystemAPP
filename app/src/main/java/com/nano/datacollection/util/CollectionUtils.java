package com.nano.datacollection.util;

import com.nano.datacollection.DeviceData;

import org.greenrobot.eventbus.EventBus;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

/**
 * 采集工具类
 * @author cz
 */
public class CollectionUtils {


    /**
     * 发送消息给仪器
     *
     * @param msg 消息
     */
    public static void sendMessageToDevice(ChannelHandlerContext ctx, String msg) {
        ctx.writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
    }

    /**
     * 剪切数据中的0值
     *
     * @param hexString 将数据字符串中的0值去掉
     * @return 返回非0值
     */
    public static String trimZero(String hexString) {

        // 当为null时返回空
        if (hexString == null) {
            return "";
        }

        // 00000的索引
        int zeroIndex = hexString.indexOf("00000");

        // 代表整个字符串都是有效的数据
        if (zeroIndex < 0) {
            return hexString;
        } else {
            return hexString.substring(0, zeroIndex);
        }
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
        }       final StringBuilder hex = new StringBuilder( 2 * raw.length );
        for ( final byte b : raw ) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }


    /**
      * 16进制直接转换成为字符串(无需Unicode解码)
      * @param hexStr
      * @return
      */
    public static String hexStringToString(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() /2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }




}
