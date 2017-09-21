package com.example.coolweather.gson;

/**
 * Created by D22396 on 2017/9/21.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
