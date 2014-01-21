package perassoft.italianverbs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class VerbDownloaderActivity extends Activity implements
		OnClickListener, OnEditorActionListener {

	private AutoCompleteTextView mEditVerb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verb_downloader);
		setTitle(R.string.download_new_verb);

		findViewById(R.id.buttonGo).setOnClickListener(this);
		findViewById(R.id.buttonCancel).setOnClickListener(this);
		mEditVerb = ((AutoCompleteTextView) findViewById(R.id.editTextVerb));
		VerbsAutoCompleteAdapter adapter = new VerbsAutoCompleteAdapter(this, R.xml.mylist);
				//android.R.layout.simple_dropdown_item_1line);

		mEditVerb.setAdapter(adapter);
		mEditVerb.setOnEditorActionListener(this);
		mEditVerb.setImeOptions(EditorInfo.IME_ACTION_DONE);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonCancel) {
			Intent intent = new Intent();
			setResult(RESULT_CANCELED, intent);
			finish();
		} else if (v.getId() == R.id.buttonGo) {
			download();
		}

	}

	private void download() {
		Editable name = mEditVerb.getText();
		final ProgressDialog progressBar = new ProgressDialog(this);
		progressBar.setCancelable(false);
		progressBar.setIndeterminate(true);
		progressBar.setMessage(getString(R.string.please_wait));
		
		progressBar.show();

		new AsyncTask<String, Void, DownloadResult>() {
			protected void onPostExecute(DownloadResult result) {
				Intent intent = new Intent();
				intent.putExtras(result.data);
				setResult(result.error ? RESULT_CANCELED : RESULT_OK, intent);
				progressBar.dismiss();
				finish();
			}

			@Override
			protected DownloadResult doInBackground(String... params) {
				DownloadResult result = new DownloadResult();
				result.data = new Bundle();
				CharSequence[] lines = new CharSequence[96];
				int i = 0;
				try {
					byte[] p = params[0].getBytes("UTF-8");
					String base64 = Base64.encodeToString(p, Base64.DEFAULT);
					URL url = new URL("http://www.ecommuters.com/verbs/get/"
							+ base64);
					InputStream openStream = url.openStream();
					BufferedReader reader;
					reader = new BufferedReader(new InputStreamReader(
							openStream));
					String line = null;
					while ((line = reader.readLine()) != null) {
						lines[i] = line;
						i++;
					}
				} catch (Exception e) {
					result.data.putString("ERROR", e.getMessage());
					result.error = true;
				}
				result.data.putCharSequenceArray("VERB", lines);
				return result;
			};
		}.execute(name.toString());

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE
				|| (event.getAction() == KeyEvent.ACTION_DOWN && event
						.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

			download();

			return true;
		}
		return false;
	}

	class DownloadResult {
		Bundle data;
		boolean error;
	}
}
