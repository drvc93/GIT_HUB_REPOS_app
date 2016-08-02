package com.lys.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.lys.mobile.asynctask.GetInspGeneralCabTask;
import com.lys.mobile.asynctask.GetInspGeneralDetTask;
import com.lys.mobile.asynctask.GetInspMaquinaCabTask;
import com.lys.mobile.asynctask.GetInspMaquinaDetTask;
import com.lys.mobile.asynctask.GetMenuTask;
import com.lys.mobile.asynctask.GetSubMenuTask;
import com.lys.mobile.data.Menu;
import com.lys.mobile.data.SubMenu;
import com.lys.mobile.dataBase.DBHelper;
import com.lys.mobile.dataBase.InspGenCabDB;
import com.lys.mobile.dataBase.InspGenDetDB;
import com.lys.mobile.dataBase.InspMaqCabDB;
import com.lys.mobile.dataBase.InspMaqDetDB;
import com.lys.mobile.dataBase.ProdMantDataBase;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.ExpandibleListMenuAdapater;
import com.lys.mobile.util.ProgressBarDailogo;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MenuPrincipal extends Activity {

    ArrayList<Menu> MenuFinalList;
    ExpandibleListMenuAdapater menuAdapater;
    ExpandableListView menuExpListView;
    String codUser ="";
    ArrayList<InspMaqDetDB> listInspDet ;
    ArrayList<InspMaqCabDB> listInsPMCab;
    ArrayList<InspGenCabDB> listInspGenCab;
    ArrayList<InspGenDetDB> listInspGenDet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.con_menu_principal);
        menuExpListView = (ExpandableListView) findViewById(R.id.ELVMenu) ;
      //  String userMaster=  getIntent().getExtras().getString("UserMaster");

        LoadMenu();
        ShowMenu();
        LoadInscpeccionMaquina();


        menuExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
//                Toast.makeText(MenuPrincipal.this,"Grupo" + String.valueOf(i)+" Hijo "+ String.valueOf(i1),Toast.LENGTH_SHORT).show();
                Menu m = MenuFinalList.get(i);
                SubMenu smenu = m.getSubMenus().get(i1);


                Intent intent = new Intent(MenuPrincipal.this , ListaOpciones.class);
                intent.putExtra("codPadre",m.getCodMenu());
                intent.putExtra("codSubMenu",smenu.getCodSubMenu());
                startActivity(intent);
                return false;
            }
        });


    }

    public  void  ShowMenu (){

        menuAdapater =  new ExpandibleListMenuAdapater(MenuPrincipal.this,MenuFinalList);
        menuExpListView.setAdapter(menuAdapater);




    }


    public  void  LoadMenu (){
        ProdMantDataBase db = new ProdMantDataBase(MenuPrincipal.this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MenuPrincipal.this);
         codUser = preferences.getString("user",null);
        ArrayList<Menu> listMenu = new ArrayList<Menu>();
        ArrayList<SubMenu> listSubMenu = new ArrayList<SubMenu>();
        AsyncTask<String,String,ArrayList<Menu> > asyncTaskMenu ;
        GetMenuTask  getMenuTask = new GetMenuTask();
        AsyncTask<String,String,ArrayList<SubMenu> > asyncTaskSubMenu;
        GetSubMenuTask getSubMenuTask = new GetSubMenuTask();

        listMenu = db.GetMenuPadre(codUser);

        listSubMenu =  db.GetMenuHijos(codUser);

        AgregarItemsAMenu(listMenu,listSubMenu);

    }

    public void  LoadInscpeccionMaquina(){

        AsyncTask<String,String,ArrayList<InspMaqCabDB>> asyncTaskCab ;
        GetInspMaquinaCabTask getInspMaquinaCabTask= new GetInspMaquinaCabTask();

        try {
            asyncTaskCab = getInspMaquinaCabTask.execute(codUser);
            listInsPMCab = (ArrayList<InspMaqCabDB>) asyncTaskCab.get();
            Log.i("Size MaqCab => " ,String.valueOf(listInsPMCab.size()));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        AsyncTask<String,String,ArrayList<InspMaqDetDB>> asyncTaskDet;
        GetInspMaquinaDetTask getInspMaquinaDetTask = new GetInspMaquinaDetTask();


        try {
            asyncTaskDet = getInspMaquinaDetTask.execute(codUser);
            listInspDet=(ArrayList<InspMaqDetDB> )asyncTaskDet.get();
            Log.i("Size MaqCab => " ,String.valueOf(listInspDet.size()));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // LISTA DE INSPECCIONES GENERALES - CABECERA
        AsyncTask <String,String,ArrayList<InspGenCabDB>> asyncTaskInspGenCab;
        GetInspGeneralCabTask  inspGeneralCabTask= new GetInspGeneralCabTask();

        try {
            asyncTaskInspGenCab = inspGeneralCabTask.execute(codUser);
            listInspGenCab = (ArrayList<InspGenCabDB>) asyncTaskInspGenCab.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //  LISTA DE INSPECCIONES GENERALES  DETALLE
        AsyncTask<String,String,ArrayList<InspGenDetDB>>  asyncTaskInspGenDet ;
        GetInspGeneralDetTask  getInspGeneralDetTask = new GetInspGeneralDetTask();

        try {
            asyncTaskInspGenDet = getInspGeneralDetTask.execute(codUser);
            listInspGenDet = (ArrayList<InspGenDetDB>)asyncTaskInspGenDet.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        DataBase basededatos = new DataBase (MenuPrincipal.this, "DBInspeccion", null, 1);
        SQLiteDatabase db = basededatos.getWritableDatabase();
     //   ContentValues ctCab = GetContentValues("CABECERA");
        if (isOnline()==true){
            ProgressDialog progressDialog  = new ProgressDialog(MenuPrincipal.this);
            progressDialog.setMessage("Sincronizando datos de inspecciones , espere unos segundos ....");
            progressDialog.setTitle("Sincronización");
            new ProgressBarDailogo(MenuPrincipal.this,listInsPMCab,listInspDet,listInspGenCab,listInspGenDet,progressDialog,"CABECERA").execute();

           // InsertTablaInspMaquina("CABECERA");
           // InsertTablaInspMaquina("DETALLE");


        }
      //  Log.i("long result ==> ",String.valueOf(var));
       // db.close();
    }

    public  void InsertTablaInspMaquina(String tableName){
        ContentValues contentValues = new ContentValues();
        DataBase basededatos = new DataBase (MenuPrincipal.this, "DBInspeccion", null, 1);
        SQLiteDatabase dbDelete = basededatos.getWritableDatabase();


        if (tableName.equals("CABECERA")){
            dbDelete.execSQL("delete from  MTP_INSPECCIONMAQUINA_CAB");
            dbDelete.close();
            for (int i =  0 ; i< listInsPMCab.size(); i++){

                SQLiteDatabase db = basededatos.getWritableDatabase();

                InspMaqCabDB inpCab = listInsPMCab.get(i);
                contentValues.put("c_compania", inpCab.getC_compania());
                contentValues.put("n_correlativo",inpCab.getN_correlativo());
                contentValues.put("c_maquina",inpCab.getC_maquina());
                contentValues.put("c_condicionmaquina",inpCab.getC_condicionmaquina());
                contentValues.put("c_comentario",inpCab.getC_comentario());
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
                Log.i("long result CAB ==> ",String.valueOf(var));
            }


        }
      //  Log.i("Content value CAB size ==> " ,String.valueOf(contentValues.size()));

        else if (tableName.equals("DETALLE")){
            dbDelete.execSQL("delete from MTP_INSPECCIONMAQUINA_DET");
            dbDelete.close();
            for (int i = 0 ; i< listInspDet.size(); i++){
                SQLiteDatabase db = basededatos.getWritableDatabase();
                InspMaqDetDB inpDt = listInspDet.get(i);
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
                Log.i("long result DET ==> ",String.valueOf(var));
            }

        }

    }

    public  void ShowProgressDialog (String titulo, String mensaje, final int cantRegistros, final String tableName){


        final ProgressDialog progressDialog = new ProgressDialog(MenuPrincipal.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).show(MenuPrincipal.this,titulo,mensaje,true);
        progressDialog.setCancelable(true);
        progressDialog.setIcon(R.drawable.icn_sincro_refresh);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InsertTablaInspMaquina(tableName);
                    Thread.sleep(cantRegistros*500);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }).start();

    }


    public  void AgregarItemsAMenu (ArrayList<Menu> menus , ArrayList<SubMenu> subMenus){

        MenuFinalList = new ArrayList<Menu>();

        for (int i  = 0 ; i<menus.size(); i++){

            String codMenu = menus.get(i).getCodMenu();
            ArrayList<SubMenu> listSubMenu = new ArrayList<SubMenu>();
            for (int j =  0 ; j<subMenus.size();j++){

                String codPadre = subMenus.get(j).getCodPadre();

                 if (codPadre.equals(codMenu)){

                     listSubMenu.add(subMenus.get(j));

                 }

            }

            menus.get(i).setSubMenus(listSubMenu);

        }

        MenuFinalList=menus;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(MenuPrincipal.this.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
