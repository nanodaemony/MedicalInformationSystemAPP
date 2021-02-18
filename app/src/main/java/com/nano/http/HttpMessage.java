package com.nano.http;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: Http传递的消息
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/30 17:20
 */
@Data
@NoArgsConstructor
public class HttpMessage {

    private RequestCodeEnum code;

    private String data;

    public HttpMessage(RequestCodeEnum code, String data) {
        this.code = code;
        this.data = data;
    }

    public HttpMessage(RequestCodeEnum code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "HttpMessage{" +
                "code=" + code +
                ", data='" + data + '\'' +
                '}';
    }
}
