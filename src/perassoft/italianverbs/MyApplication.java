package perassoft.italianverbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	
	private static MyApplication application;
	private Verbs verbs;
	private boolean verbsLoaded;
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
		if (verbsLoaded)
			return verbs;
		synchronized (this) {
			if (verbsLoaded)
				return verbs;
			verbs = Verbs.loadVerbs();
			verbsLoaded = true;
		}
		return verbs;
	}
	
	public void saveObject(File file, Object obj) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(fos);
			out.writeObject(obj);
			out.flush();
		} finally {
			out.close();
			fos.close();
		}
		
	}

	public Object readObject(File file) {
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				ObjectInput in = null;
				try {
					in = new ObjectInputStream(fis);
					try {
						return in.readObject();
					} catch (Exception ex) {
						
					}
				} catch (Exception e) {
					
				} finally {
					in.close();
					fis.close();
				}
			} catch (Exception e) {
				
			}

		}
		return null;
	}
}
