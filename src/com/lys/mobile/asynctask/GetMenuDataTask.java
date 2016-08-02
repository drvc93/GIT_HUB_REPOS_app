package com.lys.mobile.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.lys.mobile.data.Menu;
import com.lys.mobile.dataBase.MenuDB;
import com.lys.mobile.util.StringConexion;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by dvillanueva on 04/07/2016.
 */
public class GetMenuDataTask extends AsyncTask<String,String,ArrayList<MenuDB>> {

    ArrayList<MenuDB> result ;
    @Override
    protected ArrayList<MenuDB> doInBackground(String... strings) {

        ArrayList<MenuDB> listMenu = new ArrayList<MenuDB>();
        final String NAMESPACE = "http://SOAP/";
        final String URL = StringConexion.UrlSererPro;
        //final String URL="http://10.0.2.2:8084/SOAPLYS?wsdl";
        final String METHOD_NAME = "GetDataMenu";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        //request.addProperty("CodUsuario", strings[0]);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);
            SoapObject resSoap =(SoapObject)envelope.bodyIn;



            Log.i("MENU DB DATA > ", resSoap.toString());
            //lstProjects = new ArrayList<Parametros>();
            int num_projects = resSoap.getPropertyCount();

            for (int i = 0; i < num_projects; i++)
            {
                SoapObject ic = (SoapObject)resSoap.getProperty(i);

                MenuDB menu = new MenuDB();

                menu.setAppCodigo(ic.getProperty(0).toString());
                menu.setDescripcion(ic.getProperty(1).toString());
                menu.setNivel1(ic.getProperty(2).toString());
                menu.setNivel2(ic.getProperty(3).toString());
                menu.setNivel3(ic.getProperty(4).toString());
                menu.setNivel4(ic.getProperty(5).toString());
                menu.setNivel5(ic.getProperty(6).toString());

                menu.setNombreMenu(ic.getProperty(7).toString());
                listMenu.add(menu);
            }
            if (resSoap.getPropertyCount()>0){
                result =  listMenu;

            }

        }
        catch (Exception e)
        {
            Log.i("ERROR JAVA LIST WS MENUDB ---", e.getMessage() );
            result = null;
        }

        return result;
    }
}
