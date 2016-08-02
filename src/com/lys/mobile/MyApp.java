package com.lys.mobile;
import java.util.ArrayList;

import com.lys.mobile.data.FotoData;
import com.lys.mobile.data.HistorialData;
import com.lys.mobile.data.ProgramaData;

import android.app.Application;

public class MyApp extends Application {
	
	private String soapip = "100.100.100.57";
	//private String soapip = "100.100.100.186";
	private String soappuerto = "8080";
	private String urlinspeccion = "http://" + soapip + ":" + soappuerto + "/" + "webuploadmt/uploadinspeccion.jsp";
	private String urlfotoinspeccion = "http://" + soapip + ":" + soappuerto + "/" + "lys/verimageninspeccion.jsp?itemproc=";
	private String rutainspeccion = "/LysConfig/Fotos";
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();		
		//si es primera vez
	}
	public static  ArrayList<ProgramaData> listaplan = new ArrayList<ProgramaData>();
	public static  ArrayList<HistorialData> listaphisto = new ArrayList<HistorialData>();
	public  ArrayList< FotoData> fotos = new  ArrayList< FotoData>();
	
	public String getUrlFotoInspeccion(){
		return urlfotoinspeccion;
	}
	
	public String getUrlImagenInspeccion() {
		return urlinspeccion;
	}
	
	public String getRutaInspeccion() {
		return rutainspeccion;
	}
}
