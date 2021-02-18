package com.nano.activity.devicedata.healthrecord;

import java.util.regex.Pattern;

/**
 * Description: 电子病历的工具类
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/10/23 14:06
 */
public class HealthRecordUtils {


    /**
     * 判断输入是否是数字
     *
     * @param number 数字字符串
     * @return 是否是数字
     */
    public static boolean isNumber(String number) {
        Pattern pattern = Pattern.compile("^[0-9]+(.[0-9]+)?$");
        return pattern.matcher(number).matches();
    }

    /**
     * 判断身份证是否合格
     *
     * @return 是否合格
     */
    public static boolean verifyPatientId(String patientId) {

        if (patientId.length() == 0 || patientId.trim().length() == 0) {
            return false;
        }
        // 解决输入身份证信息时能输入小写x的问题
        Pattern pattern = Pattern.compile("[0-9]{17}[0123456789X]");
        return pattern.matcher(patientId).matches();
    }


    /**
     * 判断住院号是否合格
     *
     * @return 是否合格
     */
    public static boolean verifyAdmissionId(String admissionId) {
        if (admissionId.length() == 0 || admissionId.trim().length() == 0) {
            return false;
        }
        // 匹配规则
        Pattern pattern = Pattern.compile("[0-9a-zA-Z]*");
        return pattern.matcher(admissionId).matches();
    }



    /**
     * 判断输入的手麻系统序列号是否规范
     *
     * @return 是否规范
     */
    public static boolean verifyHospitalOperationNumber(String hospitalOperationNumber) {
        Pattern pattern = Pattern.compile("[0-9a-zA-Z]*");
        return pattern.matcher(hospitalOperationNumber).matches();
    }


    /**
     * 判断输入年龄是否合格
     *
     * @return 是否合格
     */
    public static boolean verifyAge(String age) {
        Pattern pattern = Pattern.compile("[0-9]{1,3}");
        if (pattern.matcher(age).matches()) {

            int a = -1;

            try {
                a = Integer.parseInt(age);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (a >= 0 && a <= 140) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    /**
     * 判断输入身高是否合格
     *
     * @return 是否合格
     */
    public static boolean verifyPatientHeight(String height) {
        Pattern pattern = Pattern.compile("[0-9]{1,3}");
        if (pattern.matcher(height).matches()) {
            int h = -1;
            try {
                h = Integer.parseInt(height);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (h >= 0 && h <= 250) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    /**
     * 判断输入体重是否合格
     *
     * @return 是否合格
     */
    public static boolean verifyPatientWeight(String weight) {

        Pattern pattern = Pattern.compile("[0-9]{1,3}");
        if (pattern.matcher(weight).matches()) {
            int w = -1;
            try {
                w = Integer.parseInt(weight);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (w >= 0 && w <= 250) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * 根据Spinner返回的Index值返回省份的字符串信息
     *
     * @param hospitalAreaCode 医院地区代码
     */
    public static String getHospitalArea(int hospitalAreaCode) {
        // "省份", "重庆", "四川", "云南", "陕西", "北京", "天津", "河北", "山西", "上海", "湖北", "湖南", "江苏", "山东",  "浙江", "河南",  "江西", "广东",  "广西", "贵州",  "安徽", "吉林"
        //        ,  "甘肃", "福建", "海南", "辽宁", "黑龙江", "青海", "宁夏", "新疆", "西藏", "内蒙古";
        switch (hospitalAreaCode) {
            case 0:
                return HealthRecordCons.NO_INPUT;
            case 1:
                return "重庆";
            case 2:
                return "四川";
            case 3:
                return "云南";
            case 4:
                return "陕西";
            case 5:
                return "北京";
            case 6:
                return "天津";
            case 7:
                return "河北";
            case 8:
                return "山西";
            case 9:
                return "上海";
            case 10:
                return "湖北";
            case 11:
                return "湖南";
            case 12:
                return "江苏";
            case 13:
                return "山东";
            case 14:
                return "浙江";
            case 15:
                return "河南";
            case 16:
                return "江西";
            case 17:
                return "广东";
            case 18:
                return "广西";
            case 19:
                return "贵州";
            case 20:
                return "安徽";
            case 21:
                return "吉林";
            case 22:
                return "甘肃";
            case 23:
                return "福建";
            case 24:
                return "海南";
            case 25:
                return "辽宁";
            case 26:
                return "黑龙江";
            case 27:
                return "青海";
            case 28:
                return "宁夏";
            case 29:
                return "新疆";
            case 30:
                return "西藏";
            case 31:
                return "内蒙古";
            default:
                return "-1";
        }
    }



    /**
     * 根据Spinner返回的Index返回医院等级的字符串
     *
     * @param hospitalLevelCode 医院等级代号
     */
    public static String getHospitalLevel(int hospitalLevelCode) {
        // "等级", "三特", "三甲", "三乙", "三丙", "二甲", "二乙", "二丙", "一甲", "一乙", "一丙";
        switch (hospitalLevelCode) {
            // Not choose
            case 0:
                return "none";
            case 1:
                return "三特";
            case 2:
                return "三甲";
            case 3:
                return "三乙";
            case 4:
                return "三丙";
            case 5:
                return "二甲";
            case 6:
                return "二乙";
            case 7:
                return "二丙";
            case 8:
                return "一甲";
            case 9:
                return "一乙";
            case 10:
                return "一丙";
            // Failed
            default:
                return "-1";
        }
    }


    /**
     * 获取心功能等级
     */
    public static String getHeartFunctionLevel(Integer index) {
        switch (index) {
            case 0:
                return "";
            case 1:
                return "I级";
            case 2:
                return "II级";
            case 3:
                return "III级";
            case 4:
                return "IV级";
            default:
                return "";
        }
    }

    /**
     * 获取肺功能等级
     */
    public static String getLungFunctionLevel(Integer index) {
        switch (index) {
            case 0:
                return "";
            case 1:
                return "1级";
            case 2:
                return "2级";
            case 3:
                return "3级";
            case 4:
                return "4级";
            default:
                return "";
        }
    }

    /**
     * 获取肝功能等级
     */
    public static String getLiverFunctionLevel(Integer index) {
        switch (index) {
            case 0:
                return "";
            case 1:
                return "A级";
            case 2:
                return "B级";
            case 3:
                return "C级";
            default:
                return "";
        }
    }

    /**
     * 获取肾功能等级
     */
     public static String getKidneyFunctionLevel(Integer index) {
        switch (index) {
            case 0:
                return "";
            case 1:
                return "1";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            default:
                return "";
        }
    }



}
