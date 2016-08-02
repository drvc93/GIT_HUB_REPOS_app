package com.lys.mobile.asynctask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.StringConexion;
import com.lys.mobile.util.Util;

public class InspeccionAsyncTask extends AsyncTask<String,Integer,String> {
	
	Context context;
	 public ProgressDialog progressDialog;

	String usuario="",password="";
	public  ArrayList<Object[]> lista; 
	
	public  InspeccionAsyncTask (Context c) {
		super();
		context=c;
	
		lista = new ArrayList<Object[]>();
		if(lista.size()>0)lista.clear(); 
		
	    progressDialog=new ProgressDialog(c);
		progressDialog.setMessage("Obteniendo Inspeccion..");
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
		
		String url= StringConexion.UrlServicesRest+"getInspeccion";
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
				  
				int tamaño =Integer.parseInt( respJSON2.getString("size"));
				Gson gson=new Gson();
				
				DataBase basededatos = new DataBase (context, "DBInspeccion", null, 1);	
			    SQLiteDatabase db = basededatos.getWritableDatabase();
			    
			    db.execSQL("DELETE FROM MTP_INSPECCION");
			     
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
		    	
		     	Log.d("cargo todo","insdpeccion");
		    	
			   	  	final ProgramaMantenimientoAsyncTask tarea=new ProgramaMantenimientoAsyncTask(context);   
			  	    tarea.execute( new String[]{Util.url+"ProgramaMantenimiento"});
			  	    
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
public void agregarbd(Object[] o,SQLiteDatabase db){
	
	String fechaa=String.valueOf(o[9]);
	
	String fechaaa=fechaa.replace(',', ' ');
	
	//String dtStart = "11/08/2013 08:48:10";
	
	Date daten=null,datenu=null;
	SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy HH:mm:ss",Locale.ENGLISH);
	try {
	    daten = (Date)format.parse(fechaaa);
		Log.d("date1", String.valueOf(daten));
	    //System.out.println(date); 
	} catch (Exception e) {
	    e.printStackTrace();
	}
	SimpleDateFormat otroFormato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
	 System.out.println("FEcha : \n" + otroFormato.format(daten));
	//for(int i=0;i<o.length;i++){
	db.execSQL("INSERT INTO MTP_INSPECCION(c_inspeccion,c_descripcion,c_tipoinspeccion,n_porcentajeminimo,n_porcentajemaximo,c_familiainspeccion,c_periodoinspeccion,c_estado,c_ultimousuario,d_ultimafechamodificacion) " +
			   "VALUES ('" + o[0] + "', '" + o[1] + "','" + o[2] +"', '" + o[3] + "', '" + o[4] + "', '" + o[5] + "', '" + o[6] + "', '" + o[7] + "', '" + o[8] + "', '" + otroFormato.format(daten) + "')");
			 
		  // db.close();
	}
	
}