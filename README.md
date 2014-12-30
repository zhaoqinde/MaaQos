MAA 加速效果sdk嵌入文档
一		集成SDK
	1. 导入jar包
		把maa_sdk_android.jar和maa_sdk_qos.jar复制到libs目录下。
	2. 在 AndroidManifest.xml 中增加 sdk 所需的权限声明
		<uses-permissionandroid:name="android.permission.INTERNET"/> 
<uses-permissionandroid:name="android.permission.ACCESS_NETWORK_STATE"/> 
<uses-permissionandroid:name="android.permission.ACCESS_WIFI_STATE"/> 
<uses-permissionandroid:name="android.permission.READ_PHONE_STATE"/>  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
二		代码嵌入
	1. 创建MaaQos对象
		MaaQos maaQos = new MaaQos();
	2. 传入需要测试的url列表
		maaQos.setUrl (ArrayList<String>  testUrls);
	3. 传入当前所在城市
		maaQos.setProvince(String  province)；
	4. 启动服务
		maaQos.start(Context  context);
三		混淆设置（非必须）
如果应用程序需要做混淆编译，请在混淆配置文件中加入以下代码：
 
 	-keep class com.mato.** { *; } 
-keep class com.maa.** { *; }
 	-dontwarn com.mato.** 
	-dontwarn com.maa.**
 	-keepattributes Exceptions, Signature, InnerClasses, EnclosingMethod  
