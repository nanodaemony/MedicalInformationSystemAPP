package com.nano.http;

import lombok.Getter;

/**
 * Description: 服务器地址
 *
 * @version: 1.0
 * @author: nano
 * @date: 2021/1/23 17:02
 */
public enum ServerPathEnum {


    /**
     * 查询网络状态
     */
    QUERY_NETWORK_STATUS("/eval/device-collection/network"),

    /**
     * 上传医疗仪器信息
     */
    POST_MEDICAL_DEVICE_INFO("/eval/device-collection/post-collection-info-pad"),


    /**
     * 仪器开始采集
     */
    DEVICE_START_COLLECTION("/eval/device-collection/device-start-collection-pad"),

    /**
     * 仪器完成采集
     */
    DEVICE_FINISH_COLLECTION("/eval/device-collection/device-finish-collection-pad"),

    /**
     * 仪器放弃采集
     */
    DEVICE_ABANDON_COLLECTION("/eval/device-collection/device-abandon-collection-pad"),

    /**
     * 新增手术事件信息
     */
    OPERATION_MARK_EVENT_ADD("/eval/operation-mark/add"),

    /**
     * 修改手术事件信息
     */
    OPERATION_MARK_EVENT_UPDATE("/eval/operation-mark/update"),


    /**
     * 删除手术事件信息
     */
    OPERATION_MARK_EVENT_DELETE("/eval/operation-mark/delete"),


    /**
     * 新增采集完成后评价信息表
     */
    AFTER_COLLECTION_EVALUATION_TABLE_ADD("/eval/after-collection-evaluation/add"),


    /**
     * 获取常用标记列表
     */
    GET_OFTEN_USE_MARK_EVENT_LIST("/eval/mark-event/get-often-use-mark-event-list"),

    /**
     * 搜索匹配的手术事件列表
     */
    SEARCH_MATCH_MARK_EVENT_LIST("/eval/mark-event/search-mark-event-list"),

    /**
     * 新增自定义的标记事件
     */
    ADD_CUSTOMIZE_MARK_EVENT("/eval/mark-event/add-customise-mark-event"),


    /**
     * 上传仪器数据
     */
    POST_DEVICE_DATA("/eval/device-data/add-device-data-pad"),


    /**
     * 获取文件存储的OSS路径
     */
    GET_FILE_STORAGE_URL("/eval/oss/get-file-storage-url"),


    /**
     * 用户注册
     */
    USER_REGISTER("/eval/patientchannel/register-user"),


    /**
     * 用户登录
     */
    USER_LOGIN("/eval/patientchannel/user-login"),


    /**
     * 用户登录
     */
    UPLOAD_DATA_TO_OSS("/eval/patientchannel/upload-medical-data-to-blockchain"),

    /**
     * 查询全部历史医疗索引记录数据
     */
    QUERY_ALL_HISTORY_MEDICAL_INDEX_DATA("/eval/patientchannel/queryAllMedicalIndexDataByPid"),


    /**
     * 测试获取系统PID列表
     */
    TEST_GET_SYSTEM_PID_LIST("/eval/patientchannel/get-system-pid-list"),

    /**
     * 数据分享请求
     */
    DATA_SHARE_REQUEST("/eval/patientchannel/data-sharing"),


    /**
     * 通过PID查询数据使用记录
     */
    QUERY_DATA_USAGE_INFO_BY_PID("/eval/patientchannel/query-data-usage-info-by-pid"),


    /**
     * 查询区块链信息
     */
    QUERY_LEDGER_INFO("/eval/patientchannel/query-ledger-info"),

    ;
    @Getter
    String path;


    ServerPathEnum(java.lang.String path) {
        this.path = path;
    }
}
