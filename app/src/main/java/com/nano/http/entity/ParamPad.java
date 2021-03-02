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
public class ParamPad {

    private static final long serialVersionUID = 233410313723289238L;

    /**
     * MAC地址
     */
    private final String mac = AppStatic.macAddress;

    /**
     * 手术场次号
     */
    private Integer collectionNumber;

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

    public ParamPad(Integer collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public ParamPad(String data) {
        this.data = data;
    }

    public ParamPad(Integer collectionNumber, String data) {
        this.collectionNumber = collectionNumber;
        this.data = data;
    }
}
