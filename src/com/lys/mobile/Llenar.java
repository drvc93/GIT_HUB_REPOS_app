package com.lys.mobile;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AnalogClock;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import com.lys.mobile.asynctask.EnviarReporteInspeccionAsyncTask;

import com.lys.mobile.asynctask.UploadService;
import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.data.ProgramaData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.ModeloCheckBox;
import com.lys.mobile.util.Util;
import com.lys.mobile.MyApp;
/**
 * Created by User on 15/09/2014.
 */
public class Llenar extends Activity {
	ListView listView;

	private AnalogClock reloj;
	int TAKE_PHOTO_CODE = 0;
	public static int count = 0, posicion = 0;
	public String dir = "//", filepath = "NULL", periodos = "NULL",
			comentariocab = "", condiciones = "NULL", cb = "";
	public ArrayList<InspeccionData> lista;
	public ArrayList<ProgramaData> listaperiodo;
	public int seconds = 60;
	public int minutes = 10;
	Timer t;
	TextView user, maquina, descripcion, estado, finicio, ffin;
	Spinner periodo, condicion;
	Button com;
	Calendar ca = Calendar.getInstance();
	public String fechainicio, fechafin;
	String c_cabecera = "0", c_detalle = "1", cabec = "NULL";
	MyApp app;

	List<String> listacondn;
	ArrayAdapter<String> dataAdapterConn;
	private static final int REQUEST_CAMERA = 1;
	String errores = "";
	boolean seborroarchivo = false;
	Context contexto;
	int respuestafinal = 0;
	public boolean pasar;
	public boolean tomo = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.llenar);
		
		app = ((MyApp)getApplicationContext());
		lista = new ArrayList<InspeccionData>();
		listaperiodo = new ArrayList<ProgramaData>();
		if (lista.size() > 0)
			lista.clear();
		listView = (ListView) findViewById(R.id.listainspeccion);

		// imageView2
		Button guardarreporte = (Button) findViewById(R.id.send);
		contexto = this;
		
		listacondn = new ArrayList<String>();
		
		// recuperar fecha y tiempo aqui
		
		listacondn.add("--Seleccione--");
		listacondn.add("OK");
		listacondn.add("FALLA");
		
		dataAdapterConn = new ArrayAdapter<String>(Llenar.this, android.R.layout.simple_spinner_item, listacondn);
		
		dataAdapterConn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// tiempo= ( TextView) findViewById(R.id.ttiemp);
		user = (TextView) findViewById(R.id.tvinllusuario);
		condicion = (Spinner) findViewById(R.id.tcondi);
		maquina = (TextView) findViewById(R.id.tmaquina);
		descripcion = (TextView) findViewById(R.id.tdescripcion);
		estado = (TextView) findViewById(R.id.testadoca);
		finicio = (TextView) findViewById(R.id.tfin);
		ffin = (TextView) findViewById(R.id.tffin);
		periodo = (Spinner) findViewById(R.id.tperiodo);// tfin tffin
		com = (Button) findViewById(R.id.tcomentin);
		
		com.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog alertDialog = new Dialog(Llenar.this);

				alertDialog.setContentView(R.layout.dialogo_comentario);
				alertDialog.setTitle("Comentario");
				alertDialog.setCancelable(false);
				final EditText text = (EditText) alertDialog
						.findViewById(R.id.ecomentario);

				Button dialogButton = (Button) alertDialog
						.findViewById(R.id.baceptar);
				// if button is clicked, close the custom dialog
				dialogButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						if (text.getText().toString().length() == 0) {

							Toast.makeText(Llenar.this, "Coloque Comentario",
									Toast.LENGTH_SHORT).show();
						} else {
							comentariocab = text.getText().toString();
							com.setText(comentariocab);
							alertDialog.cancel();
						}

					}
				});

				Button dialogCancelar = (Button) alertDialog
						.findViewById(R.id.bcancelar);
				// if button is clicked, close the custom dialog
				dialogCancelar.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						alertDialog.cancel();

					}
				});

				alertDialog.show();

			}
		});

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Llenar.this);
		String us = preferences.getString("nombreuser", "");
		cb = preferences.getString("cmaquina", "");
		String maqui = preferences.getString("cmaquina", "");
		String cmaqui = preferences.getString("maquina", "");
		String barra = preferences.getString("codbarra", "");
		String nive = preferences.getString("nivel", "");
		String condi = preferences.getString("condicion", "");
		
		user.setText("" + us);
		
		// condicion.setText(lista.get(0).c_condicionmaquina);
		maquina.setText("" + maqui);
		descripcion.setText("" + cmaqui);
		// descripcion.setText("");
		// estado.setText("");
		fechainicio = df.format(ca.getTime());
		finicio.setText(fechainicio);
		// ffin= ( TextView) findViewById(R.id.tffin);
		
		if (listaperiodo.size() > 0)
			listaperiodo.clear();
		DataBase basededatos = new DataBase(this, "DBInspeccion", null, 1);
		SQLiteDatabase db = basededatos.getWritableDatabase();
		
		String queryp = "SELECT c_periodoinspeccion,c_descripcion,c_estado,c_ultimousuario,d_ultimafechamodificacion FROM  MTP_PERIODOINSPECCION ";
		
		Log.d("sentencia", queryp);
		Cursor cp = db.rawQuery(queryp, null);
		if (listaperiodo.size() > 0)
			listaperiodo.clear();
		if (cp.moveToFirst()) {
			
			do {
				
				ProgramaData ins = new ProgramaData();
				
				ins.c_periodoinspeccion = cp.getString(0);
				ins.c_descripcion = cp.getString(1);
				ins.c_estado = cp.getString(2);
				ins.c_ultimousuario = cp.getString(3);
				ins.d_ultimafechamodificacion = cp.getString(4);
				
				listaperiodo.add(ins);
				
			} while (cp.moveToNext());
			
		} else {
			
			Toast.makeText(this, "No hay datos de programa", Toast.LENGTH_SHORT).show();
			Log.d("plan", "vacio");
		}
		
		cp.close();
		db.close();
		
		List<String> list = new ArrayList<String>();
		list.add("--Seleccione--");
		for (int i = 0; i < listaperiodo.size(); i++) {
			list.add(listaperiodo.get(i).c_descripcion);
		}
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		periodo.setAdapter(dataAdapter);
		
		periodo.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position > 0) {
					periodos = listaperiodo.get(position - 1).c_periodoinspeccion;
					// periodos=String.valueOf(position-1);
					Log.d("peri", periodos);
					llenar(periodos);
				} else {

					periodos = "NULL";
				}
				// llenarcon(periodos);}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		List<String> listacond = new ArrayList<String>();
		listacond.add("--Seleccione--");
		listacond.add("ABIERTA");
		listacond.add("CERRADA");
		
		ArrayAdapter<String> dataAdapterCon = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listacond);
		
		dataAdapterCon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		condicion.setAdapter(dataAdapterCon);
		
		condicion.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position > 0) {

					if (parent.getItemAtPosition(position).toString()
							.equals("ABIERTA")) {

						condiciones = "A";
						Log.d("CONDI", condiciones);
						
					} else if (parent.getItemAtPosition(position).toString()
							.equals("CERRADA")) {
						
						condiciones = "C";
						Log.d("CONDI", condiciones);
					}
					
				} else {
					
					condiciones = "NULL";
				}
				// llenarcon(periodos);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		guardarreporte.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (lista.size() == 0) {
					
					Toast.makeText(Llenar.this, "Lista de detalles vacia!", Toast.LENGTH_SHORT).show();
					
				} else {
					
					if (c_cabecera.equals("0")) {
						Log.e("GuardarReporte: ",c_cabecera);
						GuardarReporte();
						
					} else {
						Log.e("ModificarReporte: ",c_cabecera);
						ModificarReporte();
						
					}
					
				}
				
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
			}
		});
		
		//dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)	+ "/picFolder/";
		//File newdir = new File(dir);
		//newdir.mkdirs();
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
	
	public class UserItemAdapter extends ArrayAdapter<InspeccionData> {
		private ArrayList<InspeccionData> listcontact;

		public UserItemAdapter(Context context, int textViewResourceId,
				ArrayList<InspeccionData> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			tomo = false;
			Log.e("Entro aqui","getView");
			Log.e("position",String.valueOf(position));
			
			View v = convertView;
			LayoutInflater vi = null;
			View row = convertView;

			final InspeccionData user = listcontact.get(position);

			int valor = 0;

			if (v == null) {

				if (user.n_porcentajeminimo.substring(0, 1).equals("0")
						&& user.n_porcentajemaximo.substring(0, 1).equals("0")) {// user.n_porcentajeminimo
																					// y
					vi = (LayoutInflater) getContext().getSystemService(
							Context.LAYOUT_INFLATER_SERVICE); // user.n_porcentajemaximo
					// =0

					valor = 1;
					Log.d("valor", "1");

					Log.d("minimo", user.n_porcentajeminimo.substring(0, 1));
					Log.d("maximo", user.n_porcentajemaximo.substring(0, 1));
					Log.d("aq", "----------------------------");
					v = vi.inflate(R.layout.con_inspeccion_se, null);

					// }else
					// if(!user.n_porcentajeminimo.substring(0,1).equals("0") &&

					// !user.n_porcentajemaximo.substring(0,1).equals("0")){

				} else {
					vi = (LayoutInflater) getContext().getSystemService(
							Context.LAYOUT_INFLATER_SERVICE);
					// LayoutInflater vi =
					// (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					valor = 2;
					Log.d("valor", "2");

					Log.d("minimo", user.n_porcentajeminimo.substring(0, 1));
					Log.d("maximo", user.n_porcentajemaximo.substring(0, 1));
					Log.d("aq", "----------------------------");

					v = vi.inflate(R.layout.con_inspeccion_sm, null);

				}

			} else {

				if (user.n_porcentajeminimo.substring(0, 1).equals("0")
						&& user.n_porcentajemaximo.substring(0, 1).equals("0")) {// user.n_porcentajeminimo
																					// y
					vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); // user.n_porcentajemaximo
					// =0

					valor = 1;
					Log.d("valor", "1");

					Log.d("minimo", user.n_porcentajeminimo.substring(0, 1));
					Log.d("maximo", user.n_porcentajemaximo.substring(0, 1));
					Log.d("aq", "----------------------------");
					v = vi.inflate(R.layout.con_inspeccion_se, null);

					// }else
					// if(!user.n_porcentajeminimo.substring(0,1).equals("0") &&

					// !user.n_porcentajemaximo.substring(0,1).equals("0")){

				} else {
					vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					// LayoutInflater vi =
					// (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					valor = 2;
					Log.d("valor", "2");

					Log.d("minimo", user.n_porcentajeminimo.substring(0, 1));
					Log.d("maximo", user.n_porcentajemaximo.substring(0, 1));
					Log.d("aq", "----------------------------");

					v = vi.inflate(R.layout.con_inspeccion_sm, null);

				}

			}

			if (user != null) {

				if (valor == 1) {

					TextView numero = (TextView) v.findViewById(R.id.tnumero);
					TextView detalle = (TextView) v.findViewById(R.id.tdetalle);
					TextView systema = (TextView) v.findViewById(R.id.tsystema);
					TextView minimo = (TextView) v.findViewById(R.id.tminimo);
					TextView maximo = (TextView) v.findViewById(R.id.tmaximo);
					TextView campo = (TextView) v.findViewById(R.id.tcampo);
					Spinner ok = (Spinner) v.findViewById(R.id.tok);

					final Button comentario = (Button) v.findViewById(R.id.bcomentario);
					Button imagen = (Button) v.findViewById(R.id.bimagen);

					try {

						ok.setAdapter(dataAdapterConn);
					} catch (Exception e) {

						Log.d("err", e.getMessage().toString());
					}

					try {

						if (user.c_estado != null || user.c_estado.length() > 0) {

							if (user.c_estado.equals("O")) {
								int spinnerPostion = dataAdapterConn.getPosition("OK");
								ok.setSelection(spinnerPostion);

								// ok.setSelection(((ArrayAdapter<String>)ok.getAdapter()).getPosition("OK"));
							} else if (user.c_estado.equals("F")) {
								int spinnerPostion = dataAdapterConn
										.getPosition("FALLA");
								ok.setSelection(spinnerPostion);
								// ok.setText("FALLA");

								// ok.setSelection(((ArrayAdapter<String>)ok.getAdapter()).getPosition("FALLA"));
							}

						}
						if (user.c_comentario != null
								|| user.c_comentario.length() > 0) {

							comentario.setText("" + user.c_comentario);

						}
						if (user.c_rutafoto != null
								|| user.c_rutafoto.length() > 0) {

							//imagen.setText("" + user.c_rutafoto);
							imagen.setText(user.c_rutafoto);
							
							user.c_rutafoto = imagen.getText().toString().trim();

						}

					} catch (Exception e) {
						// llenar.setText("");
					}

					if (numero != null) {
						user.n_linea = (String.valueOf(position + 1));
					 
						numero.setText(""+   user.n_linea);
					}
					if (detalle != null) {
						detalle.setText("" + user.c_descripcion);
					}
					if (systema != null) {
						systema.setText("" + user.c_tipoinspeccion);
					}
					if (campo != null) {
						// campo.setText(user.n_porcentajeinspeccion);
					}
					if (minimo != null) {
						minimo.setText("" + user.n_porcentajeminimo);
					}
					if (maximo != null) {
						maximo.setText("" + user.n_porcentajemaximo);
					}

					if (comentario != null) {
						// if(user.c_comentario.equals("") ||
						// !(user.c_comentario==null))

						// comentario.setText(user.c_comentario);
					}
					if (ok != null) {
						// if(user.c_rutafoto.equals("") ||
						// !(user.c_rutafoto==null))

						// imagen.setText(user.c_rutafoto);

					}

					int s = ok.getSelectedItemPosition();
					if (s == 0) {
						user.c_estado = null;

					}
					if (s == 1) {
						user.c_estado = "O";

					}
					if (s == 2) {
						user.c_estado = "F";

					}

					Log.d("tamaa", String.valueOf(listacondn.size()));

					ok.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							if (position > 0) {

								if (parent.getItemAtPosition(position)
										.toString().equals("OK")) {

									user.c_estado = "O";

								} else if (parent.getItemAtPosition(position)
										.toString().equals("FALLA")) {
									user.c_estado = "F";

								}

							} else {
								user.c_estado = null;

							}
							// llenarcon(periodos);
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub

						}
					});

					user.c_comentario = comentario.getText().toString().trim();
					comentario.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							final Dialog alertDialog = new Dialog(Llenar.this);

							alertDialog.setContentView(R.layout.dialogo_comentario);
							alertDialog.setTitle("Comentario");
							alertDialog.setCancelable(false);
							final EditText text = (EditText) alertDialog.findViewById(R.id.ecomentario);
							text.setText("" + user.c_comentario);

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {
								
								public void onClick(View v) {

									if (text.getText().toString()
											.length() == 0) {

										Toast.makeText(Llenar.this,
												"Coloque Comentario",
												Toast.LENGTH_SHORT)
												.show();
									} else {
										user.c_comentario = text
												.getText().toString();
										comentario.setText(""
												+ user.c_comentario);
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
					//user.c_rutafoto = imagen.getText().toString().trim();
					imagen.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
														
							String state = Environment.getExternalStorageState();
							Log.e("state",state);
							if (Environment.MEDIA_MOUNTED.equals(state)) {
								Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(intent, REQUEST_CAMERA);
							}
							
							posicion = position;
							tomo = true;
							
							/*Calendar c = Calendar.getInstance();
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String fecha = df.format(c.getTime());
							fecha = fecha.replace(":", " ");
							fecha = fecha.replaceAll("\\s", "");
							//String nombrefoto = "LysMobile" + fecha;
							String nombrefoto = cb + fecha;
							
							filepath = dir + nombrefoto + ".jpg";
							File newfile = new File(filepath);
							
							try {
								newfile.createNewFile();
							} catch (IOException e) {
							}
							
							Uri outputFileUri = Uri.fromFile(newfile);
							
							Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
							
							startActivityForResult(cameraIntent,TAKE_PHOTO_CODE);*/
							
						}
					});
					if (tomo == true) {
						user.c_rutafoto = filepath;
					}

				} else if (valor == 2) {

					TextView numero = (TextView) v.findViewById(R.id.tnumero);
					TextView detalle = (TextView) v.findViewById(R.id.tdetalle);
					TextView systema = (TextView) v.findViewById(R.id.tsystema);
					TextView minimo = (TextView) v.findViewById(R.id.tminimo);
					TextView maximo = (TextView) v.findViewById(R.id.tmaximo);
					final EditText llenar = (EditText) v.findViewById(R.id.tllenarsm);
					final TextView ok = (TextView) v.findViewById(R.id.toki);
					final Button comentario = (Button) v.findViewById(R.id.bcomentario);
					Button imagen = (Button) v.findViewById(R.id.bimagen);
					
					// String amt = data.get(position).get("dueAmount");
					
					try {
						
						if (user.n_porcentajeinspeccion != null
								|| user.n_porcentajeinspeccion.length() > 0) {
							
							llenar.setText(user.n_porcentajeinspeccion);
							
							user.n_porcentajeinspeccion = llenar.getText().toString().trim();
						}
						
						if (user.c_estado != null || user.c_estado.length() > 0) {
							
							if (user.c_estado.equals("O")) {
								
								ok.setText("OK");
								user.c_estado = "O";
							} else if (user.c_estado.equals("F")) {
								
								ok.setText("FALLA");
								user.c_estado = "F";
							}
							
						}
						if (user.c_comentario != null
								|| user.c_comentario.length() > 0) {
							
							comentario.setText("" + user.c_comentario);
							
						}
						if (user.c_rutafoto != null
								|| user.c_rutafoto.length() > 0) {
							
							//imagen.setText("" + user.c_rutafoto);
							imagen.setText(user.c_rutafoto);
							
							user.c_rutafoto = imagen.getText().toString().trim();
							
						}
						
					} catch (Exception e) {
						//llenar.setText("");
						
					}
					
					if (numero != null) {
						user.n_linea = (String.valueOf(position + 1));
						
						numero.setText(""+   user.n_linea);
						
					}
					if (detalle != null) {
						detalle.setText("" + user.c_descripcion);
					}
					if (systema != null) {
						systema.setText("" + user.c_tipoinspeccion);
					}
					if (minimo != null) {
						minimo.setText("" + user.n_porcentajeminimo);
					}
					if (maximo != null) {
						maximo.setText("" + user.n_porcentajemaximo);
					}
					if (llenar != null) {
						// if(user.c_estado.equals("") ||
						// !(user.c_estado==null))
						
						// ok.setText(user.c_estado);
					}
					if (ok != null) {
						// if(user.c_estado.equals("") ||
						// !(user.c_estado==null))
						
						// ok.setText(user.c_estado);
					}
					if (comentario != null) {
						// if(user.c_comentario.equals("") ||
						// !(user.c_comentario==null))
						// comentario.setText(user.c_comentario);
					}
					if (imagen != null) {
						// if(user.c_rutafoto.equals("") ||
						// !(user.c_rutafoto==null))
						// imagen.setText(user.c_rutafoto);
						
					}
					
					/*
					 * llenar.setOnFocusChangeListener(new
					 * View.OnFocusChangeListener() { public void
					 * onFocusChange(View v, boolean hasFocus) { if (!hasFocus)
					 * { EditText et = (EditText)
					 * v.findViewById(R.id.tllenarsm);
					 * user.n_porcentajeinspeccion
					 * =et.getText().toString().trim(); } } });
					 */
					
					llenar.addTextChangedListener(new TextWatcher() {
						public void afterTextChanged(Editable s) {
							
							String valor = "0";
							
							valor = llenar.getText().toString();
							
							if (valor.length() == 0) {
								
								llenar.setHint("0");
								user.n_porcentajeinspeccion = null;
							} else {
								
								if (Double.parseDouble(valor) >= Double.parseDouble(user.n_porcentajeminimo)
										&& Double.parseDouble(valor) <= Double.parseDouble(user.n_porcentajemaximo)) {

									user.n_porcentajeinspeccion = valor;
									user.c_estado = "O";
									ok.setText("OK");

								}

								if (Double.parseDouble(valor) < Double.parseDouble(user.n_porcentajeminimo)) {

									user.n_porcentajeinspeccion = valor;
									user.c_estado = "F";
									ok.setText("FALLA");

								}
								
								if (Double.parseDouble(valor) > Double.parseDouble(user.n_porcentajemaximo)) {

									user.n_porcentajeinspeccion = valor;
									user.c_estado = "F";
									ok.setText("FALLA");
								}
							}
							// user.notaInspeccion
							ok.setPadding(10, 0, 0, 0);
							ok.setGravity(Gravity.CENTER_VERTICAL);

						}

						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {
						}

						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
						}
					});

					ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							user.c_estado = "F";

						}
					});

					user.c_comentario = comentario.getText().toString().trim();
					comentario.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							final Dialog alertDialog = new Dialog(Llenar.this);

							alertDialog.setContentView(R.layout.dialogo_comentario);
							alertDialog.setTitle("Comentario");
							alertDialog.setCancelable(false);
							final EditText text = (EditText) alertDialog.findViewById(R.id.ecomentario);

							text.setText("" + user.c_comentario);

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {
								
								public void onClick(View v) {

									if (text.getText().toString()
											.length() == 0) {

										Toast.makeText(Llenar.this,
												"Coloque Comentario",
												Toast.LENGTH_SHORT)
												.show();
									} else {
										user.c_comentario = text
												.getText().toString();
										comentario.setText(""
												+ user.c_comentario);
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
					
					//user.c_rutafoto = imagen.getText().toString().trim();
					// user.c_rutafoto = "sin foto";
					imagen.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							
							// abre camara
							// sonar
							
							String state = Environment.getExternalStorageState();
							Log.e("state",state);
							if (Environment.MEDIA_MOUNTED.equals(state)) {
								Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(intent, REQUEST_CAMERA);
							}
							
							posicion = position;
							tomo = true;
							/*Calendar c = Calendar.getInstance();
							DateFormat df = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							String fecha = df.format(c.getTime());
							fecha = fecha.replace(":", " ");
							fecha = fecha.replaceAll("\\s", "");
							//String nombrefoto = "LysMobile" + fecha;
							String nombrefoto = cb + fecha;
							
							filepath = dir + nombrefoto + ".jpg";
							File newfile = new File(filepath);
							try {
								newfile.createNewFile();
							} catch (IOException e) {
							}
							
							Uri outputFileUri = Uri.fromFile(newfile);
							
							Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
							
							startActivityForResult(cameraIntent,TAKE_PHOTO_CODE);*/
							
						}
					});
					if (tomo == true){
						user.c_rutafoto = filepath;
					}					
				}
				
			}
			return v;
			
		}

	}
	
	public void llenar(String periodo) {
		
		// select
		
		DataBase basededatos = new DataBase(this, "DBInspeccion", null, 1);
		SQLiteDatabase db = basededatos.getWritableDatabase();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Llenar.this);
		String familia = preferences.getString("familia","");
		
		String query = "SELECT c_inspeccion,c_descripcion,c_tipoinspeccion,n_porcentajeminimo, " + 
		"n_porcentajemaximo,c_familiainspeccion,c_periodoinspeccion,c_estado from MTP_INSPECCION " + 
		"where c_familiainspeccion='" + familia + "' and c_periodoinspeccion= '" + periodo + "'";
		
		Log.d("sentencia", query);
		Cursor c = db.rawQuery(query, null);
		
		if (lista.size() > 0)
			lista.clear();
		
		if (c.moveToFirst()) {
			
			do {
				
				InspeccionData ins = new InspeccionData();
				
				ins.c_inspeccion = c.getString(0);
				ins.c_descripcion = c.getString(1);
				ins.c_tipoinspeccion = c.getString(2);
				String stringmi = c.getString(3);
				String stringma = c.getString(4);
				String Str1 = new String(stringmi);
				String Str2 = new String(stringma);
				
				if (Str2.substring(0, 3).equals("100")) {
					
					ins.n_porcentajeminimo = Str1.substring(0, 5);
					ins.n_porcentajemaximo = Str2.substring(0, 6);
				} else {
					
					ins.n_porcentajeminimo = Str1.substring(0, 5);
					ins.n_porcentajemaximo = Str2.substring(0, 5);
					
				}
				ins.n_porcentajeinspeccion = null;
				ins.c_familiainspeccion = c.getString(5);
				ins.d_periodoinspeccion = c.getString(6);
				ins.c_estado = c.getString(7);
				
				// MyApp m=(MyApp)getApplicationContext();
				
				lista.add(ins);
				
			} while (c.moveToNext());
			
		} else {
			
			Toast.makeText(this, "No hay datos de inspeccion",Toast.LENGTH_SHORT).show();
			Log.d("plan", "vacio");
		}
		c.close();
		db.close();
		
		Log.d("tamao array", String.valueOf(lista.size()));
		
		for (int i = 0; i < lista.size(); i++) {
			Log.d("valoress", String.valueOf(lista.get(i).c_descripcion));
			
		}
		
		// if(!(InspeccionAsyncTask.lista.get(0).fecha==null))
		// fecha.setText(InspeccionAsyncTask.lista.get(0).fecha);
		// if(!(InspeccionAsyncTask.lista.get(0).periocidad==null))
		// periodo.setText(InspeccionAsyncTask.lista.get(0).periocidad);
		
		listView.setAdapter(new UserItemAdapter(getApplicationContext(),android.R.layout.simple_list_item_1, lista));
		
	}/*
	 * public void llenarcon(String p){
	 * 
	 * 
	 * //select
	 * 
	 * ArrayList<InspeccionData> listaaux=new ArrayList<InspeccionData>();
	 * 
	 * 
	 * 
	 * 
	 * 
	 * for(int i=0;i<lista.size();i++){
	 * 
	 * if(lista.get(i).d_periocidad.equals(p)) listaaux.add(lista.get(i));
	 * 
	 * }
	 * 
	 * Log.d("tamao array",String.valueOf(listaaux.size())); lista.clear();
	 * 
	 * for(int i=0;i<listaaux.size();i++){
	 * 
	 * 
	 * lista.add(listaaux.get(i));
	 * 
	 * }
	 * 
	 * listaaux.clear(); // if(!(InspeccionAsyncTask.lista.get(0).fecha==null))
	 * //fecha.setText(InspeccionAsyncTask.lista.get(0).fecha);
	 * //if(!(InspeccionAsyncTask.lista.get(0).periocidad==null)) //
	 * periodo.setText(InspeccionAsyncTask.lista.get(0).periocidad);
	 * 
	 * 
	 * listView.setAdapter(new UserItemAdapter(getApplicationContext(),
	 * android.R.layout.simple_list_item_1,lista));
	 * 
	 * 
	 * 
	 * }
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_CAMERA) {
			if (resultCode == Activity.RESULT_OK) {
				Bitmap x = (Bitmap) data.getExtras().get("data");
				//finishFromChild(this);
				ActualizarFotoTomada(x);
				
				Log.e("Paso aki","1");
				View v = getViewByPosition(posicion, listView);
				Button foto = (Button) v.findViewById(R.id.bimagen);
				foto.setText(filepath);
				
				lista.get(posicion).c_rutafoto = filepath;
				Log.e("filepath posicion" + String.valueOf(posicion),filepath);
				Log.e("imagen posicion" + String.valueOf(posicion),lista.get(posicion).c_rutafoto);
			}
		}
		
		/*if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
			Log.d("CameraDemo", "Pic saved");
			
			View v = getViewByPosition(posicion, listView);
			Button foto = (Button) v.findViewById(R.id.bimagen);
			foto.setText(filepath);
			
			lista.get(posicion).c_rutafoto = filepath;
			
		}*/
	}
	
	public View getViewByPosition(int position, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition
				+ listView.getChildCount() - 1;
		
		if (position < firstListItemPosition || position > lastListItemPosition) {
			return listView.getAdapter().getView(position, null, listView);
		} else {
			final int childIndex = position - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}
	
	public void ModificarReporte() {
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		DataBase basededatos = new DataBase(this, "DBInspeccion", null, 1);
		SQLiteDatabase db = basededatos.getWritableDatabase();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Llenar.this);
		String us = preferences.getString("nombreuser", "");
		// String cb= preferences.getString("cmaquina", "");
		
		// recuperar fecha tiempo aqui
		Calendar ca = Calendar.getInstance();
		fechafin = df.format(ca.getTime());
		int vacio = 0;
		
		cabec = c_cabecera;
		/*
		 * if(comentariocab.equals("") ){
		 * 
		 * Toast.makeText(Llenar.this, "Campo comentario requerido",
		 * Toast.LENGTH_SHORT).show(); vacio=1;
		 * 
		 * 
		 * }
		 */
		if (condiciones.equals("NULL")) {
			
			Toast.makeText(Llenar.this, "Seleccione la condicion",Toast.LENGTH_SHORT).show();
			vacio = 1;
			
		}
		if (periodos.equals("NULL")) {
			
			Toast.makeText(Llenar.this, "Seleccione el periodo",Toast.LENGTH_SHORT).show();
			vacio = 1;
			
		}
		
		for (int i = 0; i < lista.size(); i++) {
			
			if (lista.get(i).n_porcentajeminimo.substring(0, 1).equals("0")
					&& lista.get(i).n_porcentajemaximo.substring(0, 1).equals("0")) {
				
				if (lista.get(i).c_estado == null || lista.get(i).c_estado.length() == 0) {
					
					vacio = 1;
					Toast.makeText(Llenar.this,"Hay campos de estado que no ha llenado",
							Toast.LENGTH_SHORT).show();
					break;
					
				}
				
			} else {
				
				if (lista.get(i).n_porcentajeinspeccion == null
						|| lista.get(i).n_porcentajeinspeccion.length() == 0) {
					
					vacio = 1;
					Toast.makeText(Llenar.this,"Hay campos de porcentaje que no ha llenado",Toast.LENGTH_SHORT).show();
					
					break;
					
				}
				
			}
			
		}
		// significa que esta lleno arriba
		
		if (vacio == 0) {
			
			for (int i = 0; i < lista.size(); i++) {
				
				if (lista.get(i).n_porcentajeminimo.substring(0, 1).equals("0")
						&& lista.get(i).n_porcentajemaximo.substring(0, 1).equals("0")) {
					
					// significa que todo arriba y abajo lleno
					vacio = 2;
					
					ContentValues con = new ContentValues();
					con.put("c_compania", Util.compania);
					con.put("n_linea", lista.get(i).n_linea);
					con.put("c_inspeccion", lista.get(i).c_inspeccion);
					con.put("c_tipoinspeccion", lista.get(i).c_tipoinspeccion);
					con.put("n_porcentajeinspeccion",lista.get(i).n_porcentajeinspeccion);
					con.put("n_porcentajeminimo",lista.get(i).n_porcentajeminimo);
					con.put("n_porcentajemaximo",lista.get(i).n_porcentajemaximo);
					con.put("c_estado", lista.get(i).c_estado);
					con.put("c_comentario", lista.get(i).c_comentario);
					if (lista.get(i).c_rutafoto==null || lista.get(i).c_rutafoto.equals("null")){
						con.put("c_rutafoto","");
						Log.e("c_rutafoto_M1","");
					}else{
						con.put("c_rutafoto",lista.get(i).c_rutafoto);
						Log.e("c_rutafoto_M1",lista.get(i).c_rutafoto);
					}
					Log.e("c_cabecera_M1",c_cabecera);
					Log.e("n_linea_M1",String.valueOf(lista.get(i).n_linea));
					con.put("c_ultimousuario", us);
					con.put("d_ultimafechamodificacion", fechafin);
					
					String[] args = new String[] { Util.compania, c_cabecera, lista.get(i).n_linea };
					db.update(" MTP_INSPECCIONMAQUINA_DET", con,"c_compania=? and n_correlativo=? and n_linea=?", args);
					
					//db.update(" MTP_INSPECCIONMAQUINA_DET", con,"c_compania='" + Util.compania + "' and n_correlativo='" + c_cabecera + "' and n_linea='" + lista.get(i).n_linea + "'", null);
					
					Log.d("vuelta actualizo_M1", lista.get(i).n_linea);
					
				} else {
					
					// significa que todo arriba y abajo lleno
					vacio = 2;
					
					ContentValues con = new ContentValues();
					con.put("c_compania", Util.compania);
					con.put("n_linea", lista.get(i).n_linea);
					con.put("c_inspeccion", lista.get(i).c_inspeccion);
					con.put("c_tipoinspeccion", lista.get(i).c_tipoinspeccion);
					con.put("n_porcentajeinspeccion",lista.get(i).n_porcentajeinspeccion);
					con.put("n_porcentajeminimo",lista.get(i).n_porcentajeminimo);
					con.put("n_porcentajemaximo",lista.get(i).n_porcentajemaximo);
					con.put("c_estado", lista.get(i).c_estado);
					con.put("c_comentario", lista.get(i).c_comentario);
					//con.put("c_rutafoto", lista.get(i).c_rutafoto);
					if (lista.get(i).c_rutafoto==null || lista.get(i).c_rutafoto.equals("null")){
						con.put("c_rutafoto","");
						Log.e("c_rutafoto_M2","");
					}else{
						con.put("c_rutafoto",lista.get(i).c_rutafoto);
						Log.e("c_rutafoto_M2","");
					}
					Log.e("c_cabecera_M2",c_cabecera);
					Log.e("n_linea_M2",String.valueOf(lista.get(i).n_linea));
					con.put("c_ultimousuario", us);
					con.put("d_ultimafechamodificacion", fechafin);
					
					String[] args = new String[] { Util.compania, c_cabecera, lista.get(i).n_linea };
					db.update(" MTP_INSPECCIONMAQUINA_DET", con,"c_compania=? and n_correlativo=? and n_linea=?", args);
					
					//db.update(" MTP_INSPECCIONMAQUINA_DET", con,"c_compania='" + Util.compania + "' and n_correlativo='" + c_cabecera + "' and n_linea='" + lista.get(i).n_linea + "'", null);
					
					Log.d("vuelta actualizo_M2", lista.get(i).n_linea);
					
				}
				
			}

		}
		
		if (vacio == 2) {
			
			vacio = 3;
			
			ContentValues conc = new ContentValues();
			conc.put("c_compania", Util.compania);
			conc.put("n_correlativo", c_cabecera);
			//conc.put("c_maquina", cb);
			conc.put("c_condicionmaquina", condiciones);
			conc.put("c_comentario", comentariocab);
			conc.put("c_estado", "I");
			conc.put("d_fechaInicioInspeccion", fechainicio);
			conc.put("d_fechaFinInspeccion", fechafin);
			conc.put("c_usuarioInspeccion", us);
			conc.put("c_usuarioenvio", us);
			conc.put("d_fechaenvio", fechafin);
			conc.put("c_ultimousuario", us);
			conc.put("d_ultimafechamodificacion", fechafin);
			conc.put("c_periodoinspeccion", periodos);
			
			db.update(" MTP_INSPECCIONMAQUINA_CAB", conc, "n_correlativo = ?",
					new String[] { c_cabecera });
			
			// db.insert("PENDIENTE_MTP_INSPECCIONMAQUINA_CAB", null,conc);
			
			estado.setText("INGRESADA");
			ffin.setText(fechafin);
			
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
			Editor ed = p.edit();
			ed.putString("data", "si");
			ed.commit();
			
		}
		
		db.close();
		
		if (vacio == 3) {
			new AlertDialog.Builder(Llenar.this)
					.setTitle("LysMobile")
					.setMessage(
							"	Se guardo el reporte localmente ,desea enviar el reporte en este momento?")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// continue with delete
									EnviarReporte(c_cabecera);
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();

		}

	}

	public void GuardarReporte() {
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		DataBase basededatos = new DataBase(this, "DBInspeccion", null, 1);
		SQLiteDatabase db = basededatos.getWritableDatabase();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Llenar.this);
		String us = preferences.getString("nombreuser", "");
		// String cb= preferences.getString("cmaquina", "");
		
		// recuperar fehca tiempo aqui
		Calendar ca = Calendar.getInstance();
		fechafin = df.format(ca.getTime());
		int vacio = 0;
		
		String query = "SELECT n_correlativo from MTP_INSPECCIONMAQUINA_CAB order by cast(n_correlativo as int) DESC limit 1 ";
		Cursor c = db.rawQuery(query, null);
		if (c.moveToFirst()) {
			
			c_cabecera = c.getString(0);
			c_cabecera = String.valueOf(Integer.parseInt(c_cabecera) + 1);
			
		} else {
			
			c_cabecera = "1";
			
		}
		
		c.close();
		
		// db.close();
		
		cabec = c_cabecera;
		
		/*
		 * if(comentariocab.equals("") ){
		 * 
		 * Toast.makeText(Llenar.this, "Campo comentario requerido",
		 * Toast.LENGTH_SHORT).show(); vacio=1;
		 * 
		 * 
		 * }
		 */
		if (condiciones.equals("NULL")) {
			
			Toast.makeText(Llenar.this, "Seleccione la condicion",Toast.LENGTH_SHORT).show();
			c_cabecera = "0";
			vacio = 1;
			
		}
		if (periodos.equals("NULL")) {
			
			Toast.makeText(Llenar.this, "Seleccione el periodo",Toast.LENGTH_SHORT).show();
			c_cabecera = "0";
			vacio = 1;
			
		}
		
		for (int i = 0; i < lista.size(); i++) {
			
			if (lista.get(i).n_porcentajeminimo.substring(0, 1).equals("0")
					&& lista.get(i).n_porcentajemaximo.substring(0, 1).equals("0")) {
				
				if (lista.get(i).c_estado == null
						|| lista.get(i).c_estado.length() == 0) {

					vacio = 1;
					Toast.makeText(Llenar.this,"Hay campos de estado que no ha llenado",Toast.LENGTH_SHORT).show();
					c_cabecera = "0";
					break;
					
				}
				
			} else {
				
				if (lista.get(i).n_porcentajeinspeccion == null
						|| lista.get(i).n_porcentajeinspeccion.length() == 0) {
					
					vacio = 1;
					Toast.makeText(Llenar.this,"Hay campos de porcentaje que no ha llenado",Toast.LENGTH_SHORT).show();
					c_cabecera = "0";
					break;
					
				}
				
			}
			
		}
		// significa que esta lleno arriba
		
		if (vacio == 0) {
			
			for (int i = 0; i < lista.size(); i++) {
				
				if (lista.get(i).n_porcentajeminimo.substring(0, 1).equals("0")
						&& lista.get(i).n_porcentajemaximo.substring(0, 1).equals("0")) {
					
					// significa que todo arriba y abajo lleno
					vacio = 2;
					
					ContentValues con = new ContentValues();
					con.put("c_compania", Util.compania);
					con.put("n_correlativo", c_cabecera);
					if (lista.get(i).n_linea.length() == 1) {
						con.put("n_linea", "0" + lista.get(i).n_linea);
					} else {
						con.put("n_linea", lista.get(i).n_linea);
					}					
					con.put("c_inspeccion", lista.get(i).c_inspeccion);
					con.put("c_tipoinspeccion", lista.get(i).c_tipoinspeccion);
					con.put("n_porcentajeinspeccion",lista.get(i).n_porcentajeinspeccion);
					con.put("n_porcentajeminimo",lista.get(i).n_porcentajeminimo);
					con.put("n_porcentajemaximo",lista.get(i).n_porcentajemaximo);
					con.put("c_estado", lista.get(i).c_estado);
					con.put("c_comentario", lista.get(i).c_comentario);
					//con.put("c_rutafoto", lista.get(i).c_rutafoto);
					if (lista.get(i).c_rutafoto==null || lista.get(i).c_rutafoto.equals("null")){
						con.put("c_rutafoto","");
					}else{
						con.put("c_rutafoto",lista.get(i).c_rutafoto);
					}
					//Log.e("c_rutafoto_G1", lista.get(i).c_rutafoto);
					con.put("c_ultimousuario", us);
					con.put("d_ultimafechamodificacion", fechafin);
					
					db.insert("MTP_INSPECCIONMAQUINA_DET", null, con);
					
					Log.d("vuelta ctualzo", lista.get(i).n_linea);
					
				} else {
					
					// significa que todo arriba y abajo lleno
					vacio = 2;
					
					ContentValues con = new ContentValues();
					con.put("c_compania", Util.compania);
					con.put("n_correlativo", c_cabecera);
					//con.put("n_linea", lista.get(i).n_linea);
					if (lista.get(i).n_linea.length() == 1) {
						con.put("n_linea", "0" + lista.get(i).n_linea);
					} else {
						con.put("n_linea", lista.get(i).n_linea);
					}
					con.put("c_inspeccion", lista.get(i).c_inspeccion);
					con.put("c_tipoinspeccion", lista.get(i).c_tipoinspeccion);
					con.put("n_porcentajeinspeccion",lista.get(i).n_porcentajeinspeccion);
					con.put("n_porcentajeminimo",lista.get(i).n_porcentajeminimo);
					con.put("n_porcentajemaximo",lista.get(i).n_porcentajemaximo);
					con.put("c_estado", lista.get(i).c_estado);
					con.put("c_comentario", lista.get(i).c_comentario);
					//con.put("c_rutafoto", lista.get(i).c_rutafoto);
					if (lista.get(i).c_rutafoto==null || lista.get(i).c_rutafoto.equals("null")){
						con.put("c_rutafoto","");
					}else{
						con.put("c_rutafoto",lista.get(i).c_rutafoto);
					}
					//Log.e("c_rutafoto_G2", lista.get(i).c_rutafoto);
					con.put("c_ultimousuario", us);
					con.put("d_ultimafechamodificacion", fechafin);
					
					db.insert("MTP_INSPECCIONMAQUINA_DET", null, con);
					
					Log.d("vuelta ctualzo", lista.get(i).n_linea);
					
				}
				
			}
			
		}
		
		if (vacio == 2) {
			
			vacio = 3;
			
			ContentValues conc = new ContentValues();
			conc.put("c_compania", Util.compania);
			conc.put("n_correlativo", c_cabecera);
			conc.put("c_maquina", cb);
			conc.put("c_condicionmaquina", condiciones);
			conc.put("c_comentario", comentariocab);
			conc.put("c_estado", "I");
			conc.put("d_fechaInicioInspeccion", fechainicio);
			conc.put("d_fechaFinInspeccion", fechafin);
			conc.put("c_usuarioInspeccion", us);
			conc.put("c_usuarioenvio", us);
			conc.put("d_fechaenvio", fechafin);
			conc.put("c_ultimousuario", us);
			conc.put("d_ultimafechamodificacion", fechafin);
			conc.put("c_periodoinspeccion", periodos);
			
			db.insert("MTP_INSPECCIONMAQUINA_CAB", null, conc);
			// db.insert("PENDIENTE_MTP_INSPECCIONMAQUINA_CAB", null,conc);
			
			estado.setText("INGRESADA");
			ffin.setText(fechafin);
			
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
			Editor ed = p.edit();
			ed.putString("data", "si");
			ed.commit();
			
		}
		
		db.close();
		
		if (vacio == 3) {

			new AlertDialog.Builder(Llenar.this)
					.setTitle("LysMobile")
					.setMessage(
							"	Se guardo el reporte localmente, desea enviar el reporte en este momento?")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// continue with delete

									EnviarReporte(c_cabecera);
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();

		}

	}

	public void EnviarReporte(String correlativo) {
		int resp = 0; // Sin datos enviados
		String imagen;
		boolean existe = false;
		pasar = true;
		if (isOnline()) {
			Log.e("entro aqui online","1");
			ArrayList<String> fotoslistain = new ArrayList<String>();
			
			/*Intent intent = new Intent(Llenar.this, UploadService.class);
			intent.putStringArrayListExtra("lista", fotoslistain);
			startService(intent);*/
			
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Llenar.this);
			String us = preferences.getString("nombreuser", "");

			final EnviarReporteInspeccionAsyncTask enviarinspeccion = new EnviarReporteInspeccionAsyncTask(
					this, correlativo, cabec, Util.compania, us, cb,
					condiciones, comentariocab, "E", periodos, fechainicio,
					fechafin, lista);
				enviarinspeccion.execute(new String[] { Util.url + "registrarInspeccion" });

			Thread thread = new Thread() {
				public void run() {
					try {
						enviarinspeccion.get(50000, TimeUnit.MILLISECONDS);
						// tarea.get(30000, TimeUnit.MILLISECONDS);

					} catch (Exception e) {
						enviarinspeccion.cancel(true);
						((Activity) Llenar.this).runOnUiThread(new Runnable() {

							public void run() {

								enviarinspeccion.progressDialog.dismiss();
								pasar = false;
								Toast.makeText(Llenar.this,
										"No se pudo establecer comunicacion .",
										Toast.LENGTH_LONG).show();
								
							}
						});
					}
				}
			};
			thread.start();
			
		} else {
			pasar = false;
			Toast.makeText(Llenar.this, "No hay acceso a internet .",
					Toast.LENGTH_LONG).show();
		}
		
		if (pasar == true) {
			for (int i = 0; i < lista.size(); i++) {
				Log.e("entro aqui online for 2",String.valueOf(lista.size()));
				//fotoslistain.add(lista.get(i).c_rutafoto);
				//!lista.get(i).c_rutafoto.equals("") && 
				if (lista.get(i).c_rutafoto != null) {
					imagen = Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
							"/" + lista.get(i).c_rutafoto;
					existe = true;
					break;
				}
			}
			Log.e("entro aqui online for 2",String.valueOf(existe));
			if (existe == true)
				new AsyncSender().execute();
		}
		
	}
	/*
	 * public void RecuperarReporte(Button v){
	 * 
	 * 
	 * 
	 * SharedPreferences
	 * preferences=PreferenceManager.getDefaultSharedPreferences(Llenar.this);
	 * String codigo= preferences.getString("codbarra", "");
	 * 
	 * DataBase basededatos = new DataBase (this, "DBInspeccion", null, 1);
	 * SQLiteDatabase db = basededatos.getWritableDatabase();
	 * 
	 * String query =
	 * "SELECT idInspeccion ,codMaquina, fecha, detalle, codTabla,fechaHoraInspeccion,referenciaMinima,referenciaMaxima,notaInspeccion,estado,campoComentario,campoRutaFoto,cabecera,periocidad,condicionMaquina,idDetalleInspeccion,Systema from Inspeccion where codMaquina='"
	 * +codigo+"'"; Cursor c = db.rawQuery(query,null); if( c.moveToFirst()) {
	 * 
	 * do{ // LastId = c.getString(0); InspeccionData ins=new InspeccionData();
	 * 
	 * 
	 * ins.idInspeccion= c.getString(0); ins.codMaquina= c.getString(1);
	 * ins.fecha= c.getString(2); ins.detalle= c.getString(3); ins.codTabla=
	 * c.getString(4); ins.fechaHoraInspeccion= c.getString(5);
	 * ins.referenciaMinima= c.getString(6); ins.referenciaMaxima=
	 * c.getString(7); ins.notaInspeccion= c.getString(8); ins.estado=
	 * c.getString(9); ins.campoComentario= c.getString(10); ins.campoRutaFoto=
	 * c.getString(11); ins.cabecera= c.getString(12); ins.periocidad=
	 * c.getString(13); ins.condicionMaquina= c.getString(14);
	 * ins.idDetalleInspeccion= c.getString(15); ins.Systema= c.getString(16);
	 * 
	 * lista.add(ins); }while(c.moveToNext());
	 * 
	 * 
	 * 
	 * 
	 * 
	 * }else{
	 * 
	 * Toast.makeText(this, "No hay data", Toast.LENGTH_SHORT).show(); }
	 * c.close(); db.close();
	 * 
	 * 
	 * 
	 * if(lista.size()>0){
	 * 
	 * 
	 * TextView user= ( TextView) findViewById(R.id.tvinllusuario); TextView
	 * nivel= ( TextView) findViewById(R.id.tnivel); TextView condicion= (
	 * TextView) findViewById(R.id.tcondi); TextView maquina= ( TextView)
	 * findViewById(R.id.tmaquina); TextView fecha= ( TextView)
	 * findViewById(R.id.tfecha); TextView periodo= ( TextView)
	 * findViewById(R.id.tperiodo);
	 * 
	 * // SharedPreferences
	 * preferences=PreferenceManager.getDefaultSharedPreferences(Llenar.this);
	 * String us= preferences.getString("nombreuser", ""); String maqui=
	 * preferences.getString("maquina", ""); String nive=
	 * preferences.getString("nivel", ""); String condi=
	 * preferences.getString("condicion", "");
	 * 
	 * 
	 * user.setText(us); nivel.setText(nive); condicion.setText(condi);
	 * maquina.setText(maqui); if(!(lista.get(0).fecha==null))
	 * fecha.setText(lista.get(0).fecha); if(!(lista.get(0).periocidad==null))
	 * periodo.setText(lista.get(0).periocidad);
	 * 
	 * 
	 * listView.setAdapter(new UserItemAdapter(getApplicationContext(),
	 * android.R.layout.simple_list_item_1,lista)); }
	 * //v.setVisibility(View.INVISIBLE); }
	 */
	
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
			//if (!lista.get(i).c_rutafoto.equals("") && (lista.get(i).c_rutafoto != null)) {
			if (lista.get(i).c_rutafoto != null) {
				imagen = Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
						"/" + lista.get(i).c_rutafoto;
				
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
	
	public void BorrarFile(String files) {
		File f = new File(files);
		f.delete();
		f = null;
		//seborroarchivo = true;
	}
	
	public void EventoTomarFoto(View view) {
		String state = Environment.getExternalStorageState();
		Log.e("state",state);
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, REQUEST_CAMERA);
		}
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
        
		/*String thumbnailPath = "", largeImagePath = "", sort = "", nuevocodigo = "";
		File fileNew, fileGaleria, fileThumbail;

		// Imagen de Galeria
		String[] largeFileProjection = { BaseColumns._ID, MediaColumns.DATA };
		sort = BaseColumns._ID + " DESC";
		Cursor myCursor=null;
		
		try {
			Log.e("mi rutse",String.valueOf(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
			
			Log.e("mi amagen 1","mi imagen 1");
			myCursor = this.managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					largeFileProjection, null, null, sort);
			
			Log.e("mi amagen 1.1a","mi imagen 1.1.a");
			if(myCursor==null){
				Log.e("mi amagen 1.1ax","mi imagen 1.1.ax");
			}
			int x2 = myCursor.getCount();
			Log.e("tamao curos",String.valueOf(x2));
			
			
			myCursor.moveToFirst();
			Log.e("mi amagen 1.1b","mi imagen 1.1.b");
			largeImagePath = myCursor.getString(myCursor
					.getColumnIndexOrThrow(MediaColumns.DATA));
			Log.e("mi amagen 1.1c","mi imagen 1.1c");
		} catch (Exception e){
			Log.e("mi amagen 2","mi imagen 2");
			e.printStackTrace();
		} finally {
			myCursor.close();
			Log.e("mi amagen 3","mi imagen 3");
		}
		Log.e("mi amagen 4","mi imagen 4");
		Log.e("mi amagen",largeImagePath);
		if(largeImagePath.length()>0){
			fileGaleria = new File(largeImagePath);
			fileGaleria.delete();
			fileGaleria = null;
			
			// Genera nueva imagen
			try {
	
				nuevocodigo = getGenerarCodigoImagen();
				Log.e("Paso aki 3 nuevocodigo","nuevocodigo");
				fileNew = new File(nuevocodigo);
				Uri outputFileUri = Uri.fromFile(fileNew);
	
				OutputStream outstream = getContentResolver().openOutputStream(
						outputFileUri);
				x.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
				outstream.close();
				outstream = null;
				fileNew = null;
				outputFileUri = null;
				
				EventoUltimaFoto(nuevocodigo);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}
	
	private String getGenerarCodigoImagen() {
		//String codigo = lblCodigo.getText().toString().toLowerCase();
		String nombre = cb;
		int max = 0;
		String valnum = "0";
		String codmaquina = cb;//app.getCodigoVendedor();//Cdigo Mquina
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

		/*if (f.exists()) {
			File[] files = f.listFiles();
			for (File x : files) {
				String archivo = x.getName().toLowerCase();
				Log.e("archivo 1 antes if",archivo);
				if (archivo.startsWith(iniciacon)
						&& archivo.endsWith(terminacon)) {
					valnum = archivo.substring(archivo.indexOf("-") + 1,
							archivo.lastIndexOf("_"));
					Log.e("archivo 2 dentro if",archivo);
					Log.e("iniciacon",iniciacon);
					Log.e("terminacon",terminacon);
					Log.e("valnum",valnum);
					if (max < Integer.parseInt(valnum))
						max = Integer.parseInt(valnum);
					Log.e("max",String.valueOf(max));
				}
			}

			files = null;
			Log.e("files",String.valueOf(files));
		}

		f = null;*/
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
	
	/**
	 * Redimensionar un Bitmap. By TutorialAndroid.com
	* @param Bitmap mBitmap
	* @param float newHeight
	* @param float newHeight
	 * @param float newHeight
	 * @return Bitmap
	 */
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

    //private Bitmap redimensionarImagenMaximo3(Bitmap bm, float scale) {

    /*    float aspect = bm.getWidth() / bm.getHeight();

        int scaleWidth = (int) (bm.getWidth() * scale);
        int scaleHeight = (int) (bm.getHeight() * scale);
*/
        // original image is 720x402 and SampleSize=4 produces 180x102, which is
        // still too large

        /*BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 4;

        return BitmapFactory.decodeResource(getResources(), R.drawable.a000001570402, bfo);
    }*/
	
}