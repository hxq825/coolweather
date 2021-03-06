package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by User on 2017/2/14.
 *      提供访问网络 服务器
 */

public class HttpUtil {

    public static void sendHttpRequst(final String address,
            final HttpCallbackListener listener){

        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpURLConnection connection =null;

                try {
                    URL url =new URL(address);
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in =connection.getInputStream();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(in));

                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine())!=null){

                        response.append(line);
                    }
                    if (listener !=null){
                        //回调
                        listener.onFnish(response.toString());
                    }

                }catch (Exception e){
                    //回调
                    if (listener!=null){
                        listener.onError(e);
                    }
                }
            finally {
              if (connection!=null){
                  connection.disconnect();
              }
                }
            }
        }).start();
    }

}
