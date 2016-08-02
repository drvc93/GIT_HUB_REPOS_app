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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.lys.mobile.MyApp;
import com.lys.mobile.data.FotoData;
//import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.data.InspeccionGData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.Util;

public class EnviarReporteDetalleInspeccionGAsyncTask extends AsyncTask<String,Integer,String>{
	
	Context context;
	public ProgressDialog progressDialogg;
    String corr="",cabe="",cma="",tipoi="",com="",usui="",fechai="",esta="",usue="",fechae="",usuu="", fechau="",idca="";
    
    int posicion=0;
	public ArrayList<InspeccionGData> lista;  
	
	public EnviarReporteDetalleInspeccionGAsyncTask (Context c,String idc,String corre,String cabecera,
			String compania,String tipoinspeccion,String cmaquina,String comentario,String usuinsp,
			String fechainsp,String estado,String usuenv,String fechaenv,String usuult,
			String fechault,ArrayList<InspeccionGData> detalle) {
			
			super();
			context=c;
			lista=detalle;
			corr=corre;
			idca=idc;
			cabe=cabecera;
			tipoi=tipoinspeccion;
			cma=cmaquina;
			com=comentario;
			usui=usuinsp;
			fechai=fechainsp;
			esta=estado;
			usue=usuenv;
			fechae=fechaenv;
			usuu=usuult; 
			fechau=fechault;

			progressDialogg=new ProgressDialog(c);
			progressDialogg.setMessage("Enviando Reporte Informacion Detalles ..");
			progressDialogg.setCancelable(false);

	 }
	@Override
	public String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		if (isCancelled())
		{
			//Toast.makeText(getApplication(), getApplication().getString(R.string.error_serverconnection), Toast.LENGTH_SHORT).show();
			return "";
		}
		
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
			
			for(int i = 0; i < lista.size(); i++) {
				posicion = i;
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
				nameValuePairs.add(new BasicNameValuePair("c_compania",Util.compania));
				nameValuePairs.add(new BasicNameValuePair("idcabecera", idca));
		        nameValuePairs.add(new BasicNameValuePair("n_linea", lista.get(i).getLinea()));
		        nameValuePairs.add(new BasicNameValuePair("c_comentario", lista.get(i).getComentario()));
		        nameValuePairs.add(new BasicNameValuePair("c_rutafoto",lista.get(i).getRutaFoto()));
		        nameValuePairs.add(new BasicNameValuePair("c_ultimousuario", usuu));
		        nameValuePairs.add(new BasicNameValuePair("d_ultimafechamodificacion", fechafin));
		        nameValuePairs.add(new BasicNameValuePair("c_tiporevisiong", lista.get(i).getTipoRevisionG()));
		        nameValuePairs.add(new BasicNameValuePair("c_flagadictipo", lista.get(i).getFlagAdicTipo()));
		        
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		        
		        HttpResponse respuesta = cliente.execute(httppost);
		        
		        InputStream contenido = respuesta.getEntity().getContent();
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(contenido));
				
				publishProgress(40);
				
				while((data = reader.readLine())!=null){
					
					html +=data;
					
				}
				
				reader.close();
				contenido.close();
				
				publishProgress(100);
				
				Log.d("grabo posicion:",String.valueOf(posicion));
				
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				cancel(true);
				return "";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
				cancel(true);
				return "";
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
			
				e.printStackTrace();
				cancel(true);
				return "";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
				cancel(true);
				return "";
			} 
			
			finally {
				
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
						Log.d("status", "0");
						return "0";

					}else if(status.equals("1")){   
						Log.d("status", "1");
						String resp="";
						JSONObject respJSON2;
						
						try {
							respJSON2 = new JSONObject(html.toString());
							String cad = respJSON2.getString("mensaje");
							resp="1";
							Log.d("resp=1", resp);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							Log.d("resp vacio", resp);
							e.printStackTrace();
						}
	
						return resp;
					
					}
			
				} catch (Exception e) {
					// TODO Auto-generated catch block
					;
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
		
		progressDialogg.dismiss();
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
		    
		    for(int i=posicion;i<lista.size();i++){
			
		    	String query = "SELECT n_correlativo from PENDIENTE_MTP_INSPECCIONGENERAL_CAB order by n_correlativo DESC limit 1 ";
	            Cursor c = db.rawQuery(query,null);
	            if(c.moveToFirst()) 
	                {
	            		c_cabecera= c.getString(0);
	                	c_cabecera=String.valueOf(Integer.parseInt(c_cabecera)+1);

	                } else {
	                	
	                	c_cabecera="1";	

	                }
	            
	            ContentValues con = new ContentValues();
			    con.put("c_compania", Util.compania);
			    con.put("n_correlativo", c_cabecera);
			    con.put("n_linea", lista.get(i).getLinea());
			    con.put("c_comentario", lista.get(i).getComentario());
			    con.put("c_rutafoto", lista.get(i).c_rutafoto);
			    con.put("c_ultimousuario", usuu);
			    con.put("d_ultimafechamodificacion",  fechafin);
			    con.put("c_tiporevisiong", lista.get(i).getTipoRevisionG());
			    con.put("c_flagadictipo", lista.get(i).getFlagAdicTipo());
			    
			    db.insert("PENDIENTE_MTP_INSPECCIONGENERAL_DET", null,con);
		    }
			
		    db.close();
			
		    Toast t=Toast.makeText(context,"Servidor no disponible", Toast.LENGTH_SHORT);
			t.show();
			
		}else{

			//update a enviado

			if(result.equals("0")){

				Toast t=Toast.makeText(context,"No se pudo almacenar error webservice!", Toast.LENGTH_SHORT);
				t.show();
				
				((Activity)context).finish();

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
			    
			    for(int i=posicion;i<lista.size();i++){
				
			    	String query = "SELECT n_correlativo from PENDIENTE_MTP_INSPECCIONGENERAL_CAB  order by n_correlativo DESC limit 1 ";
		            Cursor c = db.rawQuery(query,null);
		            
		            if( c.moveToFirst()) 
		            	{
		                	c_cabecera= c.getString(0);
		                	c_cabecera=String.valueOf(Integer.parseInt(c_cabecera)+1);

		                }else{

		                	c_cabecera="1";	
		                
		                }
		                
		            ContentValues con = new ContentValues();
		            con.put("c_compania", Util.compania);
				    con.put("n_correlativo", c_cabecera);
				    con.put("n_linea", lista.get(i).getLinea());
				    con.put("c_comentario", lista.get(i).getComentario());
				    con.put("c_rutafoto", lista.get(i).c_rutafoto);
				    con.put("c_ultimousuario", usuu);
				    con.put("d_ultimafechamodificacion", fechafin);
				    con.put("c_tiporevisiong", lista.get(i).getTipoRevisionG());
				    con.put("c_flagadictipo", lista.get(i).getFlagAdicTipo());
				    
				    db.insert("PENDIENTE_MTP_INSPECCIONGENERAL_DET", null,con);
			    	
			    }
			    
			    db.close();
			    
			    Log.d("registro","no registro");

		    }else if(result.equals("1")){

		    	Log.d("registro","si registro");
		    	
		    	SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(context);
		    	Editor ed=p.edit();
		    	ed.putString("data", "no");
		    	ed.putString("grabo", "si");
		    	ed.commit();

		    	final Actualizar2 tarea = new Actualizar2 (context,cabe);
		    	
				tarea.execute(new String[] { Util.url + "Transferir2/"+idca });

				Thread thread = new Thread() {
					public void run() {
						try {
							tarea.get(300000, TimeUnit.MILLISECONDS);
							// tarea.get(30000, TimeUnit.MILLISECONDS);

						} catch (Exception e) {
							tarea.cancel(true);
							((Activity) context).runOnUiThread(new Runnable() {
								public void run() {
									// acceder.setEnabled(true);
									Log.d("fno","n");
									}
								});
							}
						}
					};
					thread.start();
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
		 progressDialogg.dismiss();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {//paso2
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		progressDialogg.setProgress(values[0]);
	}

}

