package perassoft.italianverbs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class Learn {

	private Context context;
	private int verbIndex;
	private CharSequence mood;
	private CharSequence tense;
	LearnDone done;
	private Verbs verbs;

	public Learn(Context context) {
		this.context = context;

	}

	public void start(LearnDone done) {
		this.done = done;
		chooseVerb();

	}

	private void chooseVerb() {

		verbs = MyApplication.getInstance().getVerbs();
		final CharSequence[] items = new CharSequence[verbs.size()];

		for (int i = 0; i < verbs.size(); i++) {
			items[i] = verbs.get(i).getName();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(R.string.choose_verb)
				.setSingleChoiceItems(items, -1, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int index) {
						verbIndex = index;
						chooseMood();
						dialog.dismiss();
					}

				}).show();
	}

	private void chooseMood() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(R.string.choose_mood)
				.setSingleChoiceItems(Verb.moods, -1, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int index) {
						mood = Verb.moods[index];
						chooseTense(index);
						dialog.dismiss();

					}

				}).show();

	}

	private void chooseTense(int index) {
		final String[] tenses = Verb.tenses[index];
		if (tenses.length == 1) {
			tense = tenses[0];
			finish();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(R.string.choose_tense)
				.setSingleChoiceItems(tenses, -1, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int index) {
						tense = tenses[index];
						finish();
						dialog.dismiss();
					}

				}).show();
	}

	private void finish() {
		Verb v = MyApplication.getInstance().getVerbs().get(verbIndex);
		int startMood = -1;
		int endMood = Verb.MAX_VERBS;
		for (int i = 0; i < Verb.MAX_VERBS; i++) {
			if (startMood == -1) {
				if (v.getMood(i).equals(mood))
					startMood = i;
			} else {
				if (!v.getMood(i).equals(mood)) {
					endMood = i;
					break;
				}

			}
		}
		
		
		int startTense = -1;
		int endTense = endMood;
		for (int i = startMood; i < endMood; i++) {
			if (startTense == -1) {
				if (v.getTense(i).equals(tense))
					startTense = i;
			} else {
				if (!v.getTense(i).equals(tense)) {
					endTense = i;
					break;
				}

			}
		}
		done.onChooseTense(verbs.get(verbIndex), startTense, endTense);
	}
}
