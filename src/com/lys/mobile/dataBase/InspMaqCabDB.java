package com.lys.mobile.dataBase;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by dvillanueva on 15/07/2016.
 */
public class InspMaqCabDB implements KvmSerializable {

    private String c_compania;
    private String n_correlativo;
    private String c_maquina;
    private String c_condicionmaquina;
    private String c_comentario;
    private String c_estado;
    private String d_fechaInicioInspeccion;
    private String d_fechaFinInspeccion;
    private String c_periodoinspeccion ;
    private String c_usuarioInspeccion;
    private String c_usuarioenvio;
    private String d_fechaEnvio;
    private String c_ultimousuario;
    private String d_ultimafechamodificacion;

    public InspMaqCabDB() {
    }

    public InspMaqCabDB(String c_compania, String n_correlativo, String c_maquina, String c_condicionmaquina, String c_comentario, String c_estado, String d_fechaInicioInspeccion, String d_fechaFinInspeccion, String c_periodoinspeccion, String c_usuarioInspeccion, String c_usuarioenvio, String d_fechaEnvio, String c_ultimousuario, String d_ultimafechamodificacion) {
        this.c_compania = c_compania;
        this.n_correlativo = n_correlativo;
        this.c_maquina = c_maquina;
        this.c_condicionmaquina = c_condicionmaquina;
        this.c_comentario = c_comentario;
        this.c_estado = c_estado;
        this.d_fechaInicioInspeccion = d_fechaInicioInspeccion;
        this.d_fechaFinInspeccion = d_fechaFinInspeccion;
        this.c_periodoinspeccion = c_periodoinspeccion;
        this.c_usuarioInspeccion = c_usuarioInspeccion;
        this.c_usuarioenvio = c_usuarioenvio;
        this.d_fechaEnvio = d_fechaEnvio;
        this.c_ultimousuario = c_ultimousuario;
        this.d_ultimafechamodificacion = d_ultimafechamodificacion;
    }

    public String getC_compania() {
        return c_compania;
    }

    public void setC_compania(String c_compania) {
        this.c_compania = c_compania;
    }

    public String getN_correlativo() {
        return n_correlativo;
    }

    public void setN_correlativo(String n_correlativo) {
        this.n_correlativo = n_correlativo;
    }

    public String getC_maquina() {
        return c_maquina;
    }

    public void setC_maquina(String c_maquina) {
        this.c_maquina = c_maquina;
    }

    public String getC_condicionmaquina() {
        return c_condicionmaquina;
    }

    public void setC_condicionmaquina(String c_condicionmaquina) {
        this.c_condicionmaquina = c_condicionmaquina;
    }

    public String getC_comentario() {
        return c_comentario;
    }

    public void setC_comentario(String c_comentario) {
        this.c_comentario = c_comentario;
    }

    public String getC_estado() {
        return c_estado;
    }

    public void setC_estado(String c_estado) {
        this.c_estado = c_estado;
    }

    public String getD_fechaInicioInspeccion() {
        return d_fechaInicioInspeccion;
    }

    public void setD_fechaInicioInspeccion(String d_fechaInicioInspeccion) {
        this.d_fechaInicioInspeccion = d_fechaInicioInspeccion;
    }

    public String getD_fechaFinInspeccion() {
        return d_fechaFinInspeccion;
    }

    public void setD_fechaFinInspeccion(String d_fechaFinInspeccion) {
        this.d_fechaFinInspeccion = d_fechaFinInspeccion;
    }

    public String getC_periodoinspeccion() {
        return c_periodoinspeccion;
    }

    public void setC_periodoinspeccion(String c_periodoinspeccion) {
        this.c_periodoinspeccion = c_periodoinspeccion;
    }

    public String getC_usuarioInspeccion() {
        return c_usuarioInspeccion;
    }

    public void setC_usuarioInspeccion(String c_usuarioInspeccion) {
        this.c_usuarioInspeccion = c_usuarioInspeccion;
    }

    public String getC_usuarioenvio() {
        return c_usuarioenvio;
    }

    public void setC_usuarioenvio(String c_usuarioenvio) {
        this.c_usuarioenvio = c_usuarioenvio;
    }

    public String getD_fechaEnvio() {
        return d_fechaEnvio;
    }

    public void setD_fechaEnvio(String d_fechaEnvio) {
        this.d_fechaEnvio = d_fechaEnvio;
    }

    public String getC_ultimousuario() {
        return c_ultimousuario;
    }

    public void setC_ultimousuario(String c_ultimousuario) {
        this.c_ultimousuario = c_ultimousuario;
    }

    public String getD_ultimafechamodificacion() {
        return d_ultimafechamodificacion;
    }

    public void setD_ultimafechamodificacion(String d_ultimafechamodificacion) {
        this.d_ultimafechamodificacion = d_ultimafechamodificacion;
    }


    @Override
    public Object getProperty(int i) {

        switch (i){

            case 0:
                return  c_compania;
            case 1:
                return n_correlativo;
            case 2:
                return c_maquina;
            case 3:
                return c_condicionmaquina;
            case 4:
                return  c_comentario;
            case 5:
                return c_estado;
            case 6:
                return d_fechaInicioInspeccion;
            case 7:
                return d_fechaFinInspeccion;
            case 8:
                return c_periodoinspeccion;
            case 9:
                return c_usuarioInspeccion;
            case 10:
                return c_usuarioenvio;
            case 11:
                return d_fechaEnvio;
            case 12:
                return c_ultimousuario;
            case 13:
                return d_ultimafechamodificacion;



        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 14;
    }

    @Override
    public void setProperty(int i, Object o) {

        switch (i){


            case 0:

                c_compania = o.toString();
                break;
            case 1:
                n_correlativo = o.toString();
                break;
            case 2:
                c_maquina = o.toString();
                break;
            case 3:
                c_condicionmaquina = o.toString();
                break;
            case 4:
                c_comentario = o.toString();
                break;
            case 5:
                c_estado = o.toString();
                break;
            case 6:
                d_fechaInicioInspeccion = o .toString();
                break;
            case 7:
                d_fechaFinInspeccion = o.toString();
                break;
            case  8:
                c_periodoinspeccion = o.toString();
                break;
            case 9:
                c_usuarioInspeccion = o.toString();
                break;
            case 10:
                c_usuarioenvio = o.toString();
                break;
            case 11 :
                d_fechaEnvio = o.toString();
                break;
            case  12 :
                c_ultimousuario = o.toString();
                break;
            case 13:
                d_ultimafechamodificacion = o.toString();
                break;
            default:
                break;



        }


    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {

        switch ( i){

            case 0:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "c_compania";
                break;

            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "n_correlativo";
                break;

            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "c_maquina";
                break;
            case  3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "c_condicionmaquina";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "c_comentario";
                break;

            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "c_estado";
                break;

            case 6:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "d_fechaInicioInspeccion";
                break;
            case 7:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "d_fechaFinInspeccion";
                break;
            case 8:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "c_periodoinspeccion";
                break;
            case 9:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "c_usuarioInspeccion";
                break;
            case 10:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "c_usuarioenvio";
                break;
            case 11:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "d_fechaEnvio";
                break;
            case 12:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "c_ultimousuario";
                break;
            case 13:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name= "d_ultimafechamodificacion";
                break;
            default:break;


        }


    }
}
