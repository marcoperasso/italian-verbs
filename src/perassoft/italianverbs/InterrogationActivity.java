package perassoft.italianverbs;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class InterrogationActivity extends CommonActivity implements
		OnClickListener, OnEditorActionListener {

	private static final String SCORE = "score";
	private static final String VERBINDEX = "VIDX";
	private static final String QUESTION = "Q";
	private Random random;
	private int score = 0;
	private String[] messages;
	private Verb verb;
	private int question;
	private int verbIndex;
	private Button mSpeakButton;
	private Button mHelpButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interrogation);

		random = new Random(System.currentTimeMillis());
		messages = getResources().getStringArray(R.array.joke_messages);

		if (savedInstanceState != null) {
			score = savedInstanceState.getInt(SCORE, 0);
			verbIndex = savedInstanceState.getInt(VERBINDEX);
			question = savedInstanceState.getInt(QUESTION);
			verb = MyApplication.getInstance().getVerbs().getVisibleVerbs()
					.get(verbIndex);
			setQuestionText();
		}

		updateScoreView();
		mSpeakButton = (Button) findViewById(R.id.buttonSpeak);
		mSpeakButton.setOnClickListener(this);
		mHelpButton = (Button) findViewById(R.id.buttonHelp);
		mHelpButton.setOnClickListener(this);

		EditText editText = (EditText) findViewById(R.id.editTextAnswer);
		editText.setOnEditorActionListener(this);
		editText.setImeOptions(EditorInfo.IME_ACTION_DONE);

	}

	private Locale getRandomMessage(StringBuilder sb) {
		
		Sentences ss = Sentences.getSentences();
		if (ss.isOnlyMine())
		{
			if (ss.size() == 0)
			{
				return null;
			}
			int idx = random.nextInt(ss.size());
			
			sb.append(ss.get(idx));
			return Locale.getDefault();
		}
		int idx = random.nextInt(messages.length + ss.size());
		if (idx < messages.length)
		{
			sb.append(messages[idx]);
			return getCurrentJokeMessageLocale();
		}
		sb.append(ss.get(idx - messages.length));
		return Locale.getDefault();
	}

	private void updateScoreView() {
		TextView scoreText = (TextView) findViewById(R.id.textViewScore);
		scoreText.setText(String.format(getString(R.string.score), score));

	}

	@Override
	public void onInit(int status) {
		super.onInit(status);
		generateQuestion();
	};

	/**
	 * Fire an intent to start the speech recognition activity.
	 * 
	 * 
	 * @param text
	 */
	private void startVoiceRecognitionActivity(String text) {
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
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, text);

		// Given an hint to the recognizer about what the user is going to say
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

		// Specify how many results you want to receive. The results will be
		// sorted
		// where the first result is the one with higher confidence.
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

		// Specify the recognition language. This parameter has to be specified
		// only if the
		// recognition has to be done in a specific language and not the default
		// one (i.e., the
		// system locale). Most of the applications do not have to set this
		// parameter.

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "it-IT");

		startActivityForResult(intent, RESULT_VOICE_RECOGNITION);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(SCORE, score);
		outState.putInt(VERBINDEX, verbIndex);
		outState.putInt(QUESTION, question);
		super.onSaveInstanceState(outState);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_VOICE_RECOGNITION) {
			if (resultCode == RESULT_OK) {
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				testAnswer(matches);

			}
		} else if (requestCode == RESULT_SETTINGS || requestCode == RESULT_VERBS){
			generateQuestion();
			super.onActivityResult(requestCode, resultCode, data);
		}else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void testAnswer(ArrayList<String> matches) {
		// Fill the list view with the strings the recognizer thought it
		// could have heard
		boolean right = verb.verify(question, matches);
		if (right) {
			message(verb.get(question), NEUTRAL, getCurrentJokeMessageLocale(),
					true);
			StringBuilder msg = new StringBuilder();
			Locale loc = getRandomMessage(msg);
			if (loc != null)
				message(msg.toString(), QUESTION_NEEDED,
					loc, true);
			else
				generateQuestion();
			score++;
		} else {
			if (matches.size() > 0) {
				message(matches.get(0) + "?", NEUTRAL,
						getCurrentJokeMessageLocale(), true);
			}
			message(getString(R.string.wrong), NEUTRAL, getCurrentLocale(),
					true);
			score -= 2;
		}
		updateScoreView();
	}

	private void generateQuestion() {

		Verbs verbs = MyApplication.getInstance().getVerbs();
		ArrayList<Verb> visibleVerbs = verbs.getVisibleVerbs();
		verbIndex = random.nextInt(visibleVerbs.size());
		verb = visibleVerbs.get(verbIndex);

		ArrayList<Integer> visibleVerbItems = Verb.getVisibleVerbItems();
		int i = random.nextInt(visibleVerbItems.size());

		question = visibleVerbItems.get(i);
		String s = verb.get(question);
		if (s.equals("-")) {
			generateQuestion();
			return;
		}
		setQuestionText();
		String description = verb.getDescription(question);
		message(description, NEUTRAL, getCurrentJokeMessageLocale(), false);

	}

	private String setQuestionText() {
		TextView textQuestion = (TextView) findViewById(R.id.textViewQuestion);
		String questionText = verb.getDescription(question);
		textQuestion.setText(questionText);
		return questionText;
	}

	@Override
	protected void onSpeechEnded(String utteranceId) {
		if (utteranceId.equals(QUESTION_NEEDED))
			generateQuestion();

	}


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonSpeak) {
			if (verb != null)

				startVoiceRecognitionActivity(verb.getDescription(question));
		} else if (v.getId() == R.id.buttonHelp) {
			if (verb != null) {
				message(verb.get(question), QUESTION_NEEDED,
						getCurrentJokeMessageLocale(), true);
				score -= 2;
				updateScoreView();
			}
		}

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE
				|| (event.getAction() == KeyEvent.ACTION_DOWN && event
						.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

			ArrayList<String> list = new ArrayList<String>();
			list.add(v.getText().toString());
			testAnswer(list);

			return true;
		}
		return false;
	}
}
