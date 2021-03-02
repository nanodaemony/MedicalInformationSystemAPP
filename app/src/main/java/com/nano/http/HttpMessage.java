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

    private ServerPathEnum pathEnum;

    private String data;

    private String data2;

    public HttpMessage(ServerPathEnum pathEnum) {
        this.pathEnum = pathEnum;
    }

    public HttpMessage(ServerPathEnum pathEnum, String data) {
        this.pathEnum = pathEnum;
        this.data = data;
    }

    public HttpMessage(ServerPathEnum pathEnum, String data, String data2) {
        this.pathEnum = pathEnum;
        this.data = data;
        this.data2 = data2;
    }

    public HttpMessage(String data) {
        this.data = data;
    }

}
