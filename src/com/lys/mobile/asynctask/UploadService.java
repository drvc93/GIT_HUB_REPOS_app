package com.lys.mobile.asynctask;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.lys.mobile.Login;
import com.lys.mobile.data.FotoData;
import com.lys.mobile.util.*;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.lys.mobile.MyApp;
import com.lys.mobile.R;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UploadService extends IntentService{
    String ruta=" ";
    ArrayList<FotoData> fotos;
    MyApp app;
    String name="";

	private NotificationManager notificationManager;
	private Notification notification;
    private int serverResponseCode = 0;
	public UploadService(String name) {
		super(name);
	}
	
	public UploadService(){
		super("UploadService");
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		app  =(MyApp)getApplicationContext();
		notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
	    
		//if(fotos.size()>0)fotos.clear();
		fotos = app.fotos;
	  
	    Log.i("rutaaaaaaaaaaaa", String.valueOf(ruta));
		Thread t = new Thread(new BackgroundThread(this));
		t.start();
	}

	private class BackgroundThread implements Runnable, CountingInputStreamEntity.UploadListener {
		
		Context context;  

		int lastPercent = 0;
		
		public BackgroundThread(Context context) {
			this.context = context;
			
		}

		@SuppressWarnings("deprecation")
		public void run() {

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Calendar ca = Calendar.getInstance();
		    String fechafin = df.format(ca.getTime());
	
			Intent notificationIntent = new Intent();
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

			notification = new Notification(R.drawable.ic_launcher,
					"Subiendo imagen", System.currentTimeMillis());
			notification.flags = notification.flags
					| Notification.FLAG_ONGOING_EVENT;
			notification.contentView = new RemoteViews(getApplicationContext()
					.getPackageName(), R.layout.upload_progress_bar);
			notification.contentIntent = contentIntent;
			notification.contentView.setProgressBar(R.id.progressBar1, 100,0, false);
			
			notificationManager.notify(1, notification);
			
			Log.i("FOO", "Notification started");
			
	            HttpURLConnection conn = null;
			    DataOutputStream dos = null;  
			    String lineEnd = "\r\n";
			    String twoHyphens = "--";
			    String boundary = "*****";
			    int bytesRead, bytesAvailable, bufferSize;
			    byte[] buffer;
			    int maxBufferSize = 1 * 1024 * 1024; 

				for(int i=0;i<fotos.size();i++){
			    
					 fechafin=fechafin.replaceAll("-","" );
		             fechafin=fechafin.replaceAll(":","" );
		             fechafin=fechafin.replaceAll(" ","" );
			    
			    ruta=fotos.get(i).ruta;
			  	String carpetaFoto = "/storage/sdcard0/LysConfig/Fotos/";
					ruta  = carpetaFoto+ruta;
					Log.i("Nuev ruuta de fotoo ====>", ruta);
			    Log.i("ruta foto ===> ", ruta);
	              
			    File sourceFile = new File(ruta); 
			
			try {
				
				
				Log.i("FOO", "About to call httpClient.execute");
			
				String no=fotos.get(i).nombre+fechafin;

			    FileInputStream fileInputStream = new FileInputStream(sourceFile);
	             URL url = new URL(Util.url+"upload");
	              
	             conn = (HttpURLConnection) url.openConnection(); 
	             conn.setDoInput(true); // Allow Inputs
	             conn.setDoOutput(true); // Allow Outputs
	             conn.setUseCaches(false); // Don't use a Cached Copy
	             conn.setRequestMethod("POST");
	             conn.setRequestProperty("Connection", "Keep-Alive");
	             conn.setRequestProperty("ENCTYPE", "multipart/form-data");
	             conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
	             conn.setRequestProperty("file", ruta); 
	             conn.setRequestProperty("linea",fotos.get(i).idlinea);
	             conn.setRequestProperty("correlativo", fotos.get(i).idcorrelativo);
	              conn.setRequestProperty("uploaded_file",ruta);
	             Log.i("rutafoto", ruta);
	           
	            
	             dos = new DataOutputStream(conn.getOutputStream());
	    
	             dos.writeBytes(twoHyphens + boundary + lineEnd); 
	             dos.writeBytes("Content-Disposition: form-data; name=\"file\"; fileName=\""
                         +  no  + "\"" + lineEnd);

	             dos.writeBytes(lineEnd);  
	    
	          
	             bytesAvailable = fileInputStream.available(); 
	    
	             
	             Log.i("bytes",String.valueOf( bytesAvailable));
	             bufferSize = Math.min(bytesAvailable, maxBufferSize);
	             buffer = new byte[bufferSize];
	    
	             // read file and write it into form...
	             bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
	                
	             while (bytesRead > 0) {
	                  
	               dos.write(buffer, 0, bufferSize);
	               bytesAvailable = fileInputStream.available();
	               bufferSize = Math.min(bytesAvailable, maxBufferSize);
	               bytesRead = fileInputStream.read(buffer, 0, bufferSize);   

	              }

	             dos.writeBytes(lineEnd);
	             dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

	             serverResponseCode = conn.getResponseCode();
	             String serverResponseMessage = conn.getResponseMessage();
	               
	             Log.i("uploadFile", "HTTP Response is : "
	                     + serverResponseMessage + ": " + serverResponseCode);
	            
	        	 final  ModificaFoto  enviarinspeccion=new  ModificaFoto(context, fotos.get(i).idcorrelativo,fotos.get(i).idlinea,no,fotos.get(i).cabe);   
	   	    	 enviarinspeccion.execute( new String[]{Util.url+"modificarFoto"});
	    	      Thread thread = new Thread(){
		              public void run(){
		                  try {
		                	  enviarinspeccion.get(120000, TimeUnit.MILLISECONDS); 
		                     // tarea.get(30000, TimeUnit.MILLISECONDS); 

		                  } catch (Exception e) {
		                	  enviarinspeccion.cancel(true);                           
		                      ((Activity) context).runOnUiThread(new Runnable()
		                      {
		                           
		                          public void run()
		                           {
		                        	  
		                              Toast.makeText(context, "No se pudo establecer comunicacion al registrar foto .", Toast.LENGTH_LONG).show();
		                            
		                           }
		                      });
		                  }
		              }   
	    	      };
		          thread.start();
			    
				fileInputStream.close();
	             dos.flush();
	             dos.close();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				if (serverResponseCode == 200) {
					
					//modi9fica
					Log.i("FOO", "All done");
					//Log.i()
				} else {
					Log.i("FOO", "Screw up with http - " + "error");
				}

				}

				notification.setLatestEventInfo(context, "Subiendo ", "Subido", contentIntent);
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(1, notification);
			
		}

		public void onChange(int percent) {
			if(percent > lastPercent) {
				notification.contentView.setProgressBar(R.id.progressBar1, 100, percent, false);
				notificationManager.notify(1, notification);
				lastPercent = percent;
			}
		}
		
	}

}
