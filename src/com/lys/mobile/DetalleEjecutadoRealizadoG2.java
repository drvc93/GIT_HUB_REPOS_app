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
import com.lys.mobile.asynctask.EnviarReporteInspeccionGAsyncTask;
import com.lys.mobile.data.DetalleInspeccionRealizadasGData;
import com.lys.mobile.data.DetalleProgramaEjecutado;
import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.data.InspeccionGData;
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
import android.webkit.WebChromeClient;
import android.webkit.WebView;
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

public class DetalleEjecutadoRealizadoG2 extends Activity {
	public ArrayList<DetalleInspeccionRealizadasGData> lista;
	int posicion;
	AQuery aq;
	MyApp app;
	String errores = "";
	boolean seborroarchivo = false;
	int respuestafinal = 0;
	public boolean pasar;
	Context contexto;
	public String fechamod;
	
	//DD
	LinearLayout tabCondicionInspeccion,tabDetalle;
	TextView lblFechaInsp,lblInspector,lblMaquina,
			 lblNombreInspector,lblAreaProb,lblTipoInsp,
			 lblCCosto,lblEstado;
	Button btnSiguiente;
	ListView listaInspecciones;
	String GrabaInspeccion = "V",codCompania = "",codInspeccion = "0";
	TextView lblTab01_TituloMaquina,lblTab01_TituloEstado;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.detalle_ejecutadog);
		setContentView(R.layout.mantinspecciong_detrealizadas);
		
		lista = new ArrayList<DetalleInspeccionRealizadasGData>();
		aq = new AQuery(getApplicationContext());// imageView1"
		// recupero valor y select y escribo
		contexto = this;
		
		app = ((MyApp)getApplicationContext());
		EnlazarControles();
		/*TextView tipo = (TextView) findViewById(R.id.ttipo2);
		TextView lblestado = (TextView) findViewById(R.id.tlestado2);
		TextView estado = (TextView) findViewById(R.id.testado2);
		TextView usui = (TextView) findViewById(R.id.tusuarioinsp2);
		TextView fechai = (TextView) findViewById(R.id.tfecha2);
		TextView comentario = (TextView) findViewById(R.id.etcomentario2);
		TextView maquina = (TextView) findViewById(R.id.etmaquina2);
		ListView listView = (ListView) findViewById(R.id.listainspecciong2);
		Button atras = (Button) findViewById(R.id.sendd2);
		Button env = (Button) findViewById(R.id.env2);*/	
		
		lblTab01_TituloEstado.setVisibility(View.INVISIBLE);
		lblEstado.setVisibility(View.INVISIBLE);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			
			String correla = extras.getString("correlativohg");//
			String compa = extras.getString("companiahg");//
			
			if (lista.size() > 0)
				lista.clear();
			DataBase basededatos = new DataBase(DetalleEjecutadoRealizadoG2.this,"DBInspeccion", null, 1);
			SQLiteDatabase db = basededatos.getWritableDatabase();
			
			String query = "SELECT c.c_compania,c.n_correlativo,c.c_tipoinspeccion,c.c_maquina, " + 
			"c.c_comentario,c.c_usuarioinspeccion,c.d_fechainspeccion, c.c_estado, " +
			"c.c_ultimousuario,c.d_ultimafechamodificacion,c.c_ultimousuario,c.d_ultimafechamodificacion, " + 
			"d.c_compania,d.n_correlativo,d.n_linea,d.c_comentario,d.c_rutafoto,d.c_tiporevisiong " +
			"from MTP_INSPECCIONGEJE_CAB c,MTP_INSPECCIONGEJE_DET d " + 
			"where d.n_correlativo=c.n_correlativo and d.c_compania=c.c_compania and c.c_compania='"
			+ compa + "' and c.n_correlativo='" + correla+"' order by d.n_linea asc";
			Log.d("query", "" + query);
			Cursor c = db.rawQuery(query, null);
			if (c.moveToFirst()) {
				
				do {
					Log.d("ingreso", "1");
					DetalleInspeccionRealizadasGData p = new DetalleInspeccionRealizadasGData();
					
					p.c_compania = c.getString(0);
					p.n_correlativo = c.getString(1);
					p.c_tipoinspeccion = c.getString(2);
					p.c_maquina = c.getString(3);
					p.c_comentario = c.getString(4);
					p.c_usuarioinspeccion = c.getString(5);
					p.d_fechainspeccion = c.getString(6);
					p.c_estado = c.getString(7);
					p.c_usuarioenvio = c.getString(8);
					p.d_fechaenvio = c.getString(9);
					p.c_ultimousuario = c.getString(10);
					p.d_ultimafechamodificacion = c.getString(11);
					
					p.c_companiad = c.getString(12);
					p.n_correlativod = c.getString(13);
					p.n_linead = c.getString(14);
					p.c_comentariod = c.getString(15);
					p.c_rutafotod = c.getString(16);
					p.c_tiporevisiongd = c.getString(17);
					/*p.c_flagadictipod = c.getString(18);
					p.c_flaggensolxdetd = c.getString(19);*/
					
					lista.add(p);
					
				} while (c.moveToNext());
				
				if (lista.get(0).c_estado.equalsIgnoreCase("I"))
					lblEstado.setVisibility(View.VISIBLE);
				
				lblInspector.setText("" + lista.get(0).c_usuarioinspeccion);
				lblFechaInsp.setText("" + lista.get(0).d_fechainspeccion);
				
				if(lista.get(0).c_tipoinspeccion.equalsIgnoreCase("OT")){
					
					lblTipoInsp.setText("OTRO");
					
				}else if(lista.get(0).c_tipoinspeccion.equalsIgnoreCase("MQ")){
					
					lblTipoInsp.setText("MAQUINA" );	
				}
				
				if(lista.get(0).c_estado.equalsIgnoreCase("E")){
					
					lblEstado.setText("ENVIADA");
					
				}else if(lista.get(0).c_estado.equalsIgnoreCase("I")){
					
					lblEstado.setText("INGRESADA");	
				}
				
				lblAreaProb.setText("" + lista.get(0).c_comentario);
				lblMaquina.setText("" + lista.get(0).c_maquina);
				
				//Log.e("entro aqui 1", String.valueOf(lista.size()));
				listaInspecciones.setAdapter(new UserItemAdapter(getApplicationContext(),android.R.layout.simple_list_item_1, lista));

			} else {

				Toast.makeText(DetalleEjecutadoRealizadoG2.this,
						"No hay informacion disponible", Toast.LENGTH_SHORT)
						.show();

				Log.d("informacion", "no hay");

			}
			c.close();
			db.close();

		}
		
	}
	
	private void EnlazarControles() {
		tabCondicionInspeccion = (LinearLayout)findViewById(R.id.tabInspeccionT01);
		tabDetalle = (LinearLayout)findViewById(R.id.tabInspeccionT02);
		
		//TextView tipo = (TextView) findViewById(R.id.ttipo2);
		//TextView estado = (TextView) findViewById(R.id.testado2);
		//TextView usui = (TextView) findViewById(R.id.tusuarioinsp2);
		//TextView fechai = (TextView) findViewById(R.id.tfecha2);
		//TextView comentario = (TextView) findViewById(R.id.etcomentario2);
		//TextView maquina = (TextView) findViewById(R.id.etmaquina2);
		//TextView costo = (TextView) findViewById(R.id.etccosto2);
		//ListView listView = (ListView) findViewById(R.id.listainspecciong2);
		//Button atras = (Button) findViewById(R.id.sendd2);
		//Button env = (Button) findViewById(R.id.env2);
		
		// Datos del Tab 01: Condiciones de Inspeccin G.
		lblTab01_TituloMaquina = (TextView)findViewById(R.id.lblTab01TituloMaquina);
		lblMaquina = (TextView)findViewById(R.id.etmaquina2);
		lblTipoInsp = (TextView)findViewById(R.id.ttipo2);
		lblCCosto = (TextView)findViewById(R.id.etccosto2);
		lblFechaInsp = (TextView)findViewById(R.id.tfecha2);
		lblAreaProb = (TextView)findViewById(R.id.etcomentario2);
		lblInspector = (TextView)findViewById(R.id.tusuarioinsp2);
		lblNombreInspector = (TextView)findViewById(R.id.tnomusuarioinsp2);
		lblTab01_TituloEstado = (TextView)findViewById(R.id.lblTab01TituloEstado);
		lblEstado = (TextView)findViewById(R.id.testado2);
				
		// Datos del Tab 02: Detalle
		listaInspecciones = (ListView)findViewById(R.id.listainspecciong2);
		
		
		//Pie
		btnSiguiente = (Button)findViewById(R.id.btnSiguiente);
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
				btnSiguiente.setText("Finalizar");
			} else {
				Log.e("val", "false");
				btnSiguiente.setText("Siguiente");
			}
		} else {
			finish();
		}
		
	}
	
	public class UserItemAdapter extends ArrayAdapter<DetalleInspeccionRealizadasGData>  {
		private LayoutInflater li;
		private ArrayList<DetalleInspeccionRealizadasGData> listcontact;
		
		public UserItemAdapter(Context context, int textViewResourceId,ArrayList<DetalleInspeccionRealizadasGData> listcontact) {
			super(context, textViewResourceId, listcontact);
			li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);			
			if (listcontact != null){
				this.listcontact = listcontact;
			}
		}
		
		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		public View getView(final int position, View convertView,final ViewGroup parent) {
			View v = convertView;
			final DetalleInspeccionRealizadasGData user = listcontact.get(position);
			
			if (v == null) {
				v = li.inflate(R.layout.det_inspecciongeneral2_det, null);
			}
			
			TextView numerog = (TextView) v.findViewById(R.id.tnumerog2);
			TextView tiporevg = (TextView) v.findViewById(R.id.ttiporevisiong2);
			TextView comentariog = (TextView) v.findViewById(R.id.tcomentariog2);
			Button imageng = (Button) v.findViewById(R.id.bimageng2);
			
			if (numerog != null) {
				numerog.setText(user.n_linead);
				Log.e("linea",user.n_linead);
			}
			
			if (tiporevg != null) {
				DataBase basededatos = new DataBase(DetalleEjecutadoRealizadoG2.this,"DBInspeccion", null, 1);
				SQLiteDatabase db = basededatos.getWritableDatabase();
				
				String des = "";

				String queryc = "Select c_descripcion from mtp_tiporevisiong where c_tiporevisiong ='"
						+ user.c_tiporevisiongd + "' ";
				Cursor cc = db.rawQuery(queryc, null);
				if (cc.moveToFirst()) {

					des = cc.getString(0);

				}
				cc.close();
				
				tiporevg.setText(des);
				Log.e("Tipo Revisión",des);
			}
			
			if (comentariog != null) {
				comentariog.setText("" + user.c_comentariod);
				Log.e("comentario",user.c_comentariod);
			}
			
			if (imageng != null) {
				imageng.setText("" + user.c_rutafotod);
				Log.e("imagen",user.c_rutafotod);
			}
			
			imageng.setOnClickListener(new View.OnClickListener() {
				@SuppressWarnings("deprecation")
				@SuppressLint("SetJavaScriptEnabled")
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					// abre camara
					// sonar

					final Dialog alertDialog = new Dialog(DetalleEjecutadoRealizadoG2.this);

					alertDialog.setContentView(R.layout.dialogo_foto_2);
					alertDialog.setTitle("Visualizar Foto");
					//alertDialog.setCancelable(false);
					
					final WebView webDescarga = (WebView) alertDialog.findViewById(R.id.webDescarga);
					final ProgressBar barraProgreso = (ProgressBar) alertDialog.findViewById(R.id.barraProgreso);
					
					alertDialog.getWindow().setLayout(
							(int) (DetalleEjecutadoRealizadoG2.this.getWindow().peekDecorView()
									.getWidth() * 0.95),
							(int) (DetalleEjecutadoRealizadoG2.this.getWindow().peekDecorView()
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
							DetalleEjecutadoRealizadoG2.this.setProgress(progress * 1000);
							barraProgreso.incrementProgressBy(progress);
							if (progress == 100) {
								barraProgreso.setVisibility(View.GONE);
							}
						}
					});
					//rutafoto = user.c_rutafotod;
					//CargarFoto();
					
					String html = app.getUrlFotoInspeccion() + user.c_rutafotod; //+ ".jpg";
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
		DetalleEjecutadoRealizadoG2.this.finish();
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