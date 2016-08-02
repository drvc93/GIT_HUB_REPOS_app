package com.lys.mobile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.lys.mobile.util.DataBase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MainInspeccionT extends Activity {
	private static final String BS_PACKAGE = "com.google.zxing.client.android";
	public static final int REQUEST_CODE = 0x0000c0de;
	static String contents = "";
	ListView lvdatos;
	RadioButton chkSel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainprincipal_tabs);
		
		lvdatos = (ListView) findViewById(R.id.lista);
		ArrayAdapter<ModeloRadioButton> adapter = new ModeloRadioButtonadp(
				this, getDatos(), lvdatos);
		lvdatos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvdatos.setAdapter(adapter);
		lvdatos.setItemChecked(0, true);
		
		lvdatos.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SeleccionarItem();
			}
		});
		
	}
	
	private List<ModeloRadioButton> getDatos() {
		List<ModeloRadioButton> list = new ArrayList<ModeloRadioButton>();
		list.add(get(this.getString(R.string.maininspecciont_cbarra),
				this.getString(R.string.maininspecciont_cbarra_sub)));
		list.add(get(this.getString(R.string.maininspecciont_nuevo),
				this.getString(R.string.maininspecciont_nuevo_sub)));
		list.add(get(this.getString(R.string.maininspecciont_inspeccionenlinea),
				this.getString(R.string.maininspecciont_inspeccionenlinea_sub)));
		list.add(get(this.getString(R.string.maininspecciont_listar),
				this.getString(R.string.maininspecciont_listar_sub)));
		
		return list;
	}
	
	private ModeloRadioButton get(String s, String sub) {
		return new ModeloRadioButton(s, sub);
	}
	
	@SuppressLint("NewApi") 
	public void SeleccionarItem() {
		Calendar cnow = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int year = cnow.get(Calendar.YEAR);
		int month = cnow.get(Calendar.MONTH);
		int day = cnow.get(Calendar.DATE);
		cnow.set(year, month, day, 23, 59, 59);

		String fechaactual = df.format(cnow.getTime());
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainInspeccionT.this);
		
		Calendar cback = Calendar.getInstance();
		cback.add(Calendar.DAY_OF_YEAR, -30);
		int yearr = cback.get(Calendar.YEAR);
		int monthr = cback.get(Calendar.MONTH);
		int dayr = cback.get(Calendar.DATE);
		cback.set(yearr, monthr, dayr, 0, 0, 0);
		String fechaanterior = df.format(cback.getTime());
		
		Log.d("actual", fechaactual);
		Log.d("atras", fechaanterior);

		SharedPreferences preferencess = PreferenceManager.getDefaultSharedPreferences(MainInspeccionT.this);
		Editor editore = preferencess.edit();
		editore.putString("fechainp", fechaanterior);
		editore.putString("fechafinp", fechaactual);
		editore.commit();
		
		int posicion = this.lvdatos.getCheckedItemPosition();
		
		if (posicion == 0) {//Escanear Cdigo de Barras
			Intent intentScan = new Intent(BS_PACKAGE + ".SCAN");
			intentScan.putExtra("PROMPT_MESSAGE", "Enfoque entre 9 y 11 cm. apuntado el codigo de barras de la maquina");
			startActivityForResult(intentScan, REQUEST_CODE);
		} else if (posicion == 1) {
			SharedPreferences preferences2 = PreferenceManager.getDefaultSharedPreferences(MainInspeccionT.this);
			String cmaquina = preferences2.getString("cmaquina", "");
			//cmaquina = "BOM-01";
			if (cmaquina.length()==0) {
				Toast.makeText(this, "Aún no ha escaneado código de barra.", Toast.LENGTH_SHORT).show();
			} else {
				// Nueva Inspeccin T.
				Intent intent = new Intent().setClass(this, MantInspeccionT.class);
				intent.putExtra("GrabaInspeccion", "N");
				intent.putExtra("codInspeccion", "0");
				this.startActivity(intent);
			}
		} else if (posicion == 2) {
			// Listado Inspecciones en Lnea
			Intent intent1 = new Intent(MainInspeccionT.this, HistoInspeccion.class);
			this.startActivity(intent1);
		} else if (posicion == 3) {
			// Listado Inspecciones T. Realizadas
			Intent intent2 = new Intent(MainInspeccionT.this, InspeccionesRealizadas.class);
			startActivity(intent2);
		}
	}
	
	public void EventoSeleccionar(View view) {
		SeleccionarItem();
	}
		
	// Ejecuta el evento del boton seleccionar
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//@SuppressWarnings("unused")
		//String dataclass = data.getComponent().getShortClassName();
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				contents = intent.getStringExtra("SCAN_RESULT");
				String cmaquina = "",nmaquina = "",cbarra = "",estado = "";
				String familia = "",centro = "";
				
				DataBase objDB = new DataBase(this, "DBInspeccion", null, 1);
				SQLiteDatabase _db = objDB.getWritableDatabase();
				
				String sql = "Select c_maquina,c_descripcion,c_codigobarras,c_estado,c_familiainspeccion,"
							 + "c_centrocosto from MTP_MAQUINAS where c_codigobarras = '"+ contents + "' ";
				
				Cursor c = _db.rawQuery(sql, null);
				if (c.moveToFirst()) {
					cmaquina = c
							.getString(0);
					nmaquina = c.getString(1);
					cbarra = c.getString(2);
					estado = c.getString(3);
					familia = c.getString(4);
					centro = c.getString(5);
					
					SharedPreferences preferences = PreferenceManager.
							getDefaultSharedPreferences(MainInspeccionT.this);
					Editor editor = preferences.edit();
					editor.putString("cmaquina", cmaquina);
					editor.putString("maquina", nmaquina);
					editor.putString("codbarra", cbarra);
					editor.putString("estado", estado);
					editor.putString("familia", familia);
					editor.putString("centro", centro);
					
					editor.commit();
					Toast.makeText(this, "Máquina: " + cmaquina + " - " + nmaquina + ". Cod. Familia: "
										 + familia + ".", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, "Máquina no disponible para ese código.", Toast.LENGTH_SHORT).show();
				}
				c.close();
				_db.close();
			}
		}
		return;
	}
			
	public void EventoSalir(View view) {
		finish();
	}
	
}
