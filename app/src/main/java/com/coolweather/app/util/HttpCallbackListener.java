package com.coolweather.app.util;

/**
 * Created by User on 2017/2/14.
 */

public interface HttpCallbackListener {

    void onFnish(String response);

    void onError(Exception e);
}
