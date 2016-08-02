package com.lys.mobile.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.lys.mobile.data.SubMenu;
import com.lys.mobile.dataBase.InspMaqCabDB;
import com.lys.mobile.util.StringConexion;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * Created by dvillanueva on 15/07/2016.
 */
public class GetInspMaquinaCabTask extends AsyncTask<String,String,ArrayList<InspMaqCabDB>> {
    ArrayList<InspMaqCabDB> result ;
    @Override
    protected ArrayList<InspMaqCabDB> doInBackground(String... strings) {
        ArrayList<InspMaqCabDB> list = new ArrayList<InspMaqCabDB>();

        final String NAMESPACE = "http://SOAP/";
        final String URL= StringConexion.UrlSererPro;
        final String METHOD_NAME = "GetInspeccionesMaqCab";
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

            Log.i("rsoap - MTP INSPECCIONCAB----- >", resSoap.toString());

            //lstProjects = new ArrayList<Parametros>();
            int count = resSoap.getPropertyCount();

            for (int i = 0; i < count; i++)
            {
                SoapObject ic = (SoapObject) resSoap.getProperty(i);

                InspMaqCabDB inspMaqCabDB = new InspMaqCabDB();

                inspMaqCabDB.setC_compania(ic.getProperty(1).toString());
                inspMaqCabDB.setN_correlativo(ic.getProperty(13).toString());
                inspMaqCabDB.setC_maquina(ic.getProperty(4).toString());
                inspMaqCabDB.setC_condicionmaquina(ic.getProperty(2).toString());
                inspMaqCabDB.setC_comentario(ic.getProperty(0).toString());
                inspMaqCabDB.setC_estado(ic.getProperty(3).toString());
                inspMaqCabDB.setD_fechaInicioInspeccion(ic.getProperty(11).toString());
                inspMaqCabDB.setD_fechaFinInspeccion(ic.getProperty(10).toString());
                inspMaqCabDB.setC_periodoinspeccion(ic.getProperty(5).toString());
                inspMaqCabDB.setC_usuarioInspeccion(ic.getProperty(7).toString());
                inspMaqCabDB.setC_usuarioenvio(ic.getProperty(8).toString());
                inspMaqCabDB.setD_fechaEnvio(ic.getProperty(9).toString());
                inspMaqCabDB.setC_ultimousuario(ic.getProperty(6).toString());
                inspMaqCabDB.setD_ultimafechamodificacion(ic.getProperty(12).toString());
                list.add(inspMaqCabDB);
            }
            if (resSoap.getPropertyCount()>0){
                result =  list;

            }

        }
        catch (Exception e)
        {
            Log.i("ERROR WS  Get Maquina Cabecera  ===> ", e.getMessage() );
            result = new ArrayList<InspMaqCabDB>();

        }

        return result;
    }
}
