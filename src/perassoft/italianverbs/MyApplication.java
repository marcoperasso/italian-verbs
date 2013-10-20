package perassoft.italianverbs;

import android.app.Application;

public class MyApplication extends Application {
	
	private static MyApplication application;
	@Override
	public void onCreate() {
		this.application = this;
		super.onCreate();
	}
	
	public static MyApplication getInstance() {
		return application;
	}
	
	
}
