package perassoft.italianverbs;

import android.app.Application;

public class MyApplication extends Application {
	
	private static MyApplication application;
	private Verbs verbs;
	@Override
	public void onCreate() {
		MyApplication.application = this;
		new Thread(new Runnable(){

			@Override
			public void run() {
				getVerbs();
			}}).start();
		super.onCreate();
	}
	
	public static MyApplication getInstance() {
		return application;
	}

	public Verbs getVerbs() {
		if (verbs == null)
		{
		synchronized (this) {
			if (verbs == null)
			{
				verbs = new Verbs();
			}
		}
		}
		return verbs;
	}
	
}
