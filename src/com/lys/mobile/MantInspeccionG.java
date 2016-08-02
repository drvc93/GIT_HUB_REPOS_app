package com.lys.mobile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.lys.mobile.asynctask.EnviarReporteInspeccionGAsyncTask;
import com.lys.mobile.data.*;
import com.lys.mobile.util.*;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class MantInspeccionG extends Activity {
	String syncro;
	public static int count = 0, posicion = 0;
	public String comentariocab = "", condiciones = "NULL", 
			      filepath = "NULL", periodos = "NULL", cb = "";
	//private static final String BS_PACKAGE = "com.google.zxing.client.android";
	public static final int REQUEST_CODE = 0x0000c0de;
	static String contents = "",mensaje = "";
	boolean enviadoServCorrecto = false, mResult = false;
	LinearLayout tabCondicionInspeccion,tabDetalle;
	TextView lblFechaInsp,lblInspector,
			 lblNombreInspector;
	Button btnAreaProb;
	Spinner cboTipoInsp,cboCCosto,cboMaquina;
	ListView listaInspecciones;
	
	TextView lblTab01_TituloMaquina;
	
	String GrabaInspeccion = "V",codCompania = "",codInspeccion = "0",comInspeccion = "";
	Button btnAgregar,btnSiguiente;	
	Context contexto;
	String c_cabecera = "0", c_detalle = "1", cabec = "NULL";
	MyApp app;
	
	List<String> listacondn;
	private static final int REQUEST_CAMERA = 1;
	public ArrayList<DetalleInspeccionRealizadasGData> lista;
	
	Calendar ca = Calendar.getInstance();
	public String fechainicio, fechafin;
	SQLiteDatabase _db;
	String sql = "";
	Cursor cx;
	public String familia;
	public boolean pasar;
	public boolean tomo = false;
	boolean seborroarchivo = false;
	int respuestafinal = 0;
	
	//Insp. Gen.
	public String fechamod;
	List<String> listatipos;
	ArrayAdapter<String> dataAdapterConn;
	String tipoinspeccion="NULL",comentario="",maquina="NULL",costo="NULL";
	EditText txtMaquina, txtAreaProb;
	String errores = "",cmaq = "";
	ListView IGListView;	
	private InspeccionGAdp dataAdapterInsp;
	ArrayList<CentroCostoData> listacent = new ArrayList<CentroCostoData>();
	ArrayList<MaquinasData> listamaq = new ArrayList<MaquinasData>();
	public String tiporev;
	public String filename = "NULL";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mantinspecciong);
		lista = new ArrayList<DetalleInspeccionRealizadasGData>();
		app = ((MyApp)getApplicationContext());
		syncro = getIntent().getExtras().getString("Sincro");
		codCompania = "00100000";
		codInspeccion = getIntent().getExtras().getString("codInspeccionG");
		GrabaInspeccion = getIntent().getExtras().getString("GrabaInspeccionG");
		
		EnlazarControles();
		contexto = this;
		txtMaquina.setEnabled(false);
		cboCCosto.setEnabled(false);
		
		setupListViewAdapter();
		
		setupAddLineaButton();
		
		/*****Maquinas******/
		if (listamaq.size() > 0)
			listamaq.clear();
		MaquinasData m00 = new MaquinasData();
		m00.c_compania = "0";
		m00.c_maquina = "0";
		m00.c_descripcion = "0";
		m00.c_codigobarras = "0";
		m00.c_familiainspeccion = "0";
		m00.c_centrocosto = "0";
		m00.c_estado = "0";

		listamaq.add(m00);
		
		DataBase basededatos2 = new DataBase(MantInspeccionG.this, "DBInspeccion", null, 1);
		SQLiteDatabase db2 = basededatos2.getWritableDatabase();
		
		String queryc2 = "SELECT c_compania,c_maquina,c_descripcion,c_codigobarras,c_familiainspeccion,c_centrocosto,c_estado from MTP_MAQUINAS  ";
		Cursor cc2 = db2.rawQuery(queryc2, null);
		if (cc2.moveToFirst()) {

			do {
				
				MaquinasData mm2 = new MaquinasData();
				mm2.c_compania = cc2.getString(0);
				mm2.c_maquina = cc2.getString(1);
				mm2.c_descripcion = cc2.getString(2);
				mm2.c_codigobarras = cc2.getString(3);
				mm2.c_familiainspeccion = cc2.getString(4);
				mm2.c_centrocosto = cc2.getString(5);
				mm2.c_estado = cc2.getString(6);
				
				listamaq.add(mm2);

			} while (cc2.moveToNext());

		} else {
			//listView.setAdapter(null);
			Toast.makeText(MantInspeccionG.this, "Maquinas no disponibles.", Toast.LENGTH_SHORT)
					.show();
		}
		cc2.close();
		db2.close();
		
		MaquinaItemAdapter dataAdapterMa = new MaquinaItemAdapter(MantInspeccionG.this, android.R.layout.simple_spinner_item, listamaq);

		dataAdapterMa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		cboMaquina.setAdapter(dataAdapterMa);
		cboMaquina.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				// cb= listama.get(position).c_maquina;
				cmaq = listamaq.get(position).c_maquina;
				if (cmaq.equals("0"))
					cmaq = "";
				txtMaquina.setText(cmaq);
				Log.d("codigo cmaq", cmaq);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		/***Fin Maquinas****/
		
		if (listacent.size() > 0)
			listacent.clear();
		CentroCostoData m0 = new CentroCostoData();
		m0.c_compania = "0";
		m0.c_centrocosto = "0";
		m0.c_descripcion = "0";
		m0.c_estado = "0";
		listacent.add(m0);

		DataBase basededatos1 = new DataBase(this, "DBInspeccion", null, 1);
		SQLiteDatabase db1 = basededatos1.getWritableDatabase();

		String queryc = "SELECT c_compania,c_centrocosto,c_descripcion,c_estado from MTP_CENTROCOSTO  ";
		Cursor cc = db1.rawQuery(queryc, null);
		if (cc.moveToFirst()) {

			do {

				CentroCostoData mm = new CentroCostoData();
				mm.c_compania = cc.getString(0);
				mm.c_centrocosto = cc.getString(1);
				mm.c_descripcion = cc.getString(2);
				mm.c_estado = cc.getString(3);

				// tmaquina.setText(cbarra);
				// tcentro.setText(centro);
				listacent.add(mm);

			} while (cc.moveToNext());

		} else {
			//listView.setAdapter(null);
			Toast.makeText(this, "Centros no disponible", Toast.LENGTH_SHORT)
					.show();
		}
		cc.close();
		db1.close();

		CentroItemAdapter dataAdapterCe = new CentroItemAdapter(this, android.R.layout.simple_spinner_item, listacent);

		dataAdapterCe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		cboCCosto.setAdapter(dataAdapterCe);
		// tcentro.setText(centr);

		cboCCosto.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				// cb= listama.get(position).c_maquina;
				costo = listacent.get(position).c_centrocosto;
				if (costo.equals("0"))
					costo = "NULL";

				Log.d("codigo centr", costo);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		/**Fin CCosto**/
		listatipos = new ArrayList<String>();
		listatipos.add("--Seleccione--");
		listatipos.add("OTRO");
		listatipos.add("MAQUINA");
		
		dataAdapterConn = new ArrayAdapter<String>(MantInspeccionG.this, android.R.layout.simple_spinner_item, listatipos);
		
		dataAdapterConn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		cboTipoInsp.setAdapter(dataAdapterConn);
		cboTipoInsp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position > 0) {

					if (parent.getItemAtPosition(position).toString()
							.equals("OTRO")) {
						lblTab01_TituloMaquina.setText("PROBLEMA DETECTADO");
						//tlblmaquina.setVisibility(View.INVISIBLE);
						txtMaquina.setEnabled(true);
						txtAreaProb.setEnabled(true);
						//bcodbar.setVisibility(View.INVISIBLE);
						cboMaquina.setEnabled(false);
						tipoinspeccion = "OT";
						txtMaquina.setText("");
						cboCCosto.setEnabled(true);
						cboCCosto.setSelection(0);
						dataAdapterInsp.clear();
						//setupListaDefault();
						//IGListView.setAdapter(dataAdapterInsp);
						Log.d("TIPO INSP.", tipoinspeccion);
						
					} else if (parent.getItemAtPosition(position).toString()
							.equals("MAQUINA")) {
						lblTab01_TituloMaquina.setText("MAQUINA");
						//tlblmaquina.setVisibility(View.VISIBLE);
						txtMaquina.setEnabled(false);
						txtAreaProb.setEnabled(true);
						//bcodbar.setVisibility(View.VISIBLE);
						cboMaquina.setEnabled(true);


						tipoinspeccion = "MQ";
						txtMaquina.setText("");
						cboCCosto.setEnabled(false);
						cboCCosto.setSelection(0);
						dataAdapterInsp.clear();
						//setupListaDefault();
						//IGListView.setAdapter(dataAdapterInsp);
						Log.d("TIPO INSP.", tipoinspeccion);
					} else if (parent.getItemAtPosition(position).toString()
							.equals("--SELECCIONE--")) {
						lblTab01_TituloMaquina.setText("ASUNTO");
						//tlblmaquina.setVisibility(View.INVISIBLE);
						txtMaquina.setEnabled(false);
						txtAreaProb.setEnabled(false);
						cboMaquina.setEnabled(false);
						//bcodbar.setVisibility(View.INVISIBLE);
						tipoinspeccion = "NULL";
						txtMaquina.setText("");
						cboCCosto.setSelection(0);
						dataAdapterInsp.clear();
						//setupListaDefault();
						//IGListView.setAdapter(dataAdapterInsp);
						cboCCosto.setEnabled(false);
					} 
					
				} else {
					lblTab01_TituloMaquina.setText("PROBLEMA DETECTADO");
					//tlblmaquina.setVisibility(View.INVISIBLE);
					txtMaquina.setEnabled(false);
					txtAreaProb.setEnabled(false);
					cboMaquina.setEnabled(false);
					//bcodbar.setVisibility(View.INVISIBLE);
					tipoinspeccion = "NULL";
					txtMaquina.setText("");
					cboCCosto.setEnabled(false);
					cboCCosto.setSelection(0);
					dataAdapterInsp.clear();
					//setupListaDefault();
					//IGListView.setAdapter(dataAdapterInsp);
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	public class MaquinaItemAdapter extends ArrayAdapter<MaquinasData> {
		private ArrayList<MaquinasData> listcontact;

		public MaquinaItemAdapter(Context context, int textViewResourceId, ArrayList<MaquinasData> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}

		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}

		public View getCustomView(final int position, View convertView, final ViewGroup parent) {

			View v = convertView;

			final MaquinasData user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				v = vi.inflate(R.layout.con_maquina_lista, parent, false);

			}

			if (user != null) {

				TextView codi = (TextView) v.findViewById(R.id.tcodigo);
				TextView des = (TextView) v.findViewById(R.id.tdes);

				if (user.c_maquina.equals("0")) {

					if (codi != null) {
						codi.setText("--SELECCIONE--");
					}

					des.setVisibility(View.GONE);

				} else {

					des.setVisibility(View.VISIBLE);

					if (codi != null) {
						codi.setText("" + user.c_maquina);
					}

					if (des != null) {
						des.setText("" + user.c_descripcion);
					}

				}

			}

			return v;

		}

	}
	
	public void EventoTomarFoto(View v) {
		int pos = IGListView.getPositionForView(v);
		if (pos > -1) {
			posicion = pos;
			String state = Environment.getExternalStorageState();
			Log.e("state",state);
			//Log.e("filename 1",filename);
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				if (tipoinspeccion.equals("NULL")) {
					Toast.makeText(MantInspeccionG.this,"Seleccionar Tipo Inspección.",Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, REQUEST_CAMERA);
				}
			}
			/*String x=filename;
			Log.e("filename 2",filename);
			dataAdapterInsp.setFotoText(pos,x);*/
		}		
	}
	
	public void GuardarReporte() {
		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		DataBase basededatos = new DataBase(this, "DBInspeccion", null, 1);
		SQLiteDatabase db = basededatos.getWritableDatabase();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MantInspeccionG.this);
		String us = preferences.getString("user", "");
		
		/*Calendar ca = Calendar.getInstance();
		fechamod = df.format(ca.getTime());*/
		int vacio = 0;
		
		String query = "SELECT n_correlativo from MTP_INSPECCIONGENERAL_CAB order by cast(n_correlativo AS int) desc limit 1 ";
		Cursor c = db.rawQuery(query, null);
		if (c.moveToFirst()) {
			
			c_cabecera = c.getString(0);
			c_cabecera = String.valueOf(Integer.parseInt(c_cabecera) + 1);
			
		} else {
			
			c_cabecera = "1";
			
		}
		c.close();
		
		if (tipoinspeccion.equals("NULL")){
			Toast.makeText(MantInspeccionG.this, "Seleccione Tipo de Inspección.", Toast.LENGTH_SHORT).show();
			c_cabecera = "0";
			vacio = 1;
		}
		
		/*if (tipoinspeccion.equals("OT")&&costo.equals("NULL")){
			Toast.makeText(Llenar2.this, "Seleccione Centro de Costo", Toast.LENGTH_SHORT).show();
			c_cabecera = "0";
			vacio = 1;
		}*/
		
		if (txtAreaProb.getText().toString().compareTo("")==0) {
			Toast.makeText(MantInspeccionG.this, "Ingrese area específica del problema", Toast.LENGTH_SHORT).show();
			c_cabecera = "0";
			vacio = 1;
		}
		
		if(tipoinspeccion.equals("OT")&&txtMaquina.getText().toString().compareTo("")==0) {
			Toast.makeText(MantInspeccionG.this, "Ingrese Problema Detectado", Toast.LENGTH_SHORT).show();
			c_cabecera = "0";
			vacio = 1;
		}
		
		if(tipoinspeccion.equals("MQ")&&txtMaquina.getText().toString().compareTo("")==0) {
			Toast.makeText(MantInspeccionG.this, "Seleccione Maquina", Toast.LENGTH_SHORT).show();
			c_cabecera = "0";
			vacio = 1;
		}
		
		for (int i = 0; i < dataAdapterInsp.getCount(); i++) {
			if (dataAdapterInsp.getItem(i).getComentario().equals("")) {
				vacio = 1;
				Toast.makeText(MantInspeccionG.this,"Llenar comentario. Linea # " + String.valueOf(i+1),Toast.LENGTH_SHORT).show();
				c_cabecera = "0";
				break;
			}
		}
		
		if (vacio == 0) {
			for (int i = 0; i < dataAdapterInsp.getCount(); i++) {
				vacio = 2;
				ContentValues con = new ContentValues();
				con.put("c_compania", Util.compania);
				con.put("n_correlativo", c_cabecera);
				con.put("n_linea", dataAdapterInsp.getItemId(i) + 1);
				con.put("c_comentario", dataAdapterInsp.getItem(i).getComentario());
				con.put("c_rutafoto", dataAdapterInsp.getItem(i).getRutaFoto());
				con.put("c_ultimousuario", us);
				con.put("d_ultimafechamodificacion", fechamod);
				con.put("c_tiporevisiong", dataAdapterInsp.getItem(i).getTipoRevisionG());
				con.put("c_flagadictipo", dataAdapterInsp.getItem(i).getFlagAdicTipo());
				
				db.insert("MTP_INSPECCIONGENERAL_DET", null, con);
				
				Log.d("vuelta ctualzo", String.valueOf(dataAdapterInsp.getItemId(i) + 1));
			}
		}
		
		if (vacio == 2) {
			vacio = 3;
			ContentValues conc = new ContentValues();
			conc.put("c_compania", Util.compania);
			conc.put("n_correlativo", c_cabecera);
			conc.put("c_tipoinspeccion", tipoinspeccion);
			conc.put("c_maquina", txtMaquina.getText().toString());
			conc.put("c_centrocosto", costo);
			conc.put("c_comentario", txtAreaProb.getText().toString());
			conc.put("c_usuarioinspeccion", us);
			conc.put("d_fechainspeccion", fechamod);
			conc.put("c_estado", "I");
			conc.put("c_usuarioenvio", us);
			conc.put("d_fechaenvio", fechamod);
			conc.put("c_ultimousuario", us);
			conc.put("d_ultimafechamodificacion", fechamod);
			
			db.insert("MTP_INSPECCIONGENERAL_CAB", null, conc);
			
			/*lblInspector.setText(us);
			lblFechaInsp.setText(fechamod);*/
			
			Log.d("cabecera maquina: ", txtMaquina.getText().toString());
			Log.d("cabecera comentario: ", txtAreaProb.getText().toString());
			
		}
		
		db.close();
		
		if (vacio == 3) {
			new AlertDialog.Builder(MantInspeccionG.this)
			.setTitle("LysMobile")
			.setMessage(
					"	Se guardo el reporte localmente, desea enviar el reporte en este momento?")
			.setPositiveButton(android.R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							EnviarReporte(c_cabecera);							
						}
					})
			.setNegativeButton(android.R.string.no,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {							
							finish();
						}
					}).setIcon(android.R.drawable.ic_dialog_alert)
			.show();
		}
	}
	
	public void InicioSincronizacion() {
		if (dataAdapterInsp.getCount() == 0) {
			Toast.makeText(MantInspeccionG.this, "Lista de detalles vacia!", Toast.LENGTH_SHORT).show();
		}

		else  if (syncro.equals("NO")){
			Toast.makeText(MantInspeccionG.this, "No tiene permisos para sincronizar", Toast.LENGTH_SHORT).show();

		}
		else {
			if (c_cabecera.equals("0")) {
				Log.e("GuardarReporte: ",c_cabecera);
				GuardarReporte();
			} /*else {
				Log.e("ModificarReporte: ",c_cabecera);
				ModificarReporte();
				
			}*/
		}
	}
	
	public void EnviarReporte(String correlativo) {
		int resp = 0; // Sin datos enviados
		String imagen;
		boolean existe = false;
		pasar = true;
		Log.e("Online",String.valueOf(isOnline()));
		if (isOnline()) {
			Log.e("entro aqui online","1");
						
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MantInspeccionG.this);
			String us = preferences.getString("user", "");
			ArrayList<InspeccionGData> lista1 = new ArrayList<InspeccionGData>();
			/***INICIO***/
			for (int i = 0; i < dataAdapterInsp.getCount(); i++) {
				InspeccionGData in = new InspeccionGData(Util.compania,c_cabecera,
						String.valueOf(dataAdapterInsp.getItemId(i) + 1),
						dataAdapterInsp.getItem(i).getComentario(),
						dataAdapterInsp.getItem(i).getRutaFoto(),us,String.valueOf(fechamod),
						dataAdapterInsp.getItem(i).getTipoRevisionG(),
						dataAdapterInsp.getItem(i).getFlagAdicTipo());
						
						lista1.add(in);
			}
			/****FIN****/
			cabec = correlativo;
			final EnviarReporteInspeccionGAsyncTask enviarinspeccion = new EnviarReporteInspeccionGAsyncTask(
					MantInspeccionG.this, correlativo, cabec, Util.compania,
					tipoinspeccion, txtMaquina.getText().toString(),costo, txtAreaProb.getText().toString(), 
					us, fechamod, "E", us, fechamod, us, fechamod, lista1);
				
				enviarinspeccion.execute(new String[] { Util.url + "registrarInspeccionG" });			
			
				Thread thread = new Thread() {
					public void run() {
						try {
							enviarinspeccion.get(50000, TimeUnit.MILLISECONDS);
							// tarea.get(30000, TimeUnit.MILLISECONDS);
							//Log.d("entro Cabecera run try", "entro Cabecera run try");

						} catch (Exception e) {
							enviarinspeccion.cancel(true);
							((Activity) MantInspeccionG.this).runOnUiThread(new Runnable() {

								public void run() {
									enviarinspeccion.progressDialog.dismiss();
									pasar = false;
									Toast.makeText(MantInspeccionG.this, "No se pudo establecer comunicacion .",
											Toast.LENGTH_LONG).show();
									//Log.d("entro Cabecera run catch", "entro Cabecera run catch");
								}
							});
						}
					}
				};
				thread.start();
		} else {
			pasar = false;
			Toast.makeText(MantInspeccionG.this, "No hay acceso a internet.",
					Toast.LENGTH_LONG).show();
		}
		
		if (pasar == true) {
			for (int i = 0; i < dataAdapterInsp.getCount(); i++) {
				Log.e("entro aqui online for 2A",String.valueOf(dataAdapterInsp.getCount()));
				//fotoslistain.add(lista.get(i).c_rutafoto);
				//!lista.get(i).c_rutafoto.equals("") && 
				//if (dataAdapterInsp.getItem(i).getRutaFoto() != null || dataAdapterInsp.getItem(i).getRutaFoto().length() > 0) {
				if (dataAdapterInsp.getItem(i).getRutaFoto().length() > 0) {
					imagen = Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
							"/" + dataAdapterInsp.getItem(i).getRutaFoto();
					existe = true;
					break;
				}
			}
			Log.e("entro aqui online for 2B",String.valueOf(existe));
			if (existe == true)
				new AsyncSender().execute();
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
		return;
	}
	
	public class CentroItemAdapter extends ArrayAdapter<CentroCostoData> {
		private ArrayList<CentroCostoData> listcontact;

		public CentroItemAdapter(Context context, int textViewResourceId, ArrayList<CentroCostoData> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}

		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}

		public View getCustomView(final int position, View convertView, final ViewGroup parent) {

			View v = convertView;

			final CentroCostoData user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				v = vi.inflate(R.layout.con_maquina_lista, parent, false);

			}

			if (user != null) {

				TextView codi = (TextView) v.findViewById(R.id.tcodigo);
				TextView des = (TextView) v.findViewById(R.id.tdes);

				if (user.c_centrocosto.equals("0")) {

					if (codi != null) {
						codi.setText("--SELECCIONE--");
					}

					des.setVisibility(View.GONE);

				} else {

					des.setVisibility(View.VISIBLE);

					if (codi != null) {
						codi.setText("" + user.c_centrocosto);
					}

					if (des != null) {
						des.setText("" + user.c_descripcion);
					}

				}

			}

			return v;

		}

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
		for (int i = 0; i < dataAdapterInsp.getCount(); i++) {
			Log.e("entro aqui online for 2",String.valueOf(dataAdapterInsp.getCount()));
			//fotoslistain.add(lista.get(i).c_rutafoto);
			//if (!lista.get(i).c_rutafoto.equals("") && (lista.get(i).c_rutafoto != null)) {
			//if (dataAdapterInsp.getItem(i).getRutaFoto() != null || dataAdapterInsp.getItem(i).getRutaFoto().length() > 0) {
			if (dataAdapterInsp.getItem(i).getRutaFoto().length() > 0) {
				imagen = Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
						"/" + dataAdapterInsp.getItem(i).getRutaFoto();
				
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
	
	public void BorrarFile(String files) {
		File f = new File(files);
		f.delete();
		f = null;
		//seborroarchivo = true;
	}
	
	@SuppressWarnings("deprecation")
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				contents = data.getStringExtra("SCAN_RESULT");
				String cbarra = "",cmaquina = "",ccost = "";
				int posCCosto = 0;

				DataBase basededatos = new DataBase(this, "DBInspeccion", null,1);
				SQLiteDatabase db = basededatos.getWritableDatabase();

				String query = "SELECT c_maquina,c_centrocosto from MTP_MAQUINAS where c_codigobarras='" + contents + "' ";
				Cursor c = db.rawQuery(query, null);
				if (c.moveToFirst()) {

					String cma = c.getString(0);
					String cco = c.getString(1);
					cmaquina = cma;
					ccost = cco;

				} else {

					Toast.makeText(this,
							"Maquina no disponible para ese codigo",
							Toast.LENGTH_SHORT).show();
				}
				c.close();
				db.close();

				txtMaquina.setText(cmaquina);
				//posCCosto = getPosicionCCosto(ccost);
				//ccosto.setSelection(posCCosto);
				//Toast.makeText(this, "cmaquina//posCCosto" + cmaquina + "//" + String.valueOf(posCCosto), Toast.LENGTH_SHORT).show();
			}
		}
		
		if (requestCode == REQUEST_CAMERA){
			if (resultCode == Activity.RESULT_OK) {
				Bitmap x = (Bitmap) data.getExtras().get("data");
				
				ActualizarFotoTomada(x);
				//dataAdapterInsp.setFotoText(posicion,filename);
				if (filename.equals("NULL")) {
					dataAdapterInsp.setFotoText(posicion,"");
				} else {
					dataAdapterInsp.setFotoText(posicion,filename);
				}
			} else {
				filename = "NULL";
				//dataAdapterInsp.setFotoText(posicion,"");
			}
		}
		
		return;
	}
	
	public void EventoUltimaFoto(String photoPath) {
		if (!photoPath.equals("") && (photoPath != null)) {
			String nombreimg = photoPath;
			VerImagen dialogo = new VerImagen(this, nombreimg, null);
			dialogo.show();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void ActualizarFotoTomada(Bitmap x) {
		Bitmap y;
		String thumbnailPath = "", largeImagePath = "", sort = "", nuevocodigo = "";
        File fileNew, fileGaleria, fileThumbail;
        
        //ELIMINAR ULTIMA FOTO DE TARJETA DCIM(NO LLENAR MEMORIA)
        String[] largeFileProjection = {
                MediaStore.Images.ImageColumns._ID,
              MediaStore.Images.ImageColumns.DATA,
              MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
              MediaStore.Images.ImageColumns.DATE_TAKEN,
              MediaStore.Images.ImageColumns.MIME_TYPE};
        sort = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
        Cursor myCursor = null;
        try {
        	myCursor = getContentResolver()
        			.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, largeFileProjection, null, 
                            null, sort);
             if (myCursor.moveToFirst()) {
                 String ultimafotx = myCursor.getString(1);
                 File imageFile = new File(ultimafotx);
               if (imageFile.exists()) {
                 imageFile.delete();
               }
             }
        }catch (Exception e){
			Log.e("mi amagen 2","mi imagen 2");
			e.printStackTrace();
		} finally {
			myCursor.close();
			Log.e("mi amagen 3","mi imagen 3");
		}
                
        Log.e("Paso aki 1","Antes de generar codigo de imagen");
        //GENERA NUEVA IMAGEN EN CARPETA DE RECLAMO GARANTIA
        try {

              nuevocodigo = getGenerarCodigoImagen();
              Log.e("Paso aki 2","Despues de generar codigo de imagen");
              fileNew = new File(nuevocodigo);
              Uri outputFileUri = Uri.fromFile(fileNew);
              OutputStream outstream = getContentResolver().openOutputStream(outputFileUri);
              y = redimensionarImagenMaximo2(x,450);
              //x.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
              y.compress(Bitmap.CompressFormat.JPEG, 100, outstream); 
              outstream.close();
              outstream = null;
              fileNew = null;
              outputFileUri = null;
        } catch (FileNotFoundException e) {
              e.printStackTrace();
        } catch (IOException e) {
              e.printStackTrace();
        }
        
        EventoUltimaFoto(nuevocodigo);
	}
	
	private String getGenerarCodigoImagen() {
		//String codigo = lblCodigo.getText().toString().toLowerCase();
		String nombre = cb;
		int max = 0;
		String valnum = "0";
		String codmaquina = cb;//app.getCodigoVendedor();//Cdigo Muina
		String fecha = getFechaImagen();
		String iniciacon = codmaquina.toLowerCase();
		String terminacon = ".jpg";

		String rutacarpeta = Environment.getExternalStorageDirectory()
				+ app.getRutaInspeccion(); // + codigo;
		File f = new File(rutacarpeta);
		Log.e("rutacarpeta",rutacarpeta);
		if (f.list() == null) {
			f.mkdirs();
		}
		
		Log.e("posicion",String.valueOf(posicion));
		
		//nombre = nombre + String.valueOf(max + 1);
		//nombre = codmaquina.toLowerCase() + "_" + nombre + "_" + fecha + ".jpg";
		nombre = nombre + String.valueOf(posicion);
		nombre = nombre + fecha + ".jpg";
		filepath = nombre;
		Log.e("nombre inicial",String.valueOf(nombre));
		nombre = Environment.getExternalStorageDirectory()
				+ app.getRutaInspeccion() + "/" + nombre;
				//+ lblCodigo.getText().toString().toLowerCase() + "/" + nombre;
		Log.e("nombre final",String.valueOf(nombre));
		return nombre;
	}
	
	public String getStringFecha(Date date) {
		String fecha = "";
		if (date != null) {
			java.text.SimpleDateFormat dfm = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
			//java.text.SimpleDateFormat dfm = new java.text.SimpleDateFormat("ddMMyyyyHHmmss");
			fecha = dfm.format(date);
		}
		return fecha;
	}
	
	public String getFechaImagen() {
		Date date = new Date();
		String fecha = "";
		if (date != null) {
			//fecha = String.valueOf(date.getTime());
			java.text.SimpleDateFormat dfm = new java.text.SimpleDateFormat("ddMMyyyyHHmmss");
			fecha = dfm.format(date);
		}
		return fecha;
	}
	
	public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth){
		   //Redimensionamos
		   int width = mBitmap.getWidth();
		   int height = mBitmap.getHeight();
		   float scaleWidth = ((float) newWidth) / width;
		   float scaleHeight = ((float) newHeigth) / height;
		   // create a matrix for the manipulation
		   Matrix matrix = new Matrix();
		   // resize the bit map
		   matrix.postScale(scaleWidth, scaleHeight);
		   // recreate the new Bitmap
		   return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
		}
		/**
		 *Redimensionar imagen 2
		 */
	private Bitmap redimensionarImagenMaximo2(Bitmap bm, int newWidth) {
			
	        int width = bm.getWidth();
	        int height = bm.getHeight();
	        float aspect = (float)width / height;
	        float scaleWidth = newWidth;
	        float scaleHeight = scaleWidth / aspect;
	        // create a matrix for the manipulation
	        Matrix matrix = new Matrix();
	        // resize the bit map
	        matrix.postScale(scaleWidth / width, scaleHeight / height);
	        // recreate the new Bitmap
	        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	        bm.recycle();
	        
	        return resizedBitmap;
	    }
		
	public void removeLineaOnClickHandler(View v) {
		InspeccionGData itemToRemove = (InspeccionGData)v.getTag();
		int posi = IGListView.getPositionForView(v);
		String ruta = Environment.getExternalStorageDirectory()
		+ app.getRutaInspeccion() + "/" + dataAdapterInsp.getItem(posi).getRutaFoto();
		BorrarFile(ruta);
		dataAdapterInsp.remove(itemToRemove);
	}
	
	private void setupListViewAdapter() {
		dataAdapterInsp = new InspeccionGAdp(MantInspeccionG.this, R.layout.det_inspecciongeneral_det, new ArrayList<InspeccionGData>());
		//IGListView = (ListView)findViewById(R.id.listainspecciong);
		IGListView.setAdapter(dataAdapterInsp);
	}
	
	private void setupAddLineaButton() {
		//setupListaDefault();
		findViewById(R.id.btnAgregarIG).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog alertDialog = new Dialog(MantInspeccionG.this);
				
				final ArrayList<TipoRevisionData> listaTiposRev = new ArrayList<TipoRevisionData>();
				
				alertDialog.setContentView(R.layout.dialogo_tiporevisiong);
				alertDialog.setTitle("Tipo Revision");
				alertDialog.setCancelable(false);
				final Spinner stiporev = (Spinner) alertDialog.findViewById(R.id.stiporevision);
				
				if (listaTiposRev.size() > 0)
					listaTiposRev.clear();
				
				DataBase basededatos = new DataBase(MantInspeccionG.this, "DBInspeccion", null, 1);
				SQLiteDatabase db = basededatos.getWritableDatabase();

				String queryc = "SELECT c_tiporevisiong,c_descripcion,c_estado from MTP_TIPOREVISIONG ";
				Cursor cc = db.rawQuery(queryc, null);
				if (cc.moveToFirst()) {

					do {

						TipoRevisionData tr2 = new TipoRevisionData();
						tr2.setTipoRevisionG(cc.getString(0));
						tr2.setDescripcion(cc.getString(1));
						
						listaTiposRev.add(tr2);

					} while (cc.moveToNext());

				} else {
					//listView.setAdapter(null);
					Toast.makeText(MantInspeccionG.this, "Tipos Revision no disponible.", Toast.LENGTH_SHORT)
							.show();
				}
				cc.close();
				db.close();
				
				TipoRevItemAdapter dataAdapterTr = new TipoRevItemAdapter(MantInspeccionG.this, android.R.layout.simple_spinner_item, listaTiposRev);

				dataAdapterTr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				stiporev.setAdapter(dataAdapterTr);
				
				//stiporev.setSelection(0);
				
				stiporev.setOnItemSelectedListener(new OnItemSelectedListener() {
					
					@Override
				    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				        TipoRevisionData tr;
						tr = (TipoRevisionData) stiporev.getItemAtPosition(position);
						tiporev = tr.getTipoRevisionG();
						Log.d("tiporev",tiporev);
				    }

				    public void onNothingSelected(AdapterView<?> parent) {}
				});
				
				Button dialogAceptar = (Button) alertDialog.findViewById(R.id.baceptar);
				
				dialogAceptar.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						
						String flagadictipo = "N";
						
						/***Recorrer detalle de la inspeccin general***/
						String tiporev2 = "";
						int cont=0;
						for (int i = 0; i < dataAdapterInsp.getCount(); i++) {
							tiporev2 = dataAdapterInsp.getItem(i).getTipoRevisionG();
							if (tiporev2.compareTo(tiporev)==0) {
								cont++;
							}
						}
						/***********************************************/
						
						DataBase basededatos = new DataBase(MantInspeccionG.this,"DBInspeccion", null, 1);
						SQLiteDatabase db = basededatos.getWritableDatabase();
						String queryc = "SELECT c_flagadictipo from MTP_TIPOREVISIONG_PARAMETRO where c_compania='"
								+ Util.compania + "' and c_tiporevisiong = '" + tiporev + "'";
						Cursor cc = db.rawQuery(queryc, null);
						if (cc.moveToFirst()) {
							
							flagadictipo = cc.getString(0);
						}
						db.close();
						cc.close();
						if (flagadictipo.equals("N")&&cont>0) {
							Toast.makeText(MantInspeccionG.this, "No se permite agregar mas del Tipo Revision seleccionado.",
									Toast.LENGTH_SHORT).show();
						}else {
							dataAdapterInsp.insert(new InspeccionGData("", "", "", "", "", "", "", tiporev, flagadictipo), 0);
							alertDialog.cancel();
						}

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
	}
	
	private void setupListaDefault() {
		DataBase basededatos = new DataBase(MantInspeccionG.this, "DBInspeccion", null, 1);
		SQLiteDatabase db = basededatos.getWritableDatabase();
		
		String queryp = "Select c_tiporevisiong,c_flagadictipo from MTP_TIPOREVISIONG_PARAMETRO where c_compania = '" + Util.compania + "' " + 
						"order by c_tiporevisiong desc";
		
		Log.d("MTP_TIPOREVISIONG_PARAMETRO", queryp);
		Cursor cu = db.rawQuery(queryp, null);
		if (cu.moveToFirst()) {
			do {
				dataAdapterInsp.insert(new InspeccionGData("", "", "", "", "", "", "", cu.getString(0), cu.getString(1)),0);
				
			} while(cu.moveToNext());
		}
		cu.close();
		db.close();
		IGListView.setAdapter(dataAdapterInsp);
	}
			
	public int getPosicionCCosto(String ccost){
		int pos = -1;
		CentroCostoData cc;
		for (int i = 0; i < cboCCosto.getCount(); i++) {
			cc = (CentroCostoData) cboCCosto.getItemAtPosition(i);
			if (cc.c_centrocosto == ccost) {
				cc = null;
				return i;
			}
		}
		cc = null;
		return pos;
	}
		
	public class TipoRevItemAdapter extends ArrayAdapter<TipoRevisionData> {
		private ArrayList<TipoRevisionData> listcontact;
		
		public TipoRevItemAdapter(Context context, int textViewResourceId, ArrayList<TipoRevisionData> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}
		
		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}
		
		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}
		
		public View getCustomView(final int position, View convertView, final ViewGroup parent) {

			View v = convertView;

			final TipoRevisionData user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				v = vi.inflate(R.layout.con_tiporev_lista, parent, false);

			}

			if (user != null) {

				TextView codi = (TextView) v.findViewById(R.id.tcodigo);
				TextView des = (TextView) v.findViewById(R.id.tdes);

				if (user.getTipoRevisionG().equals("00")) {

					if (codi != null) {
						codi.setText("-Seleccione-");
					}

					des.setVisibility(View.GONE);

				} else {

					des.setVisibility(View.VISIBLE);

					if (codi != null) {
						codi.setText("" + user.getTipoRevisionG());
					}

					if (des != null) {
						des.setText("" + user.getDescripcion());
					}

				}

			}

			return v;

		}
		
	}
	
	@Override
	protected void onResume() {
	    // TODO Auto-generated method stub
	    super.onResume();
	}

	@Override
	protected void onPause() {
	    // TODO Auto-generated method stub
	    super.onPause();
	}
	
	private void EnlazarControles() {
		tabCondicionInspeccion = (LinearLayout)findViewById(R.id.tabInspeccionT01);
		tabDetalle = (LinearLayout)findViewById(R.id.tabInspeccionT02);
		
		// Datos del Tab 01: Condiciones de Inspeccin G.
		lblTab01_TituloMaquina = (TextView)findViewById(R.id.lblTab01TituloMaquina);
		txtMaquina = (EditText)findViewById(R.id.txtMaquina);
		cboMaquina = (Spinner)findViewById(R.id.cboMaquina);
		cboTipoInsp = (Spinner)findViewById(R.id.cboTipoInsp);
		cboCCosto = (Spinner)findViewById(R.id.cboCCosto);
		lblFechaInsp = (TextView)findViewById(R.id.lblFechaInsp);
		txtAreaProb = (EditText)findViewById(R.id.txtAreaProb);
		lblInspector = (TextView)findViewById(R.id.lblInspectorg);
		lblNombreInspector = (TextView)findViewById(R.id.lblNombreInspectorg);
				
		// Datos del Tab 02: Detalle
		//listaInspecciones = (ListView)findViewById(R.id.listainspecciong);
		IGListView = (ListView)findViewById(R.id.listainspecciongg);
		btnAgregar = (Button)findViewById(R.id.btnAgregarIG);
		
		//Pie
		btnSiguiente = (Button)findViewById(R.id.btnSiguiente);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MantInspeccionG.this);
		String us = preferences.getString("user", "");
		
		Calendar ca = Calendar.getInstance();
		fechamod = df.format(ca.getTime());
		lblInspector.setText(us);
		lblFechaInsp.setText(fechamod);
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
	}
	
	private int obtenerPantalla() {
		int valor = 0;
		if (tabCondicionInspeccion.getVisibility() == View.VISIBLE)
			valor = 0;
		else if (tabDetalle.getVisibility() == View.VISIBLE)
			valor = 1;
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
				Log.e("val", "true");
				if (GrabaInspeccion.equals("V")) {
					btnSiguiente.setText("Fin");
				} else {
					btnSiguiente.setText("Sincronizar");
				}
			} else {
				Log.e("val", "false");
				btnSiguiente.setText("Siguiente");
			}
		} else {
			InicioSincronizacion();
		}
		
	}
	
}
