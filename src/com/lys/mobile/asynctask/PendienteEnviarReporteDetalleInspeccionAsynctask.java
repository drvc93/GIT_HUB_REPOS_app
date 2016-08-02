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

public class PendienteEnviarReporteDetalleInspeccionAsynctask extends AsyncTask<String,Integer,String>{

	Context context;
	public  ProgressDialog progressDialogg;
    String corr="",cabe="",use="",cma="",cond="",comenta="",esta="",perio="",fechainic="", fechafi="",idca="";

   int posicion=0;
	public   ArrayList<InspeccionData> lista;  
	
	public PendienteEnviarReporteDetalleInspeccionAsynctask (Context c,String idc,String corre,String cabecera,String compania,String user,String cmaquina,String condicion,String comentario,String estado,String periodo,String fechainicio,String fechafin, ArrayList<InspeccionData> detalle) {
		super();
		context=c;
		lista=detalle;
	corr=corre;
	idca=idc;
		 cabe=cabecera;
		 use=user;
		 cma=cmaquina;
		 cond=condicion;
		comenta=comentario;
		esta=estado;
		perio=periodo;
		fechainic=fechainicio; 
		fechafi=fechafin;

	    progressDialogg=new ProgressDialog(c);
		progressDialogg.setMessage("Enviando Reporte Informacion Detalles ..");
		progressDialogg.setCancelable(false);
	
	
	
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
			     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			        Calendar ca = Calendar.getInstance();
			        String fechafin = df.format(ca.getTime());
		
				for(int i=0;i<lista.size();i++){
					
					
					posicion=i;
			 
			 
		       List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		        
		   
			
		        nameValuePairs.add(new BasicNameValuePair("c_compania",Util.compania));

		        
		        nameValuePairs.add(new BasicNameValuePair("idcabecera", idca));
		        nameValuePairs.add(new BasicNameValuePair("n_linea", lista.get(i).n_linea));
		        nameValuePairs.add(new BasicNameValuePair("c_inspeccion", lista.get(i).c_inspeccion));
		        nameValuePairs.add(new BasicNameValuePair("c_tipoinspeccion", lista.get(i).c_tipoinspeccion));
		        nameValuePairs.add(new BasicNameValuePair("n_porcentajeinspeccion", lista.get(i).n_porcentajeinspeccion));
		        nameValuePairs.add(new BasicNameValuePair("n_porcentajeminimo", lista.get(i).n_porcentajeminimo));
		        nameValuePairs.add(new BasicNameValuePair("n_porcentajemaximo", lista.get(i).n_porcentajemaximo));
		        nameValuePairs.add(new BasicNameValuePair("c_estado", lista.get(i).c_estado));
		        nameValuePairs.add(new BasicNameValuePair("c_comentario", lista.get(i).c_comentario));
		        nameValuePairs.add(new BasicNameValuePair("c_rutafoto", lista.get(i).c_rutafoto));
		        nameValuePairs.add(new BasicNameValuePair("c_ultimousuario", use));
		        nameValuePairs.add(new BasicNameValuePair("d_ultimafechamodificacion",  fechafin ));
		        nameValuePairs.add(new BasicNameValuePair("c_maquina", cma));
		        nameValuePairs.add(new BasicNameValuePair("c_condicionmaquina", cond));
		        nameValuePairs.add(new BasicNameValuePair("c_comentarioc", comenta));
		        nameValuePairs.add(new BasicNameValuePair("c_estadoc","E"));
		        nameValuePairs.add(new BasicNameValuePair("d_fechaInicioInspeccionc", fechainic));
		        nameValuePairs.add(new BasicNameValuePair("d_fechaFinInspeccionc", fechafi));
		        nameValuePairs.add(new BasicNameValuePair("c_usuarioInspeccionc",use));
		        nameValuePairs.add(new BasicNameValuePair("c_usuarioenvioc",use));
		        nameValuePairs.add(new BasicNameValuePair("d_fechaenvioc", fechafin));
		        nameValuePairs.add(new BasicNameValuePair("c_ultimousuarioc", use));
		        nameValuePairs.add(new BasicNameValuePair("d_ultimafechamodificacionc", fechafin));
		        nameValuePairs.add(new BasicNameValuePair("c_periodoinspeccion", perio));
		       
		       
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
				
				
				Log.d("grabo posicion:",String.valueOf(posicion));
		
				
				
				
				}
				
				
				
				
				
				
				
			
				
				
			
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
		
				 
					Log.d("graboo",String.valueOf(html));
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
					String cad = respJSON2.getString("mensaje");
				
					
			
						
		
					
					
				       
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
		progressDialogg.dismiss();
		//myAnim.cancel();
	
		if(result.equals("")){
			
		
			    
			 Toast t=Toast.makeText(context,"Servidor no disponible", Toast.LENGTH_SHORT);
			 t.show();
			
		}else{
			
			 //update a enviado
		
				
		    if(result.equals("0")){
		    	
		  
					
		    	  Toast t=Toast.makeText(context,"No se pudo almacenar error webservice!", Toast.LENGTH_SHORT);
				t.show();
				
				
			     Log.d("registro","no registro");
		    
		    }else if(result.equals("1")){
					
		    	  Log.d("registro","si registro");
		    	
		    		DataBase basededatos = new DataBase (context, "DBInspeccion", null, 1);	
		    	    SQLiteDatabase db = basededatos.getWritableDatabase();
		    	    
		
		    	  //  db.execSQL("DELETE FROM PENDIENTE_MTP_INSPECCIONMAQUINA_CAB");
		    	  db.execSQL("DELETE FROM PENDIENTE_MTP_INSPECCIONMAQUINA_DET");
		    	    db.close();
		        
		    	    
		    	    SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(context);
		    		Editor ed=p.edit();
		    	    ed.putString("data", "no");
		    	    ed.putString("grabo", "si");
		    	    ed.commit();
		    	 
				     Toast.makeText(context,"Se Envio Reporte", Toast.LENGTH_SHORT).show();

		    	   // Intent z = new Intent(context,Inspeccion.class);
			       // context.startActivity(z);
			      ((Activity)context).finish();

		    	
					
			}
		    
			
				
				  	
			}
			
			
			
		}
		
		
		
	

	@Override
	protected void onPreExecute() {//paso1
		// TODO Auto-generated method stub
		super.onPreExecute();
		progressDialogg.show();
	
	
    }
	 @Override
	    protected void onCancelled() {
		 progressDialogg.cancel();
	}



	@Override
	protected void onProgressUpdate(Integer... values) {//paso2
		// TODO Auto-generated method stub
		
		
		super.onProgressUpdate(values);
		progressDialogg.setProgress(values[0]);
		
		
	}
	
	
	
	
}

