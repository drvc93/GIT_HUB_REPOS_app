package com.lys.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by User on 15/09/2014.
 */
public class LlenarComentario extends Activity implements View.OnClickListener{
    private Button guardar;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        //blablabla
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comentariohill1);
        guardar=(Button)findViewById(R.id.scomentario);
        //
        guardar.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scomentario:
                finish();
                break;
        }

        }
}
