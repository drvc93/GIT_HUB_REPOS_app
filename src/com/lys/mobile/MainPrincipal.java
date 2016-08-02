package com.lys.mobile;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class MainPrincipal extends TabActivity {
	TabHost tabHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainprincipal);
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String flagmantto = preferences.getString("flagmantto", "");
		
		Resources res = getResources();
		tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		
		// Creando Tab de Inspecciones Tcnicas
		intent = new Intent().setClass(this, MainInspeccionT.class);
		spec = tabHost.newTabSpec("tabinspecciont")
				.setIndicator("Inspección Técnica", res.getDrawable(R.drawable.itecnica))
				.setContent(intent);
		tabHost.addTab(spec);
		
		// Creando Tab de Inspecciones Generales
		intent = new Intent().setClass(this, MainInspeccionG.class);
		spec = tabHost.newTabSpec("tabinspecciong")
				.setIndicator("Inspección General", res.getDrawable(R.drawable.igeneral))
				.setContent(intent);
		tabHost.addTab(spec);
		
		setTabColors();
		//tabHost.setCurrentTab(0);
		if (flagmantto.equals("S")){
			tabHost.getTabWidget().getChildTabViewAt(0).setEnabled(true);
			tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(true);
			tabHost.setCurrentTab(0);
		}else{
			tabHost.getTabWidget().getChildTabViewAt(0).setEnabled(true);//false
			tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(true);
			tabHost.setCurrentTab(1);

		}
	}
	
	protected void setTabColors() {
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.draw_tab);
			RelativeLayout rl = (RelativeLayout) tabHost.getTabWidget()
					.getChildAt(i);
			TextView textView = (TextView) rl.getChildAt(1);
			textView.setTextColor(Color.parseColor("#FFFFFF"));
		}
	}
	
}
