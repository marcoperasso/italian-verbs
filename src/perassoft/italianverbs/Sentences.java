package perassoft.italianverbs;

import java.io.IOException;
import java.util.ArrayList;

public class Sentences extends ArrayList<String> {

	private boolean onlyMine;
	private static final String SENTENCES_BIN = "sentences.bin";
	/**
	 * 
	 */
	private static final long serialVersionUID = 5232648094390766379L;
	private static Sentences sentences;

	public static Sentences getSentences() {
		if (sentences == null) {
			if (MyApplication.getInstance().getFileStreamPath(SENTENCES_BIN)
					.exists())
				sentences = (Sentences) Helper.readObject(
						MyApplication.getInstance(), SENTENCES_BIN);
			if (sentences == null)
				sentences = new Sentences();
		}
		return sentences;
	}
	
	public void save() throws IOException
	{
		Helper.saveObject(MyApplication.getInstance(), SENTENCES_BIN, this);
	}

	public boolean isOnlyMine() {
		return onlyMine;
	}

	public void setOnlyMine(boolean onlyMine) {
		this.onlyMine = onlyMine;
	}
	
	
}