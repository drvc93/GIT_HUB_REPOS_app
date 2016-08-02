package com.lys.mobile.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.lys.mobile.data.Menu;
import com.lys.mobile.data.SubMenu;
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
public class GetSubMenuTask extends AsyncTask<String,String,ArrayList<SubMenu> > {

    ArrayList<SubMenu> result ;

    @Override
    protected ArrayList<SubMenu> doInBackground(String... strings) {

        ArrayList<SubMenu> listSubMenu = new ArrayList<SubMenu>();
        final String NAMESPACE = "http://SOAP/";
        final String URL= StringConexion.UrlSererPro;
        final String METHOD_NAME = "ListSubMenu";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("CodUsuario", strings[0]);
        //request.addProperty("CodigoUsuario", params[1]);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

        SoapObject resSoap =(SoapObject)envelope.bodyIn;

            Log.i("rSOAP  SubMenu----- >", resSoap.toString());

            //lstProjects = new ArrayList<Parametros>();
            int num_projects = resSoap.getPropertyCount();

            for (int i = 0; i < num_projects; i++)
            {
                SoapObject ic = (SoapObject) resSoap.getProperty(i);

                SubMenu subMenumenu = new SubMenu();

                subMenumenu.setCodPadre( ic.getProperty(0).toString());
                subMenumenu.setCodSubMenu( ic.getProperty(1).toString());
                subMenumenu.setDescripcionSubmenu( ic.getProperty(2).toString());
                subMenumenu.setEstado(ic.getProperty(3).toString());

                listSubMenu.add(subMenumenu);
            }
            if (resSoap.getPropertyCount()>0){
                result =  listSubMenu;

            }

        }
        catch (Exception e)
        {
            Log.i("ERROR JAVA LIST WS---", e.getMessage() );
            result = null;
        }

        return result;
    }
}
