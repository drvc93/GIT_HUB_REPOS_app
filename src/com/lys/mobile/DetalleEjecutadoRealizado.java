package com.lys.mobile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.androidquery.AQuery;
import com.lys.mobile.asynctask.EnviarReporteInspeccionAsyncTask;
import com.lys.mobile.data.DetalleInspeccionRealizadasData;
import com.lys.mobile.data.DetalleProgramaEjecutado;
import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.data.ProgramaEjecutadoData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.Dialogos;
import com.lys.mobile.util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class DetalleEjecutadoRealizado extends Activity {
	public ArrayList<DetalleInspeccionRealizadasData> lista;
	int posicion;
	AQuery aq;
	String cb="";
	MyApp app;
	String errores = "";
	boolean seborroarchivo = false;
	int respuestafinal = 0;
	public boolean pasar;
	Context contexto;
	LinearLayout tabCondicionInspeccion,tabDetalle,
	 			 tabResumen;
	TextView lblMaquina,lblDescripcionMaquina,
			 lblFechaInicio,lblFechaFin,lblInspector,
			 lblNombreInspector,lblComentario,cboEstado;
	Button btnMaquina,btnComentario;
	TextView cboCondicionMaquina,cboPeriodo;
	ListView listaInspecciones;
	
	TextView lblTab03_NumeroInspeccion,lblTab03_Maquina,
			 lblTab03_CondicionMaquina,lblTab03_Estado,
			 lblTab03_FechaInicio,lblTab03_FechaFin,
			 lblTab03_Inspector,lblTab03_Comentario;
	Button btnSiguiente;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.detalle_ejecutado);
		setContentView(R.layout.mantinspecciont_detrealizadas);
		lista = new ArrayList<DetalleInspeccionRealizadasData>();
		aq = new AQuery(getApplicationContext());// imageView1"
		// recupero valor y select y escribo
		contexto = this;
		
		app = ((MyApp)getApplicationContext());
		//TextView user = (TextView) findViewById(R.id.tvinllusuariod);
		//TextView condicion = (TextView) findViewById(R.id.tcondid);
		//TextView maquina = (TextView) findViewById(R.id.tmaquinad);
		//TextView descripcion = (TextView) findViewById(R.id.tdescripciond);
		//TextView estado = (TextView) findViewById(R.id.testadocad);
		//TextView finicio = (TextView) findViewById(R.id.tfind);
		//TextView ffin = (TextView) findViewById(R.id.tffind);
		//TextView periodo = (TextView) findViewById(R.id.tperiodod);
		//Button com = (Button) findViewById(R.id.tcomentind);
		//ListView listView = (ListView) findViewById(R.id.listainspecciond);
		EnlazarControles();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			String correla = extras.getString("correlativo");//
			String compa = extras.getString("compania");//
			
			cb = extras.getString("cb");//

			if (lista.size() > 0)
				lista.clear();
			DataBase basededatos = new DataBase(DetalleEjecutadoRealizado.this,"DBInspeccion", null, 1);
			SQLiteDatabase db = basededatos.getWritableDatabase();

			String query = "SELECT c.c_compania,c.n_correlativo,c.c_maquina, c.c_condicionmaquina, " + 
			"c.c_comentario, c.c_estado,c.d_fechaInicioInspeccion, c.d_fechaFinInspeccion,c.c_periodoinspeccion, " +
			"c.c_usuarioInspeccion,c.c_usuarioenvio,c.d_fechaenvio,c.c_ultimousuario,c.d_ultimafechamodificacion, " + 
			"d.c_compania,d.n_correlativo,d.n_linea,d.c_inspeccion,d.c_tipoinspeccion,d.n_porcentajeminimo, " + 
			"d.n_porcentajemaximo,d.n_porcentajeinspeccion,d.c_estado,d.c_comentario,d.c_rutafoto " + 
			"from MTP_INSPECCIONMAQUINA_CAB c,MTP_INSPECCIONMAQUINA_DET d " + 
			"where d.n_correlativo=c.n_correlativo and d.c_compania=c.c_compania and c.c_compania='"
			+ compa + "' and c.n_correlativo='" + correla+"' order by d.n_linea asc";
			Log.d("query", "" + query);
			Cursor c = db.rawQuery(query, null);
			if (c.moveToFirst()) {

				do {

					DetalleInspeccionRealizadasData p = new DetalleInspeccionRealizadasData();

					p.c_compania = c.getString(0);
					p.n_correlativo = c.getString(1);
					p.c_maquina = c.getString(2);
					p.c_condicionmaquina = c.getString(3);
					p.c_comentario = c.getString(4);
					p.c_estado = c.getString(5);
					p.d_fechaInicioInspeccion = c.getString(6);
					p.d_fechaFinInspeccion = c.getString(7);
					p.c_periodoinspeccion = c.getString(8);
					p.c_usuarioInspeccion = c.getString(9);
					p.c_usuarioenvio = c.getString(10);
					p.d_fechaenvio = c.getString(11);
					p.c_ultimousuario = c.getString(12);
					p.d_ultimafechamodificacion = c.getString(13);

					p.c_companiad = c.getString(14);
					p.n_correlativod = c.getString(15);
					p.n_linead = c.getString(16);
					p.c_inspecciond = c.getString(17);
					p.c_tipoinspecciond = c.getString(18);
					p.n_porcentajeminimod = c.getString(19);
					p.n_porcentajemaximod = c.getString(20);
					p.n_porcentajeinspecciond = c.getString(21);
					p.c_estadod = c.getString(22);
					p.c_comentariod = c.getString(23);
					p.c_rutafotod = c.getString(24);

					lista.add(p);

				} while (c.moveToNext());

				// del usuariio de la condicion de la descripcion

				String des = "", pe = "";

				String queryc = "SELECT c_descripcion from MTP_PERIODOINSPECCION where c_periodoinspeccion='"
						+ lista.get(0).c_periodoinspeccion + "' ";
				Cursor cc = db.rawQuery(queryc, null);
				if (cc.moveToFirst()) {

					pe = cc.getString(0);

				} else {

					Toast.makeText(this, "Periodo no disponible.",
							Toast.LENGTH_SHORT).show();
				}
				cc.close();

				String queryd = "SELECT c_descripcion from MTP_MAQUINAS where c_maquina='"
						+ lista.get(0).c_maquina + "' ";
				Cursor cd = db.rawQuery(queryd, null);
				if (cd.moveToFirst()) {

					des = cd.getString(0);

				} else {

					Toast.makeText(this, "Descripcion no disponible.",
							Toast.LENGTH_SHORT).show();
				}
				cd.close();

				/*if (lista.get(0).c_estado.equalsIgnoreCase("I"))
					env.setVisibility(View.VISIBLE);*/

				lblInspector.setText("" + lista.get(0).c_usuarioInspeccion);
								
				if(lista.get(0).c_condicionmaquina.equalsIgnoreCase("A")){
					
					cboCondicionMaquina.setText("ABIERTA");
					
				}else if(lista.get(0).c_condicionmaquina.equalsIgnoreCase("C")){
										
					cboCondicionMaquina.setText("CERRADA" );	
				}		
				
				if(lista.get(0).c_estado.equalsIgnoreCase("E")){
					
					cboEstado.setText("ENVIADA");
					
				}else if(lista.get(0).c_estado.equalsIgnoreCase("I")){
										
					cboEstado.setText("INGRESADA");	
				}
		
				lblMaquina.setText("" + lista.get(0).c_maquina);
				lblDescripcionMaquina.setText("" + des);
	
				lblFechaInicio.setText("" + lista.get(0).d_fechaInicioInspeccion);
				lblFechaFin.setText("" + lista.get(0).d_fechaFinInspeccion);
				cboPeriodo.setText("" + pe);
				lblComentario.setText("" + lista.get(0).c_comentario);

				for (int i = 0; i < lista.size(); i++) {

					String querycc = "SELECT c_descripcion from MTP_INSPECCION where c_inspeccion='"
							+ lista.get(i).c_inspecciond + "' ";
					Cursor ccc = db.rawQuery(querycc, null);
					if (ccc.moveToFirst()) {

						des = ccc.getString(0);

						lista.get(i).c_descripcion = des;

					} else {
						Log.d("no hay", "");
					}
					ccc.close();

				}

				btnComentario.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						final Dialog alertDialog = new Dialog(DetalleEjecutadoRealizado.this);

						alertDialog.setContentView(R.layout.dialogo_comentario);
						alertDialog.setTitle("Comentario");
						alertDialog.setCancelable(false);
						final EditText text = (EditText) alertDialog.findViewById(R.id.ecomentario);
						text.setEnabled(false);
						text.setText("" + lista.get(0).c_comentario);

						Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
						dialogButton.setEnabled(false);
						// if button is clicked, close the custom dialog
						dialogButton.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {

								alertDialog.cancel();

							}
						});

						Button dialogCancelar = (Button) alertDialog.findViewById(R.id.bcancelar);
						// if button is clicked, close the custom dialog
						dialogCancelar.setOnClickListener(new OnClickListener() {

									public void onClick(View v) {

										alertDialog.cancel();

									}
								});

						alertDialog.show();

					}
				});

				/*env.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String imagen;
						boolean existe = false;
						pasar = true;
						if (isOnline()) {

							DataBase basededatos = new DataBase(
									DetalleEjecutadoRealizado.this,
									"DBInspeccion", null, 1);
							SQLiteDatabase db = basededatos
									.getWritableDatabase();

							SharedPreferences preferences = PreferenceManager
									.getDefaultSharedPreferences(DetalleEjecutadoRealizado.this);
							String us = preferences.getString("nombreuser", "");
					

							ArrayList<InspeccionData> lista1 = new ArrayList<InspeccionData>();

							for (int i = 0; i < lista.size(); i++) {

								InspeccionData in = new InspeccionData();

								in.n_linea = lista.get(i).n_linead;
								in.c_inspeccion = lista.get(i).c_inspecciond;

								in.c_tipoinspeccion = lista.get(i).c_tipoinspecciond;
								in.c_descripcion = lista.get(i).c_descripciond;
								in.n_porcentajeminimo = lista.get(i).n_porcentajeminimod;
								in.n_porcentajemaximo = lista.get(i).n_porcentajemaximod;
								in.n_porcentajeinspeccion = lista.get(i).n_porcentajeinspecciond;
								in.c_estado = lista.get(i).c_estadod;
								in.c_comentario = lista.get(i).c_comentariod;
								in.c_rutafoto = lista.get(i).c_rutafotod;

								in.c_familiainspeccion = lista.get(i).c_inspecciond;
								in.d_periodoinspeccion = lista.get(i).c_periodoinspeccion;

								in.c_compania = lista.get(i).c_compania;
								in.n_correlativo = lista.get(i).n_correlativo;

								in.c_ultimousuario = lista.get(0).c_ultimousuariod;
								in.d_ultimafechamodificacion = lista.get(i).d_ultimafechamodificaciond;
								in.d_periocidad = lista.get(i).c_inspecciond;
								in.c_condicionmaquina = lista.get(i).c_condicionmaquina;
								in.c_comentarioi = lista.get(i).c_comentario;
								in.c_estadoi = lista.get(i).c_estado;
								in.d_ultimafechamodificacioni = lista.get(i).d_ultimafechamodificacion;

								lista1.add(in);

							}

							final EnviarReporteInspeccionAsyncTask enviarinspeccion = new EnviarReporteInspeccionAsyncTask(
									DetalleEjecutadoRealizado.this, lista
											.get(0).n_correlativo,
									lista.get(0).n_correlativo, Util.compania,
									us, cb, lista.get(0).c_condicionmaquina,
									lista.get(0).c_comentario, "I", lista
											.get(0).c_periodoinspeccion, lista
											.get(0).d_fechaInicioInspeccion,
									lista.get(0).d_fechaFinInspeccion, lista1);
							enviarinspeccion.execute(new String[] { Util.url
									+ "registrarInspeccion" });

							Thread thread = new Thread() {
								public void run() {
									try {
										enviarinspeccion.get(120000,
												TimeUnit.MILLISECONDS);
										// tarea.get(30000,
										// TimeUnit.MILLISECONDS);

									} catch (Exception e) {
										enviarinspeccion.cancel(true);
										((Activity) DetalleEjecutadoRealizado.this)
												.runOnUiThread(new Runnable() {

													public void run() {

														enviarinspeccion.progressDialog.dismiss();
														pasar = false;
														Toast.makeText(
																DetalleEjecutadoRealizado.this,
																"No se pudo establecer comunicacion .",
																Toast.LENGTH_LONG)
																.show();

													}
												});
									}
								}
							};
							thread.start();

						} else {
							pasar = false;
							Toast.makeText(DetalleEjecutadoRealizado.this,"No hay acceso a internet .",
									Toast.LENGTH_LONG).show();

						}
						
						if (pasar == true) {
							for (int i = 0; i < lista.size(); i++) {
								Log.e("entro aqui online for 2",String.valueOf(lista.size()));
								//fotoslistain.add(lista.get(i).c_rutafoto);
								if (lista.get(i).c_rutafotod != null) {
									Log.e("entro aqui online for 2",lista.get(i).c_rutafotod);
									Log.e("entro aqui online for 2",Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
											"/" + lista.get(i).c_rutafotod);
									imagen = Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
											"/" + lista.get(i).c_rutafotod;
									existe = true;
									break;
								}
							}
							Log.e("entro aqui online for 2",String.valueOf(existe));
							if (existe == true)
								new AsyncSender().execute();
						}

					}
				});*/

				//listView.setAdapter(new UserItemAdapter(getApplicationContext(),android.R.layout.simple_list_item_1, lista));
				listaInspecciones.setAdapter(new UserItemAdapter(getApplicationContext(),android.R.layout.simple_list_item_1, lista));

			} else {

				Toast.makeText(DetalleEjecutadoRealizado.this,
						"No hay informacion disponible.", Toast.LENGTH_SHORT)
						.show();

				Log.d("informacion", "no hay");

			}
			c.close();
			db.close();

		}

		/*atras.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DetalleEjecutadoRealizado.this.finish();

			}
		});*/

	}
	
	private void EnlazarControles() {
		tabCondicionInspeccion = (LinearLayout)findViewById(R.id.tabInspeccionT01);
		tabDetalle = (LinearLayout)findViewById(R.id.tabInspeccionT02);
		tabResumen = (LinearLayout)findViewById(R.id.tabInspeccionT03);		
		
		// Datos del Tab 01: Condici�n de Inspecciones T.
		//TextView maquina = (TextView) findViewById(R.id.lblMaquina);
		lblMaquina = (TextView)findViewById(R.id.lblMaquina);
		//TextView descripcion = (TextView) findViewById(R.id.lblDescripcionMaquina);
		lblDescripcionMaquina = (TextView)findViewById(R.id.lblDescripcionMaquina);
		btnMaquina = (Button)findViewById(R.id.btnMaquina);
		//TextView condicion = (TextView) findViewById(R.id.lblCondicionMaquina);
		cboCondicionMaquina = (TextView)findViewById(R.id.lblCondicionMaquina);
		
		//TextView estado = (TextView) findViewById(R.id.lblEstado);
		cboEstado = (TextView)findViewById(R.id.lblEstado);
		//TextView finicio = (TextView) findViewById(R.id.lblFechaInicio);
		lblFechaInicio = (TextView)findViewById(R.id.lblFechaInicio);
		//TextView ffin = (TextView) findViewById(R.id.lblFechaFin);
		lblFechaFin = (TextView)findViewById(R.id.lblFechaFin);
		//TextView user = (TextView) findViewById(R.id.lblInspector);
		lblInspector = (TextView)findViewById(R.id.lblInspector);
		lblNombreInspector = (TextView)findViewById(R.id.lblNombreInspector);
		//TextView tcom = (TextView) findViewById(R.id.lblComentario);
		lblComentario = (TextView)findViewById(R.id.lblComentario);
		//Button com = (Button) findViewById(R.id.btnComentario);
		btnComentario = (Button)findViewById(R.id.btnComentario);
		
		// Datos del Tab 02: Detalle
		//ListView listView = (ListView) findViewById(R.id.listaInspecciones);
		listaInspecciones = (ListView)findViewById(R.id.listaInspecciones);
		//TextView periodo = (TextView) findViewById(R.id.lblPeriodo);
		cboPeriodo = (TextView)findViewById(R.id.lblPeriodo);
		
		//Datos del Tab 03: Resumen
		lblTab03_NumeroInspeccion = (TextView)findViewById(R.id.lblTab03_NumeroInspeccion);
		lblTab03_Maquina = (TextView)findViewById(R.id.lblTab03_Maquina);
		lblTab03_CondicionMaquina = (TextView)findViewById(R.id.lblTab03_CondicionMaquina);
		lblTab03_Estado = (TextView)findViewById(R.id.lblTab03_Estado);
		lblTab03_FechaInicio = (TextView)findViewById(R.id.lblTab03_FechaInicio);
		lblTab03_FechaFin = (TextView)findViewById(R.id.lblTab03_FechaFin);
		lblTab03_Inspector = (TextView)findViewById(R.id.lblTab03_Inspector);
		lblTab03_Comentario = (TextView)findViewById(R.id.lblTab03_Comentario);
		
		//Pie
		//Button env = (Button) findViewById(R.id.env);
		btnSiguiente = (Button)findViewById(R.id.btnSiguiente);
		//Button atras = (Button) findViewById(R.id.sendd);
	}
	
	public void EventoRetroceder(View view) {
		/*if (GrabaInspeccion.equals("V")) {
			finish();
			return;
		}*/

		int i = obtenerPantalla();
		if (i == 0) {
			finish();
		} else
			establecerPantalla(i, false);
	}
	
	public void EventoSiguiente(View view) {
		int i = obtenerPantalla();
		establecerPantalla(i, true);
		//return;
	}
	
	private int obtenerPantalla() {
		int valor = 0;
		if (tabCondicionInspeccion.getVisibility() == View.VISIBLE)
			valor = 0;
		else if (tabDetalle.getVisibility() == View.VISIBLE)
			valor = 1;
		/*else if (tabResumen.getVisibility() == View.VISIBLE)
			valor = 2;*/
		return valor;
	}
	
	private void establecerPantalla(int i, boolean val) {
		int pos = 0;
		if (val == false)
			i = i - 1;
		if (i == 0) {
			tabCondicionInspeccion.setVisibility((val == true) ? View.GONE
					: View.VISIBLE);
			tabDetalle.setVisibility((!val == true) ? View.GONE
					: View.VISIBLE);
			if (val == true) {
				if(cboEstado.getText().equals("INGRESADA"))
				{
					btnSiguiente.setText("Enviar");
				} else {
					btnSiguiente.setText("Finalizar");
				}
			} else {
				btnSiguiente.setText("Siguiente");
			}
		} else {
			if(cboEstado.getText().equals("INGRESADA"))
			{
				if (Dialogos.getYesNoWithExecutionStop("Confirmaci�n", 
						"�Desea sincronizar la Inspecci�n T.?", this)) {
					InicioSincronizacion();
				}
			} else {
				this.finish();
			}
		}
		
	}
	
	public void InicioSincronizacion() {
		String imagen;
		boolean existe = false;
		pasar = true;
		if (isOnline()) {

			DataBase basededatos = new DataBase(DetalleEjecutadoRealizado.this,"DBInspeccion", null, 1);
			SQLiteDatabase db = basededatos.getWritableDatabase();

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(DetalleEjecutadoRealizado.this);
			String us = preferences.getString("user", "");

			ArrayList<InspeccionData> lista1 = new ArrayList<InspeccionData>();

			for (int i = 0; i < lista.size(); i++) {

				InspeccionData in = new InspeccionData();

				in.n_linea = lista.get(i).n_linead;
				in.c_inspeccion = lista.get(i).c_inspecciond;

				in.c_tipoinspeccion = lista.get(i).c_tipoinspecciond;
				in.c_descripcion = lista.get(i).c_descripciond;
				in.n_porcentajeminimo = lista.get(i).n_porcentajeminimod;
				in.n_porcentajemaximo = lista.get(i).n_porcentajemaximod;
				in.n_porcentajeinspeccion = lista.get(i).n_porcentajeinspecciond;
				in.c_estado = lista.get(i).c_estadod;
				in.c_comentario = lista.get(i).c_comentariod;
				in.c_rutafoto = lista.get(i).c_rutafotod;

				in.c_familiainspeccion = lista.get(i).c_inspecciond;
				in.d_periodoinspeccion = lista.get(i).c_periodoinspeccion;

				in.c_compania = lista.get(i).c_compania;
				in.n_correlativo = lista.get(i).n_correlativo;

				in.c_ultimousuario = lista.get(0).c_ultimousuariod;
				in.d_ultimafechamodificacion = lista.get(i).d_ultimafechamodificaciond;
				in.d_periocidad = lista.get(i).c_inspecciond;
				in.c_condicionmaquina = lista.get(i).c_condicionmaquina;
				in.c_comentarioi = lista.get(i).c_comentario;
				in.c_estadoi = lista.get(i).c_estado;
				in.d_ultimafechamodificacioni = lista.get(i).d_ultimafechamodificacion;

				lista1.add(in);

			}

			final EnviarReporteInspeccionAsyncTask enviarinspeccion = new EnviarReporteInspeccionAsyncTask(
					DetalleEjecutadoRealizado.this, lista
							.get(0).n_correlativo,
					lista.get(0).n_correlativo, Util.compania,
					us, cb, lista.get(0).c_condicionmaquina,
					lista.get(0).c_comentario, "I", lista
							.get(0).c_periodoinspeccion, lista
							.get(0).d_fechaInicioInspeccion,
					lista.get(0).d_fechaFinInspeccion, lista1);
			enviarinspeccion.execute(new String[] { Util.url
					+ "registrarInspeccion" });

			Thread thread = new Thread() {
				public void run() {
					try {
						enviarinspeccion.get(120000,
								TimeUnit.MILLISECONDS);
						// tarea.get(30000,
						// TimeUnit.MILLISECONDS);

					} catch (Exception e) {
						enviarinspeccion.cancel(true);
						((Activity) DetalleEjecutadoRealizado.this)
								.runOnUiThread(new Runnable() {

									public void run() {

										enviarinspeccion.progressDialog.dismiss();
										pasar = false;
										Toast.makeText(
												DetalleEjecutadoRealizado.this,
												"No se pudo establecer comunicacion .",
												Toast.LENGTH_LONG)
												.show();

									}
								});
					}
				}
			};
			thread.start();

		} else {
			pasar = false;
			Toast.makeText(DetalleEjecutadoRealizado.this,"No hay acceso a internet .",
					Toast.LENGTH_LONG).show();

		}
		
		if (pasar == true) {
			for (int i = 0; i < lista.size(); i++) {
				Log.e("entro aqui online for 2",String.valueOf(lista.size()));
				//fotoslistain.add(lista.get(i).c_rutafoto);
				/*if (!lista.get(i).c_rutafotod.equals("") && (lista.get(i).c_rutafotod != null) && !lista.get(i).c_rutafotod.equals("null")
						) {*/
				if (lista.get(i).c_rutafotod != null) {
					Log.e("entro aqui online for 2",lista.get(i).c_rutafotod);
					Log.e("entro aqui online for 2",Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
							"/" + lista.get(i).c_rutafotod);
					imagen = Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
							"/" + lista.get(i).c_rutafotod;
					existe = true;
					break;
				}
			}
			Log.e("entro aqui online for 2",String.valueOf(existe));
			if (existe == true)
				new AsyncSender().execute();
		}
	}
	
	public class UserItemAdapter extends ArrayAdapter<DetalleInspeccionRealizadasData> {
		private ArrayList<DetalleInspeccionRealizadasData> listcontact;

		public UserItemAdapter(Context context, int textViewResourceId,
				ArrayList<DetalleInspeccionRealizadasData> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}
		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		public View getView(final int position, View convertView,
				final ViewGroup parent) {

			View v = null;

			final DetalleInspeccionRealizadasData user = listcontact.get(position);

			int valor = 0;

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				if (user.n_porcentajeminimod.substring(0, 1).equals("0")
						&& user.n_porcentajemaximod.substring(0, 1).equals("0")) {// user.n_porcentajeminimo
																					// y
																					// user.n_porcentajemaximo
																					// =0
					valor = 1;
					Log.d("valor", "1");

					Log.d("minimo", user.n_porcentajeminimod.substring(0, 1));
					Log.d("maximo", user.n_porcentajemaximod.substring(0, 1));
					Log.d("aq", "----------------------------");
					//v = vi.inflate(R.layout.con_detalle_inspeccion_se, parent,false);
					v = vi.inflate(R.layout.det_detalle_inspeccion_se, parent,false);

				} else {
					valor = 2;
					Log.d("valor", "2");

					Log.d("minimo", user.n_porcentajeminimod.substring(0, 1));
					Log.d("maximo", user.n_porcentajemaximod.substring(0, 1));
					Log.d("aq", "----------------------------");
					//v = vi.inflate(R.layout.con_detalleinspeccion_sm, parent,false);
					v = vi.inflate(R.layout.det_detalleinspeccion_sm, parent,false);

				}

			}

			if (user != null) {

				if (valor == 1) {

					TextView numero = (TextView) v.findViewById(R.id.tnumerod);
					TextView detalle = (TextView) v.findViewById(R.id.tdetalled);
					TextView systema = (TextView) v.findViewById(R.id.tsystemad);
					TextView minimo = (TextView) v.findViewById(R.id.tminimod);
					TextView maximo = (TextView) v.findViewById(R.id.tmaximod);
					TextView campo = (TextView) v.findViewById(R.id.tcampod);
					final Spinner ok = (Spinner) v.findViewById(R.id.tokd);

					final Button comentario = (Button) v.findViewById(R.id.bcomentariod);
					Button imagen = (Button) v.findViewById(R.id.bimagend);

					minimo.setEnabled(false);
					maximo.setEnabled(false);
					campo.setEnabled(false);
					ok.setEnabled(false);

					if (numero != null) {
						numero.setText(String.valueOf(position + 1));
						user.n_linead = (String.valueOf(position + 1));
					}
					if (detalle != null) {
						detalle.setText("" + user.c_descripcion);
					}
					if (systema != null) {
						systema.setText("" + user.c_tipoinspecciond);
					}
					if (campo != null) {
						// campo.setText(user.n_porcentajeinspeccion);
						//
					}
					if (minimo != null) {

						String stringmi = user.n_porcentajeminimod;

						String Str1 = new String(stringmi);

						if (Str1.substring(0, 3).equals("100")) {

							user.n_porcentajeminimod = Str1.substring(0, 6);

						} else {

							user.n_porcentajeminimod = Str1.substring(0, 5);

						}

						minimo.setText("" + user.n_porcentajeminimod);
					}
					if (maximo != null) {

						String stringma = user.n_porcentajemaximod;

						String Str2 = new String(stringma);

						if (Str2.substring(0, 3).equals("100")) {

							user.n_porcentajemaximod = Str2.substring(0, 6);
						} else {

							user.n_porcentajemaximod = Str2.substring(0, 5);

						}

						maximo.setText("" + user.n_porcentajemaximod);
					}

					if (comentario != null) {
						// if(user.c_comentario.equals("") ||
						// !(user.c_comentario==null))

						comentario.setText(user.c_comentariod);
					}
					if (imagen != null) {
						// imagen.setText(""+user.c_rutafotod);
						imagen.setText("" + user.c_rutafotod);
					}

					List<String> listacond = new ArrayList<String>();
					listacond.add("OK");
					listacond.add("FALLA");
					
					ArrayAdapter<String> dataAdapterCon = new ArrayAdapter<String>(
							DetalleEjecutadoRealizado.this,
							android.R.layout.simple_spinner_item, listacond);

					dataAdapterCon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					ok.setAdapter(dataAdapterCon);
					
					if (user.c_estadod.equals("O")) {
						ok.setSelection(0);
					} else {
						ok.setSelection(1);
					}
					
					/*if (user.c_estadod.equals("O")) {
						listacond.add("OK");
					} else {
						listacond.add("FALLA");
					}

					ArrayAdapter<String> dataAdapterCon = new ArrayAdapter<String>(
							DetalleEjecutadoRealizado.this,
							android.R.layout.simple_spinner_item, listacond);

					dataAdapterCon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					ok.setAdapter(dataAdapterCon);

					ok.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub

							// llenarcon(periodos);
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub

						}
					});*/
					comentario.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							final Dialog alertDialog = new Dialog(
									DetalleEjecutadoRealizado.this);

							alertDialog.setContentView(R.layout.dialogo_comentario);
							alertDialog.setTitle("Comentario");
							alertDialog.setCancelable(false);
							final EditText text = (EditText) alertDialog.findViewById(R.id.ecomentario);
							text.setText("" + user.c_comentariod);
							text.setEnabled(false);

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
							dialogButton.setEnabled(false);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {

											alertDialog.cancel();

										}
									});

							Button dialogCancelar = (Button) alertDialog.findViewById(R.id.bcancelar);
							// if button is clicked, close the custom dialog
							dialogCancelar.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {

											alertDialog.cancel();

										}
									});

							alertDialog.show();
						}
					});

					imagen.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							// abre camaraa
							// sonar

							posicion = position;

							// motrar kla puta foto

							final Dialog alertDialog = new Dialog(DetalleEjecutadoRealizado.this);
							alertDialog.setContentView(R.layout.dialogo_foto);
							alertDialog.setTitle("Foto");
							alertDialog.setCancelable(false);
							final ImageView ima = (ImageView) alertDialog.findViewById(R.id.ximage);
							// text.setText(""+user.c_rutafotod);

							try {

								//aq.id(ima).image(Util.urlfoto + user.c_rutafotod, true, true, 400, 0, null, 0, 0.0f);
								aq.id(ima).image(Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
										"/" + user.c_rutafotod, true, true, 400, 0, null, 0, 0.0f);

							} catch (Exception e) {

								Log.d("error", e.getMessage());

								ima.setImageDrawable(getResources().getDrawable(R.drawable.sin));
							}

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {

											alertDialog.cancel();

										}
									});

							alertDialog.show();

						}
					});

				} else if (valor == 2) {

					TextView numero = (TextView) v.findViewById(R.id.tnumerod);
					TextView detalle = (TextView) v.findViewById(R.id.tdetalled);
					TextView systema = (TextView) v.findViewById(R.id.tsystemad);
					TextView minimo = (TextView) v.findViewById(R.id.tminimod);
					TextView maximo = (TextView) v.findViewById(R.id.tmaximod);
					final EditText llenar = (EditText) v.findViewById(R.id.tllenarsmd);
					final TextView ok = (TextView) v.findViewById(R.id.tokid);
					final Button comentario = (Button) v.findViewById(R.id.bcomentariod);
					Button imagen = (Button) v.findViewById(R.id.bimagend);

					minimo.setEnabled(false);
					maximo.setEnabled(false);
					llenar.setEnabled(false);

					if (numero != null) {
						numero.setText(String.valueOf(position + 1));
						user.n_linead = (String.valueOf(position + 1));
					}
					if (detalle != null) {
						detalle.setText("" + user.c_descripcion);
					}
					if (systema != null) {
						systema.setText("" + user.c_tipoinspecciond);
					}
					if (minimo != null) {

						String stringmi = user.n_porcentajeminimod;

						String Str1 = new String(stringmi);

						if (Str1.substring(0, 3).equals("100")) {

							user.n_porcentajeminimod = Str1.substring(0, 6);

						} else {

							user.n_porcentajeminimod = Str1.substring(0, 5);

						}

						minimo.setText("" + user.n_porcentajeminimod);
					}
					if (maximo != null) {

						String stringma = user.n_porcentajemaximod;

						String Str2 = new String(stringma);

						if (Str2.substring(0, 3).equals("100")) {

							user.n_porcentajemaximod = Str2.substring(0, 6);
						} else {

							user.n_porcentajemaximod = Str2.substring(0, 5);

						}

						maximo.setText("" + user.n_porcentajemaximod);
					}
					if (llenar != null) {
						
						String stringll = user.n_porcentajeinspecciond;
						
						String Str3 = new String(stringll);
						
						if (Str3.indexOf('.') >= 0) {
							Log.e("index 1",String.valueOf(Str3.indexOf('.')));
							Log.e("1",Str3);
							llenar.setText("" + user.n_porcentajeinspecciond);
						} else {
							Log.e("index 2",String.valueOf(Str3.indexOf('.')));
							Log.e("2",Str3);
							llenar.setText("" + user.n_porcentajeinspecciond + ".00");
						}
						
						/*if (Str3.substring(0, 3).equals("100")) {

							user.n_porcentajeinspecciond = Str3.substring(0, 6);
						} else {

							user.n_porcentajeinspecciond = Str3.substring(0, 5);

						}*/
						
						//llenar.setText("" + user.n_porcentajeinspecciond);
					
					}

					if (ok != null) {
						
						if(user.c_estadod.equals("O")){
							ok.setText("OK");
							
						}else{
							
							ok.setText("FALLA");
						}
						// !(user.c_estado==null))

						// ok.setText(user.c_estado);
					}
					if (comentario != null) {
						// if(user.c_comentario.equals("") ||
						// !(user.c_comentario==null))
						comentario.setText(user.c_comentariod);
					}
					if (imagen != null) {
						// imagen.setText(""+user.c_rutafotod);user.c_rutafotod

						imagen.setText("" + user.c_rutafotod);
					}

					ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

						}
					});

					comentario.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							final Dialog alertDialog = new Dialog(
									DetalleEjecutadoRealizado.this);

							alertDialog.setContentView(R.layout.dialogo_comentario);
							alertDialog.setTitle("Comentario");
							alertDialog.setCancelable(false);
							final EditText text = (EditText) alertDialog.findViewById(R.id.ecomentario);
							text.setText("" + user.c_comentariod);
							text.setEnabled(false);

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
							dialogButton.setEnabled(false);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {

											alertDialog.cancel();

										}
									});

							Button dialogCancelar = (Button) alertDialog.findViewById(R.id.bcancelar);
							// if button is clicked, close the custom dialog
							dialogCancelar.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {

											alertDialog.cancel();

										}
									});

							alertDialog.show();
						}
					});

					imagen.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							// abre camara
							// sonar

							final Dialog alertDialog = new Dialog(DetalleEjecutadoRealizado.this);

							alertDialog.setContentView(R.layout.dialogo_foto);
							alertDialog.setTitle("Foto");
							alertDialog.setCancelable(false);
							final ImageView ima = (ImageView) alertDialog
									.findViewById(R.id.ximage);
							// text.setText(""+user.c_rutafotod);
							
							try {

								//aq.id(ima).image(Util.urlfoto + user.c_rutafotod, true, true, 400, 0, null, 0, 0.0f);
								aq.id(ima).image(Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
										"/" + user.c_rutafotod, true, true, 400, 0, null, 0, 0.0f);

							} catch (Exception e) {

								Log.d("error", e.getMessage());

								ima.setImageDrawable(getResources().getDrawable(R.drawable.sin));
							}
							
							/*if(user.c_rutafotod.contains("/")){//local
								
								try {
									
									File newfile = new File(user.c_rutafotod);
									
									
									   Bitmap myBitmap = BitmapFactory.decodeFile(newfile.getAbsolutePath());
										
									   ima.setImageBitmap(myBitmap);
									
									
								}catch (Exception e) {
									Log.d("error1", e.getMessage());
								
								}
								
							}else{
								
								
								try {
									
									
									aq.id(ima).image(
											Util.urlfoto + user.c_rutafotod, true,
											true, 400, 0, null, 0, 0.0f);
									
									
									
									
									
								} catch (Exception ee) {
									Log.d("error", ee.getMessage());
									
									// ima.setImageDrawable(getResources().getDrawable(R.drawable.sin));
								}
								
								
							}*/

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {

											alertDialog.cancel();

										}
									});

							alertDialog.show();
						}
					});

				}

				// return v;
			}

			return v;

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
		DetalleEjecutadoRealizado.this.finish();
	}
	
	/****Async Para Envio de Imagenes****/
	@SuppressLint("NewApi")
	private final class AsyncSender extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;



		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(contexto);
			pd.setTitle("Enviando datos al servidor");
			pd.setMessage("Por favor espere, data esta enviandose.");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			seborroarchivo = false;
			respuestafinal = ListaEnviarServidor();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.dismiss();
			mensajeSegunRespuestaServidor(respuestafinal);
		}
	}
	/****Fin Async Para Envio de Imagenes****/
	public int ListaEnviarServidor() {
		int resp = 0; // Sin datos enviados
		String imagen;
		for (int i = 0; i < lista.size(); i++) {
			Log.e("entro aqui online for 2",String.valueOf(lista.size()));
			//fotoslistain.add(lista.get(i).c_rutafoto);
			if (!lista.get(i).c_rutafotod.equals("") && (lista.get(i).c_rutafotod != null)) {
				imagen = Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
						"/" + lista.get(i).c_rutafotod;
				
				Log.e("Imagen",imagen);
				
				resp = enviarservidor(imagen);// Respuesta enviada del
											  // servidor(200) correcto
				Log.e("resp1 #" + String.valueOf(i),String.valueOf(resp));
				
				if (resp != 200) {
					resp = -1; // Cancela el for y emite error en envio
					break;
				} else {
					//BorrarFile(imagen);
				}
				Log.e("resp2 #" + String.valueOf(i),String.valueOf(resp));
			}
		}
		return resp;
		/*int resp = 0; // Sin datos enviados
		for (int i = 0; i < lvdatos.getCount(); i++) {
			ModeloCheckBox EModelo = (ModeloCheckBox) lvdatos
					.getItemAtPosition(i);
			if (EModelo.isSelected()) {
				String imagen = EModelo.getName();
				if (!imagen.equals("") && (imagen != null)) {
					
					Log.e("Imagen1 #" + String.valueOf(i),imagen);
					
					resp = enviarservidor(imagen);// Respuesta enviada del
												// servidor(200) correcto
					Log.e("resp",String.valueOf(resp));	
					if (resp != 200) {
						resp = -1; // Cancela el for y emite error en envio
						break;
					} else {
						BorrarFile(imagen);
					}
				}
			}
		}
		return resp;*/
	}
	private int enviarservidor(String rutadisco) {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;
		String pathToOurFile = rutadisco;
		//String urlServer = app.getRutaImagenGarantia();
		String urlServer = app.getUrlImagenInspeccion();
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int respuestaservidor = 0;

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1024; // 100kb

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(
					pathToOurFile));
			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(0);
			connection.setReadTimeout(0);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			
			String nombreweb = rutadisco.substring(rutadisco.lastIndexOf("/") + 1, rutadisco.length());
			//Log.e("nombreweb",nombreweb);
			//Log.e("Paso aqui 0","0");
			outputStream = new DataOutputStream(connection.getOutputStream());
			//Log.e("Paso aqui 1","1");
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			//Log.e("Paso aqui 2","2");
			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ nombreweb + "\"" + lineEnd);
			//Log.e("lineEnd",lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);

			// Responses from the server (code and message)
			respuestaservidor = connection.getResponseCode();
			errores = "Errores Servidor:" + connection.getResponseMessage();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (Exception ex) {
			Log.e("error entro aca", "si");
		}

		return respuestaservidor;

	}
	public void mensajeSegunRespuestaServidor(int resp) {
		String mensaje = "";
		if (resp == -1) {
			mensaje = errores;
		} else {
			mensaje = "Se enviaron satisfactoriamente todos los datos al servidor";
		}
		// mensaje=errores;
		// Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_SHORT).show();

		AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Respuesta del Servidor");
		alertDialog.setMessage(mensaje);
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				if (seborroarchivo == true) {
					//CargarLista();
				}
			}
		});
		alertDialog.show();

	}
}