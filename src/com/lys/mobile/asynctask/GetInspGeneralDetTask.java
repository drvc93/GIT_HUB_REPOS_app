package com.lys.mobile.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.lys.mobile.dataBase.InspGenCabDB;
import com.lys.mobile.dataBase.InspGenDetDB;
import com.lys.mobile.util.StringConexion;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by dvillanueva on 18/07/2016.
 */
public class GetInspGeneralDetTask   extends AsyncTask<String,String,ArrayList<InspGenDetDB>> {
    ArrayList<InspGenDetDB> result;
    @Override
    protected ArrayList<InspGenDetDB> doInBackground(String... strings) {
        ArrayList<InspGenDetDB> list = new ArrayList<InspGenDetDB>();

        final String NAMESPACE = "http://SOAP/";
        final String URL= StringConexion.UrlSererPro;
        final String METHOD_NAME = "GetInspeccionesGenDet";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("Usuario", strings[0]);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap =(SoapObject)envelope.bodyIn;

            Log.i("rsoap - MTP INSPECCION GENERAL DET----- >", resSoap.toString());

            //lstProjects = new ArrayList<Parametros>();
            int count = resSoap.getPropertyCount();

            for (int i = 0; i < count; i++)
            {
                SoapObject ic = (SoapObject) resSoap.getProperty(i);

                InspGenDetDB inspGen = new InspGenDetDB();

                 inspGen.setC_comentario(ic.getProperty(0).toString());
                 inspGen.setC_compania(ic.getProperty(1).toString());
                inspGen.setC_flagadictipo(ic.getProperty(2).toString());
                inspGen.setC_rutafoto(ic.getProperty(3).toString());
                inspGen.setC_tiporevisiong(ic.getProperty(4).toString());
                inspGen.setC_ultimousuario(ic.getProperty(5).toString());
                inspGen.setD_ultimafechamodificacion(ic.getProperty(6).toString());
                inspGen.setN_correlativo(ic.getProperty(7).toString());
                inspGen.setN_linea(ic.getProperty(8).toString());


                list.add(inspGen);
            }
            if (resSoap.getPropertyCount()>0){
                result =  list;

            }

        }
        catch (Exception e)
        {
            Log.i("ERROR WS  GetInsp General DETALLE  ===> ", e.getMessage() );
            result = new ArrayList<InspGenDetDB>();

        }

        return result;
    }
}
