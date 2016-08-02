package com.lys.mobile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MainInspeccionG extends Activity {
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
		list.add(get(this.getString(R.string.maininspecciong_nuevo),
				this.getString(R.string.maininspecciong_nuevo_sub)));
		list.add(get(this.getString(R.string.maininspecciong_inspeccionenlinea),
				this.getString(R.string.maininspecciong_inspeccionenlinea_sub)));
		list.add(get(this.getString(R.string.maininspecciong_listar),
				this.getString(R.string.maininspecciong_listar_sub)));
		
		return list;
	}

	private ModeloRadioButton get(String s, String sub) {
		return new ModeloRadioButton(s, sub);
	}
	
public void SeleccionarItem() {
	Calendar cnow = Calendar.getInstance();
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	int year = cnow.get(Calendar.YEAR);
	int month = cnow.get(Calendar.MONTH);
	int day = cnow.get(Calendar.DATE);
	cnow.set(year, month, day, 23, 59, 59);

	String fechaactual = df.format(cnow.getTime());
	
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainInspeccionG.this);
			
	Calendar cback = Calendar.getInstance();
	cback.add(Calendar.DAY_OF_YEAR, -30);
	int yearr = cback.get(Calendar.YEAR);
	int monthr = cback.get(Calendar.MONTH);
	int dayr = cback.get(Calendar.DATE);
	cback.set(yearr, monthr, dayr, 0, 0, 0);
	String fechaanterior = df.format(cback.getTime());
	
	Log.d("actual", fechaactual);
	Log.d("atras", fechaanterior);

	SharedPreferences preferencess = PreferenceManager.getDefaultSharedPreferences(MainInspeccionG.this);
	Editor editore = preferencess.edit();
	editore.putString("fechainpg", fechaanterior);
	editore.putString("fechafinpg", fechaactual);
	editore.commit();
	
	int posicion = this.lvdatos.getCheckedItemPosition();
	
	if (posicion == 0) { //Nueva Inspeccin G.
		Intent intent = new Intent().setClass(this, MantInspeccionG.class);
		intent.putExtra("GrabaInspeccionG", "N");
		intent.putExtra("codInspeccionG", "0");
		this.startActivity(intent);
	}
	else if (posicion == 1) {//Inspecciones en Lnea
		Intent intent2 = new Intent(MainInspeccionG.this, HistoInspeccionG.class);
		this.startActivity(intent2);
	}
	else if (posicion == 2) { //Listado de Inspecciones G. Realizadas
		Intent intent3 = new Intent(MainInspeccionG.this, InspeccionesRealizadasG.class);
		this.startActivity(intent3);
	}
}

public void EventoSeleccionar(View view) {
	SeleccionarItem();
}
	
// Ejecuta el evento del boton seleccionar
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	@SuppressWarnings("unused")
	String dataclass = data.getComponent().getShortClassName();
}
		
public void EventoSalir(View view) {
	finish();
}
	
}
