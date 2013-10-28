package perassoft.italianverbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Application;

public class MyApplication extends Application {

	private static MyApplication application;
	private Verbs verbs;
	private boolean verbsLoaded;
	private ArrayList<Integer> messages = new ArrayList<Integer>();

	@Override
	public void onCreate() {
		super.onCreate();
		MyApplication.application = this;
		//Verbs.dummy();//solo per caricare la classe
		new Thread(new Runnable() {

			@Override
			public void run() {
				getVerbs();
			}
		}).start();
	}

	
	public static MyApplication getInstance() {
		return application;
	}

	public void resetVerbs() {
		synchronized (this) {
			verbsLoaded = false;
		}
	}

	public Verbs getVerbs() {
		if (verbsLoaded)
			return verbs;
		synchronized (this) {
			if (verbsLoaded)
				return verbs;
			verbs = Verbs.loadVerbs();
			verbs.sort();
			verbs.countVisibleVerbs();
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


	public boolean isMessageShown(int messageId) {
		return messages .contains(messageId);
	}


	public void setMessageShown(int messageId) {
		messages.add(messageId);
		
	}
	
}
