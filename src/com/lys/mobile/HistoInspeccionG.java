package com.lys.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
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
import com.lys.mobile.Llenar.UserItemAdapter;

import com.lys.mobile.asynctask.CentroCostoAsyncTask;
import com.lys.mobile.asynctask.InspeccionAsyncTask;

import com.lys.mobile.data.CentroCostoData;
import com.lys.mobile.data.HistorialData;
import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.data.InspeccionEjecutadoGData;
import com.lys.mobile.data.MaquinasData;
import com.lys.mobile.data.ProgramaData;
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
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

/**
 * Created by User on 03/10/2014.
 */
public class HistoInspeccionG extends Activity {

	ListView listView;
	static final int DATE_DIALOG_IDI = 998;
	static final int DATE_DIALOG_IDF = 999;
	private int year, yearf;
	private int month, monthf;
	private int day, dayf;
	TextView fin;
	TextView ffin;
	//TextView periodo;
	String perm_modificar;
	String fechainicial = "", tco = "%";
	String fechafinal = "";
	MyApp app;
	Spinner tipo;//tmaquina, tcentro
	static String contents = "";
	
	Button buscar;//buscarmaquina
	public ArrayList<InspeccionEjecutadoGData> lista;
	//private static final String BS_PACKAGE = "com.google.zxing.client.android";
	//public static final int REQUEST_CODE = 0x0000c0de;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// blablabla
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.histoinspecciong);
		setContentView(R.layout.mantinspecciong_historico);

		listView = (ListView) findViewById(R.id.listainspeccionhg);
		buscar = (Button) findViewById(R.id.btnbuscarhg);
		tipo = (Spinner) findViewById(R.id.tipoinhg);// tfin tffin

		lista = new ArrayList<InspeccionEjecutadoGData>();
		perm_modificar= getIntent().getExtras().getString("Modificar");
		Log.i("Perm_modificar ==> ", perm_modificar);
		//SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		//cb = preferences.getString("codbarra", "");
		//centr = preferences.getString("centro", "");
		
		app = (MyApp) getApplicationContext();

		List<String> listatipo = new ArrayList<String>();
		listatipo.add("AMBOS");
		listatipo.add("OTRO");
		listatipo.add("MAQUINA");

		Log.d("tipo", tco);

		ArrayAdapter<String> dataAdapterCon = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listatipo);

		dataAdapterCon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		tipo.setAdapter(dataAdapterCon);

		tipo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (position > 0) {

					if (parent.getItemAtPosition(position).toString().equals("OTRO")) {

						tco = "OT";
						Log.d("tipo", tco);

					} else if (parent.getItemAtPosition(position).toString().equals("MAQUINA")) {

						tco = "MQ";
						Log.d("tipo", tco);
					} else if (parent.getItemAtPosition(position).toString().equals("AMBOS")) {

						tco = "%";
						Log.d("tipo", tco);
					}

				} else {

					tco = "%";
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
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String correlativo = lista.get(position).n_correlativo;
				String compa = lista.get(position).c_compania;

				if (perm_modificar.equals("SI")){
				Intent i = new Intent(HistoInspeccionG.this, DetalleEjecutadoRealizadoG2.class);

				i.putExtra("correlativohg", correlativo);
				i.putExtra("companiahg", compa);
				startActivity(i);
				}
				else
				{

					Toast.makeText(HistoInspeccionG.this,"No tiene permisos para modificar",Toast.LENGTH_SHORT).show();
				}

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

					SimpleDateFormat formatt = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
					SimpleDateFormat formattf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

					// fechainicial=formatt.format(d1);
					// fechafinal=formattf.format(d2);
					/*if (cb.equals("")) {

						Toast.makeText(HistoInspeccionG.this,
								"Seleccione una maquina a consultar",
								Toast.LENGTH_SHORT).show();

					} else {*/

						final HistorialInspeccionGAsyncTask tarea = new HistorialInspeccionGAsyncTask(
								HistoInspeccionG.this, tco, formatt.format(d1),
								formattf.format(d2), "", buscar); 				// ,String
																				// maquina,String
																				// fi,String
																				// ff,String
											Log.d("1",""+tco);	
											Log.d("2",""+formatt.format(d1));	
											Log.d("3",""+formatt.format(d2));
											// centro,String
																				// tipo,String
																				// reto
						tarea.execute(new String[] { Util.url + "consultarHistorialInspeccionG" });

						Thread thread = new Thread() {
							public void run() {
								try {
									tarea.get(120000, TimeUnit.MILLISECONDS);
									// tarea.get(30000, TimeUnit.MILLISECONDS);

								} catch (Exception e) {
									tarea.cancel(true);
									((Activity) HistoInspeccionG.this)
											.runOnUiThread(new Runnable() {

												public void run() {
													// acceder.setEnabled(true);
													listView.setAdapter(null);
													tarea.progressDialog.dismiss();
													Toast.makeText(HistoInspeccionG.this, "No se pudo establecer comunicacion.", Toast.LENGTH_LONG).show();

												}
											});
								}
							}
						};
						thread.start();
					//}

				} else {

					Toast.makeText(HistoInspeccionG.this, "Seleccione una fecha acorde", Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	/*public void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
	}*/

	public class UserItemAdapter extends ArrayAdapter<InspeccionEjecutadoGData> {
		private ArrayList<InspeccionEjecutadoGData> listcontact;

		public UserItemAdapter(Context context, int textViewResourceId, ArrayList<InspeccionEjecutadoGData> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}
		
		public View getView(final int position, View convertView, final ViewGroup parent) {

			View v = convertView;

			final InspeccionEjecutadoGData user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				//v = vi.inflate(R.layout.con_histo_inspecciong, null);
				v = vi.inflate(R.layout.det_histo_inspecciong, null);

			}

			if (user != null) {
				//TextView comp = (TextView) v.findViewById(R.id.thdcompania);
				TextView num = (TextView) v.findViewById(R.id.thdnumero);
				TextView tipo = (TextView) v.findViewById(R.id.thdtipo);
				TextView maqui = (TextView) v.findViewById(R.id.thdmaquina);
				TextView come = (TextView) v.findViewById(R.id.thdcomentario);
				TextView usui = (TextView) v.findViewById(R.id.thdusuarioi);
				TextView fechai = (TextView) v.findViewById(R.id.thdfechai);
				//TextView usuu = (TextView) v.findViewById(R.id.thdusuariou);
				//TextView fechau = (TextView) v.findViewById(R.id.thdfechau);
				
				/*if (comp != null) {
					if (user.c_compania.equals("00100000")) {
						comp.setText("FILTROS LYS");
					} else {
						comp.setText("" + user.c_compania);
					}
				}*/
				
				if (num != null) {
					num.setText("" + user.n_correlativo);
				}
				
				if (tipo != null) {
					//tipo.setText("" + user.c_tipoinspeccion);
					if (user.c_tipoinspeccion.equals("OT")) {
						tipo.setText("OTRO");
					} else if (user.c_tipoinspeccion.equals("MQ")) {
						tipo.setText("MAQUINA");
					}
				}
				
				if (maqui != null) {

					maqui.setText("" + user.c_maquina);
				}
				
				if (come != null) {

					come.setText("" + user.c_comentario);
				}
				
				if (usui != null) {

					usui.setText("" + user.c_usuarioinspeccion);
				}
				
				if (fechai != null) {
					Date daten = null;
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
					try {
						daten = (Date) format.parse(user.d_fechainspeccion);
						Log.d("date1", String.valueOf(daten));
						// System.out.println(date);
					} catch (Exception e) {
						e.printStackTrace();
					}
					SimpleDateFormat otroFormato = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					fechai.setText(otroFormato.format(daten));
				}

				/*if (usuu != null) {

					usuu.setText("" + user.c_ultimousuario);
				}
				
				if (fechau != null) {
					Date daten2 = null;
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
					try {
						daten2 = (Date) format.parse(user.d_ultimafechamodificacion);
						Log.d("date2", String.valueOf(daten2));
						// System.out.println(date);
					} catch (Exception e) {
						e.printStackTrace();
					}
					SimpleDateFormat otroFormato = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
					fechau.setText(otroFormato.format(daten2));
				}*/

			}

			return v;

		}

	}

	public void llenar() {

		fin = (TextView) findViewById(R.id.tfihg);
		ffin = (TextView) findViewById(R.id.tffhg);
		//periodo = (TextView) findViewById(R.id.thperiodo);

		fin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String[] items1 = fechainicial.split("-");
				year = Integer.parseInt(items1[0]);
				month = Integer.parseInt(items1[1]);
				day = Integer.parseInt(items1[2]);
				
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

		SimpleDateFormat fsd = new SimpleDateFormat("yyyy-MM-dd");
		String dateCurrente = "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		calendar.add(Calendar.MONTH,-1);
		dateCurrente=fsd.format(calendar.getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(Calendar.getInstance().getTime());


		String fi =dateCurrente;
		String ff = date;



		fechainicial = fi;
		fechafinal = ff;

		fin.setText(fi);
		ffin.setText(ff);

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
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			// TODO Auto-generated method stub
			String dia,mes,anio;
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			
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
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			// TODO Auto-generated method stub
			String diaf,mesf,aniof;
			yearf = selectedYear;
			monthf = selectedMonth;
			dayf = selectedDay;
			
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

	public class HistorialInspeccionGAsyncTask extends AsyncTask<String, Integer, String> {

		Context context;
		public ProgressDialog progressDialog;
		Button boton;

		String maqui = "", fein, fef, ce = "", ti = "", re = "";
		public ArrayList<Object[]> listacabecera;
		public ArrayList<Object[]> listadetalle;

		public HistorialInspeccionGAsyncTask(Context c, String tipo,
				String fi, String ff, String reto, Button v) {
			super();
			context = c;
			ti = tipo;
			fein = fi;
			fef = ff;
			re = reto;

			boton = v;

			listacabecera = new ArrayList<Object[]>();
			if (listacabecera.size() > 0)
				listacabecera.clear();

			listadetalle = new ArrayList<Object[]>();
			if (listadetalle.size() > 0)
				listadetalle.clear();

			if (lista.size() > 0)
				lista.clear();

			progressDialog = new ProgressDialog(c);
			progressDialog.setMessage("Obteniendo Historial Informacion de Inspeccion General..");
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
				nameValuePairs.add(new BasicNameValuePair("tipo", tco));
				nameValuePairs.add(new BasicNameValuePair("inicio", fein));
				nameValuePairs.add(new BasicNameValuePair("fin", fef));
				nameValuePairs.add(new BasicNameValuePair("retorno", re));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

				HttpResponse respuesta = cliente.execute(httppost);

				InputStream contenido = respuesta.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(contenido));

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

							int tamaño1 = Integer.parseInt(respJSON2.getString("sizeprimero"));
							int tamaño2 = Integer.parseInt(respJSON2.getString("sizesegundo"));
							Gson gson = new Gson();

							DataBase basededatos = new DataBase(context,"DBInspeccion", null, 1);
							SQLiteDatabase db = basededatos.getWritableDatabase();

							db.execSQL("DELETE FROM  MTP_INSPECCIONGEJE_DET");
							db.execSQL("DELETE FROM  MTP_INSPECCIONGEJE_CAB");
							
							for (int i = 0; i < tamaño1; i++) {

								String cad = respJSON2.getString("dataprimero" + i);
								Object[] list = new Gson().fromJson(cad, Object[].class);

								listacabecera.add(list);
							}

							for (int i = 0; i < tamaño2; i++) {

								String cad = respJSON2.getString("datasegundo" + i);
								Object[] list = new Gson().fromJson(cad, Object[].class);

								listadetalle.add(list);
							}

							for (int i = 0; i < listacabecera.size(); i++) {

								Object[] list = listacabecera.get(i);

								agregarbd1(list, db);

							}

							for (int i = 0; i < listadetalle.size(); i++) {

								Object[] list = listadetalle.get(i);

								agregarbd2(list, db);

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
				Toast t = Toast.makeText(context, "Servidor no disponible", Toast.LENGTH_SHORT);
				t.show();

			} else {

				if (result.equals("0")) {

					boton.setEnabled(true);
					listView.setAdapter(null);
					Toast t = Toast.makeText(context, "No hay informacion disponible para la consulta!", Toast.LENGTH_SHORT);
					t.show();

				} else if (result.equals("1")) {

					// select database y adaptar

					DataBase basededatos = new DataBase(HistoInspeccionG.this,"DBInspeccion", null, 1);
					SQLiteDatabase db = basededatos.getWritableDatabase();

					String query = "SELECT c_compania,n_correlativo,c_tipoinspeccion,c_maquina,c_comentario,c_usuarioinspeccion,d_fechainspeccion,c_estado,c_ultimousuario,d_ultimafechamodificacion from MTP_INSPECCIONGEJE_CAB ";
					Cursor c = db.rawQuery(query, null);
					if (c.moveToFirst()) {
						do {
							InspeccionEjecutadoGData p = new InspeccionEjecutadoGData();

							p.c_compania = c.getString(0);
							p.n_correlativo = c.getString(1);
							p.c_tipoinspeccion = c.getString(2);
							p.c_maquina = c.getString(3);
							p.c_comentario = c.getString(4);
							p.c_usuarioinspeccion = c.getString(5);
							p.d_fechainspeccion = c.getString(6);
							p.c_estado = c.getString(7);
							p.c_ultimousuario = c.getString(8);
							p.d_ultimafechamodificacion = c.getString(9);

							lista.add(p);

						} while (c.moveToNext());

					} else {
						
						listView.setAdapter(null);
						Toast.makeText(HistoInspeccionG.this, "No hay informacion disponible", Toast.LENGTH_SHORT).show();
					}
					c.close();
					db.close();

					listView.setAdapter(new UserItemAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, lista));

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

		public void agregarbd1(Object[] o, SQLiteDatabase db) {

			String fechaa = String.valueOf(o[6]);
			String fechaa2 = String.valueOf(o[9]);
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

			db.execSQL("INSERT INTO MTP_INSPECCIONGEJE_CAB(c_compania,n_correlativo,c_tipoinspeccion,c_maquina,c_comentario,c_usuarioinspeccion,d_fechainspeccion,c_estado,c_ultimousuario,d_ultimafechamodificacion)"
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
					+ otroFormato.format(daten)
					+ "', '"
					+ o[7]
					+ "', '"
					+ o[8]
					+ "', '"
					+ otroFormato.format(datenu)
					+ "')");
					
		}
		
		public void agregarbd2(Object[] o, SQLiteDatabase db) {
			
			String fechaa = String.valueOf(o[6]);
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
			
			db.execSQL("INSERT INTO MTP_INSPECCIONGEJE_DET(c_compania,n_correlativo,n_linea,c_comentario,c_rutafoto,c_ultimousuario,d_ultimafechamodificacion,c_tiporevisiong )"
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
					+ otroFormato.format(daten)
					+ "', '"
					+ o[7]
					+ "')");

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

