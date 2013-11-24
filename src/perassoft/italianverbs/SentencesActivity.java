package perassoft.italianverbs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SentencesActivity extends CommonActivity implements
		OnClickListener, OnEditorActionListener,
		OnCheckedChangeListener {

	private static final int menuDeleteSentence = 0;
	private Sentences sentences;
	private int mActiveSentence;
	private EditText sentence;
	private ListView listView;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sentences);

		sentences = Sentences.getSentences();
		findViewById(R.id.buttonSpeak).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.listViewSentences);
		registerForContextMenu(listView);
		sentence = (EditText) findViewById(R.id.editTextSentence);
		sentence.setOnEditorActionListener(this);
		sentence.setImeOptions(EditorInfo.IME_ACTION_DONE);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, sentences);

		listView.setAdapter(adapter);
		CheckBox cbMine = (CheckBox) findViewById(R.id.cbMine);
		cbMine.setChecked(sentences.isOnlyMine());
		cbMine.setOnCheckedChangeListener(this);
		showLabel();
	}

	private void showLabel() {
		findViewById(R.id.tvNoSentences).setVisibility(sentences.size() == 0 ? View.VISIBLE : View.GONE );
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.listViewSentences) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			mActiveSentence = info.position;
			String sentence = sentences.get(mActiveSentence);
			menu.setHeaderTitle(sentence);

			menu.add(Menu.NONE, menuDeleteSentence, 0,
					perassoft.italianverbs.R.string.delete_sentence);
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE
				|| actionId == EditorInfo.IME_ACTION_DONE
				|| (event.getAction() == KeyEvent.ACTION_DOWN && event
						.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

			addSentence(v.getText().toString());
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			return true;
		}
		return false;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		if (item.getItemId() == menuDeleteSentence) {
			sentences.remove(mActiveSentence);
			try {
				sentences.save();
			} catch (IOException e) {
				Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
			}
			adapter.notifyDataSetChanged();
			showLabel();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.buttonSpeak: {
			startVoiceRecognitionActivity();
		}
		}

	}

	private void addSentence(String text) {

		if (text.length() > 0) {
			sentences.add(text);
			
			try {
				sentences.save();
			} catch (IOException e) {
				Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
			}
			adapter.notifyDataSetChanged();
			showLabel();
			listView.smoothScrollToPosition(sentences.indexOf(text));
		}
	}

	private void startVoiceRecognitionActivity() {
		if (!voiceRecognition) {
			Toast.makeText(this, R.string.voice_recognition_not_available,
					Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());

		// Display an hint to the user about what he should say.
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				R.string.tell_your_sentence_to_miss_puzzy);

		// Given an hint to the recognizer about what the user is going to say
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

		// Specify how many results you want to receive. The results will be
		// sorted
		// where the first result is the one with higher confidence.
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

		startActivityForResult(intent, RESULT_VOICE_RECOGNITION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_VOICE_RECOGNITION) {
			if (resultCode == RESULT_OK) {
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				addSentence(matches.get(0));

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		sentences.setOnlyMine(isChecked);
		try {
			sentences.save();
		} catch (IOException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}

	}
}
