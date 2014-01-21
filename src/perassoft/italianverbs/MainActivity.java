package perassoft.italianverbs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends CommonActivity implements OnClickListener {

	private boolean resumed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.buttonLearn).setOnClickListener(this);
		findViewById(R.id.buttonInterrogation).setOnClickListener(this);

		resumed = savedInstanceState != null;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonLearn) {
			Intent intent = new Intent(this, StudyActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.buttonInterrogation) {
			Intent intent = new Intent(this, InterrogationActivity.class);
			startActivity(intent);
		}

	}

	@Override
	public void onInit(int status) {
		super.onInit(status);
		if (!resumed)
			message(getString(R.string.welcome_message), NEUTRAL,
					getCurrentLocale(), false);
	}

}
