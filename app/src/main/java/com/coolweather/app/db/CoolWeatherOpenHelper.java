package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 2017/2/14.
 *
 *  建立数据库的表
 *  用于存放省、市、县
 */

public class CoolWeatherOpenHelper extends SQLiteOpenHelper{

    /**
     * 创建省 数据库表 包括id,省名字，省标识代码  语句
     *
     */
    public static final String CREATE_PROVINCE ="create table Province(" +
            "id integer primary key autoincrement," +
            "province_name text," +
            "province_code text)";

    /**
     *  省份里的城市表 包括id 城市名 标识符 关联省份外键 语句
     */
    public static final String CREATE_CITY ="create table City(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer)";
    /**
     * 创建县表 包括县id 县名，标识符，关联上级id 语句
     */
    public static final String CREATE_COUNTY="create table county(" +
            "id integer primary key autoincrement," +
            "county_name text," +
            "county_code text," +
            "city_id integer)";



    /**
     *
     * @param context
     * @param name
     * @param factory
     * @param version
     */

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建三个表
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
