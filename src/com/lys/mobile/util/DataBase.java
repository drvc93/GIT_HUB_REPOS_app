package com.lys.mobile.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper{

	
    String crearcentro = "CREATE TABLE MTP_CENTROCOSTO(c_compania TEXT,c_centrocosto TEXT,c_descripcion TEXT,c_estado TEXT)";
    String crearfamilia = "CREATE TABLE MTP_FAMILIAINSPECCION(c_familiainspeccion TEXT,c_descripcion TEXT,n_tiempominimo TEXT,c_estado TEXT,d_ultimousuario TEXT,d_ultimafechamodificacion DATE)";
    String crearinspeccion = "CREATE TABLE MTP_INSPECCION(c_inspeccion TEXT,c_descripcion TEXT,c_tipoinspeccion TEXT,n_porcentajeminimo TEXT,n_porcentajemaximo TEXT,c_familiainspeccion TEXT,c_periodoinspeccion TEXT,c_estado TEXT,c_ultimousuario TEXT,d_ultimafechamodificacion DATE)";
    String crearperiodo = "CREATE TABLE MTP_PERIODOINSPECCION(c_periodoinspeccion TEXT,c_descripcion TEXT,c_estado TEXT,c_ultimousuario TEXT,d_ultimafechamodificacion DATE)";
    String crearmaquinasdetalle= "CREATE TABLE MTP_INSPECCIONMAQUINA_DET(c_compania TEXT,n_correlativo INTEGER,n_linea TEXT,c_inspeccion TEXT,c_tipoinspeccion TEXT,n_porcentajeminimo TEXT,n_porcentajemaximo TEXT,n_porcentajeinspeccion TEXT,c_estado TEXT,c_comentario TEXT,c_rutafoto TEXT,c_ultimousuario TEXT,d_ultimafechamodificacion DATE)";
    String crearmaquinascabecera = "CREATE TABLE MTP_INSPECCIONMAQUINA_CAB(c_compania TEXT,n_correlativo INTEGER,c_maquina TEXT,c_condicionmaquina TEXT,c_comentario TEXT,c_estado TEXT,d_fechaInicioInspeccion DATE,d_fechaFinInspeccion DATE,c_periodoinspeccion TEXT,c_usuarioInspeccion TEXT,c_usuarioenvio TEXT,d_fechaenvio DATE,c_ultimousuario TEXT,d_ultimafechamodificacion DATE)";
	String crearmaquinas = "CREATE TABLE MTP_MAQUINAS(c_compania TEXT,c_maquina TEXT,c_descripcion TEXT,c_codigobarras TEXT,c_familiainspeccion TEXT,c_centrocosto TEXT,c_estado TEXT,c_ultimousuario TEXT,d_ultimafechamodificacion DATE)";
    String crearusuario = "CREATE TABLE MTP_USUARIO(c_codigousuario TEXT,c_nombre TEXT,c_clave TEXT,n_persona TEXT,c_estado TEXT,c_flagmantto TEXT)";
    String crearprogramaejecutado = "CREATE TABLE MTP_PROGRAMAEJECUTADO(c_compania TEXT,c_tipo TEXT,n_correlativo TEXT,d_fecha DATE,c_maquina TEXT,c_desMaquina TEXT,c_centrocosto TEXT,c_desCentroCosto TEXT,c_codFrecuencia TEXT,c_desFrecuencia TEXT,c_observacion TEXT,c_condicionmaquina TEXT,c_comentario TEXT,c_usuarioinspeccion TEXT,n_personainspeccion TEXT,c_nombreinspeccion TEXT)";
    String crearinspeccionejedet = "CREATE TABLE MTP_INSPECCIONEJE_DET(c_compania TEXT,n_correlativo TEXT,n_linea TEXT,c_inspeccion TEXT,c_desInpeccion TEXT,c_tipoinspeccion TEXT,n_porcentajeminimo TEXT,n_porcentajemaximo TEXT,n_porcentajeinspeccion TEXT,c_estado TEXT,c_comentario TEXT,c_rutafoto TEXT)";
    String crearinspeccionejecab = "CREATE TABLE MTP_INSPECCIONEJE_CAB(c_compania TEXT,n_correlativo TEXT,c_maquina TEXT,c_desMaquina TEXT, c_condicionmaquina TEXT, c_comentario TEXT, c_estado TEXT,d_fechaInicioInspeccion DATE,d_fechaFinInspeccion DATE,c_periodoinspeccion TEXT, c_desPeriodoInspeccion TEXT,c_usuarioInspeccion TEXT,n_personainspeccion TEXT,c_nombreinspeccion TEXT,c_generoOT TEXT,n_numeroOT TEXT)";
       	
    String crearmaquinasdetallependiente= "CREATE TABLE PENDIENTE_MTP_INSPECCIONMAQUINA_DET (c_compania TEXT,n_correlativo TEXT,n_linea TEXT,c_inspeccion TEXT,c_tipoinspeccion TEXT,n_porcentajeminimo TEXT,n_porcentajemaximo TEXT,n_porcentajeinspeccion TEXT,c_estado TEXT,c_comentario TEXT,c_rutafoto TEXT,c_ultimousuario TEXT,d_ultimafechamodificacion DATE)";
    //String crearmaquinascabecerapendiente = "CREATE TABLE PENDIENTE_MTP_INSPECCIONMAQUINA_CAB (c_compania TEXT,n_correlativo TEXT,c_maquina TEXT,c_condicionmaquina TEXT,c_comentario TEXT,c_estado TEXT,d_fechaInicioInspeccion DATE,d_fechaFinInspeccion DATE,c_periodoinspeccion TEXT,c_usuarioInspeccion TEXT,c_usuarioenvio TEXT,d_fechaenvio DATE,c_ultimousuario TEXT,d_ultimafechamodificacion TEXT)";
    String crearmaquinascabecerapendiente = "CREATE TABLE PENDIENTE_MTP_INSPECCIONMAQUINA_CAB (c_compania TEXT,n_correlativo TEXT,c_maquina TEXT,c_condicionmaquina TEXT,c_comentario TEXT,c_estado TEXT,d_fechaInicioInspeccion DATE,d_fechaFinInspeccion DATE,c_periodoinspeccion TEXT,c_usuarioInspeccion TEXT,c_usuarioenvio TEXT,d_fechaenvio DATE,c_ultimousuario TEXT,d_ultimafechamodificacion DATE)";
    //Inspeccion General
    String crearinspecciongencab = "CREATE TABLE MTP_INSPECCIONGENERAL_CAB(c_compania TEXT,n_correlativo TEXT,c_tipoinspeccion TEXT,c_maquina TEXT,c_centrocosto TEXT,c_comentario TEXT, c_usuarioinspeccion TEXT,d_fechainspeccion DATE,c_estado TEXT,c_usuarioenvio TEXT,d_fechaenvio DATE,c_ultimousuario TEXT,d_ultimafechamodificacion DATE)";
	String crearinspecciongendet = "CREATE TABLE MTP_INSPECCIONGENERAL_DET(c_compania TEXT,n_correlativo TEXT,n_linea TEXT,c_comentario TEXT,c_rutafoto TEXT,c_ultimousuario,d_ultimafechamodificacion DATE,c_tiporevisiong TEXT,c_flagadictipo)";
	String crearinspecciongencabpendiente = "CREATE TABLE PENDIENTE_MTP_INSPECCIONGENERAL_CAB(c_compania TEXT,n_correlativo TEXT,c_tipoinspeccion TEXT,c_maquina TEXT,c_centrocosto TEXT,c_comentario TEXT, c_usuarioinspeccion TEXT,d_fechainspeccion DATE,c_estado TEXT,c_usuarioenvio TEXT,d_fechaenvio DATE,c_ultimousuario TEXT,d_ultimafechamodificacion DATE)";
	String crearinspecciongendetpendiente = "CREATE TABLE PENDIENTE_MTP_INSPECCIONGENERAL_DET(c_compania TEXT,n_correlativo TEXT,n_linea TEXT,c_comentario TEXT,c_rutafoto TEXT,c_ultimousuario,d_ultimafechamodificacion DATE,c_tiporevisiong TEXT,c_flagadictipo)";
	
	String crearinspecciongejedet = "CREATE TABLE MTP_INSPECCIONGEJE_DET(c_compania TEXT,n_correlativo TEXT,n_linea TEXT,c_comentario TEXT,c_rutafoto TEXT,c_ultimousuario TEXT,d_ultimafechamodificacion DATE,c_tiporevisiong TEXT)";
	String crearinspecciongejecab = "CREATE TABLE MTP_INSPECCIONGEJE_CAB(c_compania TEXT,n_correlativo TEXT,c_tipoinspeccion TEXT,c_maquina TEXT,c_centrocosto TEXT,c_comentario TEXT,c_usuarioinspeccion TEXT,d_fechainspeccion DATE,c_estado TEXT,c_ultimousuario TEXT,d_ultimafechamodificacion DATE)";
	
	String creartiporevisiong = "CREATE TABLE MTP_TIPOREVISIONG(c_tiporevisiong TEXT,c_descripcion TEXT,c_estado TEXT)";
	String creartiporevisiongparametro = "CREATE TABLE MTP_TIPOREVISIONG_PARAMETRO(c_compania TEXT,c_tiporevisiong TEXT,c_estado TEXT,c_flagadictipo)";
	
	public DataBase(Context contexto, String nombredebase, CursorFactory factory, int version){
		super(contexto, nombredebase, factory, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(crearprogramaejecutado);
		db.execSQL(crearinspeccionejedet);
		db.execSQL(crearinspeccionejecab);
		db.execSQL(crearinspeccion);
		db.execSQL(crearcentro);
		db.execSQL(crearfamilia);
		db.execSQL(crearperiodo);
		db.execSQL(crearmaquinasdetalle);
		db.execSQL(crearmaquinascabecera);
		db.execSQL(crearmaquinasdetallependiente);
		db.execSQL(crearmaquinascabecerapendiente);
		db.execSQL(crearmaquinas);
		db.execSQL(crearusuario);
		//Inspeccion General
		db.execSQL(crearinspecciongencab);
		db.execSQL(crearinspecciongendet);
		db.execSQL(crearinspecciongencabpendiente);
		db.execSQL(crearinspecciongendetpendiente);
		db.execSQL(crearinspecciongejedet);
		db.execSQL(crearinspecciongejecab);
		
		db.execSQL(creartiporevisiong);
		db.execSQL(creartiporevisiongparametro);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
		db.execSQL("DROP TABLE IF EXISTS MTP_CENTROCOSTO");
		db.execSQL("DROP TABLE IF EXISTS MMTP_FAMILIAINSPECCION");
		db.execSQL("DROP TABLE IF EXISTS MTP_INSPECCION");
		db.execSQL("DROP TABLE IF EXISTS MTP_PERIODOINSPECCION");
		db.execSQL("DROP TABLE IF EXISTS MTP_INSPECCIONMAQUINA_DET");
		db.execSQL("DROP TABLE IF EXISTS MTP_INSPECCIONMAQUINA_CAB");
		db.execSQL("DROP TABLE IF EXISTS PENDIENTE_MTP_INSPECCIONMAQUINA_DET");
		db.execSQL("DROP TABLE IF EXISTS PENDIENTE_MTP_INSPECCIONMAQUINA_CAB");
		db.execSQL("DROP TABLE IF EXISTS MTP_MAQUINAS");
		db.execSQL("DROP TABLE IF EXISTS MTP_USUARIO");
		
		db.execSQL("DROP TABLE IF EXISTS MTP_PROGRAMAEJECUTADO");
		db.execSQL("DROP TABLE IF EXISTS MTP_INSPECCIONEJE_DET");
		db.execSQL("DROP TABLE IF EXISTS MTP_INSPECCIONEJE_CAB");
		
		//Inspeccion General
		db.execSQL("DROP TABLE IF EXISTS MTP_INSPECCIONGENERAL_DET");
		db.execSQL("DROP TABLE IF EXISTS MTP_INSPECCIONGENERAL_CAB");
		db.execSQL("DROP TABLE IF EXISTS PENDIENTE_MTP_INSPECCIONGENERAL_DET");
		db.execSQL("DROP TABLE IF EXISTS PENDIENTE_MTP_INSPECCIONGENERAL_CAB");
		
		db.execSQL("DROP TABLE IF EXISTS MTP_INSPECCIONGEJE_DET");
		db.execSQL("DROP TABLE IF EXISTS MTP_INSPECCIONGEJE_CAB");
		
		db.execSQL("DROP TABLE IF EXISTS MTP_TIPOREVISIONG");
		db.execSQL("DROP TABLE IF EXISTS MTP_TIPOREVISIONG_PARAMETRO");
		
		db.execSQL(crearprogramaejecutado);
		db.execSQL(crearinspeccionejedet);
		db.execSQL(crearinspeccionejecab);
		db.execSQL(crearcentro);
		db.execSQL(crearfamilia);
		db.execSQL(crearinspeccion);
		db.execSQL(crearperiodo);
		db.execSQL(crearmaquinasdetalle);
		db.execSQL(crearmaquinascabecera);
		db.execSQL(crearmaquinas);
		db.execSQL(crearusuario);
		db.execSQL(crearmaquinasdetallependiente);
		db.execSQL(crearmaquinascabecerapendiente);
		//Inspeccion General
		db.execSQL(crearinspecciongencab);
		db.execSQL(crearinspecciongendet);
		db.execSQL(crearinspecciongencabpendiente);
		db.execSQL(crearinspecciongendetpendiente);
		db.execSQL(crearinspecciongejedet);
		db.execSQL(crearinspecciongejecab);
		
		db.execSQL(creartiporevisiong);
		db.execSQL(creartiporevisiongparametro);
	}

}
