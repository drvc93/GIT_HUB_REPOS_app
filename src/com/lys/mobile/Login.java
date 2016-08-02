package com.lys.mobile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.lys.mobile.asynctask.GetAccesosDataTask;
import com.lys.mobile.asynctask.GetMenuDataTask;
import com.lys.mobile.asynctask.UploadService;
import com.lys.mobile.asynctask.UsuariosAsyncTask;
import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.data.Menu;
import com.lys.mobile.dataBase.AccesosDB;
import com.lys.mobile.dataBase.MenuDB;
import com.lys.mobile.dataBase.ProdMantDataBase;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.StringConexion;
import com.lys.mobile.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by User on 03/10/2014.
 */
public class Login extends Activity {

	static String usuario = "", password = "";
	private Button acceder, sincro;
	private EditText user, pass;
	private  String userMaster, passwordMaster;
	final String PREFS_NAME = "MyPrefsFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		/*acceder = (Button) findViewById(R.id.btnlogin);
		sincro = (Button) findViewById(R.id.btnsin);
		user = (EditText) findViewById(R.id.etlouser);
		pass = (EditText) findViewById(R.id.etlopass);*/
		acceder = (Button) findViewById(R.id.btnEntrar);
		sincro = (Button) findViewById(R.id.btnSincronizar);
		user = (EditText) findViewById(R.id.txtUsuario);

		pass = (EditText) findViewById(R.id.txtClave);

		user.setFilters(new InputFilter[] { new InputFilter.AllCaps() });

		pass.setFilters(new InputFilter[] { new InputFilter.AllCaps() });
		createUsuarioMaster();
		sincro.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				new AlertDialog.Builder(Login.this).setTitle("LysMobile")
						.setMessage("	Tenga en cuenta que se borrara toda la informacion almacenada en el dispositivo, esta seguro?")
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// continue with delete

										sincronizar();
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
		});
		sincro.setVisibility(View.INVISIBLE);
		acceder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
				String sin = preferences.getString("sincro", "");
				
				/*Intent i = new Intent(Login.this, MainPrincipal.class);
				startActivity(i);*/
				
				if (sin.equals("si")) {

					acceder.setEnabled(false);

					usuario = user.getText().toString();
					password = pass.getText().toString();

					user.setError(null);
					pass.setError(null);

					boolean esError = false;

					if (usuario.length() == 0) {
						user.setError("Ingrese usuario");
						esError = true;

						acceder.setEnabled(true);
 
					}
					if (password.length() == 0) {

						pass.setError("Ingrese contraseña");
						esError = true;

						acceder.setEnabled(true);
									
					}

					if (esError)
						return;
					Log.i("Prueba mensaje", usuario+ " - "+  password );
					autenticar(usuario, password);
					//Log.i(tag, password)
					
				}

				else if (user.getText().toString().equals(userMaster)&& pass.getText().toString().equals(passwordMaster)){
					ShowDialogAlert();

				}
				else {

					Toast.makeText(Login.this, "Debe Sincronizar .", Toast.LENGTH_LONG).show();

				}
			}
		});
	}

	public    void createUsuarioMaster (){


		userMaster = "USERMOVIL";
		passwordMaster="USERMOVIL";

	}

	public void autenticar(String usuario, String password) {

		DataBase basededatos = new DataBase(this, "DBInspeccion", null, 1);
		SQLiteDatabase db = basededatos.getWritableDatabase();

		String query = "SELECT c_codigousuario,c_nombre,c_clave, n_persona, c_estado, c_flagmantto from MTP_USUARIO where c_codigousuario='"
				+ usuario + "' and c_clave='" + password + "'";
		Cursor c = db.rawQuery(query, null);
		if (c.moveToFirst()) {

			String codi = c.getString(0);

			String nombre = c.getString(1);
			String clave = c.getString(2);
			String persona = c.getString(3);
			String estado = c.getString(4);
			String flagmantto = c.getString(5);

			if (estado.equalsIgnoreCase("I")) {

				Toast.makeText(this, "Usted se encuentra inactivo", Toast.LENGTH_SHORT).show();

			} else {

				Log.d("Bienvenido", "usuario");

				leelo();

				//Intent i = new Intent(Login.this, MyActivity.class);
				// MainPrinciapl activity
				//Intent i =new Intent("com.example.MenuProncipal");
				Intent i = new Intent(Login.this, MenuPrincipal.class);

				startActivity(i);

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
				Editor editor = preferences.edit();
				editor.putString("user", codi);// guarda nivel

				editor.putString("nombreuser", nombre);// guarda nivel
				Log.i("Usuario ===> " , codi );
				editor.putString("flagmantto", flagmantto);

				editor.commit();

			}

		}

		else  if (usuario.equals(userMaster) && password.equals(passwordMaster) ){

			ShowDialogAlert();
		}
		else {

			Toast.makeText(this, "Usuario incorrecto", Toast.LENGTH_SHORT).show();
		}
		c.close();
		db.close();

		acceder.setEnabled(true);

	}


	public  void  ShowDialogAlert (){



		new AlertDialog.Builder(Login.this)
				.setTitle("Advertencia")
				.setMessage("Para ingresar a la aplicación  móvil , primero se debe realizar la sincronizacion")
				.setIcon(R.drawable.icn_alert)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

						sincronizar();
						user.setText("");
						pass.setText("");
					}
				}).show();


	}

	public  void SincMenuAcceso()  {



		GetMenuDataTask getMenuDataTask = new GetMenuDataTask();
		AsyncTask<String,String,ArrayList<MenuDB>> asyncTask;
		ArrayList<MenuDB> menuDBs= new ArrayList<MenuDB>();
		ProdMantDataBase db =  new ProdMantDataBase(Login.this);
		db.deleteTables();


		try {
			asyncTask = getMenuDataTask.execute();
			menuDBs= (ArrayList<MenuDB>)asyncTask.get();
			for (int i = 0 ; i <menuDBs.size();i++){

				MenuDB mn = menuDBs.get(i);
				db.InsetrtMenus(mn);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		ArrayList<AccesosDB> accesosDBs = new ArrayList<AccesosDB>();
		GetAccesosDataTask getAccesosDataTask = new GetAccesosDataTask();
		AsyncTask<String,String,ArrayList<AccesosDB>> asyncTaskAccesos;

		try {
			asyncTaskAccesos = getAccesosDataTask.execute();
			accesosDBs = (ArrayList<AccesosDB>)asyncTaskAccesos.get();
			for (int i = 0 ; i <accesosDBs.size() ; i++){
				AccesosDB acdb =  accesosDBs.get(i);
				db.InsertAccesos(acdb);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}


	}

	public void sincronizar() {

		// leeelo

		leelo();
		SincMenuAcceso();
		final UsuariosAsyncTask tarea = new UsuariosAsyncTask(Login.this);
		//tarea.execute(new String[] { Util.url + "getUsuarios" });
		tarea.execute(new String[] { StringConexion.UrlServicesRest + "getUsuarios" });

		Thread thread = new Thread() {
			public void run() {
				try {
					tarea.get(300000, TimeUnit.MILLISECONDS);
					// tarea.get(30000, TimeUnit.MILLISECONDS);

				} catch (Exception e) {
					tarea.cancel(true);
					((Activity) Login.this).runOnUiThread(new Runnable() {
						public void run() {
							acceder.setEnabled(true);
							tarea.progressDialog.dismiss();
							Toast.makeText(Login.this, "No se pudo establecer comunicacion.", Toast.LENGTH_LONG).show();

						}
					});
				}
			}
		};
		thread.start();

	}
public void leelo(){	

	File sdcard = new File(Environment.getExternalStorageDirectory(), "LysConfig");

	// Get the text file
	File file = new File(sdcard, "config.txt");

	// Read text from file
	StringBuilder text = new StringBuilder();

	try {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;

		while ((line = br.readLine()) != null) {
			text.append(line);
			text.append('\n');

		}
		br.close();

		// String[] valores=new String[2];
		String[] lines = text.toString().split("\n");
		/*
		 * for(String s: lines){
		 * 
		 * System.out.println("Content = " + s);
		 * System.out.println("Length = " + s.length()); }
		 */

		Util.url = String.valueOf(lines[0]);
		Util.urlfoto = String.valueOf(lines[1]);

		Log.d("url", String.valueOf(lines[0]));
		Log.d("fotoo", String.valueOf(lines[1]));

	} catch (Exception e) {
		// You'll need to add proper error handling here
		Log.d("error: ", e.toString());

		try {
			File root = new File(Environment.getExternalStorageDirectory(), "LysConfig");
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, "config.txt");
			FileWriter writer = new FileWriter(gpxfile);
			/*writer.append("http://100.100.100.57:8080/LysWsRest/resources/generic/"
					+ "\n" + "http://100.100.100.11/Fotos_Tablet/");*/
			writer.append("http://100.100.100.57:8080/LysWsRest/resources/generic/"
					+ "\n" + "http://100.100.100.11/Fotos_Tablet/");
			// writer.append("\n");//appends the string to the file
			// writer.append("http://192.168.1.33:81/imagenes/");
			writer.flush();
			writer.close();
			// Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
		} catch (IOException ee) {
			Log.d("error: ", ee.toString());
			e.printStackTrace();

		}

		// leeelo

		File sdcardd = new File(Environment.getExternalStorageDirectory(), "LysConfig");

		// Get the text file
		File filee = new File(sdcardd, "config.txt");

		// Read text from file
		StringBuilder textt = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filee));
			String line;

			while ((line = br.readLine()) != null) {
				textt.append(line);
				textt.append('\n');

			}
			br.close();
		} catch (IOException ee) {
			// You'll need to add proper error handling here
			Log.d("error: ", ee.toString());
		}

		// String[] valores=new String[2];
		String[] lines = textt.toString().split("\n");
		/*
		 * for(String s: lines){
		 * 
		 * System.out.println("Content = " + s);
		 * System.out.println("Length = " + s.length()); }
		 */
		Util.url = String.valueOf(lines[0]);
		Util.urlfoto = String.valueOf(lines[1]);

		Log.d("url", Util.url = String.valueOf(lines[0]));
		Log.d("fotoo", String.valueOf(lines[1]));

		//user.setText("" + String.valueOf(lines[0]));

	}
}
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

}
