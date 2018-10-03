package com.anywherecat.app

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import com.anywherecat.app.db.CollectDao
import com.anywherecat.app.dialog.SettingDialog
import com.anywherecat.app.model.Constants
import com.baidu.location.*

import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BitmapDescriptor
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.InfoWindow
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.geocode.GeoCodeResult
import com.baidu.mapapi.search.geocode.GeoCoder
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName
    private lateinit var mMapView: MapView
    private lateinit var mBaiduMap: BaiduMap
    private lateinit var mLocationClient: LocationClient
    private lateinit var myListener: BDAbstractLocationListener
    private lateinit var latLng: LatLng
    private lateinit var markerOptions: MarkerOptions
    private lateinit var bitmapDescriptor: BitmapDescriptor
    private var marker: Marker? = null
    private lateinit var settingDialog: SettingDialog
    private lateinit var mSearch: GeoCoder
    private var address: String = ""
    private lateinit var infoWindowView: View
    private lateinit var addrTv: TextView
    private val toolbar: Toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mLocationClient = LocationClient(applicationContext) // 声明LocationClient类
        myListener = MyLocationListener()
        mLocationClient.registerLocationListener(myListener) // 注册监听函数
        mLocationClient.locOption = initLocOption()
        mSearch = GeoCoder.newInstance()
        initToolbar()
        mSearch.setOnGetGeoCodeResultListener(object : OnGetGeoCoderResultListener {
            override fun onGetGeoCodeResult(geoCodeResult: GeoCodeResult) {

            }

            override fun onGetReverseGeoCodeResult(reverseGeoCodeResult: ReverseGeoCodeResult?) {
                if (reverseGeoCodeResult != null && reverseGeoCodeResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    address = reverseGeoCodeResult.address
                    if (::addrTv.isInitialized) {
                        addrTv.text = address
                    }
                    initInfoWindow()
                }
            }
        })
        mLocationClient.start()// 开始定位

        mMapView = findViewById<View>(R.id.mmap) as MapView
        mBaiduMap = mMapView.map
        mBaiduMap.mapType = BaiduMap.MAP_TYPE_NORMAL//普通地图
        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_mark)
        settingDialog = SettingDialog.Builder(this).create()
        //   initLocation();
        mBaiduMap.setOnMapStatusChangeListener(object : BaiduMap.OnMapStatusChangeListener {
            override fun onMapStatusChangeStart(mapStatus: MapStatus) {

            }

            override fun onMapStatusChangeStart(mapStatus: MapStatus, i: Int) {

            }

            override fun onMapStatusChange(mapStatus: MapStatus) {
                if (!::markerOptions.isInitialized) {
                    markerOptions = MarkerOptions().position(mapStatus.target).icon(bitmapDescriptor).zIndex(9)
                    marker = mBaiduMap.addOverlay(markerOptions) as Marker
                }
                marker!!.position = mapStatus.target
                latLng = mapStatus.target
                initInfoWindow()
            }

            override fun onMapStatusChangeFinish(mapStatus: MapStatus) {
                latLng = mapStatus.target
                mSearch.reverseGeoCode(ReverseGeoCodeOption().location(latLng))
            }
        })
        initInfoView()
    }

    override fun onResume() {
        super.onResume()
        if (settingDialog != null && settingDialog.isShowing) {
            settingDialog.cancel()
        }
    }

    private fun initToolbar() {
        toolbar.inflateMenu(R.menu.item)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_collect -> {
                    startActivity(Intent(this, CollectActivity::class.java))
                }
                R.id.action_collect2 -> {
                    startActivity(Intent(this, CollectActivity::class.java))
                }
                R.id.action_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                }
                R.id.action_intro -> {
                    startActivity(Intent(this, IntroActivity::class.java))
                }
                R.id.action_stop_location -> {
                    stopService(Intent(this, LocationService::class.java))
                    Toast.makeText(this, "模拟定位已停止", Toast.LENGTH_LONG).show()
                }
                R.id.action_down_apk -> {
                    var intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    var url = Uri.parse(Constants.WECHAT_URL)
                    intent.data = url
                    startActivity(intent)
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun initInfoView() {
        infoWindowView = LayoutInflater.from(this).inflate(R.layout.view_infowindow, null, false)
        addrTv = infoWindowView.findViewById(R.id.tv_addr)
        infoWindowView.findViewById<View>(R.id.btn_collect).setOnClickListener {
            addCollect()
            //  CollectDialog.Builder(this).setAddress(address).setLatitude(latLng.latitude).setLongitude(latLng.longitude).create().show()
        }
        infoWindowView.findViewById<View>(R.id.btn_through).setOnClickListener {
            // mBaiduMap.hideInfoWindow();
            if (canMockPosition() && !isGPSEnable()) {
                startService(Intent(this, LocationService::class.java).putExtra(Constants.LATLNG, latLng))
                AlertDialog.Builder(this).setTitle("穿越成功")
                        .setMessage("你成功穿越到了 $address\n\n提醒:可能需要打开位置信息,微信需6.3以下版本(不包含6.3),纷享销客5.20以下(不包含5.20),今目标，微博等均支持。")
                        .setPositiveButton("确定", null)
                        .create().show()
            } else {
                settingDialog = SettingDialog.Builder(this).setMock(canMockPosition()).setLocation(!isGPSEnable()).create()
                settingDialog.show()
            }
        }
    }

    private fun addCollect() {
        var linearLayout = layoutInflater.inflate(R.layout.dialog_collect2, null) as LinearLayout
        var noteEt = linearLayout.findViewById<EditText>(R.id.modifyedittext)
        var textView = linearLayout.findViewById(R.id.beizhu_textv) as TextView
        var builder = AlertDialog.Builder(this)
        textView.text = "备注名："
        builder.setTitle("添加收藏")
        builder.setMessage("收藏地点：\n" + this.address)
        builder.setView(linearLayout)
        builder.setNegativeButton("确定") { dialog, which ->
            run {
                if (TextUtils.isEmpty(noteEt.text.toString().trim())) {
                    Toast.makeText(this, "请输入备注名", Toast.LENGTH_LONG).show()
                } else {
                    var dao = CollectDao(this)
                    dao.insert(noteEt.text.toString().trim(), address, latLng.latitude, latLng.longitude)
                }
            }
        }
        builder.setPositiveButton("取消", null)
        builder.show()
    }


    private fun initInfoWindow() {
        //在地图中显示一个信息窗口，可以设置一个View作为该窗口的内容，也可以设置一个 BitmapDescriptor 作为该窗口的内容
        val infoWindow = InfoWindow(infoWindowView, latLng, -80)
        //InfoWindow infoWindow = new InfoWindow(button, latLng, -47);
        //显示信息窗口
        mBaiduMap.showInfoWindow(infoWindow)
    }


    private fun initLocOption(): LocationClientOption {
        val option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy//默认高精度定位模式
        option.setCoorType("bd09ll")//默认gcj02，设置返回的定位结果坐标系
        // option.setScanSpan(1000);//默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms
        option.setIsNeedAddress(true)//设置是否需要地址信息，默认不需要
        option.isOpenGps = true//默认false,设置是否使用gps
        option.isLocationNotify = true//默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true)//默认false，设置是否需要位置语义化结果
        option.setIsNeedLocationPoiList(true)//默认false，设置是否需要POI结果
        option.setIgnoreKillProcess(false)//默认true不杀死进程，设置是否在stop的时候杀死进程
        option.SetIgnoreCacheException(false)//默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false)//默认false，设置是否需要过滤GPS仿真结果，默认需要
        return option
    }

    private inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation?) {
            if (location == null) {
                Toast.makeText(this@MainActivity, "地址不可用", Toast.LENGTH_SHORT).show()
                return
            }
            Log.e(TAG, location.longitude.toString() + "??" + location.latitude.toString())
            latLng = if (location.latitude.toString() == "4.9E-324") {
                LatLng(40.058973, 116.312713)
            } else {
                LatLng(location.latitude, location.longitude)
            }
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng))
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f))
            mSearch.reverseGeoCode(ReverseGeoCodeOption().location(latLng))
        }

    }

    override fun onDestroy() {
        if (mLocationClient != null && mLocationClient.isStarted) {
            if (myListener != null) {
                mLocationClient.unRegisterLocationListener(myListener)
            }
            mLocationClient.stop()
        }
        super.onDestroy()
    }

    private fun canMockPosition(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val str = LocationManager.GPS_PROVIDER
        return if (Build.VERSION.SDK_INT <= 22) {
            Settings.Secure.getString(contentResolver, "mock_location") != "0"
        } else {
            try {
                locationManager.addTestProvider(str, false, true, false, false, false, false, false, 0, 5)
                locationManager.setTestProviderEnabled(str, true)
                locationManager.setTestProviderStatus(str, 2, null, System.currentTimeMillis())
                true
            } catch (e: Exception) {
                false
            }

        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        var latLng = intent.getParcelableExtra<LatLng>(Constants.LATLNG)
        latLng = LatLng(latLng.latitude, latLng.longitude)
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng))
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f))
        mSearch.reverseGeoCode(ReverseGeoCodeOption().location(latLng))

    }

    fun isGPSEnable(): Boolean {
        var string = Settings.Secure.getString(contentResolver, "location_providers_allowed")
        return string?.contains("gps") ?: false
    }


}