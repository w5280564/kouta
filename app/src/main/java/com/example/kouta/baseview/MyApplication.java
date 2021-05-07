package com.example.kouta.baseview;


import android.app.Application;
import android.content.Context;



public class MyApplication extends Application {
	private static final String TAG = "JPush";
	public static Context mycontext;

	@Override
	public void onCreate() {
		super.onCreate();


		//xUtils初始化
//		x.Ext.init(this);
//		x.Ext.setDebug(true); // 是否输出debug日志
//
////
////		//Jush初始化
//		Log.d(TAG, "[ExampleApplication] onCreate");
//		JPushInterface.setDebugMode(true);
//		JPushInterface.init(this);
////
//		//Fresco初始化
//		Fresco.initialize(this);

		mycontext = this;
	 }
}
