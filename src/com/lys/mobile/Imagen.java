package com.lys.mobile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

/**
 * Created by User on 17/09/2014.
 */
public class Imagen extends Activity implements View.OnClickListener {

    private ImageView imageview=null;
    private Button btn;
    Intent i;
    Bitmap bmp;
    final static int cons = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagen);

        btn = (Button)findViewById(R.id.btnCaptura);
        btn.setOnClickListener(this);
        imageview= (ImageView)findViewById(R.id.imageView_image);
        imageview.setImageResource(0);

    }
    @Override
    public void onClick(View arg0) {

            int id;
            id=arg0.getId();
            switch(id)
            {
                case R.id.btnCaptura:
                    i=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i,cons);
                    break;

        }
    }
    @Override///para tomar la foto
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK)
        {
            Bundle ext = data.getExtras();
            bmp = (Bitmap)ext.get("data");
            imageview.setImageBitmap(bmp);
        }

    }

}

