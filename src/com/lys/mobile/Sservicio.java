package com.lys.mobile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import com.lys.mobile.VerListaImagen;
import com.lys.mobile.MyApp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

/**
 * Created by User on 27/10/2014.
 */
public class Sservicio extends Activity implements View.OnClickListener {
	TextView lblCodigo;
	MyApp app;
	private static final int REQUEST_CAMERA = 1;
	public static final int MEDIA_TYPE_IMAGE = 1;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sservicio);
        app = ((MyApp) getApplicationContext());
        lblCodigo = (TextView) findViewById(R.id.lblCodigo);
    }
        @Override
    public void onClick(View v) {

    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			String dataclass = data.getComponent().getShortClassName();
			
		} else if (requestCode == REQUEST_CAMERA) {
			if (resultCode == Activity.RESULT_OK) {
				Bitmap x = (Bitmap) data.getExtras().get("data");
				//finishFromChild(this);
				ActualizarFotoTomada(x);
			}
		}
	}

	public void ActualizarFotoTomada(Bitmap x) {
		String thumbnailPath = "", largeImagePath = "", sort = "", nuevocodigo = "";
		File fileNew, fileGaleria, fileThumbail;
		Bitmap y;

		// Imagen de Galeria
		String[] largeFileProjection = { BaseColumns._ID, MediaColumns.DATA };
		sort = BaseColumns._ID + " DESC";
		Cursor myCursor = this.managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				largeFileProjection, null, null, sort);
		try {
			myCursor.moveToFirst();
			largeImagePath = myCursor.getString(myCursor
					.getColumnIndexOrThrow(MediaColumns.DATA));
		} finally {
			myCursor.close();
		}

		fileGaleria = new File(largeImagePath);
		fileGaleria.delete();
		fileGaleria = null;

		// Genera nueva imagen
		try {

			nuevocodigo = getGenerarCodigoImagen();
			fileNew = new File(nuevocodigo);
			Uri outputFileUri = Uri.fromFile(fileNew);

			OutputStream outstream = getContentResolver().openOutputStream(
					outputFileUri);
			//x.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
			y = redimensionarImagenMaximo(x, 500, 450);
			y.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
			outstream.close();
			outstream = null;
			fileNew = null;
			outputFileUri = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		EventoUltimaFoto(nuevocodigo);
	}
        
    public void EventoTomarFoto(View view) {
		String state = Environment.getExternalStorageState();
		Log.e("state",state);
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, REQUEST_CAMERA);
		}
	}
    
    public void EventoUltimaFoto(String photoPath) {
		if (!photoPath.equals("") && (photoPath != null)) {
			String nombreimg = photoPath;
			VerImagen dialogo = new VerImagen(this, nombreimg, null);
			dialogo.show();
		}
	}
    
    public void EventoListarFotos(View v) {
		String rutaFoto = Environment.getExternalStorageDirectory()
				+ app.getRutaInspeccion();
				//+ lblCodigo.getText().toString().toLowerCase();
		String codigo = lblCodigo.getText().toString().toLowerCase();

		File f = new File(rutaFoto);
		if (f.list() == null) {
			Toast.makeText(getApplicationContext(),
					"No existe carpeta de fotos.", Toast.LENGTH_LONG).show();
			return;
		}

		if (f.listFiles().length == 0) {
			Toast.makeText(getApplicationContext(),
					"No existen archivos en carpeta actual.", Toast.LENGTH_LONG)
					.show();
			return;
		}

		f = null;
		
		Log.e("Ruta Foto",rutaFoto);
		Log.e("Codigo",codigo);
		
		Intent intent = new Intent().setClass(this, VerListaImagen.class);
		intent.putExtra("ruta", rutaFoto);
		intent.putExtra("codigo", codigo);
		this.startActivity(intent);
	}
    
    private String getGenerarCodigoImagen() {
		String codigo = lblCodigo.getText().toString().toLowerCase();
		String nombre = codigo + "-";
		int max = 0;
		String valnum = "0";
		String vendedor = "MAQ";//
		String fecha = getFechaImagen();
		String iniciacon = vendedor.toLowerCase();
		String terminacon = ".jpg";

		String rutacarpeta = Environment.getExternalStorageDirectory()
				+ app.getRutaInspeccion(); // + codigo;
		File f = new File(rutacarpeta);

		if (f.list() == null) {
			f.mkdirs();
		}

		if (f.exists()) {
			File[] files = f.listFiles();
			for (File x : files) {
				String archivo = x.getName().toLowerCase();
				if (archivo.startsWith(iniciacon)
						&& archivo.endsWith(terminacon)) {
					valnum = archivo.substring(archivo.indexOf("-") + 1,
							archivo.lastIndexOf("_"));
					if (max < Integer.parseInt(valnum))
						max = Integer.parseInt(valnum);
				}
			}

			files = null;
		}

		f = null;

		nombre = nombre + String.valueOf(max + 1);
		nombre = vendedor.toLowerCase() + "_" + nombre + "_" + fecha + ".jpg";
		nombre = Environment.getExternalStorageDirectory()
				+ app.getRutaInspeccion() + "/" + nombre;
				//+ lblCodigo.getText().toString().toLowerCase() + "/" + nombre;
		return nombre;
	}
    
    public String getStringFecha(Date date) {
		String fecha = "";
		if (date != null) {
			java.text.SimpleDateFormat dfm = new java.text.SimpleDateFormat(
					"dd-MM-yyyy HH:mm");
			fecha = dfm.format(date);
		}
		return fecha;
	}

	public String getFechaImagen() {
		Date date = new Date();
		String fecha = "";
		if (date != null) {
			fecha = String.valueOf(date.getTime());
		}
		return fecha;
	}
 
	/**
	 * Redimensionar un Bitmap. By TutorialAndroid.com
	* @param Bitmap mBitmap
	* @param float newHeight
	* @param float newHeight
	 * @param float newHeight
	 * @return Bitmap
	 */
	public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth){
	   //Redimensionamos
	   int width = mBitmap.getWidth();
	   int height = mBitmap.getHeight();
	   float scaleWidth = ((float) newWidth) / width;
	   float scaleHeight = ((float) newHeigth) / height;
	   // create a matrix for the manipulation
	   Matrix matrix = new Matrix();
	   // resize the bit map
	   matrix.postScale(scaleWidth, scaleHeight);
	   // recreate the new Bitmap
	   return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
	}
	
}