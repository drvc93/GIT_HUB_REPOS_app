package com.lys.mobile.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import com.lys.mobile.data.Menu;
import com.lys.mobile.data.SubMenuBotones;
import com.lys.mobile.util.StringConexion;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by dvillanueva on 27/06/2016.
 */
public class GetMenuBotonesTask  extends AsyncTask<String,String , ArrayList<SubMenuBotones> > {

    ArrayList<SubMenuBotones> result = new ArrayList<SubMenuBotones>();
    @Override
    protected ArrayList<SubMenuBotones> doInBackground(String... params) {

        ArrayList<SubMenuBotones> listMenuBotones = new ArrayList<SubMenuBotones>();
        final String NAMESPACE = "http://SOAP/";
        final String URL= StringConexion.UrlSererPro;
        final String METHOD_NAME = "ListSubMenuBotones";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("CodPadre",params[0]);
        request.addProperty("CodSubMenu", params[1]);
        request.addProperty("CodUsuario", params[2]);



        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap =(SoapObject)envelope.bodyIn;

            Log.i("rSOAP MENU BOTONES ----- >", resSoap.toString());

            //lstProjects = new ArrayList<Parametros>();
            int num_projects = resSoap.getPropertyCount();

            for (int i = 0; i < num_projects; i++)
            {
                SoapObject ic = (SoapObject) resSoap.getProperty(i);

                SubMenuBotones menu = new SubMenuBotones ();

                menu.setCodMenuBoton( ic.getProperty(0).toString());
                menu.setCodPadre( ic.getProperty(1).toString());
                menu.setCodSubmenu( ic.getProperty(2).toString());
                menu.setDescripcion( ic.getProperty(3).toString());

                listMenuBotones.add(menu);
            }
            if (resSoap.getPropertyCount()>0){
                result =  listMenuBotones;

            }

        }
        catch (Exception e)
        {
           // Log.i("ERROR JAVA LISTBOTONESTASK ---", e.getMessage() );
           // result = null;
        }


        return result;
    }
}
