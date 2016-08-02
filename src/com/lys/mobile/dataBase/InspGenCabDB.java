package com.lys.mobile.dataBase;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by dvillanueva on 18/07/2016.
 */
public class InspGenCabDB implements KvmSerializable {
    private String c_compania;
    private String n_correlativo;
    private String c_tipoinspeccion;
    private String c_maquina;
    private String c_centrocosto;
    private String c_comentario;
    private String c_usuarioinspeccion;
    private String d_fechainspeccion;
    private String c_estado;
    private String c_usuarioenvio;
    private String d_fechaenvio;
    private String c_ultimousuario;
    private String d_ultimafechamodificacion;

    public InspGenCabDB() {
    }

    public InspGenCabDB(String c_compania, String n_correlativo, String c_tipoinspeccion, String c_maquina, String c_centrocosto, String c_comentario, String c_usuarioinspeccion, String d_fechainspeccion, String c_estado, String c_usuarioenvio, String d_fechaenvio, String c_ultimousuario, String d_ultimafechamodificacion) {
        this.c_compania = c_compania;
        this.n_correlativo = n_correlativo;
        this.c_tipoinspeccion = c_tipoinspeccion;
        this.c_maquina = c_maquina;
        this.c_centrocosto = c_centrocosto;
        this.c_comentario = c_comentario;
        this.c_usuarioinspeccion = c_usuarioinspeccion;
        this.d_fechainspeccion = d_fechainspeccion;
        this.c_estado = c_estado;
        this.c_usuarioenvio = c_usuarioenvio;
        this.d_fechaenvio = d_fechaenvio;
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

    public String getC_tipoinspeccion() {
        return c_tipoinspeccion;
    }

    public void setC_tipoinspeccion(String c_tipoinspeccion) {
        this.c_tipoinspeccion = c_tipoinspeccion;
    }

    public String getC_maquina() {
        return c_maquina;
    }

    public void setC_maquina(String c_maquina) {
        this.c_maquina = c_maquina;
    }

    public String getC_centrocosto() {
        return c_centrocosto;
    }

    public void setC_centrocosto(String c_centrocosto) {
        this.c_centrocosto = c_centrocosto;
    }

    public String getC_comentario() {
        return c_comentario;
    }

    public void setC_comentario(String c_comentario) {
        this.c_comentario = c_comentario;
    }

    public String getC_usuarioinspeccion() {
        return c_usuarioinspeccion;
    }

    public void setC_usuarioinspeccion(String c_usuarioinspeccion) {
        this.c_usuarioinspeccion = c_usuarioinspeccion;
    }

    public String getD_fechainspeccion() {
        return d_fechainspeccion;
    }

    public void setD_fechainspeccion(String d_fechainspeccion) {
        this.d_fechainspeccion = d_fechainspeccion;
    }

    public String getC_estado() {
        return c_estado;
    }

    public void setC_estado(String c_estado) {
        this.c_estado = c_estado;
    }

    public String getC_usuarioenvio() {
        return c_usuarioenvio;
    }

    public void setC_usuarioenvio(String c_usuarioenvio) {
        this.c_usuarioenvio = c_usuarioenvio;
    }

    public String getD_fechaenvio() {
        return d_fechaenvio;
    }

    public void setD_fechaenvio(String d_fechaenvio) {
        this.d_fechaenvio = d_fechaenvio;
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
                return  n_correlativo;
            case 2 :
                return c_tipoinspeccion;
            case 3:
                return c_maquina;
            case 4:
                return c_centrocosto;
            case 5:
                return c_comentario;
            case 6:
                return  c_usuarioinspeccion;
            case 7:
                return d_fechainspeccion;
            case 8:
                return c_estado;
            case 9:
                return  c_usuarioenvio;
            case 10:
                return  d_fechaenvio;
            case 11:
                return c_ultimousuario;
            case 12:
                return  d_ultimafechamodificacion;

        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 13;
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
                c_tipoinspeccion  =o.toString();
                break;
            case 3:
                c_maquina = o.toString();
                break;
            case 4:
                c_centrocosto = o.toString();
                break;
            case 5:
                c_comentario = o.toString();
                break;
            case 6:
                c_usuarioinspeccion = o.toString();
                break;
            case 7:
                d_fechainspeccion = o.toString();
                break;
            case 8:
                 c_estado  = o.toString();
                break;
            case 9:
                c_usuarioenvio = o.toString();
                break;
            case 10:
                d_fechaenvio = o.toString();
                break;
            case 11:
                  c_ultimousuario = o.toString();
                 break;
            case 12:
                d_ultimafechamodificacion = o.toString();
                break;
            default: break;




        }

    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {




    }
}
