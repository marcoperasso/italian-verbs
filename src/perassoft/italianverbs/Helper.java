package perassoft.italianverbs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Helper {
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

}
