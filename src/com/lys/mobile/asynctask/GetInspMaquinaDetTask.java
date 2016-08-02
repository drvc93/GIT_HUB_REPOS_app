package com.lys.mobile.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.lys.mobile.dataBase.InspMaqCabDB;
import com.lys.mobile.dataBase.InspMaqDetDB;
import com.lys.mobile.util.StringConexion;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by dvillanueva on 15/07/2016.
 */
public class GetInspMaquinaDetTask extends AsyncTask<String,String,ArrayList<InspMaqDetDB>> {
    ArrayList<InspMaqDetDB>result;

    @Override
    protected ArrayList<InspMaqDetDB> doInBackground(String... strings) {
        ArrayList<InspMaqDetDB> list = new ArrayList<InspMaqDetDB>();

        final String NAMESPACE = "http://SOAP/";
        final String URL= StringConexion.UrlSererPro;
        final String METHOD_NAME = "GetInspeccionesMaqDet";
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

            Log.i("rsoap - MTP INSPECCION DET----- >", resSoap.toString());

            //lstProjects = new ArrayList<Parametros>();
            int count = resSoap.getPropertyCount();

            for (int i = 0; i < count; i++)
            {
                SoapObject ic = (SoapObject) resSoap.getProperty(i);

                InspMaqDetDB inspMaqDetDB = new InspMaqDetDB();

                inspMaqDetDB.setC_comentario(ic.getProperty(0).toString());
                inspMaqDetDB.setC_compania(ic.getProperty(1).toString());
                inspMaqDetDB.setC_estado(ic.getProperty(2).toString());
                inspMaqDetDB.setC_inpeccion(ic.getProperty(3).toString());
                inspMaqDetDB.setC_rutafoto(ic.getProperty(4).toString());
                inspMaqDetDB.setC_tipoinspeccion(ic.getProperty(5).toString());
                inspMaqDetDB.setC_ultimousuario(ic.getProperty(6).toString());
                inspMaqDetDB.setD_ultimafechamodificacion(ic.getProperty(7).toString());
                inspMaqDetDB.setN_correlativo(ic.getProperty(8).toString());
                inspMaqDetDB.setN_linea(ic.getProperty(9).toString());
                inspMaqDetDB.setN_pocentajeinspeccion(ic.getProperty(10).toString());
                inspMaqDetDB.setN_porcentajemaximo(ic.getProperty(11).toString());
                inspMaqDetDB.setN_porcentajeminimo(ic.getProperty(12).toString());

                list.add(inspMaqDetDB);

            }
            if (resSoap.getPropertyCount()>0){
                result =  list;

            }

        }
        catch (Exception e)
        {
            Log.i("ERROR WS  Get Maquina DETALLE  ===> ", e.getMessage() );
            result = new ArrayList<InspMaqDetDB>();

        }

        return result;
    }
}
