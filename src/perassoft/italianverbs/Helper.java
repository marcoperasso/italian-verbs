package perassoft.italianverbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class Helper {
	private static final String LOG_TAG = "ITALIAN_VERBS";

	public static void dialogMessage(final Context context, String message,
			String title, DialogInterface.OnClickListener okListener,
			DialogInterface.OnClickListener cancelListener) {
		new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_alert).setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.yes, okListener)
				.setNegativeButton(android.R.string.no, cancelListener).show();

	}
	public static void dialogMessage(final Context context, String message,
			DialogInterface.OnClickListener okListener,
			DialogInterface.OnClickListener cancelListener) {
		dialogMessage(context, message, context.getString(R.string.app_name), okListener, cancelListener);

	}
	public static void dialogMessage(final Context context, int message,
			DialogInterface.OnClickListener okListener,
			DialogInterface.OnClickListener cancelListener) {
		dialogMessage(context, context.getString(message), okListener, cancelListener);

	}
	public static void dialogMessage(final Context context, int message,
			int title, DialogInterface.OnClickListener okListener,
			DialogInterface.OnClickListener cancelListener) {
		dialogMessage(context, context.getString(message),
				context.getString(title), okListener, cancelListener);

	}
	public static void saveObject(Context context, String fileName, Object obj)
			throws IOException {
		FileOutputStream fos = context.openFileOutput(fileName,
				Context.MODE_PRIVATE);
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(fos);
			out.writeObject(obj);
			out.flush();
		} finally {
			out.close();
			fos.close();
		}

	}

	public static Object readObject(Context context, String fileName) {
		File file = context.getFileStreamPath(fileName);
		if (file.exists()) {
			try {
				FileInputStream fis = context.openFileInput(fileName);
				ObjectInput in = null;
				try {
					in = new ObjectInputStream(fis);
					try {
						return in.readObject();
					} catch (Exception ex) {
						Log.e(Helper.LOG_TAG, Log.getStackTraceString(ex));
					}
				} catch (Exception e) {
					Log.e(Helper.LOG_TAG, Log.getStackTraceString(e));
				} finally {
					in.close();
					fis.close();
				}
			} catch (Exception e) {
				Log.e(Helper.LOG_TAG, Log.getStackTraceString(e));
			}

		}
		return null;
	}
}
