package com.lys.mobile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.androidquery.AQuery;
import com.lys.mobile.data.DetalleProgramaEjecutado;
import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.data.ProgramaEjecutadoData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.Dialogos;
import com.lys.mobile.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.*;
import android.webkit.WebSettings.PluginState;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class DetalleEjecutado2 extends Activity {
	public ArrayList<DetalleProgramaEjecutado> lista;
	int posicion;
	AQuery aq;
	MyApp app;
	String rutafoto = "", nombrefoto = "";
	//Context contexto;
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
		app = ((MyApp) getApplicationContext());
		lista = new ArrayList<DetalleProgramaEjecutado>();
		aq = new AQuery(getApplicationContext());// imageView1"
		// recupero valor y select y escribo
		
		//contexto = this;
		
		/*TextView user = (TextView) findViewById(R.id.tvinllusuariod);
		TextView condicion = (TextView) findViewById(R.id.tcondid);
		TextView maquina = (TextView) findViewById(R.id.tmaquinad);
		TextView descripcion = (TextView) findViewById(R.id.tdescripciond);
		TextView estado = (TextView) findViewById(R.id.testadocad);
		TextView finicio = (TextView) findViewById(R.id.tfind);
		TextView ffin = (TextView) findViewById(R.id.tffind);
		TextView periodo = (TextView) findViewById(R.id.tperiodod);// tfin tffin
		Button com = (Button) findViewById(R.id.tcomentind);
		Button atras = (Button) findViewById(R.id.sendd);
		ListView listView = (ListView) findViewById(R.id.listainspecciond);*/
		EnlazarControles();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			String correla = extras.getString("correlativo");//
			String compa = extras.getString("compania");//

			if (lista.size() > 0)
				lista.clear();
			DataBase basededatos = new DataBase(DetalleEjecutado2.this,"DBInspeccion", null, 1);
			SQLiteDatabase db = basededatos.getWritableDatabase();

			String query = "SELECT c.c_compania,c.n_correlativo,c.c_maquina,c.c_desMaquina, " + 
			"c.c_condicionmaquina, c.c_comentario, c.c_estado,c.d_fechaInicioInspeccion, " + 
			"c.d_fechaFinInspeccion,c.c_periodoinspeccion, c.c_desPeriodoInspeccion, " + 
			"c.c_usuarioInspeccion,c.n_personainspeccion,c.c_nombreinspeccion,c.c_generoOT, " + 
			"c.n_numeroOT,d.c_compania,d.n_correlativo,d.n_linea,d.c_inspeccion,d.c_desInpeccion, " + 
			"d.c_tipoinspeccion,d.n_porcentajeminimo,d.n_porcentajemaximo,d.n_porcentajeinspeccion, " + 
			"d.c_estado,d.c_comentario,d.c_rutafoto from MTP_INSPECCIONEJE_CAB c,MTP_INSPECCIONEJE_DET d " + 
			"where d.n_correlativo=c.n_correlativo and d.c_compania=c.c_compania and c.c_compania='"
			+ compa + "' and c.n_correlativo='" + correla+"' order by d.c_compania,d.n_correlativo,d.n_linea";
			Log.d("query", "" + query);
			Cursor c = db.rawQuery(query, null);
			if (c.moveToFirst()) {

				do {

					DetalleProgramaEjecutado p = new DetalleProgramaEjecutado();

					p.c_companiac = c.getString(0);
					p.n_correlativoc = c.getString(1);
					p.c_maquinac = c.getString(2);
					p.c_desMaquinac = c.getString(3);
					p.c_condicionmaquinac = c.getString(4);
					p.c_comentarioc = c.getString(5);
					p.c_estadoc = c.getString(6);
					p.d_fechaInicioInspeccionc = c.getString(7);
					p.d_fechaFinInspeccionc = c.getString(8);
					p.c_periodoinspeccionc = c.getString(9);
					p.c_desPeriodoInspeccionc = c.getString(10);
					p.c_usuarioInspeccionc = c.getString(11);
					p.n_personainspeccionc = c.getString(12);
					p.c_nombreinspeccionc = c.getString(13);
					p.c_generoOTc = c.getString(14);
					p.n_numeroOTc = c.getString(15);

					p.c_companiad = c.getString(16);
					p.n_correlativod = c.getString(17);
					p.n_linead = c.getString(18);
					p.c_inspecciond = c.getString(19);
					p.c_desInpecciond = c.getString(20);
					p.c_tipoinspecciond = c.getString(21);
					p.n_porcentajeminimod = c.getString(22);
					p.n_porcentajemaximod = c.getString(23);
					p.n_porcentajeinspecciond = c.getString(24);
					p.c_estadod = c.getString(25);
					p.c_comentariod = c.getString(26);
					p.c_rutafotod = c.getString(27);
					
					//Log.e("L�nea/% Insp. Objeto/% Insp. Cursor",c.getString(18) + "/" + p.n_porcentajeinspecciond + "/" + c.getString(24));

					lista.add(p);

				} while (c.moveToNext());
				
				lblInspector.setText("" + lista.get(0).c_usuarioInspeccionc);
				if (lista.get(0).c_condicionmaquinac.equals("A")) {
					cboCondicionMaquina.setText("ABIERTA");
				}
				if (lista.get(0).c_condicionmaquinac.equals("C")) {
					cboCondicionMaquina.setText("CERRADA");
				}
				lblMaquina.setText("" + lista.get(0).c_maquinac);
				lblDescripcionMaquina.setText("" + lista.get(0).c_desMaquinac);
				
				if (lista.get(0).c_estadoc.equalsIgnoreCase("AP")) {
					cboEstado.setText("APROBADO");
				}				
				
				lblFechaInicio.setText("" + lista.get(0).d_fechaInicioInspeccionc);
				lblFechaFin.setText("" + lista.get(0).d_fechaFinInspeccionc);
				cboPeriodo.setText("" + lista.get(0).c_desPeriodoInspeccionc);
				lblComentario.setText("" + lista.get(0).c_comentarioc);

				btnComentario.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						final Dialog alertDialog = new Dialog(DetalleEjecutado2.this);

						alertDialog.setContentView(R.layout.dialogo_comentario);
						alertDialog.setTitle("Comentario");
						alertDialog.setCancelable(false);
						final EditText text = (EditText) alertDialog.findViewById(R.id.ecomentario);
						text.setEnabled(false);
						text.setText(lista.get(0).c_comentarioc);

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

				listaInspecciones.setAdapter(new UserItemAdapter(getApplicationContext(),android.R.layout.simple_list_item_1, lista));

			} else {

				Toast.makeText(DetalleEjecutado2.this,"No hay informaci�n.", Toast.LENGTH_SHORT).show();

				Log.d("informacion", "no hay");

			}
			c.close();
			db.close();

		}

		/*atras.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DetalleEjecutado2.this.finish();

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
			/*if(cboEstado.getText().equals("INGRESADA"))
			{

				}
			} else {*/
				this.finish();
			//}
		}
		
	}
	
	public class UserItemAdapter extends ArrayAdapter<DetalleProgramaEjecutado> {
		private ArrayList<DetalleProgramaEjecutado> listcontact;

		public UserItemAdapter(Context context, int textViewResourceId,	ArrayList<DetalleProgramaEjecutado> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}
		@Override
		public boolean isEnabled(int position) {
			return false;
		}
		
		public View getView(final int position, View convertView, final ViewGroup parent) {

			//View v = convertView;
			View v = null;

			final DetalleProgramaEjecutado user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				if (user.n_porcentajeminimod.substring(0, 1).equals("0")
						&& user.n_porcentajemaximod.substring(0, 1).equals("0")) {// user.n_porcentajeminimo
																				  // y
																				  // user.n_porcentajemaximo
																				  // =0
					Log.d("aq", "aqui1");
					Log.d("valorr", user.n_porcentajeminimod.substring(0, 1));
					//v = vi.inflate(R.layout.con_detalle_inspeccion_se, null);
					v = vi.inflate(R.layout.det_detalle_inspeccion_se, null);

				} else {
					Log.d("aq", "aqui2");
					Log.d("valorr", user.n_porcentajeminimod.substring(0, 1));
					//v = vi.inflate(R.layout.con_detalleinspeccion_sm, null);
					v = vi.inflate(R.layout.det_detalleinspeccion_sm, null);

				}

			}

			if (user != null) {

				if (user.n_porcentajeminimod.substring(0, 1).equals("0")
						&& user.n_porcentajemaximod.substring(0, 1).equals("0")) {// user.n_porcentajeminimo
																				  // y
																				  // user.n_porcentajemaximo
																				  // =0
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

					if (numero != null) {
						numero.setText("" + user.n_linead);
						//user.n_linead = (String.valueOf(position + 1));
					}
					if (detalle != null) {
						detalle.setText("" + user.c_desInpecciond);
					}
					if (systema != null) {
						if (user.c_tipoinspecciond.equals("M")) {
							systema.setText("MECANICA");
						} 
						if (user.c_tipoinspecciond.equals("E")) {
							systema.setText("ELECTRICA");
						}
					}
					if (campo != null) {
						//Log.e("L�nea;% Insp.",user.n_linead + ";" + user.n_porcentajeinspecciond);
						campo.setText("" + user.n_porcentajeinspecciond);
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
						// if(user.c_rutafoto.equals("") ||
						// !(user.c_rutafoto==null))
						// imagen.setText(user.c_rutafoto);
					}

					List<String> listacond = new ArrayList<String>();
					if (user.c_estadod.equals("O")) {
						listacond.add("OK");
					} 
					if (user.c_estadod.equals("F")) {
						listacond.add("FALLA");
					}
					
					//listacond.add("OK");
					//listacond.add("FALLA");

					ArrayAdapter<String> dataAdapterCon = new ArrayAdapter<String>(
							DetalleEjecutado2.this,	android.R.layout.simple_spinner_item, listacond);

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
					});
					
					//ok.setSelection(0);
					ok.setEnabled(false);
					
					comentario.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							final Dialog alertDialog = new Dialog(DetalleEjecutado2.this);

							alertDialog.setContentView(R.layout.dialogo_comentario);
							alertDialog.setTitle("Comentario");
							alertDialog.setCancelable(false);
							final EditText text = (EditText) alertDialog.findViewById(R.id.ecomentario);
							text.setText("" + user.c_comentariod);
							text.setEnabled(false);

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
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
					
					imagen.setText("" + user.c_rutafotod);
					imagen.setOnClickListener(new OnClickListener() {

						@SuppressWarnings("deprecation")
						@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							// abre camara
							// sonar

							posicion = position;

							// motrar la foto

							final Dialog alertDialog = new Dialog(DetalleEjecutado2.this);
							alertDialog.setContentView(R.layout.dialogo_foto_2);
														
							final WebView webDescarga = (WebView) alertDialog.findViewById(R.id.webDescarga);
							final ProgressBar barraProgreso = (ProgressBar) alertDialog.findViewById(R.id.barraProgreso);
							
							alertDialog.setTitle("Visualizar Foto");
							alertDialog.setCancelable(false);
							alertDialog.getWindow().setLayout(
									(int) (DetalleEjecutado2.this.getWindow().peekDecorView()
											.getWidth() * 0.95),
									(int) (DetalleEjecutado2.this.getWindow().peekDecorView()
											.getHeight() * 0.85));
							
							rutafoto = user.c_rutafotod;
							Log.e("rutafoto",rutafoto);
							
							//String html = app.getUrlFotoInspeccion() + rutafoto;
														
							webDescarga.getSettings().setJavaScriptEnabled(true);
							webDescarga.getSettings().setBuiltInZoomControls(true);
							webDescarga.getSettings().setPluginState(PluginState.ON);
							//webDescarga.getSettings().setPluginsEnabled(true);
							webDescarga.clearCache(true);
							webDescarga.setWebChromeClient(new WebChromeClient() {
								@Override
								public void onProgressChanged(WebView view, int progress) {
									barraProgreso.setProgress(0);
									barraProgreso.setVisibility(View.VISIBLE);
									DetalleEjecutado2.this.setProgress(progress * 1000);
									barraProgreso.incrementProgressBy(progress);
									if (progress == 100) {
										barraProgreso.setVisibility(View.GONE);
									}
								}
							});
							rutafoto = user.c_rutafotod;
							Log.e("rutafoto",rutafoto);
							//CargarFoto();
							
							String html = app.getUrlFotoInspeccion() + rutafoto; //+ ".jpg";
							Log.e("html",html);
							//try {
								//if (funciones.verificaConexionInternet(contexto) == true) {
									webDescarga.loadUrl(html);
									//webDescarga.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
									webDescarga.setInitialScale((int) (1 * webDescarga.getScale()));
									/*webDescarga.setInitialScale(75);
									webDescarga.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);*/
									webDescarga.getSettings().setUseWideViewPort(true);
								//}
							/*} catch (Exception ex) {
								Log.e("error", ex.getMessage());
							}*/
							
							/*final ImageView ima = (ImageView) alertDialog
									.findViewById(R.id.image);*/
							// text.setText(""+user.c_rutafotod);

							/*try {

								aq.id(ima).image(user.c_rutafotod, true, true,
										400, 0, null, 0, 0.0f);

							} catch (Exception e) {

								ima.setImageDrawable(getResources()
										.getDrawable(R.drawable.sin));
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

				} else  {

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
					//llenar.setEnabled(false);

					if (numero != null) {
						//numero.setText(String.valueOf(position + 1));
						//user.n_linead = (String.valueOf(position + 1));
						numero.setText("" + user.n_linead);
					}
					if (detalle != null) {
						detalle.setText("" + user.c_desInpecciond);
					}
					if (systema != null) {
						if (user.c_tipoinspecciond.equals("M")) {
							systema.setText("MEC�NICA");
						} 
						if (user.c_tipoinspecciond.equals("E")) {
							systema.setText("EL�CTRICA");
						}
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

						String stringmi = user.n_porcentajeinspecciond;

						String Str1 = new String(stringmi);

						if (Str1.substring(0, 3).equals("100")) {

							user.n_porcentajeinspecciond = Str1.substring(0, 6);
						} else {

							user.n_porcentajeinspecciond = Str1.substring(0, 5);

						}

						llenar.setText("" + user.n_porcentajeinspecciond);
					}

					if (ok != null) {
						if (user.c_estadod.equals("O")) {
							ok.setText("OK");
						}
						if (user.c_estadod.equals("F")){
							ok.setText("FALLA");
						}
					}
					if (comentario != null) {
						// if(user.c_comentario.equals("") ||
						// !(user.c_comentario==null))
						comentario.setText(user.c_comentariod);
					}
					if (imagen != null) {
						// if(user.c_rutafoto.equals("") ||
						// !(user.c_rutafoto==null))
						// imagen.setText(user.c_rutafoto);
					}

					comentario.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							final Dialog alertDialog = new Dialog(DetalleEjecutado2.this);

							alertDialog.setContentView(R.layout.dialogo_comentario);
							alertDialog.setTitle("Comentario");
							alertDialog.setCancelable(false);
							final EditText text = (EditText) alertDialog.findViewById(R.id.ecomentario);
							text.setText("" + user.c_comentariod);
							text.setEnabled(false);

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
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
					imagen.setText("" + user.c_rutafotod);
					imagen.setOnClickListener(new OnClickListener() {
						
						@SuppressWarnings("deprecation")
						@SuppressLint("SetJavaScriptEnabled")
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							// abre camara
							// sonar

							final Dialog alertDialog = new Dialog(DetalleEjecutado2.this);

							alertDialog.setContentView(R.layout.dialogo_foto_2);
							alertDialog.setTitle("Visualizar Foto");
							//alertDialog.setCancelable(false);
							
							final WebView webDescarga = (WebView) alertDialog.findViewById(R.id.webDescarga);
							final ProgressBar barraProgreso = (ProgressBar) alertDialog.findViewById(R.id.barraProgreso);
							
							alertDialog.getWindow().setLayout(
									(int) (DetalleEjecutado2.this.getWindow().peekDecorView()
											.getWidth() * 0.95),
									(int) (DetalleEjecutado2.this.getWindow().peekDecorView()
											.getHeight() * 0.85));
							
							webDescarga.getSettings().setJavaScriptEnabled(true);
							webDescarga.getSettings().setBuiltInZoomControls(true);
							webDescarga.getSettings().setPluginsEnabled(true);
							webDescarga.clearCache(true);
							webDescarga.setWebChromeClient(new WebChromeClient() {
								@Override
								public void onProgressChanged(WebView view, int progress) {
									barraProgreso.setProgress(0);
									barraProgreso.setVisibility(View.VISIBLE);
									DetalleEjecutado2.this.setProgress(progress * 1000);
									barraProgreso.incrementProgressBy(progress);
									if (progress == 100) {
										barraProgreso.setVisibility(View.GONE);
									}
								}
							});
							rutafoto = user.c_rutafotod;
							//CargarFoto();
							
							String html = app.getUrlFotoInspeccion() + rutafoto; //+ ".jpg";
							Log.e("html",html);
							//try {
								//if (funciones.verificaConexionInternet(contexto) == true) {
									webDescarga.loadUrl(html);
									//webDescarga.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
									webDescarga.setInitialScale((int) (1 * webDescarga.getScale()));
									/*webDescarga.setInitialScale(75);
									webDescarga.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);*/
									webDescarga.getSettings().setUseWideViewPort(true);
								//}
							/*} catch (Exception ex) {
								Log.e("error", ex.getMessage());
							}*/
							
							/*final ImageView ima = (ImageView) alertDialog
									.findViewById(R.id.image);*/
							// text.setText(""+user.c_rutafotod);

							/*try {

								aq.id(ima).image(user.c_rutafotod, true, true,
										400, 0, null, 0, 0.0f);

							} catch (Exception e) {

								ima.setImageDrawable(getResources()
										.getDrawable(R.drawable.sin));
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
				
			}

			return v;

		}

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		DetalleEjecutado2.this.finish();
	}

}
