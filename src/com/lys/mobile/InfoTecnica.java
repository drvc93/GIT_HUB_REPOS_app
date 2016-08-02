package com.lys.mobile;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by User on 14/09/2014.
 */
public class InfoTecnica extends Activity implements View.OnClickListener{
	private Button realizar, revisar, insre;
    protected void onCreate (Bundle savedInstanceState) {
        //blablabla
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infotecnica);
        realizar = (Button) findViewById(R.id.btitft);
        revisar = (Button) findViewById(R.id.btitpl);
        insre = (Button) findViewById(R.id.btitca);
        realizar.setOnClickListener(this);
        revisar.setOnClickListener(this);
        insre.setOnClickListener(this);
    }
    
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
    	
    	Calendar cnow = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int year = cnow.get(Calendar.YEAR);
		int month = cnow.get(Calendar.MONTH);
		int day = cnow.get(Calendar.DATE);
		cnow.set(year, month, day, 23, 59, 59);

		String fechaactual = df.format(cnow.getTime());
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(InfoTecnica.this);
				
		Calendar cback = Calendar.getInstance();
		cback.add(Calendar.DAY_OF_YEAR, -30);
		int yearr = cback.get(Calendar.YEAR);
		int monthr = cback.get(Calendar.MONTH);
		int dayr = cback.get(Calendar.DATE);
		cback.set(yearr, monthr, dayr, 0, 0, 0);
		String fechaanterior = df.format(cback.getTime());
		
		Log.d("actual", fechaactual);
		Log.d("atras", fechaanterior);

		SharedPreferences preferencess = PreferenceManager.getDefaultSharedPreferences(InfoTecnica.this);
		Editor editore = preferencess.edit();
		editore.putString("fechainpg", fechaanterior);
		editore.putString("fechafinpg", fechaactual);
		// editor.putString("periocidadp", lista.get(0).periocidad);
		editore.commit();
    	
        switch (v.getId()) {
            case R.id.btitft:
            	realizar.setEnabled(false);
                Intent l = new Intent("com.example.user.codbar.Llenar2");
                startActivity(l);
                realizar.setEnabled(true);
                break;
            case R.id.btitpl:
            	revisar.setEnabled(false);
            	Intent n = new Intent(InfoTecnica.this, HistoInspeccionG.class);
            	startActivity(n);
            	revisar.setEnabled(true);
            	break;
            case R.id.btitca:
            	insre.setEnabled(false);
            	/*Intent m = new Intent("com.example.user.codbar.InspeccionesRealizadasG");
                startActivity(m);*/
            	Intent m = new Intent(InfoTecnica.this, InspeccionesRealizadasG.class);
            	startActivity(m);
                insre.setEnabled(true);
            	break;
        }
       }

    }
