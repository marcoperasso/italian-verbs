package perassoft.italianverbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Verbs extends ArrayList<Verb> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3464198266953132774L;

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
		File file = MyApplication.getInstance().getFileStreamPath("Verbi.bin");
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

}
