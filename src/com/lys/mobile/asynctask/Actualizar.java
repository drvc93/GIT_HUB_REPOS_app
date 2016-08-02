package com.lys.mobile.asynctask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.Util;

public class Actualizar extends AsyncTask<String, Integer, String> {

	Context context;
	String cabe="",men="";
	

	public Actualizar(Context c, String cab) {
		super();
		context = c;
		cabe = cab;
		// view

	}

	@Override
	public String doInBackground(String... params) {
		// TODO Auto-generated method stub

		if (isCancelled()) {
			// Toast.makeText(getApplication(),
			// getApplication().getString(R.string.error_serverconnection),
			// Toast.LENGTH_SHORT).show();
			return "";
		}
		String url = params[0];
		DefaultHttpClient cliente = new DefaultHttpClient();

		HttpGet httppost = new HttpGet(url);
		String data = "";
		String html = "";

		publishProgress(10);
		Log.d("Logueo", String.valueOf(url));

		try {

			HttpResponse respuesta = cliente.execute(httppost);

			InputStream contenido = respuesta.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					contenido));

			publishProgress(40);

			while ((data = reader.readLine()) != null) {

				if (isCancelled()) {

					break;
				}

				html += data;

			}

			reader.close();
			contenido.close();

			publishProgress(100);

		} catch (MalformedURLException e) {

			e.printStackTrace();
			cancel(true);
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			cancel(true);
			return "";
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			cancel(true);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			cancel(true);
			return "";
		} finally {

			Log.d("Logueo", String.valueOf(html));
			try {

				JSONObject respJSON;
				String status = "";

				try {
					respJSON = new JSONObject(html.toString());
					status = respJSON.getString("status");
					men = respJSON.getString("mensaje");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (status.equals("0")) {

					return "0";

				} else if (status.equals("1")) {

				

					return "1";

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
				cancel(true);
				return "";

			}

		}

		return "";

	}

	@Override
	protected void onPostExecute(String result) {// paso 4
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		// myAnim.cancel();

		if (result.equals("")) {

			// boton.setEnabled(true);
			/*
			 * Toast t = Toast.makeText(context, "Servidor no disponible",
			 * Toast.LENGTH_SHORT); t.show();
			 */

			Log.d("mando", "invoco");
			
			DataBase basededatos = new DataBase(context, "DBInspeccion",
					null, 1);
			SQLiteDatabase db = basededatos.getWritableDatabase();

			ContentValues cv = new ContentValues();
			cv.put("c_estado", "I");
			db.update(" MTP_INSPECCIONMAQUINA_CAB", cv,
					"n_correlativo = ?", new String[] { cabe });
		
			db.close();

			

			Log.d("mando", "cmando");


		} else {

			if (result.equals("0")) {

				// boton.setEnabled(true);
				Log.d("mando", "error");

			

				Toast.makeText(
						context,
						"Se Envio Reporte pero ocurrio un error de transferencia a base datos global : Error: "+men,
						Toast.LENGTH_LONG).show();

				DataBase basededatos = new DataBase(context, "DBInspeccion",
						null, 1);
				SQLiteDatabase db = basededatos.getWritableDatabase();

				ContentValues cv = new ContentValues();
				cv.put("c_estado", "I");
				db.update(" MTP_INSPECCIONMAQUINA_CAB", cv,
						"n_correlativo = ?", new String[] { cabe });
			
				db.close();

				

				Log.d("mando", "cmando");

			
			} else if (result.equals("1")) {

			
				DataBase basededatos = new DataBase(context, "DBInspeccion",
						null, 1);
				SQLiteDatabase db = basededatos.getWritableDatabase();

				ContentValues cv = new ContentValues();
				cv.put("c_estado", "E");
				db.update(" MTP_INSPECCIONMAQUINA_CAB", cv,
						"n_correlativo = ?", new String[] { cabe });
			
				db.close();

				Toast.makeText(context, "Se Envio Reporte correctamente",
						Toast.LENGTH_SHORT).show();

				Log.d("mando", "cmando");

			}

		}

	}

	@Override
	protected void onPreExecute() {// paso1
		// TODO Auto-generated method stub
		super.onPreExecute();

	}

	@Override
	protected void onCancelled() {

		// progressDialog.cancel();

	}

	@Override
	protected void onProgressUpdate(Integer... values) {// paso2
		// TODO Auto-generated method stub

		super.onProgressUpdate(values);

	}

}
