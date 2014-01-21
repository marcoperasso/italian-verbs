package perassoft.italianverbs;

import java.io.IOException;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class VerbsActivity extends CommonActivity implements OnClickListener {

	private static final int menuDeleteLocal = 0;
	private Verb[] arVerbs;
	private Verb mActiveItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verbs);
		setTitle(R.string.choose_verbs);
		populate();
		findViewById(R.id.buttonAdd).setOnClickListener(this);
		findViewById(R.id.buttonReset).setOnClickListener(this);
	}

	private ListView populate() {
		Verbs verbs = MyApplication.getInstance().getVerbs();
		arVerbs = new Verb[verbs.size()];
		verbs.toArray(arVerbs);
		final ListView lv = (ListView) findViewById(R.id.list_verbs);
		ArrayAdapter<Verb> adapter = new ArrayAdapter<Verb>(this,
				android.R.layout.simple_list_item_multiple_choice, arVerbs);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				boolean itemChecked = lv.isItemChecked(position);
				if (!itemChecked
						&& MyApplication.getInstance().getVerbs()
								.getVisibleVerbs().size() == 1) {
					lv.setItemChecked(position, true);
					return;
				}

				Verb v = (Verb) lv.getItemAtPosition(position);
				MyApplication.getInstance().getVerbs()
						.setHiddenVerb(v, !itemChecked);

			}
		});
		registerForContextMenu(lv);
		for (int i = 0; i < arVerbs.length; i++)
			lv.setItemChecked(i, !Verbs.isHiddenVerb(arVerbs[i]));
		return lv;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list_verbs) {
			if (arVerbs.length == 1)
				return;
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			mActiveItem = arVerbs[info.position];
			menu.setHeaderTitle(mActiveItem.toString());

			menu.add(Menu.NONE, menuDeleteLocal, 0, R.string.delete);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case menuDeleteLocal:

			Helper.dialogMessage(
					this,
					getString(R.string.are_you_sure_to_delete_verb,
							mActiveItem.getName()),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							deleteActiveVerb();
						}

					}, null);

			break;

		}

		return true;
	}

	private void deleteActiveVerb() {
		Verbs verbs = MyApplication.getInstance().getVerbs();
		try {
			verbs.delete(mActiveItem);
		} catch (IOException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			return;
		}
		if (verbs.size() == 1)
			verbs.setHiddenVerb(verbs.get(0), false);
		populate();

	}

	private void addVerb(Verb verb) {
		Verbs verbs = MyApplication.getInstance().getVerbs();
		try {
			verbs.addVerb(verb);
		} catch (IOException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			return;
		}
		populate();

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonAdd) {
			/*
			 * PackageManager pm = getPackageManager(); Intent intent = new
			 * Intent(); intent.setClassName("perassoft.italianverbsextension",
			 * "perassoft.italianverbsextension.VerbDownloaderActivity");
			 * List<ResolveInfo> activities = pm.queryIntentActivities(intent,
			 * 0); if (activities.size() == 0) {
			 * startActivityForInstallExtension(); }
			 */
			Intent intent = new Intent(this, VerbDownloaderActivity.class);

			startActivityForResult(intent, RESULT_DOWNLOAD_VERB);

		} else if (v.getId() == R.id.buttonReset) {
			Verbs.restoreOriginal();
			populate();
		}
	}

	private void startActivityForInstallExtension() {
		doOnAsk(new Runnable() {

			@Override
			public void run() {
				Intent marketIntent = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("market://search?q=pname:perassoft.italianverbsextension"));
				startActivity(marketIntent);
			}
		}, R.string.need_extension_components);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_DOWNLOAD_VERB) {
			if (RESULT_OK == resultCode) {
				if (data.getExtras() == null)
					return;
				CharSequence[] lines = data.getExtras().getCharSequenceArray(
						"VERB");
				if (lines == null)
					return;
				Verb verb = new Verb(lines[0].toString());
				for (int i = 1; i <= Verb.MAX_VERBS; i++) {
					verb.add(lines[i].toString());
				}
				addVerb(verb);

			} else {
				// Toast.makeText(this, R.string.not_available_yet,
				// Toast.LENGTH_LONG).show();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
