package com.coolweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.coolweather.app.model.Province;
import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

/**
 * Created by User on 2017/2/16.
 *
 * 实现后台自动更新天气
 */

public class AutoUpdateService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        //创建定时任务   为了保证软件不会消耗过多的流量
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour =8* 60* 60 *1000;//8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;//定时器
        Intent i =new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather(){
        SharedPreferences pfs = PreferenceManager.getDefaultSharedPreferences(this);

        String weatherCode =pfs.getString("weather_code","");
        String address ="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        HttpUtil.sendHttpRequst(address, new HttpCallbackListener() {
            @Override
            public void onFnish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);

            }

            @Override
            public void onError(Exception e) {
            e.printStackTrace();
            }
        });


    }
}
