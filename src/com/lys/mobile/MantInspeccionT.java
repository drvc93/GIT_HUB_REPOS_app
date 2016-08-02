package com.lys.mobile;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.lys.mobile.asynctask.EnviarReporteInspeccionAsyncTask;
import com.lys.mobile.asynctask.GuardarImagenTask;
import com.lys.mobile.data.*;
import com.lys.mobile.util.*;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import org.apache.commons.io.FileUtils;

public class MantInspeccionT extends Activity {
	//ArrayList<detalleinspecciontent> detalleinspecciont = new ArrayList<detalleinspecciontent>();
	//detalleinspecciontent EDetalle;
	public static int count = 0, posicion = 0;
	public String comentariocab = "", condiciones = "NULL", 
			      filepath = "NULL", periodos = "NULL", cb = "";
	private static final String BS_PACKAGE = "com.google.zxing.client.android";
	public static final int REQUEST_CODE = 0x0000c0de;
	static String contents = "",mensaje = "";
	boolean enviadoServCorrecto = false, mResult = false;
	LinearLayout tabCondicionInspeccion,tabDetalle,
				 tabResumen;
	TextView lblMaquina,lblDescripcionMaquina,
			 lblFechaInicio,lblFechaFin,lblInspector,
			 lblNombreInspector,lblComentario,cboEstado;
	Button btnMaquina,btnComentario;
	Spinner cboCondicionMaquina,cboPeriodo;
	ListView listaInspecciones;
	
	TextView lblTab03_NumeroInspeccion,lblTab03_Maquina,
			 lblTab03_CondicionMaquina,lblTab03_Estado,
			 lblTab03_FechaInicio,lblTab03_FechaFin,
			 lblTab03_Inspector,lblTab03_Comentario;
	
	String GrabaInspeccion = "V",codCompania = "",codInspeccion = "0",comInspeccion = "";
	Button btnSiguiente;	
	Context contexto;
	String c_cabecera = "0", c_detalle = "1", cabec = "NULL";
	MyApp app;
	
	List<String> listacondn;
	ArrayAdapter<String> dataAdapterConn;
	private static final int REQUEST_CAMERA = 1;
	String errores = "";
	public ArrayList<InspeccionData> lista;
	public ArrayList<ProgramaData> listaperiodo;
	//appglobal app;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mantinspecciont);
		//app = ((appglobal) getApplicationContext());
		app = ((MyApp)getApplicationContext());
		lista = new ArrayList<InspeccionData>();
		listaperiodo = new ArrayList<ProgramaData>();
		if (lista.size() > 0) {
			lista.clear();
		}
		
		contexto = this;
		
		listacondn = new ArrayList<String>();
		
		// recuperar fecha y tiempo aqui
		
		listacondn.add("--SELECT--");
		listacondn.add("OK");
		listacondn.add("FALLA");
		
		dataAdapterConn = new ArrayAdapter<String>(MantInspeccionT.this, android.R.layout.simple_spinner_item, listacondn);
		
		dataAdapterConn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		codCompania = "00100000";
		codInspeccion = getIntent().getExtras().getString("codInspeccion");
		GrabaInspeccion = getIntent().getExtras().getString("GrabaInspeccion");
		
		EnlazarControles();
		
		loadCondicionesInspeccionT();
		
		cboCondicionMaquina.setOnItemSelectedListener(new OnItemSelectedListener() {
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
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		/*btnMaquina.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intentScan = new Intent(BS_PACKAGE + ".SCAN");
				intentScan.putExtra("PROMPT_MESSAGE", "Enfoque entre 9 y 11 cm. apuntado el codigo de barras de la maquina");
				startActivityForResult(intentScan, REQUEST_CODE);
			}
		});*/
		
		btnComentario.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog alertDialog = new Dialog(MantInspeccionT.this);

				alertDialog.setContentView(R.layout.dialogo_comentario);
				alertDialog.setTitle("Comentario");
				alertDialog.setCancelable(false);
				final EditText text = (EditText) alertDialog
						.findViewById(R.id.ecomentario);

				Button dialogButton = (Button) alertDialog
						.findViewById(R.id.baceptar);
				text.setText(lblComentario.getText().toString());
				if (GrabaInspeccion.equals("V") || GrabaInspeccion.equals("M"))
				{
					text.setEnabled(false);
					dialogButton.setEnabled(false);
				}
				// if button is clicked, close the custom dialog
				dialogButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						if (text.getText().toString().length() == 0) {

							Toast.makeText(MantInspeccionT.this, "Ingrese Comentario",
									Toast.LENGTH_SHORT).show();
						} else {
							comentariocab = text.getText().toString();
							//btnComentario.setText(comentariocab);
							lblComentario.setText(comentariocab);
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
		
		if (GrabaInspeccion.equals("M")) {
			tabCondicionInspeccion.setVisibility(View.VISIBLE);
			cboCondicionMaquina.setEnabled(false);
			cboEstado.setEnabled(false);
		} else if (GrabaInspeccion.equals("V")) {
			tabCondicionInspeccion.setVisibility(View.GONE);
			tabDetalle.setVisibility(View.GONE);
			//tabResumen.setVisibility(View.VISIBLE);
			cboCondicionMaquina.setEnabled(false);
			cboEstado.setEnabled(false);
		} else if (GrabaInspeccion.equals("N")) {
			tabCondicionInspeccion.setVisibility(View.VISIBLE);
			cboCondicionMaquina.setEnabled(true);
			cboEstado.setEnabled(false);
		}
		
	}
	
	private void EnlazarControles() {
		tabCondicionInspeccion = (LinearLayout)findViewById(R.id.tabInspeccionT01);
		tabDetalle = (LinearLayout)findViewById(R.id.tabInspeccionT02);
		tabResumen = (LinearLayout)findViewById(R.id.tabInspeccionT03);
		
		// Datos del Tab 01: Condicin de Inspecciones T.
		lblMaquina = (TextView)findViewById(R.id.lblMaquina);
		lblDescripcionMaquina = (TextView)findViewById(R.id.lblDescripcionMaquina);
		btnMaquina = (Button)findViewById(R.id.btnMaquina);
		cboCondicionMaquina = (Spinner)findViewById(R.id.cboCondicionMaquina);
		//cboEstado = (Spinner)findViewById(R.id.cboEstado);
		cboEstado = (TextView)findViewById(R.id.lblEstado);
		lblFechaInicio = (TextView)findViewById(R.id.lblFechaInicio); 
		lblFechaFin = (TextView)findViewById(R.id.lblFechaFin);
		lblInspector = (TextView)findViewById(R.id.lblInspector);
		lblNombreInspector = (TextView)findViewById(R.id.lblNombreInspector);
		lblComentario = (TextView)findViewById(R.id.lblComentario);
		btnComentario = (Button)findViewById(R.id.btnComentario);
		
		// Datos del Tab 02: Detalle
		listaInspecciones = (ListView)findViewById(R.id.listaInspecciones);
		cboPeriodo = (Spinner)findViewById(R.id.cboPeriodo);
		
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
		btnSiguiente = (Button)findViewById(R.id.btnSiguiente);		
	}
	
	/*public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				maquinaent objEnt;
				maquinaneg objNeg;
				contents = intent.getStringExtra("SCAN_RESULT");
				objNeg = new maquinaneg(app.getConexion());
				objEnt = objNeg.buscarMaquina(contents.trim());
				if (objEnt != null) {
					lblMaquina.setText(objEnt.getMaquina().trim());
					lblDescripcionMaquina.setText(objEnt.getDescripcion());
				} else {
					Dialogos.DialogToast(contexto,"Mquina no disponible para ese Cdigo de Barras",true);
					return;
				}
			}
		}
	}*/
	
	public void EventoRetroceder(View view) {
		if (GrabaInspeccion.equals("V")) {
			finish();
			return;
		}

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
		} //else if (i == 1) {
			/*if (listaInspecciones.getCount() == 0) {
				Toast.makeText(getApplicationContext(), 
						"Ingrese al menos un item.", Toast.LENGTH_SHORT)
						.show();
				return;
			}*/
			
			//loadCondicionesInspeccionT();
			
			/*if (val == false && GrabaInspeccion.equals("V")) {
				this.finish();
			}*/
			
			/*if (val == true) {
				Log.e("val", "true");
				//btnSiguiente.setText("Sincronizar");
			} else {
				Log.e("val", "false");
				//btnSiguiente.setText("Siguiente");
			}*/
			
			/*tabDetalle.setVisibility((val == true) ? View.GONE
					: View.VISIBLE);
			tabResumen.setVisibility((!val == true) ? View.GONE : View.VISIBLE);*/
			
		/*}*/ else {
			InicioSincronizacion();
		}
		
	}
	
	@Override
	public void onBackPressed() {
		return;
	}
	
	public void InicioSincronizacion() {
		/*if (Dialogos.getYesNoWithExecutionStop("Confirmacin",

			//this.Sincronizar();
		} else {*/
			//Validaciones
			if (lista.size() == 0) {
				Toast.makeText(MantInspeccionT.this, "Lista de Detalles vacía.", Toast.LENGTH_SHORT).show();
			} else {
				if (c_cabecera.equals("0")) {
					Log.e("GuardarReporte: ",c_cabecera);
					GuardarReporte();
				} else {
					Log.e("ModificarReporte: ",c_cabecera);
					ModificarReporte();
				}
			}
		//}
	}
	
	private void GuardarReporte() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		DataBase objDB = new DataBase(this, "DBInspeccion", null, 1);
		_db = objDB.getWritableDatabase();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MantInspeccionT.this);
		String us = preferences.getString("user", "");
		String nous = preferences.getString("nombreuser", "");
		
		// recuperar fecha tiempo aqui
		Calendar ca = Calendar.getInstance();
		fechafin = df.format(ca.getTime());
		int vacio = 0;
		
		String query = "Select n_correlativo from MTP_INSPECCIONMAQUINA_CAB "
					   + "order by cast(n_correlativo as int) DESC limit 1 ";
		Cursor cx = _db.rawQuery(query, null);
		if (cx.moveToFirst()) {
			
			c_cabecera = cx.getString(0);
			c_cabecera = String.valueOf(Integer.parseInt(c_cabecera) + 1);
			
		} else {
			
			c_cabecera = "1";
			
		}
		
		cx.close();
		
		cabec = c_cabecera;
		
		if (condiciones.equals("NULL")) {
			
			Toast.makeText(MantInspeccionT.this, "Seleccione Condición Maquina",Toast.LENGTH_SHORT).show();
			c_cabecera = "0";
			vacio = 1;
			
		}
		if (periodos.equals("NULL")) {
			
			Toast.makeText(MantInspeccionT.this, "Seleccione Periodo Inspección.",Toast.LENGTH_SHORT).show();
			c_cabecera = "0";
			vacio = 1;
			
		}
		
		for (int i = 0; i < lista.size(); i++) {
			
			if (lista.get(i).n_porcentajeminimo.substring(0, 1).equals("0")
					&& lista.get(i).n_porcentajemaximo.substring(0, 1).equals("0")) {
				
				if (lista.get(i).c_estado == null
						|| lista.get(i).c_estado.length() == 0) {

					vacio = 1;
					Toast.makeText(MantInspeccionT.this,"Hay campos de Estado(OK o FALLA) que no ha llenado",Toast.LENGTH_SHORT).show();
					c_cabecera = "0";
					break;
					
				}
				
			} else {
				
				if (lista.get(i).n_porcentajeinspeccion == null
						|| lista.get(i).n_porcentajeinspeccion.length() == 0) {
					
					vacio = 1;
					Toast.makeText(MantInspeccionT.this,"Hay campos de %Insp. que no ha llenado",Toast.LENGTH_SHORT).show();
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
					
					_db.insert("MTP_INSPECCIONMAQUINA_DET", null, con);
					
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
					
					_db.insert("MTP_INSPECCIONMAQUINA_DET", null, con);
					
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
			
			_db.insert("MTP_INSPECCIONMAQUINA_CAB", null, conc);
			// db.insert("PENDIENTE_MTP_INSPECCIONMAQUINA_CAB", null,conc);
			
			cboEstado.setText("INGRESADA");
			//estado.setText("INGRESADA");
			//ffin.setText(fechafin);
			lblFechaFin.setText(fechafin);
			
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
			Editor ed = p.edit();
			ed.putString("data", "si");
			ed.commit();
			//GuardarImagenServer(lista);

		}
		
		_db.close();
		
		if (vacio == 3) {

			new AlertDialog.Builder(MantInspeccionT.this)
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/*Toast.makeText(MantInspeccionT.this, "requestCode || REQUEST_CAMERA: " 
											 + String.valueOf(requestCode) + " || " + String.valueOf(REQUEST_CAMERA)
											 + ", resultCode || POSICION: " + String.valueOf(resultCode) + " || " + String.valueOf(posicion), 
				Toast.LENGTH_LONG).show();*/
		if (requestCode == REQUEST_CAMERA) {
			if (resultCode == Activity.RESULT_OK) {
				Bitmap x = (Bitmap) data.getExtras().get("data");
				//finishFromChild(this);
				ActualizarFotoTomada(x);
			//	filepath = "2";
				Log.e("Paso aki","1");
				View v = getViewByPosition(posicion, listaInspecciones);
				//Button foto = (Button) v.findViewById(R.id.bimagen);
			//	foto.setText(filepath);
				
				lista.get(posicion).c_rutafoto = filepath;
				Log.e("filepath posicion" + String.valueOf(posicion),filepath);
				Log.e("imagen posicion" + String.valueOf(posicion),lista.get(posicion).c_rutafoto);
			}
		}
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
		
		DataBase objDB = new DataBase(this, "DBInspeccion", null, 1);
		_db = objDB.getWritableDatabase();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MantInspeccionT.this);
		String us = preferences.getString("user", "");
		// String cb= preferences.getString("cmaquina", "");
		
		// recuperar fecha tiempo aqui
		Calendar ca = Calendar.getInstance();
		fechafin = df.format(ca.getTime());
		int vacio = 0;
		
		cabec = c_cabecera;
		
		if (condiciones.equals("NULL")) {
			
			Toast.makeText(MantInspeccionT.this, "Seleccione Condición Máquina.",Toast.LENGTH_SHORT).show();
			vacio = 1;
			
		}
		if (periodos.equals("NULL")) {
			
			Toast.makeText(MantInspeccionT.this, "Seleccione Periodo Inspección.",Toast.LENGTH_SHORT).show();
			vacio = 1;
			
		}
		
		for (int i = 0; i < lista.size(); i++) {
			
			if (lista.get(i).n_porcentajeminimo.substring(0, 1).equals("0")
					&& lista.get(i).n_porcentajemaximo.substring(0, 1).equals("0")) {
				
				if (lista.get(i).c_estado == null || lista.get(i).c_estado.length() == 0) {
					
					vacio = 1;
					Toast.makeText(MantInspeccionT.this,"Hay campos de Estado(OK o FALLA) que no ha llenado.",
							Toast.LENGTH_SHORT).show();
					break;
					
				}
				
			} else {
				
				if (lista.get(i).n_porcentajeinspeccion == null
						|| lista.get(i).n_porcentajeinspeccion.length() == 0) {
					
					vacio = 1;
					Toast.makeText(MantInspeccionT.this,"Hay campos de %Insp. que no ha llenado.",Toast.LENGTH_SHORT).show();
					
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
					_db.update(" MTP_INSPECCIONMAQUINA_DET", con,"c_compania=? and n_correlativo=? and n_linea=?", args);
					
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
					_db.update(" MTP_INSPECCIONMAQUINA_DET", con,"c_compania=? and n_correlativo=? and n_linea=?", args);
					
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
			
			_db.update(" MTP_INSPECCIONMAQUINA_CAB", conc, "n_correlativo = ?",
					new String[] { c_cabecera });
			
			// db.insert("PENDIENTE_MTP_INSPECCIONMAQUINA_CAB", null,conc);
			
			cboEstado.setText("INGRESADA");
			//estado.setText("INGRESADA");
			//ffin.setText(fechafin);
			lblFechaFin.setText(fechafin);
			
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
			Editor ed = p.edit();
			ed.putString("data", "si");
			ed.commit();
			
		}
		
		_db.close();
		
		if (vacio == 3) {
			new AlertDialog.Builder(MantInspeccionT.this)
					.setTitle("LysMobile")
					.setMessage(
							"Se guardó el reporte localmente ,desea enviar el reporte en este momento?")
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
			
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MantInspeccionT.this);
			String us = preferences.getString("user", "");

			final EnviarReporteInspeccionAsyncTask enviarinspeccion = new EnviarReporteInspeccionAsyncTask(
					this, correlativo, cabec, Util.compania, us, cb,
					condiciones, comentariocab, "E", periodos, fechainicio,
					fechafin, lista);
				enviarinspeccion.execute(new String[] { StringConexion.UrlServicesRest + "registrarInspeccion" });

			Thread thread = new Thread() {
				public void run() {
					try {
						enviarinspeccion.get(50000, TimeUnit.MILLISECONDS);
						// tarea.get(30000, TimeUnit.MILLISECONDS);
						//GuardarImagenServer(lista);

					} catch (Exception e) {
						enviarinspeccion.cancel(true);
						((Activity) MantInspeccionT.this).runOnUiThread(new Runnable() {

							public void run() {

								enviarinspeccion.progressDialog.dismiss();
								pasar = false;
								Toast.makeText(MantInspeccionT.this,
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
			Toast.makeText(MantInspeccionT.this, "No hay acceso a internet .",
					Toast.LENGTH_LONG).show();
		}
		
		if (pasar == true) {
			for (int i = 0; i < lista.size(); i++) {
				Log.e("entro aqui online for 2",String.valueOf(lista.size()));
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
	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
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
	}
	
	private String getGenerarCodigoImagen() {
		//String codigo = lblCodigo.getText().toString().toLowerCase();
		String nombre = cb;
		int max = 0;
		String valnum = "0";
		String codmaquina = cb;//app.getCodigoVendedor();//
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
	
	private void loadDetalle(String familia,String periodo) {
		DataBase objDB = new DataBase(this, "DBInspeccion", null, 1);
		
		sql = "SELECT c_inspeccion,c_descripcion,c_tipoinspeccion,n_porcentajeminimo, " + 
		"n_porcentajemaximo,c_familiainspeccion,c_periodoinspeccion,c_estado from MTP_INSPECCION " + 
		"where c_familiainspeccion='" + familia + "' and c_periodoinspeccion= '" + periodo + "'";
		
		_db = objDB.getWritableDatabase();
		
		cx = _db.rawQuery(sql, null);
		if (lista.size() > 0)
			lista.clear();
		if (cx.moveToFirst()) {
			do {				
				InspeccionData ins = new InspeccionData();
				
				ins.c_inspeccion = cx.getString(0);
				ins.c_descripcion = cx.getString(1);
				ins.c_tipoinspeccion = cx.getString(2);
				String stringmi = cx.getString(3);
				String stringma = cx.getString(4);
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
				ins.c_familiainspeccion = cx.getString(5);
				ins.d_periodoinspeccion = cx.getString(6);
				ins.c_estado = cx.getString(7);
				
				lista.add(ins);
				
			} while (cx.moveToNext());
			
		} else {
			
			Toast.makeText(this, "No hay datos de inspeccion",Toast.LENGTH_SHORT).show();
			Log.d("plan", "vacio");
		}
		cx.close();
		_db.close();
		
		Log.d("tamaño array", String.valueOf(lista.size()));
		
		for (int i = 0; i < lista.size(); i++) {
			Log.d("valoress", String.valueOf(lista.get(i).c_descripcion));
		}
		//listaInspecciones.setAdapter(new UserItemAdapter(getApplicationContext(),android.R.layout.simple_list_item_1, lista));
		listaInspecciones.setAdapter(new UserItemAdapter(MantInspeccionT.this,
				android.R.layout.simple_list_item_1, lista));
		
		/*detalleinspecciontneg objNDetInspeccionT = new detalleinspecciontneg(app.getConexion());
		if (GrabaInspeccion.equals("M")) {
			detalleinspecciont = objNDetInspeccionT.obtieneDetalle(codCompania, codInspeccion);
			listaInspecciones.setAdapter(new DetalleInspeccionTadp(this, detalleinspecciont));
		}		
		objNDetInspeccionT = null;*/
	}
	
	private void loadCondicionesInspeccionT() {

		/*list.add(new ModeloCombo("--SELECT--",""));
		list.add(new ModeloCombo("ABIERTA","A"));
		list.add(new ModeloCombo("CERRADA","C"));
		cboCondicionMaquina.setAdapter(new ComboCondicionMaquinaadp(this, list));*/
		List<String> listacond = new ArrayList<String>();
		listacond.add("--SELECT--");
		listacond.add("ABIERTA");
		listacond.add("CERRADA");
		
		ArrayAdapter<String> dataAdapterCon = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listacond);
		
		dataAdapterCon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		cboCondicionMaquina.setAdapter(dataAdapterCon);
		

		/*ArrayList<ModeloCombo> list = new ArrayList<ModeloCombo>();
		list = new ArrayList<ModeloCombo>();
		list.add(new ModeloCombo("INGRESADA","I"));
		list.add(new ModeloCombo("ENVIADA","E"));
		cboEstado.setAdapter(new ComboEstadoadp(this, list));*/
		
		DataBase objDB = new DataBase(this, "DBInspeccion", null, 1);
		sql = "Select c_periodoinspeccion,c_descripcion,c_estado from MTP_PERIODOINSPECCION ";
		
		_db = objDB.getWritableDatabase();
		
		cx = _db.rawQuery(sql, null);
		if (listaperiodo.size() > 0)
			listaperiodo.clear();
		if (cx.moveToFirst()){
			do {
				ProgramaData ins = new ProgramaData();
				
				ins.c_periodoinspeccion = cx.getString(0);
				ins.c_descripcion = cx.getString(1);
				ins.c_estado = cx.getString(2);
				
				listaperiodo.add(ins);
			} while (cx.moveToNext());
		} else {
			Toast.makeText(this, "No hay datos para Periodo InspecciOn.", Toast.LENGTH_SHORT).show();
		}
		_db.close();
		cx.close();
		sql = "";
		
		List<String> listap = new ArrayList<String>();
		listap.add("--Seleccione--");
		for (int i = 0; i < listaperiodo.size(); i++) {
			listap.add(listaperiodo.get(i).c_descripcion);
		}
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listap);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		cboPeriodo.setAdapter(dataAdapter);
		
		cboPeriodo.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position > 0) {
					periodos = listaperiodo.get(position - 1).c_periodoinspeccion;
					Log.d("periodo", periodos);
					loadDetalle(familia, periodos);
					//Toast.makeText(this, "Periodo Sel.: " + periodos + ".", Toast.LENGTH_SHORT).show();
					//llenar(periodos);
				} else {

					periodos = "NULL";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		
		if (GrabaInspeccion.equals("N")) {
			cboCondicionMaquina.setSelection(0);
			cboEstado.setText("INGRESADA");
			//cboEstado.setSelection(0);
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			String user = preferences.getString("user", "");
			String nombreuser = preferences.getString("nombreuser", "");
			String cmaquina = preferences.getString("cmaquina", "");
			cb = cmaquina;
			String nommaquina = preferences.getString("maquina", "");
			familia = preferences.getString("familia","");
			//cb = "BOM-01";
			//familia = "009";
			
			lblMaquina.setText(cmaquina);
			lblDescripcionMaquina.setText(nommaquina);
			lblInspector.setText(user);
			lblNombreInspector.setText(nombreuser);
			
			fechainicio = df.format(ca.getTime());
			lblFechaInicio.setText(fechainicio);
			lblFechaFin.setText("");
			
			//loadDetalle(familia, periodos);
			
			
		} else {
			
		}
		
		/*lblInspector.setText(app.getUsuario());
		lblNombreInspector.setText(app.getNombreUsuario());
		
		lblFechaInicio.setText(fechainicio);
		lblFechaFin.setText("");*/
		
		/*inspecciontneg objNInspeccion = new inspecciontneg(app.getConexion());
		inspecciontent EInspeccion;
		EInspeccion = objNInspeccion.DataCondicionInspeccionT(codCompania, codInspeccion);
		Log.e("Size Cond. Insp.", EInspeccion.getCMaquina().toString());
		if (EInspeccion != null) {*/
			//lblMaquina.setText(EInspeccion.getCMaquina().toString().trim());
			/*lblDescripcionMaquina.setText(EInspeccion.getCNombreMaquina().trim());
			cboCondicionMaquina.setSelection((EInspeccion.getCCondicionMaquina().equals("A"))?0:1);
			cboEstado.setSelection((EInspeccion.getCEstado().equals("PE"))?0:1);
			lblFechaInicio.setText(EInspeccion.getDFechaFinInspeccion().toString().trim());
			lblFechaFin.setText(EInspeccion.getDFechaFinInspeccion().toString().trim());
			lblInspector.setText(EInspeccion.getCUsuarioInspeccion().trim());
			lblNombreInspector.setText(EInspeccion.getCNombreUsuarioInspeccion().trim());
			lblComentario.setText(EInspeccion.getCComentario().trim());*/
		/*}
		objNInspeccion = null;
		EInspeccion = null;*/
	}
	
	/*private boolean validarCondicionesInspeccionT() {
		if (lblMaquina.getText().toString().trim().length() == 0)
			return false;
		if (lblInspector.getText().toString().trim().length() == 0)
			return false;
		if (lblFechaInicio.getText().toString().trim().length() == 0)
			return false;
		return true;
	}*/
	
	/*public void EventoModificarComentario(View v) {
		EventoModComentario();
		lblComentario.setText(comInspeccion);
	}
	
	public boolean EventoModComentario() {
		final Handler handler = new Handler() {
			public void handleMessage(Message mesg) {
				throw new RuntimeException();
			}
		};
		
		final Dialog dialog = new Dialog(contexto);
		dialog.setContentView(R.layout.agregarcomentario);
		TextView txtTitulo = (TextView) dialog.findViewById(R.id.tituloReg);
		Button dialogButton = (Button) dialog.findViewById(R.id.btnAceptarCom);
		final EditText txtCom = (EditText) dialog.findViewById(R.id.txtComent);
		txtCom.setText(comInspeccion);
		txtCom.setSelection(txtCom.getText().length());
		

		txtTitulo.setText("Comentario:");
		dialog.getWindow().setLayout(
				(int) (MantInspeccionT.this.getWindow().peekDecorView()
						.getWidth() * 0.85),
				(int) (MantInspeccionT.this.getWindow().peekDecorView()
						.getHeight() * 0.70));
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String comIn = txtCom.getText().toString().trim();
				if (comIn.equals("")) {
					Dialogos.DialogToast(contexto, "Ingresar comentario por favor.", false);
					return;
				}
				comInspeccion = comIn;
				mResult = true;
				handler.sendMessage(handler.obtainMessage());
				dialog.dismiss();
			}
		});
		dialog.show();
		try {
			Looper.loop();
		} catch (RuntimeException e2) {
			
		}
		return true;
	}*/
	
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
					//v = vi.inflate(R.layout.con_inspeccion_se, null);
					v = vi.inflate(R.layout.det_inspeccion_se, null);

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

					//v = vi.inflate(R.layout.con_inspeccion_sm, null);
					v = vi.inflate(R.layout.det_inspeccion_sm, null);

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
					//v = vi.inflate(R.layout.con_inspeccion_se, null);
					v = vi.inflate(R.layout.det_inspeccion_se, null);

				} else {
					vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					// LayoutInflater vi =
					// (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					valor = 2;
					Log.d("valor", "2");

					Log.d("minimo", user.n_porcentajeminimo.substring(0, 1));
					Log.d("maximo", user.n_porcentajemaximo.substring(0, 1));
					Log.d("aq", "----------------------------");

					//v = vi.inflate(R.layout.con_inspeccion_sm, null);
					v = vi.inflate(R.layout.det_inspeccion_sm, null);

				}

			}

			if (user != null) {

				if (valor == 1) {

					TextView numero = (TextView) v.findViewById(R.id.lblLinea);
					TextView detalle = (TextView) v.findViewById(R.id.lblInspeccion);
					TextView systema = (TextView) v.findViewById(R.id.lblTipoInsp);
					TextView minimo = (TextView) v.findViewById(R.id.lblPorcMin);
					TextView maximo = (TextView) v.findViewById(R.id.lblPorcMax);
					TextView campo = (TextView) v.findViewById(R.id.lblPorcInsp);
					Spinner ok = (Spinner) v.findViewById(R.id.cboEstadoDet);

					final Button comentario = (Button) v.findViewById(R.id.btnComentarioDet);
					Button imagen = (Button) v.findViewById(R.id.btnFotoDet);

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

							final Dialog alertDialog = new Dialog(MantInspeccionT.this);

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

										Toast.makeText(MantInspeccionT.this,"Ingrese Comentario.",Toast.LENGTH_SHORT)
												.show();
									} else {
										user.c_comentario = text.getText().toString();
										comentario.setText("" + user.c_comentario);
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
						}
					});
					if (tomo == true) {
						user.c_rutafoto = filepath;
					}

				} else if (valor == 2) {

					TextView numero = (TextView) v.findViewById(R.id.lblLinea);
					TextView detalle = (TextView) v.findViewById(R.id.lblInspeccion);
					TextView systema = (TextView) v.findViewById(R.id.lblTipoInsp);
					TextView minimo = (TextView) v.findViewById(R.id.lblPorcMin);
					TextView maximo = (TextView) v.findViewById(R.id.lblPorcMax);
					final EditText llenar = (EditText) v.findViewById(R.id.lblPorcInsp);
					final TextView ok = (TextView) v.findViewById(R.id.lblEstadoDet);
					final Button comentario = (Button) v.findViewById(R.id.btnComentarioDet);
					Button imagen = (Button) v.findViewById(R.id.btnFotoDet);
					
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
							Log.i("User.rutaFoto  ==>", user.c_rutafoto );
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

							final Dialog alertDialog = new Dialog(MantInspeccionT.this);

							alertDialog.setContentView(R.layout.dialogo_comentario);
							alertDialog.setTitle("Comentario");
							alertDialog.setCancelable(false);
							final EditText text = (EditText) alertDialog.findViewById(R.id.ecomentario);

							text.setText("" + user.c_comentario);

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {
								
								public void onClick(View v) {

									if (text.getText().toString().length() == 0) {
										Toast.makeText(MantInspeccionT.this,"Ingrese Comentario.",Toast.LENGTH_SHORT).show();
									} else {
										user.c_comentario = text.getText().toString();
										comentario.setText("" + user.c_comentario);
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


	public  void GuardarImagenServer (ArrayList<InspeccionData> listImages ){
		Log.i("Metodo Guardar  ", "pass");
		String fileCarp = "/storage/sdcard0/LysConfig/Fotos/";
		for (int i = 0 ; i<listImages.size();i++){
			if (listImages.get(i).c_rutafoto==null || listImages.get(i).equals("")){

			}

			else {
			Log.i("Metodo GuardarImagen == >" , listImages.get(i).c_rutafoto);
			String fileName = listImages.get(i).c_rutafoto;
			String filePath = fileCarp+fileName;
			File file = new File(filePath);

			byte [] bytes = new byte[(int)file.length()];
			try {
				bytes =  FileUtils.readFileToByteArray(file);
				AsyncTask asyncTask = null;
				GuardarImagenTask  guardarImagenTask = new GuardarImagenTask(bytes,listImages.get(i).c_rutafoto);
				Log.i("Parmaetro exex GuardarTask ===>" , listImages.get(i).c_rutafoto);
				asyncTask = guardarImagenTask.execute();
				String resp = (String) asyncTask.get();
				Toast.makeText(MantInspeccionT.this, resp, Toast.LENGTH_SHORT).show();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			}

		}



	}
	public int getPosicionPeriodo(String codigo) {
		int pos = -1;
		ProgramaData EPeriodo;
		for (int i = 0; i < cboPeriodo.getCount(); i++) {
			EPeriodo = (ProgramaData) cboPeriodo
					.getItemAtPosition(i);
			if (String.valueOf(EPeriodo.c_periodoinspeccion).trim()
					.equalsIgnoreCase(codigo.trim()) == true) {
				EPeriodo = null;
				return i;
			}
		}
		EPeriodo = null;
		return pos;
	}
	
}
