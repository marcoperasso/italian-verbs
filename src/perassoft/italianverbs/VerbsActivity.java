package perassoft.italianverbs;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class VerbsActivity extends Activity implements OnClickListener {

	private static final int menuDeleteLocal = 0;
	private Verb[] arVerbs;
	private Verb mActiveItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verbs);
		setTitle(R.string.choose_verbs);
		getVerbs();
		populate();
		findViewById(R.id.buttonAdd).setOnClickListener(this);
	}

	private void getVerbs() {
		Verbs verbs = MyApplication.getInstance().getVerbs();
		arVerbs = new Verb[verbs.size()];
		verbs.toArray(arVerbs);
	}

	private ListView populate() {
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
								.getVisibleVerbs() == 1) {
					lv.setItemChecked(position, true);
					return;
				}

				Verb v = (Verb) lv.getItemAtPosition(position);
				Verbs.setHiddenVerb(v, !itemChecked);
				MyApplication.getInstance().getVerbs().countVisibleVerbs();
			}
		});
		for (int i = 0; i < arVerbs.length; i++)
			lv.setItemChecked(i, !Verbs.isHiddenVerb(arVerbs[i]));
		return lv;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list_verbs) {
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
		getVerbs();
		populate();

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonAdd) {
			Toast.makeText(this, R.string.not_available_yet, Toast.LENGTH_LONG).show();
		}

	}
}