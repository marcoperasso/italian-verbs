package perassoft.italianverbs;

import java.util.HashMap;
import java.util.Locale;

import perassoft.italianverbs.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class CommonActivity extends Activity  implements OnInitListener{
	private TextToSpeech tts;
	private static final int RESULT_SPEECH_CHECK_CODE = 0;
	private static final String TTS = "TTS";
	private static final int RESULT_SETTINGS = 1;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		 Intent checkIntent = new Intent();
		 checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		 startActivityForResult(checkIntent, RESULT_SPEECH_CHECK_CODE);
	}

	private void learn() {
		Intent intent = new Intent(this, StudyActivity.class);
		startActivity(intent);
		
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
	protected void message(String text, String messageId, Locale locale,
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_SPEECH_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL) {
				startActivityForInstallTTS();
			} else {

				tts = new TextToSpeech(this, this);
				MyApplication.getInstance().setTTS(tts);

			}
		} 
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
	
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		// TTS is successfully initialized
		if (status == TextToSpeech.SUCCESS) {
			// Setting speech language
			Locale current = MyApplication.getInstance().getCurrentLocale();
			if (tts.isLanguageAvailable(current) < TextToSpeech.LANG_AVAILABLE
					|| tts.isLanguageAvailable(MyApplication.getInstance().getCurrentJokeMessageLocale()) < TextToSpeech.LANG_AVAILABLE) {
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
			// TTS is not initialized properly
		} else {
			Toast.makeText(this, R.string.tts_initilization_failed,
					Toast.LENGTH_LONG).show();
			Log.e(TTS, getString(R.string.tts_initilization_failed));
		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void onSpeechEnded(String utteranceId) {
		
	}
	protected void doOnAsk(String message, final Runnable runnable) {
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
		case R.id.action_learn: {
			learn();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		if (tts != null) {
			tts.stop();
		}
		super.onDestroy();
	}
	private String getNeededLanguages() {
		StringBuilder sb = new StringBuilder();
		String displayLanguage = MyApplication.getInstance().getCurrentLocale().getDisplayLanguage();
		sb.append(displayLanguage);
		String displayLanguage2 = MyApplication.getInstance().getCurrentJokeMessageLocale()
				.getDisplayLanguage();

		if (!displayLanguage2.equals(displayLanguage))
			sb.append(", ");
		sb.append(displayLanguage2);
		return sb.toString();
	}

}
