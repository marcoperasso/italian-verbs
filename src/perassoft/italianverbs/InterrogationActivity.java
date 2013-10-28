package perassoft.italianverbs;

import java.util.ArrayList;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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
	private static final String QUESTION_NEEDED = "1";
	private static final String NEUTRAL = "2";
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 2;
	private Random random;
	private int score = 0;
	private String[] messages;
	private Verb verb;
	private int question;
	private int verbIndex;
	private Button mSpeakButton;
	private Button mHelpButton;
	private boolean voiceRecognition;

	

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
			verb = MyApplication.getInstance().getVerbs().get(verbIndex);
			setQuestionText();
		} 
		updateScoreView();
		mSpeakButton = (Button) findViewById(R.id.buttonSpeak);
		mSpeakButton.setOnClickListener(this);
		mHelpButton = (Button) findViewById(R.id.buttonHelp);
		mHelpButton.setOnClickListener(this);
		
		EditText editText = (EditText)findViewById(R.id.editTextAnswer);
		editText.setOnEditorActionListener(this);
		editText.setImeOptions(EditorInfo.IME_ACTION_DONE);

	}

	private String getRandomMessage() {
		return messages[random.nextInt(messages.length)];
	}

	private void updateScoreView() {
		TextView scoreText = (TextView) findViewById(R.id.textViewScore);
		scoreText.setText(String.format(getString(R.string.score), score));

	}

	/**
	 * Fire an intent to start the speech recognition activity.
	 * 
	 * 
	 * @param text
	 */
	private void startVoiceRecognitionActivity(String text) {
		if (!voiceRecognition)
		{
			Toast.makeText(this, R.string.voice_recognition_not_available, Toast.LENGTH_LONG).show();
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

		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}



	

	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(SCORE, score);
		outState.putInt(VERBINDEX, verbIndex);
		outState.putInt(QUESTION, question);
		super.onSaveInstanceState(outState);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				testAnswer(matches);

			}
		}
	}

	private void testAnswer(ArrayList<String> matches) {
		// Fill the list view with the strings the recognizer thought it
		// could have heard
		boolean right = verb.verify(question, matches);
		if (right) {
			message(verb.get(question), NEUTRAL,
					MyApplication.getInstance().getCurrentJokeMessageLocale(), true);
			message(getRandomMessage(), QUESTION_NEEDED,
					MyApplication.getInstance().getCurrentJokeMessageLocale(), true);
			score++;
		} else {
			if (matches.size() > 0)
			{
				message(matches.get(0) + "?", NEUTRAL,
						MyApplication.getInstance().getCurrentJokeMessageLocale(), true);
			}
			message(getString(R.string.wrong), NEUTRAL,
					MyApplication.getInstance().getCurrentLocale(), true);
			score -= 2;
		}
		updateScoreView();
	}

	

	

	private void generateQuestion() {

		Verbs verbs = MyApplication.getInstance().getVerbs();
		verbIndex = random.nextInt(verbs.getVisibleVerbs());
		verb = verbs.get(verbIndex);
		while (Verbs.isHiddenVerb(verb)) {
			verbIndex++;
			verb = verbs.get(verbIndex);
		}
		question = random.nextInt(Verb.getVisibleVerbItems());
		while (!Verb.isVisible(question))
			question++;

		String s = verb.get(question);
		if (s.equals("-")) {
			generateQuestion();
			return;
		}
		setQuestionText();
		String description = verb.getDescription(question);
		message(description, NEUTRAL, MyApplication.getInstance().getCurrentJokeMessageLocale(), false);

	}

	private String setQuestionText() {
		TextView textQuestion = (TextView) findViewById(R.id.textViewQuestion);
		String questionText = verb.getDescription(question);
		textQuestion.setText(questionText);
		return questionText;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	

	private void onSpeechEnded(String utteranceId) {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
				message(verb.get(question), NEUTRAL,
						MyApplication.getInstance().getCurrentJokeMessageLocale(), true);
				score -= 2;
				updateScoreView();
				generateQuestion();
			}
		}

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		 if (actionId == EditorInfo.IME_ACTION_DONE || (event.getAction() == KeyEvent.ACTION_DOWN &&
	                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

			 ArrayList<String> list  = new ArrayList<String>();
				list.add(v.getText().toString());
				testAnswer(list);

             return true;
         }
		return false;
	}
}