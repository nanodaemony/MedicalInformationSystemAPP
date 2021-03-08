package com.nano.http.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * Usage:
 * 1.
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/3/5 16:14
 */
@Data
@NoArgsConstructor
public class ParamMedical implements Serializable {


    private static final long serialVersionUID = 233410313723289678L;

    /**
     * 代号
     */
    private Integer code;

    /**
     * 包含信息的Data
     */
    private String data;

    private String data2;


    private boolean booleanValue;


    public ParamMedical(Integer code, String data, String data2) {
        this.code = code;
        this.data = data;
        this.data2 = data2;
    }

    public ParamMedical(Integer code, String data) {
        this.code = code;
        this.data = data;
    }

    public ParamMedical(String data, String data2) {
        this.data = data;
        this.data2 = data2;
    }

    public ParamMedical(Integer code) {
        this.code = code;
    }

    public ParamMedical(String data) {
        this.data = data;
    }
}
