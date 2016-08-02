package com.lys.mobile.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.lys.mobile.data.Menu;
import com.lys.mobile.util.StringConexion;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by dvillanueva on 24/06/2016.
 */
public class GetMenuTask extends AsyncTask<String,String , ArrayList<Menu> > {

    ArrayList<Menu> result ;
    @Override
    protected ArrayList<Menu> doInBackground(String... strings) {

        ArrayList<Menu> listMenu = new ArrayList<Menu>();
        final String NAMESPACE = "http://SOAP/";
        final String URL= StringConexion.UrlSererPro;
        final String METHOD_NAME = "ListaMenu";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

         request.addProperty("CodUsuario", strings[0]);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);
           SoapObject resSoap =(SoapObject)envelope.bodyIn;
          //  Vector<?> resSoap =( Vector <?>)envelope.getResponse();

           Log.i("rSOAP Menu ----- >", resSoap.toString());

            //lstProjects = new ArrayList<Parametros>();
            int num_projects = resSoap.getPropertyCount();

            for (int i = 0; i < num_projects; i++)
            {
                SoapObject ic = (SoapObject)resSoap.getProperty(i);

                Menu menu = new Menu();

                menu.setCodMenu( ic.getProperty(1).toString());
                menu.setDescripcionMenu( ic.getProperty(2).toString());
                menu.setCodAplicacion(ic.getProperty(0).toString());

                listMenu.add(menu);
            }
            if (resSoap.getPropertyCount()>0){
                result =  listMenu;

            }

        }
        catch (Exception e)
        {
            Log.i("ERROR JAVA LIST WS MENU ---", e.getMessage() );
            result = null;
        }

        return result;
    }
}
