package com.coolweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2017/2/14.
 *
 * 遍历省市县
 *
 */

public class ChooseAreaActivity extends Activity {

    //级别
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY =2;

    private ProgressDialog mProgressDialog;//进度条
    private TextView titlText ;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private CoolWeatherDB mCoolWeatherDB;//封装数据库
    //
    private List<String> dataList =new ArrayList<String>();

    /**
     * 省列表
     */
    private List<Province> mProvinceList;
    /**
     * 市列表
     */
    private List<City> mCityList;

    /**
     * 县列表
     */
    private List<County> mCountyList;
    //选中省份
    private Province selectedProvince;
    //选中城市
    private City selectedCity;

    //当前选中的级别
    private int currentLevel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        titlText = (TextView) findViewById(R.id.title_text);
        mListView = (ListView) findViewById(R.id.list_view);
        //适配器
        mAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        mListView.setAdapter(mAdapter);

        mCoolWeatherDB =CoolWeatherDB.getInstance(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //选中
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince =mProvinceList.get(i);
                    //下一级市级要加载显示出来
                    queryCities();
                } else if (currentLevel ==LEVEL_CITY){
                    selectedCity =mCityList.get(i);
                //下一级县要加载出来
                    queryCounties();
                }
            }
        });
        queryProvinces();//加载省级数据

    }
    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到在去服务器上查询
     */
    private void queryProvinces(){
        mProvinceList =mCoolWeatherDB.loadProvice();
        if (mProvinceList.size()>0){
            dataList.clear();
            for (Province province:mProvinceList){

                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();//更新
            mListView.setSelection(0);
            titlText.setText("中国");
            currentLevel =LEVEL_PROVINCE;

        }else {
            //从服务器获取
            queryFromServer(null,"province");
        }
    }

    private void queryCities(){
        mCityList =mCoolWeatherDB.loadCities(selectedProvince.getId());

        if (mCityList.size()>0){
            dataList.clear();
            for (City city:mCityList){
                dataList.add(city.getCityName());//城市名填加到集合
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            titlText.setText(selectedProvince.getProvinceName());
            currentLevel =LEVEL_CITY;//选中级别

        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }

    }
    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到在去服务器上查询
     */
    private void queryCounties(){
        mCountyList =mCoolWeatherDB.loadCounties(selectedCity.getId());
        if (mCountyList.size()>0){
            //从数据库获取
            dataList.clear();
            for (County county: mCountyList){
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            titlText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;//选中级别  3级县级
        } else {

            //从服务器获取
            queryFromServer(selectedCity.getCityCode(),"county");

        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县数据
     */

    private void queryFromServer(final String code,final String type){

        String address;
        if (!TextUtils.isEmpty(code)){
            //接口
            address ="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }
        else {
            address ="http://www.weather.cpm.cn/data/list3/city.xml";
        }
        //
        HttpUtil.sendHttpRequst(address, new HttpCallbackListener() {
            @Override
            public void onFnish(String response) {
                boolean result =false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(mCoolWeatherDB,response);
                }else if ("city".equals(type)){
                    result =Utility.handleCitiResponse(mCoolWeatherDB,
                            response,selectedProvince.getId());
                }
                else if ("county".equals(type)){
                    result =Utility.handleCountiesRsponse(mCoolWeatherDB,response,
                            selectedCity.getId());
                }
                if (result){
                    //通过runOnUoThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog();//显示进度对话框
                            if ("province".equals(type)){
                                queryProvinces();
                            } else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }

            }

            @Override
            public void onError(Exception e) {
            //通过runOnUiTread()方法回到主线程处理逻辑
                //因为UI界面处涉及线程，需要在主线程处理
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    closePrgressDialog();//关闭进度对话框
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
    /**
     * 显示进度对话框
     */

    private void showProgressDialog(){

        if (mProgressDialog ==null){
            mProgressDialog=new ProgressDialog(this);
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);//弹出后会点击其它屏幕不消失

        }
        mProgressDialog.show();
    }
    /**
     * 关闭进度对话框
     */
    private void closePrgressDialog(){
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();//解散 关闭
        }

    }

    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表 还是直接退出
     */

    @Override
    public void onBackPressed() {
        if (currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel==LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }
}










