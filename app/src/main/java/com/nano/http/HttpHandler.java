package com.nano.http;

/**
 * Description: HTTP处理器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/30 17:06
 */
public interface HttpHandler {

    /**
     * 处理请求正常的消息
     * @param message 数据
     */
    void handleSuccessfulHttpMessage(HttpMessage message);


    /**
     * 处理请求失败的消息
     * @param message 数据
     */
    void handleFailedHttpMessage(HttpMessage message);

    /**
     * 处理网络异常
     */
    void handleNetworkFailedMessage();
}
