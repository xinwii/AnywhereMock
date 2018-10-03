package com.anywherecat.app

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import com.anywherecat.app.utils.PositionUtil
import com.baidu.mapapi.model.LatLng

/**
 * @author xinwii
 * 定位服务
 */

class LocationService : Service() {
    private val TAG = javaClass.simpleName
    private lateinit var locationManager: LocationManager
    private val mMockProviderName = LocationManager.GPS_PROVIDER
    private lateinit var intent:Intent

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initLocation()
        Thread{
            while (true){
                Thread.sleep(100)
                mockLocation()
            }
        }.start()
    }
    /**
     * setLocation 设置GPS的位置
     */
    private fun mockLocation() {
        val location = Location(mMockProviderName)
        location.time = System.currentTimeMillis()
        var latlng:LatLng = intent.getParcelableExtra("latLng")
        var gps = PositionUtil.bd09_To_Gps84(latlng.latitude, latlng.longitude)
        location.latitude = gps.wgLat
        location.longitude = gps.wgLon
        location.altitude = 2.0
        location.accuracy = 3.0f
        if (Build.VERSION.SDK_INT > 16) {
            //api 16以上的需要加上这一句才能模拟定位 , 也就是targetSdkVersion > 16
            location.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }

        Log.e(TAG,"start location")
        locationManager.setTestProviderLocation(mMockProviderName, location)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        this.intent = intent!!
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * inilocation 初始化 位置模拟
     */
    private fun initLocation() {
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.addTestProvider(mMockProviderName, false, true, false,
                false, true, true, true, 0, 5)
        locationManager.setTestProviderEnabled(mMockProviderName, true)
    }

}