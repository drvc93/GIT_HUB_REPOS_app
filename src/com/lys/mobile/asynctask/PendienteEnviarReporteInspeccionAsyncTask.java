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

public class PendienteEnviarReporteInspeccionAsyncTask extends AsyncTask<String,Integer,String>{

	Context context;
	public  ProgressDialog progressDialog;
    String corr="",cabe="",use="",cma="",cond="",comenta="",esta="",perio="",fechainic="", fechafi="",idca="";

 
	public   ArrayList<InspeccionData> lista;  
	
	public PendienteEnviarReporteInspeccionAsyncTask(Context c,String corre,String cabecera,String compania,String user,String cmaquina,String condicion,String comentario,String estado,String periodo,String fechainicio,String fechafin, ArrayList<InspeccionData> detalle) {
		super();
		context=c;
		lista=detalle;
	corr=corre;
		 cabe=cabecera;
		 use=user;
		 cma=cmaquina;
		 cond=condicion;
		comenta=comentario;
		esta=estado;
		perio=periodo;
		fechainic=fechainicio; 
		fechafi=fechafin;

	    progressDialog=new ProgressDialog(c);
		progressDialog.setMessage("Enviando Reporte Informacion ..");
		progressDialog.setCancelable(false);
	
	
	
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
		
				
					
					
				
			 
			 
		       List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		        
		   
			
		        nameValuePairs.add(new BasicNameValuePair("c_compania",Util.compania));

		        nameValuePairs.add(new BasicNameValuePair("n_linea", lista.get(0).n_linea));
		        nameValuePairs.add(new BasicNameValuePair("c_inspeccion", lista.get(0).c_inspeccion));
		        nameValuePairs.add(new BasicNameValuePair("c_tipoinspeccion", lista.get(0).c_tipoinspeccion));
		        nameValuePairs.add(new BasicNameValuePair("n_porcentajeinspeccion", lista.get(0).n_porcentajeinspeccion));
		        nameValuePairs.add(new BasicNameValuePair("n_porcentajeminimo", lista.get(0).n_porcentajeminimo));
		        nameValuePairs.add(new BasicNameValuePair("n_porcentajemaximo", lista.get(0).n_porcentajemaximo));
		        nameValuePairs.add(new BasicNameValuePair("c_estado", lista.get(0).c_estado));
		        nameValuePairs.add(new BasicNameValuePair("c_comentario", lista.get(0).c_comentario));
		        nameValuePairs.add(new BasicNameValuePair("c_rutafoto", lista.get(0).c_rutafoto));
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
		progressDialog.dismiss();
		//myAnim.cancel();
		
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
		        
			
		    	
				String query = "SELECT n_correlativo from PENDIENTE_MTP_INSPECCIONMAQUINA_CAB  order by n_correlativo DESC limit 1 ";
                Cursor c = db.rawQuery(query,null);
                if( c.moveToFirst()) 
                {
                	
                	c_cabecera= c.getString(0);
                	c_cabecera=String.valueOf(Integer.parseInt(c_cabecera)+1);
                
                }else{
                	
                	c_cabecera="1";	
                	
                
                }
                
                
			    
			    
		    	  ContentValues conc = new ContentValues();
		    	  conc.put("c_compania",Util.compania);
		    	  conc.put("n_correlativo",  corr);
		    	  conc.put("c_maquina", cma);
		    	  conc.put("c_condicionmaquina", cond);
		    	  conc.put("c_comentario", comenta);
		    	  conc.put("c_estado", "I");
		    	  conc.put("d_fechaInicioInspeccion", fechainic);
		    	  conc.put("d_fechaFinInspeccion", fechafi);
		    	  conc.put("c_usuarioInspeccion",use);
		    	  conc.put("c_usuarioenvio",use);
		    	  conc.put("d_fechaenvio", fechafi);
		    	  conc.put("c_ultimousuario", use);
		    	  conc.put("d_ultimafechamodificacion", fechafin);
		    	  conc.put("c_periodoinspeccion", perio);
		    	  
		    	
		    	  db.insert("PENDIENTE_MTP_INSPECCIONMAQUINA_CAB", null,conc);
			    
			    
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
			        
				
			    	
					String query = "SELECT n_correlativo from PENDIENTE_MTP_INSPECCIONMAQUINA_CAB  order by n_correlativo DESC limit 1 ";
	                Cursor c = db.rawQuery(query,null);
	                if( c.moveToFirst()) 
	                {
	                	
	                	c_cabecera= c.getString(0);
	                	c_cabecera=String.valueOf(Integer.parseInt(c_cabecera)+1);
	                
	                }else{
	                	
	                	c_cabecera="1";	
	                	
	                
	                }
	                
	                
				    
			    	  ContentValues conc = new ContentValues();
			    	  conc.put("c_compania",Util.compania);
			    	  conc.put("n_correlativo", c_cabecera);
			    	  conc.put("c_maquina", cma);
			    	  conc.put("c_condicionmaquina", cond);
			    	  conc.put("c_comentario", comenta);
			    	  conc.put("c_estado", "I");
			    	  conc.put("d_fechaInicioInspeccion", fechainic);
			    	  conc.put("d_fechaFinInspeccion", fechafi);
			    	  conc.put("c_usuarioInspeccion",use);
			    	  conc.put("c_usuarioenvio",use);
			    	  conc.put("d_fechaenvio", fechafi);
			    	  conc.put("c_ultimousuario", use);
			    	  conc.put("d_ultimafechamodificacion", fechafin);
			    	  conc.put("c_periodoinspeccion", perio);
			    	  
			    	
			    	  db.insert("PENDIENTE_MTP_INSPECCIONMAQUINA_CAB", null,conc);
				    
				    
			    	  db.close();
				    
				    
				    
				   
			     Log.d("registro","no registro");
		    
		    }else if(result.equals("1")){
					
		    	  Log.d("registro","si registro");
		    	
		    		DataBase basededatos = new DataBase (context, "DBInspeccion", null, 1);	
		    	    SQLiteDatabase db = basededatos.getWritableDatabase();
		    	    
		    	    ContentValues cv = new ContentValues();
		    	    cv.put("c_estado","E");
		    	   db.update(" MTP_INSPECCIONMAQUINA_CAB", cv, "n_correlativo = ?", new String[]{ cabe});
		    	    //db.execSQL("DELETE FROM PENDIENTE_MTP_INSPECCIONMAQUINA_CAB");
		    	 // db.execSQL("DELETE FROM PENDIENTE_MTP_INSPECCIONMAQUINA_DET");
		    	    db.close();
		        
		    	    
		    	    SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(context);
		    		Editor ed=p.edit();
		    	    ed.putString("data", "no");
		    	    ed.putString("grabo", "si");
		    	    ed.commit();
		    	 
		    	    
		    		 final EnviarReporteDetalleInspeccionAsyncTask  enviarinspeccion=new EnviarReporteDetalleInspeccionAsyncTask (context,idca,corr,cabe,Util.compania,use, cma,cond,comenta,"E",perio,fechainic,fechafi,lista);   
		   	    	 enviarinspeccion.execute( new String[]{Util.url+"registrarInspeccionDetalle"});
		    	      Thread thread = new Thread(){
			              public void run(){
			                  try {
			                	  enviarinspeccion.get(300000, TimeUnit.MILLISECONDS); 
			                     // tarea.get(30000, TimeUnit.MILLISECONDS); 

			                  } catch (Exception e) {
			                	  enviarinspeccion.cancel(true);                           
			                      ((Activity) context).runOnUiThread(new Runnable()
			                      {
			                           
			                          public void run()
			                           {
			                        	
			                          	enviarinspeccion.progressDialogg.dismiss();
			                              Toast.makeText(context, "No se pudo establecer comunicacion .", Toast.LENGTH_LONG).show();
			                            
			                           }
			                      });
			                  }
			              }   
		    	      };
			          thread.start();

		    	   // Intent z = new Intent(context,Inspeccion.class);
			       // context.startActivity(z);
			    //  ((Activity)context).finish();

		    	
					
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
		 progressDialog.cancel();
	}



	@Override
	protected void onProgressUpdate(Integer... values) {//paso2
		// TODO Auto-generated method stub
		
		
		super.onProgressUpdate(values);
		progressDialog.setProgress(values[0]);
		
		
	}
	
	
	
	
}


