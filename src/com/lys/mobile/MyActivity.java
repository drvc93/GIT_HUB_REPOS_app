package com.lys.mobile;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.lys.mobile.asynctask.EnviarReporteInspeccionAsyncTask;
import com.lys.mobile.data.DetalleInspeccionData;
import com.lys.mobile.data.InspeccionRealizadasData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends Activity implements View.OnClickListener {

	private static final String BS_PACKAGE = "com.google.zxing.client.android";
	public static final int REQUEST_CODE = 0x0000c0de;
	private TextView lbl;
	private Button boton;
	static String contents = "";
	// menu
	private Button llenar, sv, itm, ind;
	private TextView bienvenido;
	public static ArrayList<InspeccionRealizadasData> lista;
	public static ArrayList<DetalleInspeccionData> lista2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout);

		bienvenido = (TextView) findViewById(R.id.tvmnombre);
		bienvenido.setText("     BIENVENIDO SR. " + Login.usuario + "   ");
		llenar = (Button) findViewById(R.id.bthill);
		sv = (Button) findViewById(R.id.btsv);
		itm = (Button) findViewById(R.id.btit);
		ind = (Button) findViewById(R.id.btim);
		// clicks
		sv.setOnClickListener(this);
		itm.setOnClickListener(this);
		ind.setOnClickListener(this);
		llenar.setOnClickListener(this);
		lbl = (TextView) findViewById(R.id.lblCodbar);
		boton = (Button) findViewById(R.id.button1);

		lista = new ArrayList<InspeccionRealizadasData>();
		lista2 = new ArrayList<DetalleInspeccionData>();

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String codigo = preferences.getString("cmaquina", "");
		String flagmantto = preferences.getString("flagmantto", "");
		
		if (flagmantto.equals("S")){
			boton.setEnabled(true);
			llenar.setEnabled(true);
			itm.setEnabled(true);
			lbl.setVisibility(View.VISIBLE);
		}else{
			boton.setEnabled(false);
			llenar.setEnabled(false);
			itm.setEnabled(true);
			lbl.setVisibility(View.INVISIBLE);
		}
		
		// if(llenar.equals("") || !(llenar==null))
		lbl.setText("CODIGO MAQUINA: " + codigo);

		boton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// boton.setEnabled(false);
				Intent intentScan = new Intent(BS_PACKAGE + ".SCAN");
				intentScan.putExtra("PROMPT_MESSAGE", "Enfoque entre 9 y 11 cm. apuntado el codigo de barras de la maquina");
				startActivityForResult(intentScan, REQUEST_CODE);
			}
		});
	}

	// /

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				contents = intent.getStringExtra("SCAN_RESULT");
				String cbarra = "",cmaquina = "";

				DataBase basededatos = new DataBase(this, "DBInspeccion", null,1);
				SQLiteDatabase db = basededatos.getWritableDatabase();

				String query = "SELECT c_maquina,c_descripcion,c_codigobarras,c_estado,c_familiainspeccion," +
						"c_centrocosto from MTP_MAQUINAS where c_codigobarras='"
						+ contents + "' ";
				Cursor c = db.rawQuery(query, null);
				if (c.moveToFirst()) {

					String cma = c.getString(0);
					cmaquina = cma;
					String desc = c.getString(1);
					cbarra = c.getString(2);
					String estado = c.getString(3);
					String familia = c.getString(4);
					String centro = c.getString(5);

					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyActivity.this);
					Editor editor = preferences.edit();
					editor.putString("cmaquina", cma);
					editor.putString("codbarra", cbarra);
					editor.putString("maquina", desc);
					editor.putString("familia", familia);
					editor.putString("condicion", estado);
					editor.putString("centro", centro);
					
					editor.commit();

				} else {

					Toast.makeText(this, "Maquina no disponible para ese codigo", Toast.LENGTH_SHORT).show();
				}
				c.close();
				db.close();

				//lbl.setText("Codigo Maquina: " + cbarra);
				lbl.setText("CODIGO MAQUINA: " + cmaquina);
				boton.setText("Volver a escanear");
			}
		}
		return;
	}

	// ///
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// menu
		case R.id.bthill:
			Intent o = new Intent("com.example.user.codbar.Inspeccion");
			startActivity(o);

			// aca
			break;
		case R.id.btit:
			Intent k = new Intent("com.example.user.codbar.Infotecnica");
			startActivity(k);
			break;
		case R.id.btsv:
			Intent n = new Intent("com.example.user.codbar.Sservicio");
			startActivity(n);
			break;
		case R.id.btim:
			Intent m = new Intent("com.example.user.codbar.Indicadoresmantto");
			startActivity(m);
			break;
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

}
