package com.lys.mobile.util;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.lys.mobile.dataBase.InspGenCabDB;
import com.lys.mobile.dataBase.InspGenDetDB;
import com.lys.mobile.dataBase.InspMaqCabDB;
import com.lys.mobile.dataBase.InspMaqDetDB;

import java.util.ArrayList;

/**
 * Created by dvillanueva on 18/07/2016.
 */
public class ProgressBarDailogo extends AsyncTask<Void,Void,Void> {

    ProgressDialog progressDialog;
    Context context;
    String tableName;
    ArrayList<InspMaqCabDB> listCabecera;
    ArrayList<InspMaqDetDB> listDetalle;
    ArrayList <InspGenCabDB> listGenCab;
    ArrayList<InspGenDetDB>listGenDet;

    public ProgressBarDailogo(Context context, ArrayList<InspMaqCabDB> listCabecera, ArrayList<InspMaqDetDB> listDetalle, ArrayList<InspGenCabDB> listGenCab, ArrayList<InspGenDetDB> listGenDet, ProgressDialog progressDialog, String tableName) {
        this.context = context;
        this.listCabecera = listCabecera;
        this.listDetalle = listDetalle;
        this.progressDialog = progressDialog;
        this.tableName = tableName;
        this.listGenCab = listGenCab;
        this.listGenDet = listGenDet;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(context, "Sincronziación correcta.", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
    progressDialog.show();


    }

    @Override
    protected Void doInBackground(Void... voids) {


        DataBase basededatos = new DataBase (context, "DBInspeccion", null, 1);
        SQLiteDatabase dbDelete = basededatos.getWritableDatabase();



            dbDelete.execSQL("delete from  MTP_INSPECCIONMAQUINA_CAB");
            dbDelete.close();
            for (int i =  0 ; i< listCabecera.size(); i++){
                ContentValues contentValues = new ContentValues();
                SQLiteDatabase db = basededatos.getWritableDatabase();

                InspMaqCabDB inpCab = listCabecera.get(i);
                contentValues.put("c_compania", inpCab.getC_compania());
                contentValues.put("n_correlativo",inpCab.getN_correlativo());
                contentValues.put("c_maquina",inpCab.getC_maquina());
                contentValues.put("c_condicionmaquina",inpCab.getC_condicionmaquina());
                if (inpCab.getC_comentario().equals("anyType{}")) {
                    contentValues.put("c_comentario", "-");
                }
                else {
                    contentValues.put("c_comentario", inpCab.getC_comentario());
                }
                contentValues.put("c_estado", inpCab.getC_estado());
                contentValues.put("d_fechaInicioInspeccion",inpCab.getD_fechaInicioInspeccion());
                contentValues.put("d_fechaFinInspeccion",inpCab.getD_fechaFinInspeccion());
                contentValues.put("c_periodoinspeccion",inpCab.getC_periodoinspeccion());
                contentValues.put("c_usuarioInspeccion",inpCab.getC_usuarioInspeccion());
                contentValues.put("c_usuarioenvio",inpCab.getC_usuarioenvio());
                contentValues.put("d_fechaenvio",inpCab.getD_fechaEnvio());
                contentValues.put("c_ultimousuario",inpCab.getC_ultimousuario());
                contentValues.put("d_ultimafechamodificacion",inpCab.getD_ultimafechamodificacion());
                long var= db.insert("MTP_INSPECCIONMAQUINA_CAB",null,contentValues);
                db.close();
                Log.i("long result  MAQ CAB ==> ",String.valueOf(var));
            }



        //  Log.i("Content value CAB size ==> " ,String.valueOf(contentValues.size()));

             dbDelete = basededatos.getWritableDatabase();
            dbDelete.execSQL("delete from MTP_INSPECCIONMAQUINA_DET");
            dbDelete.close();
            for (int i = 0 ; i< listDetalle.size(); i++){
                ContentValues contentValues = new ContentValues();
                SQLiteDatabase db = basededatos.getWritableDatabase();
                InspMaqDetDB inpDt = listDetalle.get(i);
                contentValues.put("c_compania", inpDt.getC_compania());
                contentValues.put("n_correlativo",inpDt.getN_correlativo());
                contentValues.put("n_linea",inpDt.getN_linea());
                contentValues.put("c_inspeccion",inpDt.getC_inpeccion());
                contentValues.put("c_tipoinspeccion",inpDt.getC_tipoinspeccion());
                contentValues.put("n_porcentajeminimo",inpDt.getN_porcentajeminimo());
                contentValues.put("n_porcentajemaximo",inpDt.getN_porcentajemaximo());
                contentValues.put("n_porcentajeinspeccion",inpDt.getN_pocentajeinspeccion());
                contentValues.put("c_estado",inpDt.getC_estado());
                contentValues.put("c_comentario",inpDt.getC_comentario());
                contentValues.put("c_rutafoto",inpDt.getC_rutafoto());
                contentValues.put("c_ultimousuario",inpDt.getC_ultimousuario());
                contentValues.put("d_ultimafechamodificacion",inpDt.getD_ultimafechamodificacion());
                long var= db.insert("MTP_INSPECCIONMAQUINA_DET",null,contentValues);
                db.close();
                Log.i("long result MAQ DET ==> ",String.valueOf(var));
            }

        dbDelete = basededatos.getWritableDatabase();
        dbDelete.execSQL("delete from MTP_INSPECCIONGENERAL_CAB");
        dbDelete.close();
            for (int i = 0 ; i<listGenCab.size();i++){

                ContentValues contentValues = new ContentValues();
                SQLiteDatabase db = basededatos.getWritableDatabase();

                InspGenCabDB inp =  listGenCab.get(i);

                contentValues.put("c_compania",inp.getC_compania());
                contentValues.put("n_correlativo",inp.getN_correlativo());
                contentValues.put("c_tipoinspeccion",inp.getC_tipoinspeccion());
                contentValues.put("c_maquina",inp.getC_maquina());
                contentValues.put("c_centrocosto",inp.getC_centrocosto());
                contentValues.put("c_comentario",inp.getC_comentario());
                contentValues.put("c_usuarioinspeccion",inp.getC_usuarioinspeccion());
                contentValues.put("d_fechainspeccion",inp.getD_fechainspeccion());
                contentValues.put("c_estado",inp.getC_estado());
                contentValues.put("c_usuarioenvio",inp.getC_usuarioenvio());
                contentValues.put("d_fechaenvio",inp.getD_fechaenvio());
                contentValues.put("c_ultimousuario",inp.getC_ultimousuario());
                contentValues.put("d_ultimafechamodificacion",inp.getD_ultimafechamodificacion());
                long var= db.insert("MTP_INSPECCIONGENERAL_CAB",null,contentValues);
                db.close();
                Log.i("long result GEN CAB ==> ",String.valueOf(var));


            }

        dbDelete = basededatos.getWritableDatabase();
        dbDelete.execSQL("delete from MTP_INSPECCIONGENERAL_DET");
        dbDelete.close();
        for (int i = 0 ; i<listGenDet.size();i++){

            ContentValues contentValues = new ContentValues();
            SQLiteDatabase db = basededatos.getWritableDatabase();
            InspGenDetDB inp =  listGenDet.get(i);
            contentValues.put("c_compania",inp.getC_compania());
            contentValues.put("n_correlativo",inp.getN_correlativo());
            contentValues.put("n_linea",inp.getN_linea());
            contentValues.put("c_comentario",inp.getC_comentario());
            contentValues.put("c_rutafoto",inp.getC_rutafoto());
            contentValues.put("c_ultimousuario",inp.getC_ultimousuario());
            contentValues.put("d_ultimafechamodificacion",inp.getD_ultimafechamodificacion());
            contentValues.put("c_tiporevisiong",inp.getC_tiporevisiong());
            contentValues.put("c_flagadictipo",inp.getC_flagadictipo());

            long var= db.insert("MTP_INSPECCIONGENERAL_DET",null,contentValues);
            db.close();
            Log.i("long result GEN DET ==> ",String.valueOf(var));


        }

        return null;
    }
}
