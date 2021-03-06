package com.devxperiments.wowclockwidget.apppicker;


import com.devxperiments.wowclockwidget.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;

public class App implements Comparable<App>{
	
	public static final String APP_PKG_CLS_PREF = "appPkgClsPref";
	public static final String APP_NONE = "none";
	public static final String APP_CONFIG = "config";
	
	private static final char PKG_CLASS_SEPARATOR = '/';

	private Drawable icon;
	private String applicationName;
	private String packageName;
	private String className;

	
	protected App(String applicationName, Drawable icon){
		this.applicationName = applicationName;
		this.icon = icon;
	}
	
	public App(Context context, String packageName, String className) throws NameNotFoundException{
		this(context.getPackageManager(), new ComponentName(packageName, className));
	}
	
	public App(Context context, ComponentName cn) throws NameNotFoundException{
		this(context.getPackageManager(), cn);
	}
	
	public App(PackageManager pm, ComponentName cn) throws NameNotFoundException{
		this(pm, cn, pm.getApplicationInfo(cn.getPackageName(), PackageManager.GET_META_DATA));
	}
	
	public App(PackageManager pm, ComponentName cn, ApplicationInfo applicationInfo) {
		this.packageName = cn.getPackageName();
		this.className = cn.getClassName();
		this.applicationName = applicationInfo.loadLabel(pm).toString();
		this.icon = applicationInfo.loadIcon(pm);
	}

	public Drawable getIcon() {
		return icon;
	}
	
	public String getApplicationName() {
		return applicationName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public ComponentName getComponentName() {
		return new ComponentName(getPackageName(), getClassName());
	}


	@Override
	public int compareTo(App another) {
		if(this instanceof ConfigApp)
			return -1;
		return getApplicationName().compareToIgnoreCase(another.getApplicationName());
	}
	
	public static App fromPrefString(Context context, String prefString) throws NameNotFoundException {
		if(prefString.equals(APP_CONFIG))
			return new ConfigApp(context);
		if(prefString.equals(APP_NONE))
			return new NoApp(context);
		int indexOfSeparator = prefString.indexOf(PKG_CLASS_SEPARATOR);
		String packageName = prefString.substring(0, indexOfSeparator);
		String className = prefString.substring(indexOfSeparator+1);
		
		if(packageName.equals(ConfigApp.CONFIG_PKG)&&className.equals(ConfigApp.CONFIG_CLS))
			return new ConfigApp(context);
		
		return new App(context, packageName, className);
	}
	
	public String toPrefString(){
		return getPackageName()+PKG_CLASS_SEPARATOR+getClassName();
	}
	
	@Override
	public String toString() {
		return toPrefString();
	}
	
	public static class ConfigApp extends App{
		
		public static final String CONFIG_PKG = "com.devxperiments.wowclockwidget";
		public static final String CONFIG_CLS = "com.devxperiments.wowclockwidget.widget.ConfigActivity";
		
		private String appName;

		private ConfigApp(Context context) throws NameNotFoundException {
			super(context, CONFIG_PKG, CONFIG_CLS);
			appName = context.getString(R.string.strConfig);
		}
		
		@Override
		public String getApplicationName() {
			return appName;
		}
	}
	
	public static ConfigApp getConfigApp(Context context){
		try {
			return new ConfigApp(context);
		} catch (NameNotFoundException e) {
			return null;
		}
	}
	
	public static NoApp getNoApp(Context context){
		return new NoApp(context);
	}
	
	public static class NoApp extends App{

		private NoApp(Context context) {
			super(context.getString(R.string.strNoApp), getNoAppIcon(context)); 
		}
		
		@Override
		public String toPrefString() {
			return APP_NONE;
		}
		
		private static Drawable getNoAppIcon(Context context){
			Drawable drawable = context.getResources().getDrawable(R.drawable.ic_launcher).mutate();
			ColorMatrix matrix = new ColorMatrix();
			matrix.setSaturation(0);
			ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
			drawable.setColorFilter(filter);
			return drawable;
		}
	}

}
