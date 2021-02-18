package com.nano.http.entity;

import com.alibaba.fastjson.JSON;
import com.nano.AppStatic;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * HTTP上传到服务器的视图对象
 * @author cz
 */
@Data
@NoArgsConstructor
public class ParamCollector {

    /**
     * 请求码
     */
    private int requestCode;

    /**
     * MAC地址
     */
    private final String mac = AppStatic.macAddress;

    /**
     * 手术场次号
     */
    private final int operationNumber = AppStatic.operationNumber;

    /**
     * 数据
     */
    private String data = "{}";

    /**
     * 产生上传到服务器的数据
     */

    public String generatePostString() {
        return JSON.toJSONString(this);
    }


    /**
     * 传入类型 其余默认值
     * @param requestCode type
     */
    public ParamCollector(int requestCode) {
        this.requestCode = requestCode;
    }


    public ParamCollector(int requestCode, String data) {
        this.requestCode = requestCode;
        this.data = data;
    }
}
