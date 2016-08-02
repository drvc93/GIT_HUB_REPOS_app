package com.lys.mobile.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import android.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class funciones {
	//static appglobal app;
	static String RegCache;

	@SuppressLint("NewApi")
	public static String ImeiCelular(Context context) {
		//app = ((appglobal) context);
		String identifier = "";
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null)
			identifier = tm.getDeviceId();
		if (identifier == null || identifier.length() == 0)
			identifier = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
		return identifier;
	}

	public static String EncriptarPassword(String as_password) {
		int w_longitud, counter, w_Caracter, w_semilla;
		String w_cifrado = "";
		char c;

		w_longitud = as_password.trim().length();
		for (counter = 1; counter <= w_longitud; counter++) {
			w_Caracter = as_password.substring(counter - 1, counter)
					.toUpperCase().codePointAt(0);
			w_semilla = counter;
			w_Caracter = w_Caracter + w_semilla;
			c = (char) w_Caracter;
			w_cifrado = w_cifrado + c;
		}
		return w_cifrado.toUpperCase();
	}

	public static String DesencriptarPassword(String as_password) {
		int w_longitud, counter, w_Caracter, w_semilla;
		String w_cifrado = "";
		char c;

		w_longitud = as_password.trim().length();
		for (counter = 1; counter <= w_longitud; counter++) {
			w_Caracter = as_password.substring(counter - 1, counter)
					.toUpperCase().codePointAt(0);
			w_semilla = counter;
			w_Caracter = w_Caracter - w_semilla;
			c = (char) w_Caracter;
			w_cifrado = w_cifrado + c;
		}

		return w_cifrado.toUpperCase();

	}

	public static boolean verificaConexionInternet(Context context) {
		boolean enabled = true;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if ((info == null || !info.isConnected() || !info.isAvailable())) {
			enabled = false;
			Builder builder = new Builder(context);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setMessage("Conexión de internet no se ecuentra disponible");
			builder.setCancelable(false);
			builder.setNeutralButton(R.string.ok, null);
			builder.setTitle("Error");
			builder.create().show();
		}
		return enabled;
	}

	@SuppressWarnings("finally")
	public static String deleteCache(Context context, boolean borrar) {
		RegCache = "";
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir, borrar);
			}
		} catch (Exception e) {
		} finally {
			return RegCache;
		}
	}

	public static boolean deleteDir(File dir, boolean borrar) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				RegCache = RegCache + dir.getPath().toString() + File.separator
						+ children[i].toString() + "\n";
				boolean success = deleteDir(new File(dir, children[i]), borrar);
				if (!success) {
					return false;
				}
			}
		}
		if (borrar == true)
			return dir.delete();
		else
			return true;
	}

	@SuppressWarnings("unused")
	public static String redondearDec(String valor) {
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		double dec;
		if (valor.indexOf(".") > 0) {
			dec = Double.parseDouble(valor);
			dec = Redondear(dec, 2);
			valor = df.format(dec);
		} else {
			valor = df.format(Double.parseDouble(valor));
		}
		return valor;
	}

	private static double Redondear(double numero, int digitos) {
		int cifras = (int) Math.pow(10, digitos);
		return Math.rint(numero * cifras) / cifras;
	}
	
	public static double Redondear2(double input, int digit){
		double out = Double.NaN;
		double rounder = Double.NaN;
		int decimal = digit + 1;
		decimal = (int) Math.pow(10, decimal);
		digit = (int) Math.pow(10, digit);

		rounder = Math.rint(input*decimal);
		//System.out.println(rounder);
		out = Math.rint(input*digit)*10;
		//System.out.println(out);

		if((rounder - out) >= 5){
		out = out + 10;
		}
		out = out/decimal;
		return out;
	}

	public static String getStringFecha(Date date, String formato) {
		String fecha = "";
		if (date != null) {
			java.text.SimpleDateFormat dfm = new java.text.SimpleDateFormat(
					formato);// formato //"dd-MM-yyyy"
			fecha = dfm.format(date);
		}
		return fecha;
	}

	public static String formatoDec(String valor) {
		DecimalFormat df = new DecimalFormat("###,###,###.00");
		try {
			valor = df.parse(valor).toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return valor;
	}

	public static String spacehtml(int cantidad) {
		String texto = "";
		for (int i = 1; i <= cantidad; i++) {
			texto = texto + "&nbsp;";
		}
		return texto;
	}

}