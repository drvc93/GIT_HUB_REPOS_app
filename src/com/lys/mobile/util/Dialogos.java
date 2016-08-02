package com.lys.mobile.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.*;
import android.os.*;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class Dialogos {
	private static boolean mResult;
	private static String mvalor;

	public static boolean getYesNoWithExecutionStop(String title,
			String message, Context context) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message mesg) {
				throw new RuntimeException();
			}
		};

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mResult = true;
				handler.sendMessage(handler.obtainMessage());
			}
		});
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mResult = false;
				handler.sendMessage(handler.obtainMessage());
			}
		});
		alert.show();
		try {
			Looper.loop();
		} catch (RuntimeException e2) {
		}

		return mResult;
	}

	@SuppressLint("NewApi")
	public static String getDialogInput(String title, String message,
			Context context) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message mesg) {
				throw new RuntimeException();
			}
		};

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		final EditText inputName = new EditText(context);
		inputName.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(inputName);

		alert.setPositiveButton("Aceptar",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						final String routeName = inputName.getText().toString();
						mvalor = routeName;
						handler.sendMessage(handler.obtainMessage());
					}
				});

		alert.show();
		try {
			Looper.loop();
		} catch (RuntimeException e2) {
		}

		return mvalor;
	}
	
	public static void DialogToast(Context context,String Mensaje,boolean largo){
		Toast.makeText(context,Mensaje,(largo==true)?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
	}

}
