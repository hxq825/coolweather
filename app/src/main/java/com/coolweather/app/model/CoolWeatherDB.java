package com.coolweather.app.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.db.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2017/2/14.
 *
 *  封装数据库 以方便未来操作
 */

public class CoolWeatherDB {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "cool_weather";
    //版本号
    public static final int VERSION =1;

    //数据库
    private SQLiteDatabase db;

    //  单例 将类私有化
    private static CoolWeatherDB coolWeatherDB;

    /**
     * 将构造方法私有化
     * @param context
     */
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper =new CoolWeatherOpenHelper(context,
                DB_NAME,null,VERSION);
        db =dbHelper.getWritableDatabase();
    }
    /**
     * 提供一个公共的访问方法  单例模式
     * 获取 类的实例
     */
    public synchronized static CoolWeatherDB getInstance(Context context){

        if (coolWeatherDB ==null){
            coolWeatherDB =new CoolWeatherDB(context);
        }

        return coolWeatherDB;
    }
    //省  将实例存储到数据库
    public void saveProvince(Province province){
        if (province !=null){
            //上下文内容储存的一种机制 只能存储基本数据类型，对象不能储存
            ContentValues values =new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /**
     * 将数据库读取全国所有的省份信息
     * @return
     */
    public List<Province> loadProvice(){

        List<Province> list =new ArrayList<Province>();
        //查询
        Cursor cursor =db.query("Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {

                Province province =new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.
                        getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.
                        getColumnIndex("province_code")));
                list.add(province);

            }while (cursor.moveToNext());
        }

        return list;
    }

    /**
     * 将城市实例存储到数据库
     * @param city
     */
    public void saveCity(City city){
        if (city !=null){
            ContentValues values =new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }

    /**
     * 从数据库读取所有城市信息
     * @param provinceId
     * @return
     */
    public List<City> loadCities(int provinceId){

        List<City> list =new ArrayList<City>();
        //查询数据库
        Cursor cursor =db.query("City",null,"province_id=?",
                new String[]{String.valueOf(provinceId)},null,null,null);

    if (cursor.moveToFirst())
        do {
            City city =new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            city.setProvinceId(provinceId);
            list.add(city);

        }while (cursor.moveToNext());
        return list;
    }

//将实例储存到数据库
    public void saveCounty(County county){

        if (county!=null){
            ContentValues values =new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("County",null,values);
        }
    }

    /**
     * 读取某城市的所有县数据
     *
     */

    public List<County> loadCounties(int cityId){
        List<County> list =new ArrayList<County>();
        Cursor cursor =db.query("County",null,"city_id =?",
                new String[]{String.valueOf(cityId)},null,null,null);
        if (cursor.moveToFirst()){

            do {
                County county =new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(
                        cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(
                        cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);

            }while (cursor.moveToNext());
        }

        return list;
    }



}
