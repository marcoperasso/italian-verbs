package perassoft.italianverbs;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class StudyActivity extends CommonActivity implements OnItemSelectedListener, OnClickListener {

	private static final String VERB_INDEX = null;
	private static final String MOOD_INDEX = null;
	private static final String TENSE_INDEX = null;
	private Verbs verbs;
	private Spinner spinVerb;
	private Spinner spinMood;
	private Spinner spinTense;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study);

		spinVerb = (Spinner) findViewById(R.id.spinnerVerb);
		spinMood = (Spinner) findViewById(R.id.spinnerMood);
		spinTense = (Spinner) findViewById(R.id.spinnerTense);
		findViewById(R.id.buttonSayVerbs).setOnClickListener(this);
		verbs = MyApplication.getInstance().getVerbs();

		List<String> list = new ArrayList<String>();
		for (Verb v : verbs)
			list.add(v.getName());
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinVerb.setAdapter(dataAdapter);

		
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Verb.moods);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinMood.setAdapter(dataAdapter);
		spinMood.setOnItemSelectedListener(this);

		if (savedInstanceState != null)
		{
			spinVerb.setSelection(savedInstanceState.getInt(VERB_INDEX));
			spinMood.setSelection(savedInstanceState.getInt(MOOD_INDEX));
			spinTense.setSelection(savedInstanceState.getInt(TENSE_INDEX));
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(VERB_INDEX, spinVerb.getSelectedItemPosition());
		outState.putInt(MOOD_INDEX, spinMood.getSelectedItemPosition());
		outState.putInt(TENSE_INDEX, spinTense.getSelectedItemPosition());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
			populateTenses(pos);
	}

	private void populateTenses(int pos) {
		

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, pos == -1 ? new String[0] : Verb.tenses[pos]);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinTense.setAdapter(dataAdapter);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		populateTenses(-1);

	}
	
	private void setLessonResult() {
		int verbIndex = spinVerb.getSelectedItemPosition();
		Verb v = verbs.get(verbIndex);
		String mood = (String) spinMood.getSelectedItem();
		String tense = (String) spinTense.getSelectedItem();
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
		teachVerb(v, startTense, endTense);
	}
	private void teachVerb(Verb verb, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < end; i++)
		{
			String text = verb.get(i);
			if (text.equals("-"))
				text = getString(R.string._missing_);
			message(text, NEUTRAL, getCurrentJokeMessageLocale(), false);
			//message(text, 0, getCurrentJokeMessageLocale(), false);
			if (sb.length() > 0)
				sb.append("\r\n");
			sb.append(text);
			
		}
		StringBuilder title = new StringBuilder();
		title.append(verb.getName());
		title.append(", ");
		title.append(verb.getMood(start));
		title.append(", ");
		title.append(verb.getTense(start));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
		.setTitle(title.toString())
		.setMessage(sb.toString())
		.setCancelable(true)
		.setNegativeButton(android.R.string.ok, null)
				.show();
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonSayVerbs)
			setLessonResult();
		
	}

}
