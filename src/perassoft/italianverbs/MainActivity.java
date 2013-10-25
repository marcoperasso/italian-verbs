package perassoft.italianverbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import perassoft.italianverbs.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnInitListener,
		OnClickListener, OnEditorActionListener {

	private static final String TTS = "TTS";
	private static final String SCORE = "score";
	private static final String VERBINDEX = "VIDX";
	private static final String QUESTION = "Q";
	private static final String QUESTION_NEEDED = "1";
	private static final String NEUTRAL = "2";
	private static final int RESULT_SPEECH_CHECK_CODE = 0;
	private static final int RESULT_SETTINGS = 1;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 2;
	private TextToSpeech tts;
	private Random random;
	private int score = 0;
	private boolean restoredFromInstanceState;
	private String[] messages;
	private Verb verb;
	private int question;
	private int verbIndex;
	private Button mSpeakButton;
	private Button mHelpButton;
	private boolean voiceRecognition;

	private void startActivityForInstallVoiceRecognition() {
		doOnAsk(getString(R.string.need_voice_recognition_components),
				new Runnable() {

					@Override
					public void run() {
						Intent marketIntent = new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("market://search?q=pname:com.google.android.voicesearch"));
						startActivity(marketIntent);
					}
				});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		 PackageManager pm = getPackageManager();
		 Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		 List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
		 if (activities.size() == 0) {
			 startActivityForInstallVoiceRecognition();
		 }
		 else
		 {
			 voiceRecognition = true;
		 }
		 Intent checkIntent = new Intent();
		 checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		 startActivityForResult(checkIntent, RESULT_SPEECH_CHECK_CODE);

		random = new Random(System.currentTimeMillis());
		messages = getResources().getStringArray(R.array.joke_messages);

		if (savedInstanceState != null) {
			score = savedInstanceState.getInt(SCORE, 0);
			verbIndex = savedInstanceState.getInt(VERBINDEX);
			question = savedInstanceState.getInt(QUESTION);
			verb = MyApplication.getInstance().getVerbs().get(verbIndex);
			restoredFromInstanceState = true;
			setQuestionText();
		} else {
			restoredFromInstanceState = false;
		}
		updateScoreView();
		mSpeakButton = (Button) findViewById(R.id.buttonSpeak);
		mSpeakButton.setOnClickListener(this);
		mHelpButton = (Button) findViewById(R.id.buttonHelp);
		mHelpButton.setOnClickListener(this);
		
		((EditText)findViewById(R.id.editTextAnswer)).setOnEditorActionListener(this);

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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings: {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent, RESULT_SETTINGS);
			break;
		}
		case R.id.action_about: {
			showAboutDialog();
			break;
		}
		case R.id.action_verbs: {
			Intent intent = new Intent(this, VerbsActivity.class);
			startActivityForResult(intent, RESULT_SETTINGS);
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAboutDialog() {
		Builder builder = new AlertDialog.Builder(this);
		PackageInfo pInfo = null;
		String version = "";
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = " v. " + pInfo.versionName;
		} catch (NameNotFoundException e) {

		}
		builder.setTitle(getString(string.app_name) + version);
		Spanned msg = Html.fromHtml(getString(R.string.about_msg));
		builder.setMessage(msg);
		builder.setCancelable(true);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
		TextView messageView = (TextView) dialog
				.findViewById(android.R.id.message);
		messageView.setLinksClickable(true);
		messageView.setMovementMethod(LinkMovementMethod.getInstance());

	}

	private void message(String text, String messageId, Locale locale,
			boolean toast) {
		if (toast)
			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		if (tts != null) {
			HashMap<String, String> params = new HashMap<String, String>();

			params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, messageId);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
			tts.setLanguage(locale);
			tts.speak(text, TextToSpeech.QUEUE_ADD, params);
		} else
			onSpeechEnded(messageId);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(SCORE, score);
		outState.putInt(VERBINDEX, verbIndex);
		outState.putInt(QUESTION, question);
		super.onSaveInstanceState(outState);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_SPEECH_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL) {
				startActivityForInstallTTS();
			} else {

				tts = new TextToSpeech(this, this);

			}
		} else if (requestCode == RESULT_SETTINGS) {

			generateQuestion();

		} else if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
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
					getCurrentJokeMessageLocale(), true);
			message(getRandomMessage(), QUESTION_NEEDED,
					getCurrentJokeMessageLocale(), true);
			score++;
		} else {
			if (matches.size() > 0)
			{
				message(matches.get(0) + "?", NEUTRAL,
						getCurrentJokeMessageLocale(), true);
			}
			message(getString(R.string.wrong), NEUTRAL,
					getCurrentLocale(), true);
			score -= 2;
		}
		updateScoreView();
	}

	private void startActivityForInstallTTS() {
		doOnAsk(getString(R.string.need_tts_components, getNeededLanguages()),
				new Runnable() {

					@Override
					public void run() {
						// missing data, install it
						Intent installIntent = new Intent();
						installIntent
								.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
						startActivity(installIntent);

					}
				});
	}

	private void doOnAsk(String message, final Runnable runnable) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(string.app_name);
		builder.setMessage(message);
		builder.setCancelable(true);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						runnable.run();
						dialog.cancel();
						finish();
					}
				});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
			}});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private String getNeededLanguages() {
		StringBuilder sb = new StringBuilder();
		String displayLanguage = getCurrentLocale().getDisplayLanguage();
		sb.append(displayLanguage);
		String displayLanguage2 = getCurrentJokeMessageLocale()
				.getDisplayLanguage();

		if (!displayLanguage2.equals(displayLanguage))
			sb.append(", ");
		sb.append(displayLanguage2);
		return sb.toString();
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
		message(description, NEUTRAL, getCurrentJokeMessageLocale(), false);

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

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		// TTS is successfully initialized
		if (status == TextToSpeech.SUCCESS) {
			// Setting speech language
			Locale current = getCurrentLocale();
			if (tts.isLanguageAvailable(current) < TextToSpeech.LANG_AVAILABLE
					|| tts.isLanguageAvailable(getCurrentJokeMessageLocale()) < TextToSpeech.LANG_AVAILABLE) {
				startActivityForInstallTTS();
				return;
			}
			int result = tts.setLanguage(current);
			tts.setPitch(1.9f);
			tts.setSpeechRate(1.1f);
			tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {

				@Override
				public void onUtteranceCompleted(String utteranceId) {
					final String id = utteranceId;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							onSpeechEnded(id);
						}

					});

				}

			});
			// If your device doesn't support language you set above
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Cook simple toast message with message
				Toast.makeText(this, R.string.language_not_supported,
						Toast.LENGTH_LONG).show();
				Log.e(TTS, getString(R.string.language_not_supported));
			}
			if (!restoredFromInstanceState) {
				{
					HashMap<String, String> params = new HashMap<String, String>();

					params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
							QUESTION_NEEDED);

					tts.speak(getString(R.string.welcome_message),
							TextToSpeech.QUEUE_ADD, params);
				}
			}

			// TTS is not initialized properly
		} else {
			Toast.makeText(this, R.string.tts_initilization_failed,
					Toast.LENGTH_LONG).show();
			Log.e(TTS, getString(R.string.tts_initilization_failed));
		}

	}

	private Locale getCurrentLocale() {
		String locale = getString(R.string.speech_locale);
		return new Locale(locale);
	}

	private Locale getCurrentJokeMessageLocale() {
		String locale = getString(R.string.speech_joke_message_locale);
		return new Locale(locale);
	}

	private void onSpeechEnded(String utteranceId) {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
		if (utteranceId.equals(QUESTION_NEEDED))
			generateQuestion();

	}

	@Override
	protected void onDestroy() {
		if (tts != null) {
			tts.stop();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonSpeak) {
			if (verb != null)
				
				startVoiceRecognitionActivity(verb.getDescription(question));
		} else if (v.getId() == R.id.buttonHelp) {
			if (verb != null) {
				message(verb.get(question), NEUTRAL,
						getCurrentJokeMessageLocale(), true);
				score -= 2;
				updateScoreView();
				generateQuestion();
			}
		}

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		 if (actionId == EditorInfo.IME_ACTION_DONE) {

			 ArrayList<String> list  = new ArrayList<String>();
				list.add(v.getText().toString());
				testAnswer(list);

             return true;
         }
		return false;
	}
}
