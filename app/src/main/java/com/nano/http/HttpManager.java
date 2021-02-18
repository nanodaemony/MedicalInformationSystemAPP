package com.nano.http;

import com.alibaba.fastjson.JSON;
import com.nano.AppStatic;
import com.nano.common.logger.Logger;
import com.nano.common.threadpool.core.TaskExecutor;
import com.nano.activity.devicedata.healthrecord.CollectionBasicInfoEntity;
import com.nano.device.DeviceEnum;
import com.nano.http.entity.CommonResult;
import com.nano.http.entity.ParamCollector;
import com.nano.http.entity.ResultVo;

import cn.hutool.http.HttpUtil;

/**
 * Description: 网络管理器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/30 17:16
 */
public class HttpManager {

    private static Logger logger = new Logger("[NetworkManager]");

    /**
     * 与服务通信的路径，默认为生产环境路径
     */
    public static String SERVER_INFO_PATH = AppStatic.serverPathEnum.getInfoPath();
    public static String SERVER_DATA_PATH = AppStatic.serverPathEnum.getDataPath();

    /**
     * HTTP响应处理器
     */
    private HttpHandler httpHandler;

    public HttpManager(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    /**
     * 获取网络状态(√)
     */
    public void getNetworkStatus() {
        RequestCodeEnum codeEnum = RequestCodeEnum.GET_SERVER_STATUS;
        int requestCode = codeEnum.getCode();

        TaskExecutor.executeHttpTask(() -> {
            logger.info("获取服务器状态");
            try {
                String res = HttpUtil.post(SERVER_INFO_PATH, new ParamCollector(requestCode, "{}").generatePostString());
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && codeEnum.getCode() == resultVo.getCode()) {
                        logger.info(res);
                        logger.info("服务器在线.");
                        // 返回成功消息
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum));
                    } else {
                        // 返回失败消息
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                // 网络异常
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }

    /**
     * 上传基本信息并得到手术场次号(√)
     *
     * @param collectionBasicInfoEntity 信息实体
     */
    public void postBasicInformationToGetOperationNumber(CollectionBasicInfoEntity collectionBasicInfoEntity) {

        RequestCodeEnum codeEnum = RequestCodeEnum.POST_BASIC_OPERATION_INFO;
        int requestCode = codeEnum.getCode();

        TaskExecutor.executeHttpTask(() -> {
            String res;
            try {
                logger.info("POST：上传基本信息请求手术场次号");
                res = HttpUtil.post(SERVER_INFO_PATH,
                        new ParamCollector(requestCode, JSON.toJSONString(collectionBasicInfoEntity)).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && requestCode == resultVo.getCode()) {
                        logger.info("基本信息上传成功且收到手术场次号:" + resultVo.getData());
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 上传仪器数据采集开始
     * @param deviceEnum 开始采集的仪器信息
     */
    public void postDeviceCollectionStart(DeviceEnum deviceEnum) {

        RequestCodeEnum codeEnum = RequestCodeEnum.POST_DEVICE_COLLECTION_START;
        int requestCode = codeEnum.getCode();

        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传开始采集信息:" + deviceEnum.getCompanyName() + ":" + deviceEnum.getDeviceName());
                String res = HttpUtil.post(SERVER_INFO_PATH, new ParamCollector(requestCode, "" + deviceEnum.getDeviceCode()).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && requestCode == resultVo.getCode()) {
                        logger.info("同意开始采集,仪器号为:" + deviceEnum.getCompanyName() + ":" + deviceEnum.getDeviceName());
                        // 传出成功消息
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 上传某仪器结束采集的信息
     */
    public void postDeviceCollectionStop(DeviceEnum deviceEnum) {
        RequestCodeEnum codeEnum = RequestCodeEnum.POST_DEVICE_COLLECTION_STOP;
        int requestCode = codeEnum.getCode();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("仪器结束采集:" + deviceEnum.getCompanyName() + ":" + deviceEnum.getDeviceName());
                String res = HttpUtil.post(SERVER_INFO_PATH, new ParamCollector(requestCode, "" + deviceEnum.getDeviceCode()).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && codeEnum.getCode() == resultVo.getCode()) {
                        logger.info("收到仪器结束采集信息:" + deviceEnum.getCompanyName() + ":" + deviceEnum.getDeviceName());
                        // 传出成功消息
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 上传某仪器放弃采集的信息
     *
     */
    public void postDeviceCollectionAbandon(DeviceEnum deviceEnum) {
        RequestCodeEnum codeEnum = RequestCodeEnum.POST_DEVICE_COLLECTION_ABANDON;
        int requestCode = codeEnum.getCode();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("仪器放弃本次采集:" + deviceEnum.getCompanyName() + ":" + deviceEnum.getDeviceName());
                String res = HttpUtil.post(SERVER_INFO_PATH, new ParamCollector(requestCode, "" + deviceEnum.getDeviceCode()).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && requestCode == resultVo.getCode()) {
                        logger.info("收到仪器放弃本次采集信息:" + deviceEnum.getCompanyName() + ":" + deviceEnum.getDeviceName());
                        // 传出成功消息
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }

    /**
     * 上传标记信息
     */
    public void postMarkEvent(String markEventString) {
        RequestCodeEnum codeEnum = RequestCodeEnum.POST_OPERATION_MARK_EVENT;
        int requestCode = codeEnum.getCode();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传标记信息");
                String res = HttpUtil.post(SERVER_INFO_PATH, new ParamCollector(requestCode, markEventString).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && requestCode == resultVo.getCode()) {
                        logger.info("收到标记信息:" + resultVo.getData());
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }

    /**
     * 上传手术后对仪器的评价信息
     */
    public void postDeviceEvaluationInfo(String evaluationString) {
        RequestCodeEnum codeEnum = RequestCodeEnum.POST_DEVICE_EVALUATION_TABLE;
        int requestCode = codeEnum.getCode();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传仪器评价信息");
                String res = HttpUtil.post(SERVER_INFO_PATH, new ParamCollector(requestCode, evaluationString).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && requestCode == resultVo.getCode()) {
                        logger.info("收到仪器评价信息:" + resultVo.getData());
                        // 传出成功消息
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }




    /**
     * 上传仪器数据
     */
    public void postDeviceData(String deviceString) {
        RequestCodeEnum codeEnum = RequestCodeEnum.POST_DEVICE_DATA;
        int requestCode = codeEnum.getCode();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.debug("Device Data:" + deviceString);
                String res = HttpUtil.post(SERVER_DATA_PATH, new ParamCollector(requestCode, deviceString).generatePostString());
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && requestCode == resultVo.getCode()) {
                        logger.debug("OK." + resultVo.getData());
                        // 传出成功消息
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 上传采集采集中的错误日志信息
     */
    public void postCollectorErrorInfo(String errorInfo) {
        RequestCodeEnum codeEnum = RequestCodeEnum.POST_COLLECTOR_ERROR_INFO;
        int requestCode = codeEnum.getCode();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传采集器错误信息");
                String res = HttpUtil.post(SERVER_INFO_PATH,
                        new ParamCollector(requestCode, errorInfo).generatePostString());
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 查询常用标记信息列表
     */
    public void getOftenUseMarkEventList() {
        RequestCodeEnum codeEnum = RequestCodeEnum.QUERY_OFTEN_USED_MARK_EVENT_LIST;
        int requestCode = codeEnum.getCode();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("查询常用标记信息列表");
                String res = HttpUtil.post(SERVER_INFO_PATH, new ParamCollector(requestCode, "{}").generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && requestCode == resultVo.getCode()) {
                        logger.info("查询常用标记信息列表:" + resultVo.getData());
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 查询标记信息列表
     */
    public void getMatchedMarkEventList(String keyWord) {
        RequestCodeEnum codeEnum = RequestCodeEnum.QUERY_MATCHED_MARK_EVENT_LIST_BY_KEY_WORD;
        int requestCode = codeEnum.getCode();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("查询匹配标记信息列表");
                String res = HttpUtil.post(SERVER_INFO_PATH, new ParamCollector(requestCode, keyWord).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && requestCode == resultVo.getCode()) {
                        logger.info("查询匹配标记信息列表:" + resultVo.getData());
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }

    /**
     * 上传其他新增的标记信息
     */
    public void postAddSomeOtherNewMarkEvent(String newEvent) {
        RequestCodeEnum codeEnum = RequestCodeEnum.POST_ADD_SOME_OTHER_NEW_MARK_EVENT;
        int requestCode = codeEnum.getCode();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传其他新增的标记信息");
                String res = HttpUtil.post(SERVER_INFO_PATH, new ParamCollector(requestCode, newEvent).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && requestCode == resultVo.getCode()) {
                        logger.info("上传其他新增的标记信息:" + resultVo.getData());
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 更新标记事件的时间
     */
    public void postModifyMarkEventTime(String event) {
        RequestCodeEnum codeEnum = RequestCodeEnum.POST_MODIFY_EVENT_MARK_TIME;
        int requestCode = codeEnum.getCode();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("更新标记事件的时间");
                String res = HttpUtil.post(SERVER_INFO_PATH, new ParamCollector(requestCode, event).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && requestCode == resultVo.getCode()) {
                        logger.info("更新标记事件的时间:" + resultVo.getData());
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }

    /**
     * 删除标记事件
     */
    public void postDeleteMarkEvent(String event) {
        RequestCodeEnum codeEnum = RequestCodeEnum.DELETE_A_MARK_EVENT;
        int requestCode = codeEnum.getCode();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("删除标记事件");
                String res = HttpUtil.post(SERVER_INFO_PATH, new ParamCollector(requestCode, event).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
                    if (resultVo != null && requestCode == resultVo.getCode()) {
                        logger.info("删除标记事件:" + resultVo.getData());
                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
                    } else {
                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
                    }
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }

}
