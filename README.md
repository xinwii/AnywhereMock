使用android内置的模拟定位实现，可以收藏地址

### 1.开启一个后台service，service中启动一个子线程，每隔100ms不停locationManager.setTestProviderLocation(mMockProviderName, location)
### 2.采用百度地图，模拟定位时，将bd0911转换gps84
#### 1.gcj02：国家要求的加密算法
#### 2.bd0911：百度基于gcj02自行研发的加密算法
#### 3.gps84，gps用的经纬度