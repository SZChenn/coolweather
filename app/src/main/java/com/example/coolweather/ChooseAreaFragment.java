package com.example.coolweather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D22396 on 2017/9/20.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private TextView titleText;
    private Button backButton;
    private ListView mListView;

    private ArrayAdapter<String> mAdapter;
    private List<String> dataList = new ArrayList<>();

    //省市县列表
    private List<Province> mProvinceList;
    private List<City> mCityList;
    private List<County> mCountyList;

    //选中的省市县
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    private int currentLevel;//当前选中的级别

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        mListView = view.findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = mProvinceList.get(i);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = mCityList.get(i);
                    queryCounties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });
        queryProvince();
    }
    /**
     * 查询全国所有的县，优先从数据库中查找，没找到再去服务器查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.INVISIBLE);
        mCountyList = DataSupport.where("cityid = ?",
                String.valueOf(selectedCity.getId())).
                find(County.class);
        if(mCountyList.size() > 0){
            dataList.clear();
            for (County county : mCountyList) {
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel =LEVEL_COUNTY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address,"county");
        }
    }
    /**
     * 查询全国所有的市，优先从数据库中查找，没找到再去服务器查询
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.INVISIBLE);
        mCityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectedProvince.getId())).
                find(City.class);
        if(mCityList.size() > 0){
            dataList.clear();
            for (City city : mCityList) {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel =LEVEL_CITY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询全国所有的省，优先从数据库中查找，没找到再去服务器查询
     */
    private void queryProvince(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);
        if(mProvinceList.size() > 0){
            dataList.clear();
            for (Province province : mProvinceList) {
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    private void queryFromServer(String address, String province) {
    }
}
