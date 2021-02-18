package com.nano.activity.devicedata.healthrecord;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: 电子病历的常量,比如需要填充到界面上的常量
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/10/23 14:13
 */
public interface HealthRecordCons {

    /**
     * 没有输入数据
     */
    String NO_INPUT = "";

    /**
     * 默认无内容的情况
     */
    String DEFAULT_CONTENT = "无";


    /**
     * 医院地区列表
     */
    List<String> HOSPITAL_AREA_LIST = new LinkedList<>(Arrays.asList("省份", "重庆市", "四川", "云南", "陕西", "北京市", "天津市", "河北", "山西", "上海市", "湖北", "湖南", "江苏", "山东", "浙江", "河南", "江西", "广东", "广西", "贵州", "安徽", "吉林"
            , "甘肃", "福建", "海南", "辽宁", "黑龙江", "青海", "宁夏", "新疆", "西藏", "内蒙古"));


    /**
     * 医院名称
     */
    String[] HOSPITAL_NAME = {"陆军军医大学第二附属医院", "沙坪坝区人民医院", "重庆市中医院", "北京市第二医院", "北京市第六医院"};


    /**
     * 新桥医院的官方名称
     */
    String XIN_QIAO_HOSPITAL_NAME = "陆军军医大学第二附属医院";

    /**
     * 医院等级
     */
    List<String> HOSPITAL_LEVELS = new LinkedList<>(Arrays.asList("等级", "三特", "三甲", "三乙", "三丙", "二甲", "二乙", "二丙", "一甲", "一乙", "一丙"));


    /**
     * 麻醉方式列表
     */
    List<String> MAI_ZUI_FANG_SHI = new LinkedList<>(Arrays.asList("麻醉方式", "局麻", "腰麻", "MAC麻醉", "阻滞麻醉", "吸入麻醉", "静吸复合麻醉", "全凭静脉麻醉", "腰硬联合麻醉", "持续硬膜外麻醉"));

    /**
     * 心功能分级
     */
    List<String> HEART_FUNCTION_LEVEL = new LinkedList<>(Arrays.asList("请选择", "I级", "II级", "III级", "IV级"));

    /**
     * 肺功能分级
     */
    List<String> LUNG_FUNCTION_LEVEL = new LinkedList<>(Arrays.asList("请选择", "1级", "2级", "3级", "4级"));

    /**
     * 肝功能分级
     */
    List<String> LIVER_FUNCTION_LEVEL = new LinkedList<>(Arrays.asList("请选择", "A级", "B级", "C级"));

    /**
     * 肾功能分级
     */
    List<String> KIDENY_FUNCTION_LEVEL = new LinkedList<>(Arrays.asList("请选择", "1", "2", "3", "4", "5"));

}
