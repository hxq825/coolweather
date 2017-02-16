package com.coolweather.app.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/**
 * Created by User on 2017/2/14.
 *
 *由于服务器返回的省市县数据都是代号|城市，代号|城市这种格式的
 * 提供一个工具类来
 * 解析和处理数据这种数据
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     *
     * @param coolWeatherDB
     * @param response
     * @return
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB
    ,String response){
        if (!TextUtils.isEmpty(response)){
            Log.d("logcat","--------Utility------"+ response);
            String[] allProvinces =response.split(",");
            if (allProvinces !=null&& allProvinces.length>0){
                for (String p:allProvinces){

                    String[] array =p.split("\\|");
                    Province province =new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     *  拼装数据
     */

    public static boolean handleCitiResponse(CoolWeatherDB coolWeatherDB,
            String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            Log.d("logcat","--------Utility------"+ response);
            String[] allCities = response.split(",");
            if (allCities !=null && allCities.length>0){
                for (String c: allCities){
                    String[] array =c.split("\\|");

                    City city =new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到City表
                    coolWeatherDB.saveCity(city);

                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     *
     */

    public static boolean handleCountiesRsponse(CoolWeatherDB coolWeatherDB,
            String response, int cityId){
        if (!TextUtils.isEmpty(response)){
            Log.d("logcat","--------Utility------"+ response);
        String[] allCounties = response.split(",");
            if (allCounties!=null&&allCounties.length>0){
                for (String c:allCounties){
                    String[] array =c.split("\\|");

                    County county =new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的数据存储到county表
                    coolWeatherDB.saveCounty(county);

                }
                return true;
            }
        }
        return false;
    }


    /**
     * 天气
     * 解析服务器返回的全部JSON数据，并将解析出的数据存储到本地
     * 接口信息如下
     *{"weatherinfo":{"city":"昆山","cityid":"101190404","temp1":"21°C","temp2":"9°C",
     * "weather":"多云转小雨","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}}
     * 图片不打算用 img
     *
     */

    public static void handleWeatherResponse(Context context,String response){

        try{
            JSONObject jsonObject =new JSONObject(response);
            JSONObject weatherInfo =jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode =weatherInfo.getString("cittyid");
            String temp1 =weatherInfo.getString("temp1");
            String temp2 =weatherInfo.getString("temp2");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime =weatherInfo.getString("ptime");
            //解析完之后保存
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2
            ,weatherDesp,publishTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中
     */

    @TargetApi(Build.VERSION_CODES.N)
    public static void saveWeatherInfo(Context context,String cityName,
            String weatherCode,String temp1,String temp2,String weather,String publishTime){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        //
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("",temp1);
        editor.putString("",temp2);
        editor.putString("",weather);
        editor.putString("",publishTime);
        editor.putString("",sdf.format(new Date()));
        editor.commit();



    }

}
