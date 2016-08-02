package com.lys.mobile.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.lys.mobile.dataBase.AccesosDB;
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
public class GetAccesosDataTask extends AsyncTask<String,String,ArrayList<AccesosDB>> {
    ArrayList<AccesosDB> result;
    @Override
    protected ArrayList<AccesosDB> doInBackground(String... strings) {
        ArrayList<AccesosDB> accesosDBs  =new  ArrayList<AccesosDB>();
        final String NAMESPACE = "http://SOAP/";
        final String URL= StringConexion.UrlSererPro;
        final String METHOD_NAME = "GetDataAccesos";
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

                AccesosDB acceso = new AccesosDB();
                acceso.setAcceso(ic.getProperty(0).toString());
                acceso.setAppCodigo(ic.getProperty(1).toString());
                acceso.setEliminar(ic.getProperty(2).toString());
                acceso.setModificar(ic.getProperty(3).toString());
                acceso.setNivelAcc(ic.getProperty(4).toString());
                acceso.setNuevo(ic.getProperty(5).toString());
                acceso.setOtros(ic.getProperty(6).toString());
                acceso.setUsuario(ic.getProperty(7).toString());
                accesosDBs.add(acceso);
            }
            if (resSoap.getPropertyCount()>0){
                result =  accesosDBs;

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
