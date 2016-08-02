package com.lys.mobile.data;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lys.mobile.ListaOpciones;
import com.lys.mobile.MainInspeccionT;
import com.lys.mobile.MantInspeccionT;

import java.util.ArrayList;

/**
 * Created by dvillanueva on 27/06/2016.
 */
public class ActivityCls {
    Context var_context = null;
    ArrayList<SubMenuBotones> lisSubMBotones = new ArrayList<SubMenuBotones>();

    public  ActivityCls( Context c){
        this.var_context =c;
        MantInspeccionT  mantInspeccionT = new MantInspeccionT();
        SubMenuBotones l1 = new SubMenuBotones("01","01","01","",CreateIntenst(c,mantInspeccionT.getClass()));



        lisSubMBotones.add(l1);

    };




    public Intent GetActivity(Context context ,SubMenuBotones menuBotones){
        Intent result = null;
        String var_concat =  menuBotones.getCodPadre()+menuBotones.getCodSubmenu()+menuBotones.getCodMenuBoton();
        Log.i("concat ==> " , var_concat);
        if (var_concat.equals("010101")){

            result = new Intent(context,MantInspeccionT.class);

        }

        return  result;


      /* Intent result = null;

        for (int i = 0 ; i<lisSubMBotones.size();i++){
            SubMenuBotones sboton =  lisSubMBotones.get(i);
            String rec_codPadre = sboton.getCodPadre();
            String rec_codSubMenu = sboton.getCodSubmenu();
            String rec_codBoton = sboton.getCodMenuBoton();

           if (rec_codPadre.equals(menuBotones.getCodPadre()) && rec_codSubMenu.equals(menuBotones.getCodSubmenu()) && rec_codBoton.equals(menuBotones.getCodMenuBoton())){

               result = sboton.getaClass();

           }
        }

        return  result;*/
    }


    public  Intent CreateIntenst  (Context context, Class<?> cls){

        Intent intent = new Intent(context,cls.getClass());

        return  intent;
    }

}
