package com.lys.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

import com.lys.mobile.data.CentroCostoData;
import com.lys.mobile.data.MaquinasData;
import com.lys.mobile.data.ProgramaEjecutadoData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.Util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

/**
 * Created by User on 03/10/2014.
 */
public class HistoInspeccion extends Activity {

	ListView listView;
	static final int DATE_DIALOG_IDI = 998;
	static final int DATE_DIALOG_IDF = 999;
	private int year, yearf, month, monthf, day, dayf;
	TextView fin, ffin, periodo;
	String perm_modificar="";
	String fechainicial = "", fechafinal = "", tco = "A", centr = "%", cb = "%";
	MyApp m;
	Spinner tipo, tmaquina, tcentro;
	static String contents = "";

	Button buscarmaquina, buscar;
	public ArrayList<ProgramaEjecutadoData> lista;
	private static final String BS_PACKAGE = "com.google.zxing.client.android";
	public static final int REQUEST_CODE = 0x0000c0de;
	ArrayList<MaquinasData> listama = new ArrayList<MaquinasData>();
	ArrayList<CentroCostoData> listacent = new ArrayList<CentroCostoData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// blablabla
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.histoinspeccion);
		setContentView(R.layout.mantinspecciont_historico);

		listView = (ListView) findViewById(R.id.listainspeccion); // imageView2
		buscar = (Button) findViewById(R.id.btnbuscarh);
		buscarmaquina = (Button) findViewById(R.id.bmaquina);
		tipo = (Spinner) findViewById(R.id.tipoin);// tfin tffin
		tcentro = (Spinner) findViewById(R.id.centroc);// thmaquina
		tmaquina = (Spinner) findViewById(R.id.thmaquina);

		lista = new ArrayList<ProgramaEjecutadoData>();
		perm_modificar = getIntent().getExtras().getString("Modificar");
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		cb = preferences.getString("codbarra", "");
		centr = preferences.getString("centro", "");

		fin = (TextView) findViewById(R.id.tfih_1);
		ffin = (TextView) findViewById(R.id.tffh_1);

		// listacond.add("--Seleccione--");
		if (listama.size() > 0)
			listama.clear();
		MaquinasData m1 = new MaquinasData();
		m1.c_maquina = "0";
		m1.c_descripcion = "0";
		m1.c_codigobarras = "0";
		m1.c_estado = "0";
		m1.c_familiainspeccion = "0";
		m1.c_centrocosto = "0";

		listama.add(m1);

		DataBase basededatos = new DataBase(this, "DBInspeccion", null, 1);
		SQLiteDatabase db = basededatos.getWritableDatabase();

		String query = "SELECT c_maquina,c_descripcion,c_codigobarras,c_estado,c_familiainspeccion,c_centrocosto from MTP_MAQUINAS  ";
		Cursor c = db.rawQuery(query, null);
		if (c.moveToFirst()) {

			do {

				MaquinasData m = new MaquinasData();
				m.c_maquina = c.getString(0);
				m.c_descripcion = c.getString(1);
				m.c_codigobarras = c.getString(2);
				m.c_estado = c.getString(3);
				m.c_familiainspeccion = c.getString(4);
				m.c_centrocosto = c.getString(5);

				// tmaquina.setText(cbarra);
				// tcentro.setText(centro);
				listama.add(m);

			} while (c.moveToNext());

		} else {
			listView.setAdapter(null);
			Toast.makeText(this, "Maquinas no disponible", Toast.LENGTH_SHORT)
					.show();
		}
		c.close();
		db.close();

		// tmaquina.setAdapter(new MaquinaItemAdapter(getApplicationContext(),
		// android.R.layout.simple_spinner_item,listama));

		MaquinaItemAdapter dataAdapterConn = new MaquinaItemAdapter(this,
				android.R.layout.simple_spinner_item, listama);

		dataAdapterConn
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		tmaquina.setAdapter(dataAdapterConn);
		// tcentro.setText(centr);

		tmaquina.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				cb = listama.get(position).c_maquina;
				// centr= listama.get(position).c_centrocosto;
				if (cb.equals("0"))
					cb = "%";

				Log.d("codigo maquina", cb);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		// ---------------------------
		if (listacent.size() > 0)
			listacent.clear();
		CentroCostoData m0 = new CentroCostoData();
		m0.c_compania = "0";
		m0.c_centrocosto = "0";
		m0.c_descripcion = "0";
		m0.c_estado = "0";

		// tmaquina.setText(cbarra);
		// tcentro.setText(centro);
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
			listView.setAdapter(null);
			Toast.makeText(this, "Centros no disponible", Toast.LENGTH_SHORT)
					.show();
		}
		cc.close();
		db1.close();

		CentroItemAdapter dataAdapterCe = new CentroItemAdapter(this, android.R.layout.simple_spinner_item, listacent);

		dataAdapterCe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		tcentro.setAdapter(dataAdapterCe);
		// tcentro.setText(centr);

		tcentro.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				// cb= listama.get(position).c_maquina;
				centr = listacent.get(position).c_centrocosto;
				if (centr.equals("0"))
					centr = "%";

				Log.d("codigo centr", centr);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		m = (MyApp) getApplicationContext();

		List<String> listacond = new ArrayList<String>();
		// listacond.add("--Seleccione--");
		listacond.add("AMBOS");
		listacond.add("PROGRAMADO");
		listacond.add("EJECUTADO");

		Log.d("tipo", tco);

		ArrayAdapter<String> dataAdapterCon = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listacond);

		dataAdapterCon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		tipo.setAdapter(dataAdapterCon);

		tipo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position > 0) {

					if (parent.getItemAtPosition(position).toString().equals("PROGRAMADO")) {
						tco = "P";
						Log.d("tipo", tco);

					} else if (parent.getItemAtPosition(position).toString().equals("EJECUTADO")) {
						tco = "E";
						Log.d("tipo", tco);
					} else if (parent.getItemAtPosition(position).toString().equals("AMBOS")) {
						tco = "A";
						Log.d("tipo", tco);
					}

				} else {

					tco = "A";
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		llenar();

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String item = lista.get(position).c_tipo;
				String correlativo = lista.get(position).n_correlativo;
				String compa = lista.get(position).c_compania;
			if (perm_modificar.equals("SI")){
				if (item.equalsIgnoreCase("E")) {

					Intent i = new Intent(HistoInspeccion.this,DetalleEjecutado2.class);
					
					i.putExtra("cb", "");
					i.putExtra("correlativo", correlativo);
					i.putExtra("compania", compa);
					startActivity(i);

				}
			}
				else {

				Toast.makeText(HistoInspeccion.this,"No tiene permisos para modificar", Toast.LENGTH_SHORT).show();
			}
			}
		});

		buscarmaquina.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intentScan = new Intent(BS_PACKAGE + ".SCAN");
				intentScan.putExtra("PROMPT_MESSAGE", "Enfoque entre 9 y 11 cm. apuntado el codigo de barras de la maquina");
				startActivityForResult(intentScan, REQUEST_CODE);

			}
		});

		buscar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("tipo", tco);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date d1 = null, d2 = null, dif = null, dff = null;
				try {
					d1 = format.parse(fechainicial);
					d2 = format.parse(fechafinal);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (d2.after(d1) && d1.before(d2)) {

					SimpleDateFormat formatt = new SimpleDateFormat(
							"yyyy-MM-dd 00:00:00");
					SimpleDateFormat formattf = new SimpleDateFormat(
							"yyyy-MM-dd 23:59:59");

					// fechainicial=formatt.format(d1);
					// fechafinal=formattf.format(d2);
					if (cb.equals("")) {

						Toast.makeText(HistoInspeccion.this, "Seleccione una maquina a consultar", Toast.LENGTH_SHORT).show();

					} else {

						final HistorialInspeccionAsyncTask tarea = new HistorialInspeccionAsyncTask(
								HistoInspeccion.this, cb, formatt.format(d1),
								formatt.format(d2), centr, tco, "", buscar); // ,String
																				// maquina,String
																				// fi,String
																				// ff,String
											Log.d("1",""+cb);	
											Log.d("2",""+formatt.format(d1));	
											Log.d("3",""+formatt.format(d2));	
											Log.d("4",""+centr);	
											Log.d("5",""+tco);	
												// centro,String
																				// tipo,String
																				// reto
						tarea.execute(new String[] { Util.url + "consultarHistorialProgramaInspeccion" });

						Thread thread = new Thread() {
							public void run() {
								try {
									tarea.get(120000, TimeUnit.MILLISECONDS);
									// tarea.get(30000, TimeUnit.MILLISECONDS);

								} catch (Exception e) {
									tarea.cancel(true);
									((Activity) HistoInspeccion.this)
											.runOnUiThread(new Runnable() {

												public void run() {
													// acceder.setEnabled(true);
													listView.setAdapter(null);
													tarea.progressDialog
															.dismiss();
													Toast.makeText(
															HistoInspeccion.this,
															"No se pudo establecer comunicacion .",
															Toast.LENGTH_LONG)
															.show();

												}
											});
								}
							}
						};
						thread.start();
					}

				} else {

					Toast.makeText(HistoInspeccion.this, "Seleccione una fecha acorde", Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				contents = intent.getStringExtra("SCAN_RESULT");

				DataBase basededatos = new DataBase(this, "DBInspeccion", null,
						1);
				SQLiteDatabase db = basededatos.getWritableDatabase();

				String query = "SELECT c_maquina,c_descripcion,c_codigobarras,c_estado,c_familiainspeccion,c_centrocosto from MTP_MAQUINAS where c_codigobarras='"
						+ contents + "' ";
				Cursor c = db.rawQuery(query, null);
				if (c.moveToFirst()) {

					String cma = c.getString(0);
					String desc = c.getString(1);
					String cbarra = c.getString(2);
					String estado = c.getString(3);
					String familia = c.getString(4);
					String centro = c.getString(5);

					cb = cbarra;
					centr = centro;
					// tmaquina.setText(cbarra);
					// tcentro.setText(centro);

				} else {

					Toast.makeText(this,
							"Maquina no disponible para ese codigo",
							Toast.LENGTH_SHORT).show();
				}
				c.close();
				db.close();

				// lbl.setText("Prensa hidráulica = "+contents);

			}
		}
		return;
	}

	public class UserItemAdapter extends ArrayAdapter<ProgramaEjecutadoData> {
		private ArrayList<ProgramaEjecutadoData> listcontact;

		public UserItemAdapter(Context context, int textViewResourceId, ArrayList<ProgramaEjecutadoData> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}
		
		public View getView(final int position, View convertView, final ViewGroup parent) {

			View v = convertView;

			final ProgramaEjecutadoData user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				//v = vi.inflate(R.layout.con_histo_inspeccion, null);
				v = vi.inflate(R.layout.det_histo_inspeccion, null);

			}

			if (user != null) {

				TextView tipo = (TextView) v.findViewById(R.id.ttipo);
				TextView num = (TextView) v.findViewById(R.id.tnumero);
				TextView fecha = (TextView) v.findViewById(R.id.tfecha);
				TextView cod = (TextView) v.findViewById(R.id.tcodigo);
				//TextView maqui = (TextView) v.findViewById(R.id.tmaquina);
				TextView cc = (TextView) v.findViewById(R.id.tcc);
				//TextView centr = (TextView) v.findViewById(R.id.tcentro);
				TextView fre = (TextView) v.findViewById(R.id.tfrecuencia);
				TextView condi = (TextView) v.findViewById(R.id.tcondicion);
				TextView obs = (TextView) v.findViewById(R.id.tobservacion);
				TextView come = (TextView) v.findViewById(R.id.tcomentario);
				TextView ins = (TextView) v.findViewById(R.id.tinspeccionado);

				if (tipo != null) {
					tipo.setText("" + user.c_tipo);
				}

				if (num != null) {
					num.setText("" + user.n_correlativo);
				}

				if (fecha != null) {
					Date daten = null;
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
					try {
						daten = (Date) format.parse(user.d_fecha);
						Log.d("date1", String.valueOf(daten));
						// System.out.println(date);
					} catch (Exception e) {
						e.printStackTrace();
					}
					SimpleDateFormat otroFormato = new SimpleDateFormat(
							"yyyy-MM-dd", Locale.ENGLISH);
					// System.out.println("FEcha : \n" +
					// otroFormato.format(daten));
					fecha.setText(otroFormato.format(daten));
				}

				if (cod != null) {

					cod.setText("" + user.c_maquina);
				}
				/*if (maqui != null) {

					maqui.setText("" + user.c_desMaquina);
				}*/
				if (cc != null) {

					cc.setText("" + user.c_centrocosto);
				}
				/*if (centr != null) {

					centr.setText("" + user.c_desCentroCosto);
				}*/

				if (fre != null) {

					fre.setText("" + user.c_desFrecuencia);
				}

				if (condi != null) {

					condi.setText("" + user.c_condicionmaquina);
				}

				if (obs != null) {

					obs.setText("" + user.c_observacion);
				}

				if (come != null) {

					come.setText("" + user.c_comentario);
				}

				if (ins != null) {

					ins.setText("" + user.c_nombreinspeccion);
				}

			}

			return v;

		}

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

	public void llenar() {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(HistoInspeccion.this);
		String fi = "";
		String ff ="";
		// String maqui= preferences.getString("maquina", "");
		// String peri= preferences.getString("periocidadp", "");
		SimpleDateFormat fsd = new SimpleDateFormat("yyyy-MM-dd");
		String dateCurrente = "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		calendar.add(Calendar.MONTH,-1);
		dateCurrente=fsd.format(calendar.getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(Calendar.getInstance().getTime());


		String[] items1 = fi.split(" ");
		String[] items2 = ff.split(" ");
		fi = dateCurrente;
		ff = date;

		fechainicial = fi;
		fechafinal = ff;

		fin.setText(fi);
		ffin.setText(ff);
		//fin = (TextView) findViewById(R.id.tfih_1);
		//ffin = (TextView) findViewById(R.id.tffh_1);
		/*SimpleDateFormat fsd = new SimpleDateFormat("yyyy-MM-dd");
		String dateCurrente = "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		calendar.add(Calendar.MONTH,-1);
		dateCurrente=fsd.format(calendar.getTime());
		fin.setText(dateCurrente);



		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(Calendar.getInstance().getTime());
		ffin.setText(date);
		periodo = (TextView) findViewById(R.id.thperiodo);*/
		// maquina= ( TextView) findViewById(R.id.thmaquina);

		fin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String[] items1 = fechainicial.split("-");
				yearf = Integer.parseInt(items1[0]);
				monthf = Integer.parseInt(items1[1]);
				dayf = Integer.parseInt(items1[2]);


				showDialog(DATE_DIALOG_IDI);
			}
		});
		ffin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] items1 = fechafinal.split("-");
				yearf = Integer.parseInt(items1[0]);
				monthf = Integer.parseInt(items1[1]);
				dayf = Integer.parseInt(items1[2]);

				showDialog(DATE_DIALOG_IDF);

			}
		});


		//periodo.setText(peri);
		// maquina.setText(maqui);

		// select asn llenar bean

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_IDI:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListener, year, month - 1, day);
		case DATE_DIALOG_IDF:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListenerf, yearf, monthf - 1, dayf);
		}

		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			// TODO Auto-generated method stub
			String dia,mes,anio;
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// if(day<0) day=Integer.parseInt("0"+day);

			// fechainicial=String.valueOf(new
			// StringBuilder().append(year).append("-").append(month+1).append("-").append(day)
			// .append(""));

			//fechainicial = String.valueOf(year + "-" + (month + 1) + "-" + day);
			anio = String.valueOf(year);
			month = month + 1;
			if (month >= 0 && month <= 9) {
				//mes = "0" + String.valueOf(month + 1);
				mes = "0" + String.valueOf(month);
			} else {
				//mes = String.valueOf(month + 1);
				mes = String.valueOf(month);
			}
			if (day >= 0 && day <= 9) {
				dia = "0" + String.valueOf(day);
			} else {
				dia = String.valueOf(day);
			}
			fechainicial = anio + "-" + mes + "-" + dia;

			fin.setText(fechainicial);

		}
	};

	private DatePickerDialog.OnDateSetListener datePickerListenerf = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			// TODO Auto-generated method stub
			String diaf,mesf,aniof;
			yearf = selectedYear;
			monthf = selectedMonth;
			dayf = selectedDay;
			// if(dayf<0) day=Integer.parseInt("0"+dayf);

			//fechafinal = String.valueOf(yearf + "-" + (monthf + 1) + "-" + dayf);
			aniof = String.valueOf(yearf);
			monthf = monthf + 1;
			if (monthf >= 0 && monthf <= 9) {
				//mesf = "0" + String.valueOf(monthf + 1);
				mesf = "0" + String.valueOf(monthf);
			} else {
				//mesf = String.valueOf(monthf + 1);
				mesf = String.valueOf(monthf);
			}
			if (dayf >= 0 && dayf <= 9) {
				diaf = "0" + String.valueOf(dayf);
			} else {
				diaf = String.valueOf(dayf);
			}
			fechafinal = aniof + "-" + mesf + "-" + diaf;
			
			ffin.setText(fechafinal);

		}
	};

	public class HistorialInspeccionAsyncTask extends
			AsyncTask<String, Integer, String> {

		Context context;
		public ProgressDialog progressDialog;
		Button boton;

		String maqui = "", fein, fef, ce = "", ti = "", re = "";
		public ArrayList<Object[]> listaprograma;
		public ArrayList<Object[]> listacabecera;
		public ArrayList<Object[]> listadetalle;

		public HistorialInspeccionAsyncTask(Context c, String maquina,
				String fi, String ff, String centro, String tipo, String reto,
				Button v) {
			super();
			context = c;
			maqui = maquina;
			fein = fi;
			fef = ff;
			ce = centro;
			ti = tipo;
			re = reto;

			boton = v;

			listaprograma = new ArrayList<Object[]>();
			if (listaprograma.size() > 0)
				listaprograma.clear();

			listacabecera = new ArrayList<Object[]>();
			if (listacabecera.size() > 0)
				listacabecera.clear();

			listadetalle = new ArrayList<Object[]>();
			if (listadetalle.size() > 0)
				listadetalle.clear();

			if (lista.size() > 0)
				lista.clear();

			progressDialog = new ProgressDialog(c);
			progressDialog
					.setMessage("Obteniendo Historial Informacion de Inspeccion..");
			progressDialog.setCancelable(false);

		}

		@Override
		public String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String url = params[0];
			DefaultHttpClient cliente = new DefaultHttpClient();

			HttpPost httppost = new HttpPost(url);
			String data = "";
			String html = "";

			publishProgress(10);
			Log.d("Logueo", String.valueOf(url));

			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("maquina", maqui));
				nameValuePairs.add(new BasicNameValuePair("inicio", fein));
				nameValuePairs.add(new BasicNameValuePair("fin", fef));
				nameValuePairs.add(new BasicNameValuePair("centro", ce));
				nameValuePairs.add(new BasicNameValuePair("tipo", tco));
				nameValuePairs.add(new BasicNameValuePair("retorno", re));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						HTTP.UTF_8));

				HttpResponse respuesta = cliente.execute(httppost);

				InputStream contenido = respuesta.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(contenido));

				publishProgress(40);

				while ((data = reader.readLine()) != null) {

					html += data;

				}

				reader.close();
				contenido.close();

				publishProgress(100);

			} catch (MalformedURLException e) {

				e.printStackTrace();
				return "";
			} catch (IOException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
				return "";
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
				return "";
			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
				return "";
			} finally {

				Log.d("Logueo", String.valueOf(html));
				try {

					JSONObject respJSON;
					String status = "";

					try {
						respJSON = new JSONObject(html.toString());
						status = respJSON.getString("status");

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (status.equals("0")) {

						return "0";

					} else if (status.equals("1")) {

						String resp = "";

						JSONObject respJSON2;

						try {

							respJSON2 = new JSONObject(html.toString());

							int tamaño1 = Integer.parseInt(respJSON2
									.getString("sizeprimero"));
							int tamaño2 = Integer.parseInt(respJSON2
									.getString("sizesegundo"));
							int tamaño3 = Integer.parseInt(respJSON2
									.getString("sizetercero"));
							Gson gson = new Gson();

							DataBase basededatos = new DataBase(context,
									"DBInspeccion", null, 1);
							SQLiteDatabase db = basededatos
									.getWritableDatabase();

							db.execSQL("DELETE FROM  MTP_PROGRAMAEJECUTADO");
							db.execSQL("DELETE FROM  MTP_INSPECCIONEJE_DET");
							db.execSQL("DELETE FROM  MTP_INSPECCIONEJE_CAB");

							for (int i = 0; i < tamaño1; i++) {

								String cad = respJSON2.getString("dataprimero"
										+ i);
								Object[] list = new Gson().fromJson(cad,
										Object[].class);

								listaprograma.add(list);
							}
							for (int i = 0; i < tamaño2; i++) {

								String cad = respJSON2.getString("datasegundo"
										+ i);
								Object[] list = new Gson().fromJson(cad,
										Object[].class);

								listacabecera.add(list);
							}

							for (int i = 0; i < tamaño3; i++) {

								String cad = respJSON2.getString("datatercero"
										+ i);
								Object[] list = new Gson().fromJson(cad,
										Object[].class);

								listadetalle.add(list);
							}

							for (int i = 0; i < listaprograma.size(); i++) {

								Object[] list = listaprograma.get(i);

								agregarbd(list, db);

							}

							for (int i = 0; i < listacabecera.size(); i++) {

								Object[] list = listacabecera.get(i);

								agregarbd2(list, db);

							}

							for (int i = 0; i < listadetalle.size(); i++) {

								Object[] list = listadetalle.get(i);

								agregarbd3(list, db);

							}

							db.close();

							resp = "1";

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						return resp;

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block

					e.printStackTrace();

				}

			}

			return "";

		}

		@Override
		protected void onPostExecute(String result) {// paso 4
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			// myAnim.cancel();

			if (result.equals("")) {

				boton.setEnabled(true);
				listView.setAdapter(null);
				Toast t = Toast.makeText(context, "Servidor no disponible",
						Toast.LENGTH_SHORT);
				t.show();

			} else {

				if (result.equals("0")) {

					boton.setEnabled(true);
					listView.setAdapter(null);
					Toast t = Toast.makeText(context,
							"No hay informacion disponible para la consulta!",
							Toast.LENGTH_SHORT);
					t.show();

				} else if (result.equals("1")) {

					// select database y adaptar

					DataBase basededatos = new DataBase(HistoInspeccion.this,
							"DBInspeccion", null, 1);
					SQLiteDatabase db = basededatos.getWritableDatabase();

					String query = "SELECT c_compania,c_tipo,n_correlativo,d_fecha, c_maquina, c_desMaquina, c_centrocosto,c_desCentroCosto, c_codFrecuencia, c_desFrecuencia , c_observacion, c_condicionmaquina, c_comentario, c_usuarioinspeccion, n_personainspeccion,c_nombreinspeccion from MTP_PROGRAMAEJECUTADO ";
					Cursor c = db.rawQuery(query, null);
					if (c.moveToFirst()) {
						do {
							ProgramaEjecutadoData p = new ProgramaEjecutadoData();

							p.c_compania = c.getString(0);
							p.c_tipo = c.getString(1);
							p.n_correlativo = c.getString(2);
							p.d_fecha = c.getString(3);
							p.c_maquina = c.getString(4);
							p.c_desMaquina = c.getString(5);
							p.c_centrocosto = c.getString(6);
							p.c_desCentroCosto = c.getString(7);
							p.c_codFrecuencia = c.getString(8);
							p.c_desFrecuencia = c.getString(9);
							p.c_observacion = c.getString(10);
							p.c_condicionmaquina = c.getString(11);
							p.c_comentario = c.getString(12);
							p.c_usuarioinspeccion = c.getString(13);
							p.n_personainspeccion = c.getString(14);
							p.c_nombreinspeccion = c.getString(15);

							lista.add(p);

						} while (c.moveToNext());

					} else {
						
						listView.setAdapter(null);
						Toast.makeText(HistoInspeccion.this,
								"No hay informacion disponible",
								Toast.LENGTH_SHORT).show();
					}
					c.close();
					db.close();

					listView.setAdapter(new UserItemAdapter(
							getApplicationContext(),
							android.R.layout.simple_list_item_1, lista));

					boton.setEnabled(true);
					Log.d("Codigo Inspeccion", "historialencontrado");

				}

			}

		}

		@Override
		protected void onPreExecute() {// paso1
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog.show();

		}

		@Override
		protected void onCancelled() {
			progressDialog.cancel();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {// paso2
			// TODO Auto-generated method stub

			super.onProgressUpdate(values);
			progressDialog.setProgress(values[0]);

		}

		public void agregarbd(Object[] o, SQLiteDatabase db) {

			String fechaa = String.valueOf(o[3]);

			String fechaaa = fechaa.replace(',', ' ');

			// String dtStart = "11/08/2013 08:48:10";

			Date daten = null;
			SimpleDateFormat format = new SimpleDateFormat(
					"MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
			try {
				daten = (Date) format.parse(fechaaa);
				Log.d("date1", String.valueOf(daten));
				// System.out.println(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
			SimpleDateFormat otroFormato = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			System.out.println("FEcha : \n" + otroFormato.format(daten));
			// for(int i=0;i<o.length;i++){

			db.execSQL("INSERT INTO MTP_PROGRAMAEJECUTADO(c_compania,c_tipo,n_correlativo,d_fecha, c_maquina, c_desMaquina, c_centrocosto,c_desCentroCosto, c_codFrecuencia, c_desFrecuencia , c_observacion, c_condicionmaquina, c_comentario, c_usuarioinspeccion, n_personainspeccion,c_nombreinspeccion)"
					+ "VALUES ('"
					+ o[0]
					+ "', '"
					+ o[1]
					+ "','"
					+ o[2]
					+ "', '"
					+ otroFormato.format(daten)
					+ "', '"
					+ o[4]
					+ "', '"
					+ o[5]
					+ "', '"
					+ o[6]
					+ "', '"
					+ o[7]
					+ "', '"
					+ o[8]
					+ "', '"
					+ o[9]
					+ "', '"
					+ o[10]
					+ "', '"
					+ o[11]
					+ "', '"
					+ o[12]
					+ "', '" + o[13] + "', '" + o[14] + "', '" + o[15] + "')");

			// }

		}

		public void agregarbd2(Object[] o, SQLiteDatabase db) {

			String fechaa = String.valueOf(o[7]);
			String fechaa2 = String.valueOf(o[8]);
			String fechaaa = fechaa.replace(',', ' ');
			String fechaaa2 = fechaa2.replace(',', ' ');

			// String dtStart = "11/08/2013 08:48:10";

			Date daten = null, datenu = null;
			SimpleDateFormat format = new SimpleDateFormat(
					"MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
			try {
				daten = (Date) format.parse(fechaaa);
				datenu = (Date) format.parse(fechaaa2);
				Log.d("date1", String.valueOf(daten));
				// System.out.println(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
			SimpleDateFormat otroFormato = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			System.out.println("FEcha : \n" + otroFormato.format(daten));
			// for(int i=0;i<o.length;i++){

			db.execSQL("INSERT INTO MTP_INSPECCIONEJE_CAB(c_compania,n_correlativo,c_maquina,c_desMaquina, c_condicionmaquina, c_comentario , c_estado,d_fechaInicioInspeccion, d_fechaFinInspeccion,c_periodoinspeccion, c_desPeriodoInspeccion,c_usuarioInspeccion,n_personainspeccion,c_nombreinspeccion,c_generoOT,n_numeroOT)"
					+ "VALUES ('"
					+ o[0]
					+ "', '"
					+ o[1]
					+ "','"
					+ o[2]
					+ "', '"
					+ o[3]
					+ "', '"
					+ o[4]
					+ "', '"
					+ o[5]
					+ "', '"
					+ o[6]
					+ "', '"
					+ otroFormato.format(daten)
					+ "', '"
					+ otroFormato.format(datenu)
					+ "', '"
					+ o[9]
					+ "', '"
					+ o[10]
					+ "', '"
					+ o[11]
					+ "', '"
					+ o[12]
					+ "', '"
					+ o[13]
					+ "', '" + o[14] + "', '" + o[15] + "')");

			// }

		}

		public void agregarbd3(Object[] o, SQLiteDatabase db) {

			db.execSQL("INSERT INTO MTP_INSPECCIONEJE_DET(c_compania,n_correlativo,n_linea,c_inspeccion, c_desInpeccion, c_tipoinspeccion , n_porcentajeminimo,n_porcentajemaximo, n_porcentajeinspeccion, c_estado, c_comentario , c_rutafoto )"
					+ "VALUES ('"
					+ o[0]
					+ "', '"
					+ o[1]
					+ "','"
					+ o[2]
					+ "', '"
					+ o[3]
					+ "', '"
					+ o[4]
					+ "', '"
					+ o[5]
					+ "', '"
					+ o[6]
					+ "', '"
					+ o[7]
					+ "', '"
					+ o[8]
					+ "', '"
					+ o[9]
					+ "', '"
					+ o[10]
					+ "', '" 
					+ o[11] 
					+ "')");

			// }

		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
	
	public void EventoRetroceder(View view) {
		finish();
	}

}
