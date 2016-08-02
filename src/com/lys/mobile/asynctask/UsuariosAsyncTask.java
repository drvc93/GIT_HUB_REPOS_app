package com.lys.mobile.asynctask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;


import com.google.gson.Gson;
import com.lys.mobile.Login;
import com.lys.mobile.MyActivity;
import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.data.UsuarioData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.StringConexion;
import com.lys.mobile.util.Util;

public class UsuariosAsyncTask extends AsyncTask<String,Integer,String> {
	
	Context context;
	 public  ProgressDialog progressDialog;

	String usuario="",password="";
	public static  ArrayList<Object[]> lista; 
	
	public UsuariosAsyncTask(Context c) {
		super();
		context=c;
		
		lista = new ArrayList<Object[]>();
		if(lista.size()>0)lista.clear(); 
		
	    progressDialog=new ProgressDialog(c);
		progressDialog.setMessage("Obteniendo Usuarios..");
		progressDialog.setCancelable(false);
		
	 }
	@Override
	public String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		
		if (isCancelled())
		{
			//Toast.makeText(getApplication(), getApplication().getString(R.string.error_serverconnection), Toast.LENGTH_SHORT).show();
			return "";
		}

		String url= StringConexion.UrlServicesRest + "getUsuarios";
		DefaultHttpClient cliente=new DefaultHttpClient();
	
		HttpGet httppost=new HttpGet(url);
		String data="";
		String html="";
							
			publishProgress(10);
			Log.d("Logueo",String.valueOf(url));
						
			try {
				
				HttpResponse respuesta=cliente.execute(httppost);
			
		    	InputStream contenido=respuesta.getEntity().getContent();
		    	BufferedReader reader=new BufferedReader(new InputStreamReader(contenido));
				
				publishProgress(40);
								
				while((data=reader.readLine())!=null){
					 
					 if (isCancelled()){
						 break;
					 }
				    
					html +=data;
					
				}
				
				reader.close();
				contenido.close();
				
				publishProgress(100);
				
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
				cancel(true);
				return "";
			} catch (IOException e) {
				// TODO Auto-generated catch block
			
				e.printStackTrace();
				cancel(true);
				return "";
			}catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
		
				e.printStackTrace();
				cancel(true);
				return "";
			}	catch (Exception e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
				cancel(true);
				return "";
			} 
			 finally{
				 	
					Log.d("Logueo",String.valueOf(html));
			try{
			
			JSONObject respJSON;
			String status="";
			
			try {
				respJSON = new JSONObject(html.toString());
				status = respJSON.getString("status");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			if(status.equals("0")){
				
				return "0";
				
			}else if(status.equals("1")){   
				
			String resp="";
			 
			JSONObject respJSON2;
			
			try {
				 	
					respJSON2 = new JSONObject(html.toString());
					  
					//String cad = respJSON2.getString("data");
					int tamaño =Integer.parseInt( respJSON2.getString("size"));
					Gson gson=new Gson();
					
					//UsuarioData [] dataws=gson.fromJson(cad,UsuarioData [].class);
					
					DataBase basededatos = new DataBase (context, "DBInspeccion", null, 1);	
				    SQLiteDatabase db = basededatos.getWritableDatabase();
					db.execSQL("DELETE FROM MTP_USUARIO");
					 	     
				     for(int i=0;i<tamaño;i++){
							
						 String cad = respJSON2.getString("data"+i);
						 Object[] list = new Gson().fromJson(cad, Object[].class);
						 
						 lista.add(list);
					}
						
						for(int i=0;i<lista.size();i++){    
							Object[] list=lista.get(i);
							
							agregarbd(list,db);
							
						    }
							
						   db.close();
	    				
		            resp="1";   
					
			} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			
			return resp;
			
			}
			
		   } catch (Exception e) {
				// TODO Auto-generated catch block
			 
				e.printStackTrace();
				cancel(true);
				return "";
		   }
			
	  }
			
			return "";
			
	}

	
	@Override
	
	protected void onPostExecute(String result) {//paso 4
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		progressDialog.dismiss();
		//myAnim.cancel();
		
		if(result.equals("")){
			
		 //	boton.setEnabled(true);
			 Toast t=Toast.makeText(context,"Servidor no disponible", Toast.LENGTH_SHORT);
			 t.show();
			
		}else{
			
		    if(result.equals("0")){
				
		     	//boton.setEnabled(true);
		    	  Toast t=Toast.makeText(context,"Usuario no existente!", Toast.LENGTH_SHORT);
				t.show();
				
		    }else if(result.equals("1")){
					
					//pasa guarda el idusuario porseacasso
					
		    	//boton.setEnabled(true);
		    	Log.d("cargo todo","usuarioss");
		    	
		    	//traer maquinas
		    	
		    	final MaquinasAsyncTask tarea=new MaquinasAsyncTask(context);   
		  	    tarea.execute( new String[]{StringConexion.UrlServicesRest+"getMaquinas"});
		  	    
		  	    Thread thread = new Thread(){
		             public void run(){
		                 try {
		                     tarea.get(300000, TimeUnit.MILLISECONDS);  
		                    // tarea.get(30000, TimeUnit.MILLISECONDS); 

		                 } catch (Exception e) {
		                     tarea.cancel(true);                           
		                     ((Activity) context).runOnUiThread(new Runnable()
		                     {
		                          
		                         public void run()
		                          {
		                         //	acceder.setEnabled(true);
		                             tarea.progressDialog.dismiss();
		                             Toast.makeText(context, "No se pudo establecer comunicacion .", Toast.LENGTH_LONG).show();
		                           
		                          }
		                     });
		                 }
		             }
		         };
		         thread.start();
		    	
			}
		    				  	
			}
			
		}
	
	@Override
	protected void onPreExecute() {//paso1
		// TODO Auto-generated method stub
		super.onPreExecute();
		progressDialog.show();
		
    }

	 @Override
	    protected void onCancelled() {
		 progressDialog.dismiss();
		 //progressDialog.cancel();
		 
	}


	@Override
	protected void onProgressUpdate(Integer... values) {//paso2
		// TODO Auto-generated method stub
		
		super.onProgressUpdate(values);
		progressDialog.setProgress(values[0]);
		
	}
	public void agregarbd(Object[] o,  SQLiteDatabase db){
		
		//for(int i=0;i<o.length;i++){
		db.execSQL("INSERT INTO MTP_USUARIO(c_codigousuario, c_nombre, c_clave, n_persona, c_estado, c_flagmantto) " +
	               "VALUES ('" + o[0] + "','" + o[1] + "', '" + o[2] +"', '" + o[3] + "', '" + o[4] + "', '" + o[5] + "')");
			
		//}
	
	}
	
}