package com.lys.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lys.mobile.asynctask.GetAccesosDataTask;
import com.lys.mobile.asynctask.GetMenuBotonesTask;
import com.lys.mobile.asynctask.GetMenuDataTask;
import com.lys.mobile.asynctask.UsuariosAsyncTask;
import com.lys.mobile.data.ActivityCls;
import com.lys.mobile.data.Menu;
import com.lys.mobile.data.Permisos;
import com.lys.mobile.data.SubMenu;
import com.lys.mobile.data.SubMenuBotones;
import com.lys.mobile.dataBase.AccesosDB;
import com.lys.mobile.dataBase.MenuDB;
import com.lys.mobile.dataBase.ProdMantDataBase;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.ExpandibleListMenuAdapater;
import com.lys.mobile.util.StringConexion;
import com.lys.mobile.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ListaOpciones extends Activity {

 //   LinearLayout masterLy  ;
 private static final String BS_PACKAGE = "com.google.zxing.client.android";
    public static final int REQUEST_CODE = 0x0000c0de;
    static String contents = "";
    ListView LVOpciones;
    String codUser = "";
    String extraCodPadre, extraCodSubMenu;
    ArrayList<SubMenuBotones> listOpciones = new ArrayList<SubMenuBotones>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_lista_opciones);
        setTitle("Lista de opciones");
        extraCodPadre = getIntent().getExtras().getString("codPadre");
        extraCodSubMenu = getIntent().getExtras().getString("codSubMenu");

    //    masterLy = (LinearLayout) findViewById(R.id.masterLayout);
        LVOpciones = (ListView) findViewById(R.id.LVOpciones);

        if (extraCodPadre != null  && extraCodSubMenu != null) {
            Log.i("Pu extras >>>" , extraCodPadre+ " - "+ extraCodSubMenu);
            listOpciones = GetOpciones(extraCodPadre, extraCodSubMenu);
            CreateButtons();



        }
    }

    public  ArrayList<SubMenuBotones> GetOpciones(String codPadre , String smenu){
        ProdMantDataBase db = new ProdMantDataBase(ListaOpciones.this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ListaOpciones.this);
         codUser = preferences.getString("user",null);

        ArrayList<SubMenuBotones> result = db.getSubBotones(codPadre,smenu,codUser);


        return  result;

    }

    public void CreateButtons (){

        OpcionesAdapter  opcionesAdapter = new OpcionesAdapter(ListaOpciones.this,R.layout.con_sup_opciones_boton,listOpciones);
        LVOpciones.setAdapter(opcionesAdapter);
    }

    public class OpcionesAdapter  extends ArrayAdapter<SubMenuBotones>{


        public OpcionesAdapter(Context context, int resource, ArrayList<SubMenuBotones> data) {
            super(context, resource, data);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View  view = convertView;

            if (view == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                view= vi.inflate(R.layout.con_sup_opciones_boton, null);
            }

            SubMenuBotones subMenuBotones = getItem(position);
            Button btnOpcList =(Button) view.findViewById(R.id.btnListOpciones);
            if (btnOpcList!=null){

                btnOpcList.setText(subMenuBotones.getDescripcion());
            }

            btnOpcList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  Toast.makeText(ListaOpciones.this,String.valueOf(position),Toast.LENGTH_SHORT).show();

                    Log.i("Boton Seleciconado ====>cod Boton : " , listOpciones.get(position).getCodMenuBoton());
                    Log.i("Boton Seleciconado ====>codPadre : " , listOpciones.get(position).getCodPadre());
                    Log.i("Boton Seleciconado ====>submenu : ",listOpciones.get(position).getCodSubmenu());
                   // Log.i("Boton Seleciconado ====>codPadre: " , listOpciones.get(position).getaClass().getPackageName() );
                    SubMenuBotones sbmenu = listOpciones.get(position);
                    IrActivity(sbmenu);
                }
            });

            return  view;
        }
    }

    public  void  IrActivity (SubMenuBotones sb){
        ProdMantDataBase db = new ProdMantDataBase(ListaOpciones.this);
        ArrayList<Permisos> permiso = new ArrayList<Permisos>() ;
       String var_concatenado =  sb.getCodPadre()+sb.getCodSubmenu()+sb.getCodMenuBoton();
        Log.i("Contaenado nivel ==> ",var_concatenado);
        if (var_concatenado.equals("010101")){

            Intent intentScan = new Intent(BS_PACKAGE + ".SCAN");
            intentScan.putExtra("PROMPT_MESSAGE", "Enfoque entre 9 y 11 cm. apuntado el codigo de barras de la maquina");
            startActivityForResult(intentScan, REQUEST_CODE);

        }

        else  if (var_concatenado.equals("010102")){

            SharedPreferences preferences2 = PreferenceManager.getDefaultSharedPreferences(ListaOpciones.this);
            String cmaquina = preferences2.getString("cmaquina", "");
            //cmaquina = "BOM-01";
            if (cmaquina.length()==0) {
                Toast.makeText(this, "Aún no ha escaneado código de barra.", Toast.LENGTH_SHORT).show();
            } else {
                // Nueva Inspeccin T.
                Intent intent = new Intent(ListaOpciones.this, MantInspeccionT.class);
                intent.putExtra("GrabaInspeccion", "N");
                intent.putExtra("codInspeccion", "0");
                this.startActivity(intent);
            }

        }

        else  if (var_concatenado.equals("010103")){
            permiso=db.GetPermisos(codUser,var_concatenado);
            String perm_modificar = "NO";
            if (VerificarPermisos(permiso,"MODIFICAR")){

                perm_modificar = "SI";
            }
            Intent intent1 = new Intent(ListaOpciones.this, HistoInspeccion.class);
            intent1.putExtra("Modificar",perm_modificar);
            this.startActivity(intent1);
        }

        else  if (var_concatenado.equals("010104")){

            Intent intent2 = new Intent(ListaOpciones.this, InspeccionesRealizadas.class);
            startActivity(intent2);
        }

        else  if (var_concatenado.equals("010201")) { //Nueva Inspeccin G.
             permiso = db.GetPermisos(codUser,var_concatenado);

            if (permiso.size()==0){

                Toast.makeText(ListaOpciones.this,"No tiene permisos para esta funcion",Toast.LENGTH_SHORT).show();
            }
            else {
            if (VerificarPermisos(permiso,"NUEVO") ==true) {

                String sincronizar = "NO";
                if (VerificarPermisos(permiso,"SINCRONIZAR")==true){
                    sincronizar = "SI";
                }

                Intent intent = new Intent(ListaOpciones.this, MantInspeccionG.class);
                intent.putExtra("GrabaInspeccionG", "N");
                intent.putExtra("Sincro",sincronizar);
                intent.putExtra("codInspeccionG", "0");
                this.startActivity(intent);
            }
                else
            {
                Toast.makeText(ListaOpciones.this,"No tiene permisos para esta funcion",Toast.LENGTH_SHORT).show();
            }
            }
        }
        else if (var_concatenado.equals("010202")) {//Inspecciones en Lnea
            permiso = db.GetPermisos(codUser,var_concatenado);
            String perm_modificar = "NO";
            if (VerificarPermisos(permiso,"MODIFICAR")==true){
             perm_modificar="SI";
            }
            Intent intent2 = new Intent(ListaOpciones.this, HistoInspeccionG.class);
            intent2.putExtra("Modificar",perm_modificar);
            this.startActivity(intent2);
        }
        else if (var_concatenado.equals("010203")) { //Listado de Inspecciones G. Realizadas
            Intent intent3 = new Intent(ListaOpciones.this, InspeccionesRealizadasG.class);
            this.startActivity(intent3);
        }

        else if (var_concatenado.equals("030101")){

            ShowDialogAlert("MAESTROS");

        }
        else if (var_concatenado.equals("030102")){
            ShowDialogAlert("ACCESOS");
        }



    }

    public  void ShowProgressDialog (String TipoSincroniz){

        String titulo = " Espere... estamos sincronizando...";
        String mensaje = "SINCRONIZANDO DATOS DE  " + TipoSincroniz;
        final ProgressDialog progressDialog = new ProgressDialog(ListaOpciones.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).show(ListaOpciones.this,titulo,mensaje,true);
        progressDialog.setCancelable(true);
      progressDialog.setIcon(R.drawable.icn_sincro_refresh);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }).start();

    }

    public  void  ShowDialogAlert (final String TipoSincro){

        String Mensaje ="";
        if (TipoSincro.equals("MAESTROS")){
            Mensaje = "Si  sincroniza se borraran los datos guardados previamente , esta seguro que desea continuar?";
        }
        else  if (TipoSincro.equals("ACCESOS")){

            Mensaje = "¿Esta que desea continuar con la sincronizacion?";
        }

        new AlertDialog.Builder(ListaOpciones.this)
                .setTitle("Advertencia")
                .setMessage(Mensaje)
                .setIcon(R.drawable.icn_alert)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       if (TipoSincro.equals("MAESTROS")){

                           sincronizar();
                       }
                        else {

                           SincMenuAcceso(TipoSincro);
                       }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();


    }

    public  boolean VerificarPermisos(ArrayList<Permisos> perm,String  permisoName ){
        boolean result= false;


        for (int i  = 0 ; i <  perm.size() ; i++){

                Permisos p = perm.get(i);

            String var_Per = p.getDescripcion();
            var_Per = var_Per.replaceAll("\\s", "");
            if (permisoName.equals(var_Per)){

                result = true;
                break;
            }


        }

        return  result;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                contents = data.getStringExtra("SCAN_RESULT");
                String cmaquina = "",nmaquina = "",cbarra = "",estado = "";
                String familia = "",centro = "";

                DataBase objDB = new DataBase(this, "DBInspeccion", null, 1);
                SQLiteDatabase _db = objDB.getWritableDatabase();

                String sql = "Select c_maquina,c_descripcion,c_codigobarras,c_estado,c_familiainspeccion,"
                        + "c_centrocosto from MTP_MAQUINAS where c_codigobarras = '"+ contents + "' ";

                Cursor c = _db.rawQuery(sql, null);
                if (c.moveToFirst()) {
                    cmaquina = c
                            .getString(0);
                    nmaquina = c.getString(1);
                    cbarra = c.getString(2);
                    estado = c.getString(3);
                    familia = c.getString(4);
                    centro = c.getString(5);

                    SharedPreferences preferences = PreferenceManager.
                            getDefaultSharedPreferences(ListaOpciones.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("cmaquina", cmaquina);
                    editor.putString("maquina", nmaquina);
                    editor.putString("codbarra", cbarra);
                    editor.putString("estado", estado);
                    editor.putString("familia", familia);
                    editor.putString("centro", centro);

                    editor.commit();
                    Toast.makeText(this, "Máquina: " + cmaquina + " - " + nmaquina + ". Cod. Familia: "
                            + familia + ".", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Máquina no disponible para ese código.", Toast.LENGTH_SHORT).show();
                }
                c.close();
                _db.close();
            }
        }
        return;
    }

    public void sincronizar() {

        // leeelo

        leelo();
       // SincMenuAcceso();
        final UsuariosAsyncTask tarea = new UsuariosAsyncTask(ListaOpciones.this);
        //tarea.execute(new String[] { Util.url + "getUsuarios" });
        tarea.execute(new String[] { StringConexion.UrlServicesRest + "getUsuarios" });

        Thread thread = new Thread() {
            public void run() {
                try {
                    tarea.get(300000, TimeUnit.MILLISECONDS);
                    // tarea.get(30000, TimeUnit.MILLISECONDS);

                } catch (Exception e) {
                    tarea.cancel(true);
                    ((Activity) ListaOpciones.this).runOnUiThread(new Runnable() {
                        public void run() {
                           // acceder.setEnabled(true);
                            tarea.progressDialog.dismiss();
                            Toast.makeText(ListaOpciones.this, "No se pudo establecer comunicacion.", Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        };
        thread.start();

    }

    public  void SincMenuAcceso(String tipoSincro)  {



        GetMenuDataTask getMenuDataTask = new GetMenuDataTask();
        AsyncTask<String,String,ArrayList<MenuDB>> asyncTask;
        ArrayList<MenuDB> menuDBs= new ArrayList<MenuDB>();
        ProdMantDataBase db =  new ProdMantDataBase(ListaOpciones.this);
        db.deleteTables();


        try {
            asyncTask = getMenuDataTask.execute();
            menuDBs= (ArrayList<MenuDB>)asyncTask.get();
            for (int i = 0 ; i <menuDBs.size();i++){

                MenuDB mn = menuDBs.get(i);
                db.InsetrtMenus(mn);
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ArrayList<AccesosDB> accesosDBs = new ArrayList<AccesosDB>();
        GetAccesosDataTask getAccesosDataTask = new GetAccesosDataTask();
        AsyncTask<String,String,ArrayList<AccesosDB>> asyncTaskAccesos;

        try {
            asyncTaskAccesos = getAccesosDataTask.execute();
            accesosDBs = (ArrayList<AccesosDB>)asyncTaskAccesos.get();
            for (int i = 0 ; i <accesosDBs.size() ; i++){
                AccesosDB acdb =  accesosDBs.get(i);
                db.InsertAccesos(acdb);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ShowProgressDialog(tipoSincro);

    }

    public void leelo(){

        File sdcard = new File(Environment.getExternalStorageDirectory(), "LysConfig");

        // Get the text file
        File file = new File(sdcard, "config.txt");

        // Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');

            }
            br.close();

            // String[] valores=new String[2];
            String[] lines = text.toString().split("\n");
		/*
		 * for(String s: lines){
		 *
		 * System.out.println("Content = " + s);
		 * System.out.println("Length = " + s.length()); }
		 */

            Util.url = String.valueOf(lines[0]);
            Util.urlfoto = String.valueOf(lines[1]);

            Log.d("url", String.valueOf(lines[0]));
            Log.d("fotoo", String.valueOf(lines[1]));

        } catch (Exception e) {
            // You'll need to add proper error handling here
            Log.d("error: ", e.toString());

            try {
                File root = new File(Environment.getExternalStorageDirectory(), "LysConfig");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, "config.txt");
                FileWriter writer = new FileWriter(gpxfile);
			/*writer.append("http://100.100.100.57:8080/LysWsRest/resources/generic/"
					+ "\n" + "http://100.100.100.11/Fotos_Tablet/");*/
                writer.append("http://100.100.100.57:8080/LysWsRest/resources/generic/"
                        + "\n" + "http://100.100.100.11/Fotos_Tablet/");
                // writer.append("\n");//appends the string to the file
                // writer.append("http://192.168.1.33:81/imagenes/");
                writer.flush();
                writer.close();
                // Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            } catch (IOException ee) {
                Log.d("error: ", ee.toString());
                e.printStackTrace();

            }

            // leeelo

            File sdcardd = new File(Environment.getExternalStorageDirectory(), "LysConfig");

            // Get the text file
            File filee = new File(sdcardd, "config.txt");

            // Read text from file
            StringBuilder textt = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(filee));
                String line;

                while ((line = br.readLine()) != null) {
                    textt.append(line);
                    textt.append('\n');

                }
                br.close();
            } catch (IOException ee) {
                // You'll need to add proper error handling here
                Log.d("error: ", ee.toString());
            }

            // String[] valores=new String[2];
            String[] lines = textt.toString().split("\n");
		/*
		 * for(String s: lines){
		 *
		 * System.out.println("Content = " + s);
		 * System.out.println("Length = " + s.length()); }
		 */
            Util.url = String.valueOf(lines[0]);
            Util.urlfoto = String.valueOf(lines[1]);

            Log.d("url", Util.url = String.valueOf(lines[0]));
            Log.d("fotoo", String.valueOf(lines[1]));

            //user.setText("" + String.valueOf(lines[0]));

        }
    }


}
