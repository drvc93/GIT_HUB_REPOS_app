package com.lys.mobile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class VerImagen extends Dialog {
	public interface ReadyListener {
		public void ready(String name);
	}

	private String name = "";
	private ReadyListener readyListener;
	ImageView imagen;

	public VerImagen(Context context, String name, ReadyListener readyListener) {
		super(context);
		this.name = name;
		this.readyListener = readyListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verimagen);
		setTitle("Ver Imagen");
		Button buttonOK = (Button) findViewById(R.id.btnCerrar);
		imagen = (ImageView) findViewById(R.id.imagen);
		buttonOK.setOnClickListener(new OKListener());

		Bitmap myBitmap = BitmapFactory.decodeFile(name);
		imagen.setImageBitmap(myBitmap);

	}

	private class OKListener implements android.view.View.OnClickListener {
		public void onClick(View v) {
			// readyListener.ready(String.valueOf(etName.getText()));
			VerImagen.this.dismiss();
		}
	}

}
