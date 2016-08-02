package com.lys.mobile;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import com.lys.mobile.util.*;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class VerListaImagen extends Activity {
	String ruta = "", photoPath = "", codigo = "";
	//appglobal app;
	MyApp app;
	ListView lvdatos;
	Context contexto;
	CheckBox ChkTodos;
	ArrayAdapter<ModeloCheckBox> adapter;
	int posicion = -1;
	int respuestafinal = 0;
	String errores = "";
	boolean seborroarchivo = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verlistaimagen);
		//app = ((appglobal) getApplicationContext());
		app = ((MyApp) getApplicationContext());
		lvdatos = (ListView) findViewById(R.id.lista);
		ChkTodos = (CheckBox) findViewById(R.id.ChkTodos);
		ruta = getIntent().getExtras().getString("ruta");
		codigo = getIntent().getExtras().getString("codigo");
		contexto = this;

		CargarLista();

		lvdatos.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				posicion = position;
				ModeloCheckBox Emodelo = (ModeloCheckBox) parent.getItemAtPosition(position);
				if (!Emodelo.isSelected())
					Emodelo.setSelected(true);
				else
					Emodelo.setSelected(false);
				adapter.notifyDataSetChanged();
				Emodelo = null;
			}
		});

		lvdatos.setChoiceMode(2);
		ChkTodos.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SeleccionarTodos(isChecked);
			}
		});
	}

	private void cargarFotos() {
		//String vendedor = app.getCodigoVendedor();
		String vendedor = "MAQ";
		String iniciacon = vendedor.toLowerCase() + "_" + codigo;
		String terminacon = ".jpg";
		Log.e("ruta", ruta);
		List<ModeloCheckBox> list = new ArrayList<ModeloCheckBox>();
		int i = 0;

		File f = new File(ruta);

		if (f.exists()) {
			File[] files = f.listFiles();

			for (File x : files) {
				String archivo = x.getName().toLowerCase();
				if (archivo.startsWith(iniciacon) && archivo.endsWith(terminacon)) {
					list.add(get(ruta + File.separator + archivo, ""));
					list.get(i).setSelected(false);
					i++;
				}
			}
		}
		adapter = new VerListaImagenadp(this, list);
		lvdatos.setAdapter(adapter);
	}

	private void cargarFotosTodosGarantia() {
		//String rutareclamo = Environment.getExternalStorageDirectory() + app.getRutaGarantia();
		String rutareclamo = Environment.getExternalStorageDirectory() + app.getRutaInspeccion();
		//String vendedor = app.getCodigoVendedor();
		String vendedor = "MAQ";
		String iniciacon = vendedor.toLowerCase();// + "_" + codigo;
		String terminacon = ".jpg";
		List<ModeloCheckBox> list = new ArrayList<ModeloCheckBox>();
		int i = 0;

		File FileRG = new File(rutareclamo);

		if (FileRG.exists()) {

			File[] FilesRG = FileRG.listFiles();

			for (File x : FilesRG) {
				String nuevaruta = rutareclamo + x.getName().toLowerCase();
				String nuevoinicia = iniciacon + "_" + x.getName().toLowerCase();

				File f = new File(nuevaruta);
				File[] files = f.listFiles();

				for (File y : files) {
					String archivo = y.getName().toLowerCase();
					// Log.e("imagen",archivo + "-" + nuevoinicia);
					if (archivo.startsWith(nuevoinicia) && archivo.endsWith(terminacon)) {

						list.add(get(nuevaruta + File.separator + archivo, ""));
						list.get(i).setSelected(false);
						i++;
					}
				}
			}
		}

		adapter = new VerListaImagenadp(this, list);
		lvdatos.setAdapter(adapter);
	}

	private ModeloCheckBox get(String s, String sub) {
		return new ModeloCheckBox(s, sub);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void SeleccionarTodos(boolean isChecked) {
		for (int i = 0; i < lvdatos.getCount(); i++) {
			ModeloCheckBox EModelo = (ModeloCheckBox) lvdatos.getItemAtPosition(i);
			EModelo.setSelected(isChecked);
		}
		adapter.notifyDataSetChanged();
	}

	public void EventoCerrar(View view) {
		this.finish();
	}

	public void EventoVer(View view) {
		if (posicion > -1) {
			ModeloCheckBox Emodelo = (ModeloCheckBox) lvdatos.getItemAtPosition(posicion);
			String imagen = Emodelo.getName();

			if (!imagen.equals("") && (imagen != null)) {
				VerImagen dialogo = new VerImagen(contexto, imagen, null);
				dialogo.show();
			}
		}
	}

	public void EventoEnviar(View view) {
		boolean existe = false;
		for (int i = 0; i < lvdatos.getCount(); i++) {
			ModeloCheckBox EModelo = (ModeloCheckBox) lvdatos.getItemAtPosition(i);
			if (EModelo.isSelected()) {
				String imagen = EModelo.getName();
				if (!imagen.equals("") && (imagen != null)) {
					existe = true;
					break;
				}
			}
		}

		if (existe == true)
			new AsyncSender().execute();
		else
			Toast.makeText(getApplicationContext(), "No hay seleccionados imagenes para enviar al servidor.", Toast.LENGTH_SHORT).show();
	}
	
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
					CargarLista();
				}
			}
		});
		alertDialog.show();

	}

	public void CargarLista() {
		if (ruta.trim().length() > 0)
			cargarFotos();
		else
			cargarFotosTodosGarantia();
	}

	public void BorrarFile(String files) {
		File f = new File(files);
		f.delete();
		f = null;
		seborroarchivo = true;
	}

	public int ListaEnviarServidor() {
		int resp = 0; // Sin datos enviados
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
		return resp;
	}

	private int enviarservidorAnt(String rutadisco) {
		int respuestaservidor = 0;
		//String urlServer = app.getRutaImagenGarantia();
		String urlServer = app.getUrlImagenInspeccion();
		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpPost httppost = new HttpPost(urlServer);
			File file = new File(rutadisco);
			MultipartEntity mpEntity = new MultipartEntity();
			//ContentBody foto = new FileBody(file, "image/jpeg");
			ContentBody foto = new FileBody(file);
			mpEntity.addPart("fotoUp", foto);
			httppost.setEntity(mpEntity);
			httpclient.execute(httppost);
			httpclient.getConnectionManager().shutdown();
			respuestaservidor = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respuestaservidor;
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
			Log.e("nombreweb",nombreweb);
			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ nombreweb + "\"" + lineEnd);
			Log.e("lineEnd",lineEnd);
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

}