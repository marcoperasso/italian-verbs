package perassoft.italianverbs;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;

public class MainActivity extends CommonActivity {
	private boolean voiceRecognition;
	
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
		 
	}
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
	
	
	
	
	
	
	

}
