package entertainment.apc.com.myapplication;

import android.os.Handler;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by apc on 2017/4/5.
 */

public interface ResponseListenerCallBack {
    /**
     * 请求错误的时候执行的方法
     * @param request
     * @param e
     */
    public void onFailed(String failedMsg);

    /**
     * 请求正确的时候执行的方法
     * @param result
     */
    public void onSucceed(String result);
}
