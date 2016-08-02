package com.lys.mobile.asynctask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.Util;

public class ModificaFoto  extends AsyncTask<String,Integer,String>{

	Context context;

    String corr="",lin="",fo="",idca,path="",cabe="";

 
	public   ArrayList<InspeccionData> lista;  
	
	public ModificaFoto(Context c,String corre,String linea,String foto,String cab){
	super();
		context=c;
		lin=linea;
	    corr=corre;
		fo=foto;
		cabe=cab;
		

	
	
	
	 }
	@Override
	public String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		
	

		String url=params[0];
		DefaultHttpClient cliente=new DefaultHttpClient();
	
		HttpPost httppost=new HttpPost(url);
		String data="";
		String html="";
			
				
			publishProgress(10);
			Log.d("Logueo",String.valueOf(url));
			
			
			try {
			   
					
					
				
			 
			 
		       List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		        
		   
			
		      //  nameValuePairs.add(new BasicNameValuePair("c_compania",Util.compania));

		        nameValuePairs.add(new BasicNameValuePair("correlativo", corr));
		        nameValuePairs.add(new BasicNameValuePair("linea", lin));
		        nameValuePairs.add(new BasicNameValuePair("foto",fo));
		        
		       
		       
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		  
		        
				HttpResponse respuesta=cliente.execute(httppost);
			
		    	InputStream contenido=respuesta.getEntity().getContent();
		    	BufferedReader reader=new BufferedReader(new InputStreamReader(contenido));
				
				publishProgress(40);
			
				
				while((data=reader.readLine())!=null){
			
					html +=data;
					
				
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
			}catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
			
				e.printStackTrace();
			
				return "";
			}	catch (Exception e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			
				return "";
			} 
			
			
			 finally{
		
				 
				//	Log.d("graboo",String.valueOf(html));
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
					idca = respJSON2.getString("mensaje");
					path = respJSON2.getString("path");
				//	idca = respJSON2.getString("id");
				
					
			
						
		
					
					
				       
		            resp="1";   
					
			} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			
		
			
		
	
			return resp;
			
			
			}
			
		
			
			
		   } catch (Exception e) {
				// TODO Auto-generated catch block
				Toast t=Toast.makeText(context,"Error de Servidor", Toast.LENGTH_SHORT);
				t.show();
				e.printStackTrace();
			
		   }
		

			   
			
	  }
			
			return "";
			
	}

	
	@Override
	
	protected void onPostExecute(String result) {//paso 4
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		//myAnim.cancel();
		
		if(result.equals("")){
			
			
	
			
		     
		     
			   Log.d("registro","no registro");
			
		}else{
			
			 //update a enviado
		
				
		    if(result.equals("0")){
		    	
		  
					
		    	
				    
				    
				   
			     Log.d("registro","no registro");
		    
		    }else if(result.equals("1")){
					
		    	  Log.d("registro","si registro");  
		  
		        DataBase basededatos = new DataBase (context, "DBInspeccion", null, 1);	
		    	    SQLiteDatabase db = basededatos.getWritableDatabase();
		    	    
		    	    ContentValues cv = new ContentValues();
		    	    cv.put("c_rutafoto",path);
		    	   db.update(" MTP_INSPECCIONMAQUINA_DET", cv, "n_correlativo = ? and n_linea = ?",new String[]{cabe,lin});
		
		    	    db.close();
					
			}
		    
			
				
				  	
			}
			
			
			
		}
		
		
		
	

	@Override
	protected void onPreExecute() {//paso1
		// TODO Auto-generated method stub
		super.onPreExecute();
	
	
	
    }
	 @Override
	    protected void onCancelled() {

	}



	@Override
	protected void onProgressUpdate(Integer... values) {//paso2
		// TODO Auto-generated method stub
		
		
		super.onProgressUpdate(values);
	;
		
		
	}
	
	
	
	
}

