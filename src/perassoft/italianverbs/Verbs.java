package perassoft.italianverbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.content.SharedPreferences;

public class Verbs extends ArrayList<Verb> {
	private static final String VISIBLE_VERBS = "VISIBLE_VERBS";
	private static final String VERBI_BIN = "Verbi.bin";
	/**
	 * 
	 */
	private static final long serialVersionUID = 3464198266953132774L;
	private int visibleVerbs;

	private Verbs() {
		
	}

	private static Verbs loadFromResource() {
		try {
			Verbs verbs = new Verbs();
			InputStream is = MyApplication.getInstance().getResources()
					.openRawResource(R.raw.verbi);

			BufferedReader input = new BufferedReader(
					new InputStreamReader(is), 1024 * 8);

			String line = null;
			while ((line = input.readLine()) != null) {
				Verb verb = new Verb(line);
				for (int i = 0; i < Verb.MAX_VERBS; i++) {
					line = input.readLine();
					verb.add(line);
				}
				verbs.add(verb);
			}
			input.close();
			return verbs;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Verbs loadVerbs() {
		File file = MyApplication.getInstance().getFileStreamPath(VERBI_BIN);
		if (!file.exists()) {
			Verbs verbs = loadFromResource();
			try {
				MyApplication.getInstance().saveObject(file, verbs);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return verbs;
		} else {
			return (Verbs) MyApplication.getInstance().readObject(file);
		}
	}

	public Verb get(String string) {
		for (Verb v : this)
			if (v.getName().equalsIgnoreCase(string))
				return v;
		return null;
	}

	public void delete(Verb verb) throws IOException {
		remove(verb);
		File file = MyApplication.getInstance().getFileStreamPath(VERBI_BIN);
		MyApplication.getInstance().saveObject(file, this);
	}

	public static void setHiddenVerb(Verb v, boolean b) {
		SharedPreferences settings = MyApplication.getInstance().getSharedPreferences(VISIBLE_VERBS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(VISIBLE_VERBS + v.getName(), b);
		editor.commit();
		
	}
	

	public static boolean isHiddenVerb(Verb verb) {
		SharedPreferences settings = MyApplication.getInstance().getSharedPreferences(VISIBLE_VERBS, 0);
		return settings.getBoolean(VISIBLE_VERBS + verb.getName(), false);
	}

	public int countVisibleVerbs() {
		visibleVerbs = 0;
		for (int i = 0; i < size(); i++)
			if (!isHiddenVerb(get(i)))
				visibleVerbs++;
		return visibleVerbs;
	}

	public int getVisibleVerbs() {
		return visibleVerbs;
	}
}
