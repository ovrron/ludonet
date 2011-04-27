package com.ronny.ludo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

public class ErrDialog {
	private  AlertDialog alertDialog;
	private  OnDismissListener onDismissListener;

	public  void showDialog(Context ctx, String title, String message, int icon) {
		final AlertDialog.Builder alertbox = new AlertDialog.Builder(ctx);
		if (title != null) {
			alertbox.setTitle(title);
		}
		if (message != null) {
			alertbox.setMessage(message);
		}
		alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog = alertbox.create();
		if (icon != 0) {
			alertDialog.setIcon(icon);
		}
		if(onDismissListener != null) {
			alertDialog.setOnDismissListener(onDismissListener);
		}
		alertDialog.show();
	}
	
	public  void dismissDialog() {
		alertDialog.dismiss();
	}

	public void setOnDismissListener(OnDismissListener onDismissListener) {
		this.onDismissListener = onDismissListener;
	}

}
