package com.nano.common.other;

import android.webkit.JavascriptInterface;

import com.nano.GlobalApplication;
import com.nano.common.logger.Logger;


/**
 * 此类用于将APP的数据传输到WebView的JS中
 * @author cz
 */
public class JsWebDataTransmit {

    private Logger logger = GlobalApplication.getLogger();

    /**
     * 此方法不在主线程执行 使用Handler传递出数据
     * @param value JS中传回来的值
     */
    @JavascriptInterface
    public void setValue(String value){
        logger.info("JS传递至此的值为:" + value);
    }

}
