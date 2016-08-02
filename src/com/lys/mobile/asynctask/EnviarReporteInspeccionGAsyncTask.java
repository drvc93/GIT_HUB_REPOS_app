package com.lys.mobile.asynctask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lys.mobile.Inspeccion;
import com.lys.mobile.Llenar;
//import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.data.InspeccionGData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.Util;

public class EnviarReporteInspeccionGAsyncTask extends AsyncTask<String,Integer,String>
{	
	Context context;
	public static ProgressDialog progressDialog;
    String corr="",cabe="",cma="",tipoi="",ccosto="",com="",usui="",fechai="",esta="",usue="",fechae="",usuu="", fechau="",idca="";

    public ArrayList<InspeccionGData> lista;  
	
	public EnviarReporteInspeccionGAsyncTask (Context c,String corre,String cabecera,String compania,
			String tipoinspeccion,String cmaquina,String c_centrocosto,String comentario,String usuinsp,String fechainsp,String estado,
			String usuenv,String fechaenv,String usuult,String fechault,ArrayList<InspeccionGData> detalle)
	{
		super();
		context=c;
		lista=detalle;
		corr=corre;
		cabe=cabecera;
		tipoi=tipoinspeccion;
		cma=cmaquina;
		ccosto=c_centrocosto;
		com=comentario;
		usui=usuinsp;
		fechai=fechainsp;
		esta=estado;
		usue=usuenv;
		fechae=fechaenv;
		usuu=usuult; 
		fechau=fechault;
		
	    progressDialog=new ProgressDialog(c);
		progressDialog.setMessage("Enviando Reporte Informacion ..");
		progressDialog.setCancelable(false);
	}
	
	@Override
	public String doInBackground(String... params)
	{
		if (isCancelled())
		{
			return "";
		}
		
		String url=params[0];
		DefaultHttpClient cliente=new DefaultHttpClient();
	
		HttpPost httppost=new HttpPost(url);
		String data="";
		String html="";

		publishProgress(10);
		Log.d("Logueo",String.valueOf(url));
		
		try
		{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar ca = Calendar.getInstance();
		    String fechafin = df.format(ca.getTime());
		    
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    
	        nameValuePairs.add(new BasicNameValuePair("c_compania",Util.compania));		        
	        nameValuePairs.add(new BasicNameValuePair("c_tipoinspeccion", tipoi));
	        nameValuePairs.add(new BasicNameValuePair("c_maquina", cma));
	        nameValuePairs.add(new BasicNameValuePair("c_comentario", com));
	        nameValuePairs.add(new BasicNameValuePair("c_centrocosto", ccosto));
	        nameValuePairs.add(new BasicNameValuePair("c_usuarioinspeccion", usui));
	        nameValuePairs.add(new BasicNameValuePair("d_fechainspeccion", fechai));
	        nameValuePairs.add(new BasicNameValuePair("c_estado","E"));
	        nameValuePairs.add(new BasicNameValuePair("c_usuarioenvio",usue));
	        nameValuePairs.add(new BasicNameValuePair("d_fechaenvio", fechae));
	        nameValuePairs.add(new BasicNameValuePair("c_ultimousuario", usuu));
	        nameValuePairs.add(new BasicNameValuePair("d_ultimafechamodificacion", fechau));		        
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
	        
	        HttpResponse respuesta = cliente.execute(httppost);
			
	    	InputStream contenido = respuesta.getEntity().getContent();
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(contenido));
	    	
			publishProgress(40);
			
			while((data = reader.readLine())!=null){				
				html +=data;				
			}
			
			Log.d("html",html);			
			reader.close();
			contenido.close();
			
			publishProgress(100);
			
		} catch (MalformedURLException e) {			
			e.printStackTrace();			
			cancel(true);
			return "";
			
		} catch (IOException e) {
			e.printStackTrace();			
			cancel(true);
			return "";
			
		}catch (IllegalArgumentException e) {
			e.printStackTrace();			
			cancel(true);
			return "";
			
		}catch (Exception e) {
			e.printStackTrace();			
			cancel(true);
			return "";
		} 
			
		finally{			
			try{
				JSONObject respJSON;
				String status="";
			
				try{
					respJSON = new JSONObject(html.toString());
					status = respJSON.getString("status");
			
				}catch (JSONException e) {
					e.printStackTrace();
					cancel(true);
					return "";
				}
		
				if(status.equals("0")){		
					return "0";			
				}else if(status.equals("1")){
					String resp="";		 
					JSONObject respJSON2;
				
					try {
						respJSON2 = new JSONObject(html.toString());
						idca = respJSON2.getString("mensaje");
						Log.d("idca", idca);
						resp="1";   
					
					} catch (JSONException e) {
						e.printStackTrace();
					}			
					
					return resp;
				}
			
			} catch (Exception e) {
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
		
		//Log.e("entr despues de ere","etrno despue de misdim");
		if(result.equals("")){
			
			SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(context);
	  		Editor ed=p.edit();
	        ed.putString("grabo", "no");
		    ed.commit();
		     
		    DataBase basededatos = new DataBase (context, "DBInspeccion", null, 1);	
			SQLiteDatabase db = basededatos.getWritableDatabase();
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Calendar ca = Calendar.getInstance();
		    String fechafin = df.format(ca.getTime());
		    
		    String c_cabecera="";
		    
		    String query = "SELECT n_correlativo from PENDIENTE_MTP_INSPECCIONGENERAL_CAB order by n_correlativo DESC limit 1 ";
            Cursor c = db.rawQuery(query,null);
            if( c.moveToFirst()) 
            {
            	c_cabecera= c.getString(0);
                c_cabecera=String.valueOf(Integer.parseInt(c_cabecera)+1);
                
            }else{
            	
            	c_cabecera="1";
                
            }
            
            ContentValues conc = new ContentValues();
		    conc.put("c_compania", Util.compania);
		    conc.put("n_correlativo", corr);
		    conc.put("c_tipoinspeccion", tipoi);
		    conc.put("c_maquina", cma);
		    conc.put("c_centrocosto", ccosto);
		    conc.put("c_comentario", com);
		    conc.put("c_usuarioInspeccion", usui);
		    conc.put("d_fechaInspeccion", fechai);
		    conc.put("c_estado", "I");
		    conc.put("c_usuarioenvio", usue);
		    conc.put("d_fechaenvio", fechae);		    
		    conc.put("c_ultimousuario", usuu);
		    conc.put("d_ultimafechamodificacion", fechau);
		    
		    db.insert("PENDIENTE_MTP_INSPECCIONGENERAL_CAB", null,conc);
		    
		    db.close();
		    
			Toast t=Toast.makeText(context,"Servidor no disponible", Toast.LENGTH_SHORT);
			t.show();
			
		}else{
			
			//update a enviado
			
			if(result.equals("0")){
				
				Toast t=Toast.makeText(context,"No se pudo almacenar error webservice!", Toast.LENGTH_SHORT);
				t.show();
				
				SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(context);
		  		Editor ed=p.edit();
		        ed.putString("grabo", "no");
			    ed.commit();	
			    
			    DataBase basededatos = new DataBase (context, "DBInspeccion", null, 1);	
				SQLiteDatabase db = basededatos.getWritableDatabase();
				
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    Calendar ca = Calendar.getInstance();
			    String fechafin = df.format(ca.getTime());
			    String c_cabecera="";
			    
			    String query = "SELECT n_correlativo from PENDIENTE_MTP_INSPECCIONGENERAL_CAB  order by n_correlativo DESC limit 1 ";
	            Cursor c = db.rawQuery(query,null);
	            if( c.moveToFirst()) 
	            {
	            	c_cabecera= c.getString(0);
	                c_cabecera=String.valueOf(Integer.parseInt(c_cabecera)+1);
	                
                }else{
                	
                	c_cabecera="1";	
                	
                }

	            ContentValues conc = new ContentValues();
	            conc.put("c_compania", Util.compania);
			    conc.put("n_correlativo", c_cabecera);
			    conc.put("c_tipoinspeccion", tipoi);
			    conc.put("c_maquina", cma);
			    conc.put("c_centrocosto", ccosto);
			    conc.put("c_comentario", com);
			    conc.put("c_usuarioInspeccion", usui);
			    conc.put("d_fechaInspeccion", fechai);
			    conc.put("c_estado", "I");
			    conc.put("c_usuarioenvio", usue);
			    conc.put("d_fechaenvio", fechae);		    
			    conc.put("c_ultimousuario", usuu);
			    conc.put("d_ultimafechamodificacion", fechau);

			    db.insert("PENDIENTE_MTP_INSPECCIONGENERAL_CAB", null,conc);

			    db.close();
				
			    Log.d("registro","no registro");
		    
		    }else if(result.equals("1")){
					
		    	Log.d("registro","si registro");  

		    	DataBase basededatos = new DataBase (context, "DBInspeccion", null, 1);	
		    	SQLiteDatabase db = basededatos.getWritableDatabase();
		    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    Calendar ca = Calendar.getInstance();
			    String fechafin = df.format(ca.getTime());
		    	ContentValues cv = new ContentValues();
		    	cv.put("c_estado","E");
		    	cv.put("c_usuarioenvio", usue);
		    	cv.put("d_fechaenvio",fechafin);
		    	db.update(" MTP_INSPECCIONGENERAL_CAB", cv, "n_correlativo = ?", new String[]{ cabe});
		    	//db.execSQL("DELETE FROM PENDIENTE_MTP_INSPECCIONMAQUINA_CAB");
		    	// db.execSQL("DELETE FROM PENDIENTE_MTP_INSPECCIONMAQUINA_DET");
		    	db.close();

		    	SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		    	Editor ed=p.edit();
		    	ed.putString("data", "no");
		    	ed.putString("grabo", "si");
		    	ed.commit();

		    	final EnviarReporteDetalleInspeccionGAsyncTask  enviarinspeccion=new 
		    		  EnviarReporteDetalleInspeccionGAsyncTask(context,idca,corr,cabe,
		    		  Util.compania,tipoi,cma,com,usui,fechai,"E",usue,fechae,usuu,fechau,lista);
		    	
		    	enviarinspeccion.execute(new String[] { Util.url + "registrarInspeccionDetalleG" });
		    	
	    		Thread thread = new Thread(){
		              public void run(){
		                  try {
		                	  enviarinspeccion.get(300000, TimeUnit.MILLISECONDS);
		                     // tarea.get(30000, TimeUnit.MILLISECONDS);
		                	  Log.e("Detalle entro a try run()", "Detalle entro a try run()");
		                	  
		                  } catch (Exception e) {
		                	  enviarinspeccion.cancel(true);                           
		                      ((Activity) context).runOnUiThread(new Runnable()
		                      {
		                    	  
		                          public void run(){
		                          	enviarinspeccion.progressDialogg.dismiss();
		                            Toast.makeText(context, "No se pudo establecer comunicacion.",
		                            Toast.LENGTH_LONG).show();
		                            Log.e("Detalle entro a catch run()", "Detalle entro a catch run()");
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
	}

	@Override
	protected void onProgressUpdate(Integer... values) {//paso2
		// TODO Auto-generated method stub

		//super.onProgressUpdate(values);
		progressDialog.setProgress(values[0]);
	}
	
}

