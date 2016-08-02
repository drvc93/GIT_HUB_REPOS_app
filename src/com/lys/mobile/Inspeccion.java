package com.lys.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import com.lys.mobile.asynctask.InspeccionAsyncTask;

import com.lys.mobile.asynctask.ProgramaMantenimientoAsyncTask;
import com.lys.mobile.data.HistorialData;
import com.lys.mobile.data.ProgramaData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by User on 03/10/2014.
 */
public class Inspeccion extends Activity implements View.OnClickListener {

	private Button realizar, revisar, insre;

	String codigo = "";
	MyApp m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// blablabla
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inspeccion);

		realizar = (Button) findViewById(R.id.btnminin);
		revisar = (Button) findViewById(R.id.btnminhi);
		insre = (Button) findViewById(R.id.btir);
		// clicks btir
		// pm.setOnClickListener(this);
		realizar.setOnClickListener(this);
		revisar.setOnClickListener(this);
		insre.setOnClickListener(this);

		m = (MyApp) getApplicationContext();

	}

	@Override
	public void onClick(View v) {

		Calendar cnow = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int year = cnow.get(Calendar.YEAR);
		int month = cnow.get(Calendar.MONTH);
		int day = cnow.get(Calendar.DATE);
		cnow.set(year, month, day, 23, 59, 59);

		String fechaactual = df.format(cnow.getTime());
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(Inspeccion.this);
		
		Editor editor = preferences.edit();
		editor.putString("cmaquina","COM-01");
		
		editor.putString("familia", "017");
		
		editor.commit();
		
		Calendar cback = Calendar.getInstance();
		cback.add(Calendar.DAY_OF_YEAR, -30);
		int yearr = cback.get(Calendar.YEAR);
		int monthr = cback.get(Calendar.MONTH);
		int dayr = cback.get(Calendar.DATE);
		cback.set(yearr, monthr, dayr, 0, 0, 0);
		String fechaanterior = df.format(cback.getTime());
		
		Log.d("actual", fechaactual);
		Log.d("atras", fechaanterior);

		SharedPreferences preferencess = PreferenceManager.getDefaultSharedPreferences(Inspeccion.this);
		codigo = preferencess.getString("cmaquina", "");
		String data = preferencess.getString("data", "");
		Editor editore = preferencess.edit();
		editore.putString("fechainp", fechaanterior);
		editore.putString("fechafinp", fechaactual);
		// editor.putString("periocidadp", lista.get(0).periocidad);
		editore.commit();
		
		switch (v.getId()) {
		
			case R.id.btnminin:
	
				realizar.setEnabled(false);
	
				if (codigo.equals("")) {
	
					Toast.makeText(Inspeccion.this, "No hay codigo de barra", Toast.LENGTH_SHORT).show();
					realizar.setEnabled(true);
				} else {
					
					Intent z = new Intent(this, Llenar.class);
					startActivity(z);
					realizar.setEnabled(true);
				
				}
			
			break;
		/*
		 * case R.id.btpm:
		 * 
		 * 
		 * 
		 * pm.setEnabled(false);
		 * 
		 * 
		 * 
		 * if(codigo.equals("")){
		 * 
		 * pm.setEnabled(true); Toast.makeText(Inspeccion.this,
		 * "No hay codigo de barra", Toast.LENGTH_SHORT).show();
		 * 
		 * }else{
		 * 
		 * //hago select Log.d("actual",fechaactual);
		 * Log.d("atras",fechaanterior); Log.d("codigo",codigo);
		 * 
		 * //SharedPreferences
		 * preferences=PreferenceManager.getDefaultSharedPreferences(context);
		 * Editor editor=preferences.edit(); editor.putString("fechainp",
		 * fechaanterior); editor.putString("fechafinp",fechaactual); //
		 * editor.putString("periocidadp", lista.get(0).periocidad);
		 * editor.commit(); if( m.listaplan.size()>0) m.listaplan.clear();
		 * DataBase basededatos = new DataBase (this, "DBInspeccion", null, 1);
		 * SQLiteDatabase db = basededatos.getWritableDatabase();
		 * 
		 * String query =
		 * "SELECT c_periodoinspeccion,d_periocidad,c_descripcion,c_estado from MTP_PERIODOINSPECCION where d_ultimafechamodificacion between '"
		 * +fechaanterior+"' and '"+fechaactual+"'"; Log.d("sentencia",query);
		 * Cursor c = db.rawQuery(query,null); if( c.moveToFirst()) {
		 * 
		 * do {
		 * 
		 * 
		 * ProgramaData ins=new ProgramaData();
		 * 
		 * 
		 * ins.c_periodoinspeccion=c.getString(0); ins.d_periocidad=
		 * c.getString(1); ins.c_descripcion=c.getString(2);
		 * ins.c_estado=c.getString(3);
		 * 
		 * 
		 * 
		 * 
		 * MyApp m=(MyApp)getApplicationContext();
		 * 
		 * m.listaplan.add(ins);
		 * 
		 * 
		 * } while(c.moveToNext());
		 * 
		 * 
		 * 
		 * 
		 * }else{
		 * 
		 * Toast.makeText(this,"No hay datos desde hace 1 mes",
		 * Toast.LENGTH_SHORT).show(); Log.d("plan","vacio"); } c.close();
		 * db.close();
		 * 
		 * Intent i=new Intent(Inspeccion.this,PlanMantenimiento.class);
		 * startActivity(i);
		 * 
		 * pm.setEnabled(true);
		 * 
		 * }
		 * 
		 * 
		 * break;
		 */
			case R.id.btnminhi:
				revisar.setEnabled(false);
				// SharedPreferences
				// preferences=PreferenceManager.getDefaultSharedPreferences(context);
	
				Intent i = new Intent(Inspeccion.this, HistoInspeccion.class);
				startActivity(i);
				revisar.setEnabled(true);
	
				break;
				
			case R.id.btir:
	
				insre.setEnabled(false);
	
				Intent in = new Intent(Inspeccion.this,
						InspeccionesRealizadas.class);
				startActivity(in);
	
				insre.setEnabled(true);
	
				break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

}
