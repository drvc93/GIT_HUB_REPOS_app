package com.lys.mobile.dataBase;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by dvillanueva on 15/07/2016.
 */
public class InspMaqDetDB  implements KvmSerializable {

    private String c_compania;
    private String n_correlativo;
    private String n_linea;
    private String c_inpeccion;
    private String c_tipoinspeccion ;
    private String n_porcentajeminimo;
    private String n_porcentajemaximo;
    private String n_pocentajeinspeccion;
    private String c_estado;
    private String c_comentario;
    private String c_rutafoto;
    private String c_ultimousuario;
    private String d_ultimafechamodificacion;

    public InspMaqDetDB() {
    }

    public InspMaqDetDB(String c_compania, String n_correlativo, String n_linea, String c_inpeccion, String c_tipoinspeccion, String n_porcentajeminimo, String n_porcentajemaximo, String n_pocentajeinspeccion, String c_estado, String c_comentario, String c_rutafoto, String c_ultimousuario, String d_ultimafechamodificacion) {
        this.c_compania = c_compania;
        this.n_correlativo = n_correlativo;
        this.n_linea = n_linea;
        this.c_inpeccion = c_inpeccion;
        this.c_tipoinspeccion = c_tipoinspeccion;
        this.n_porcentajeminimo = n_porcentajeminimo;
        this.n_porcentajemaximo = n_porcentajemaximo;
        this.n_pocentajeinspeccion = n_pocentajeinspeccion;
        this.c_estado = c_estado;
        this.c_comentario = c_comentario;
        this.c_rutafoto = c_rutafoto;
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

    public String getN_linea() {
        return n_linea;
    }

    public void setN_linea(String n_linea) {
        this.n_linea = n_linea;
    }

    public String getC_inpeccion() {
        return c_inpeccion;
    }

    public void setC_inpeccion(String c_inpeccion) {
        this.c_inpeccion = c_inpeccion;
    }

    public String getC_tipoinspeccion() {
        return c_tipoinspeccion;
    }

    public void setC_tipoinspeccion(String c_tipoinspeccion) {
        this.c_tipoinspeccion = c_tipoinspeccion;
    }

    public String getN_porcentajeminimo() {
        return n_porcentajeminimo;
    }

    public void setN_porcentajeminimo(String n_porcentajeminimo) {
        this.n_porcentajeminimo = n_porcentajeminimo;
    }

    public String getN_porcentajemaximo() {
        return n_porcentajemaximo;
    }

    public void setN_porcentajemaximo(String n_porcentajemaximo) {
        this.n_porcentajemaximo = n_porcentajemaximo;
    }

    public String getN_pocentajeinspeccion() {
        return n_pocentajeinspeccion;
    }

    public void setN_pocentajeinspeccion(String n_pocentajeinspeccion) {
        this.n_pocentajeinspeccion = n_pocentajeinspeccion;
    }

    public String getC_estado() {
        return c_estado;
    }

    public void setC_estado(String c_estado) {
        this.c_estado = c_estado;
    }

    public String getC_comentario() {
        return c_comentario;
    }

    public void setC_comentario(String c_comentario) {
        this.c_comentario = c_comentario;
    }

    public String getC_rutafoto() {
        return c_rutafoto;
    }

    public void setC_rutafoto(String c_rutafoto) {
        this.c_rutafoto = c_rutafoto;
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
                return n_linea;
            case 3:
                return c_inpeccion;
            case 4:
                return c_tipoinspeccion;
            case 5:
                return  n_porcentajeminimo;
            case 6:
                return  n_porcentajemaximo;
            case 7:
                return n_pocentajeinspeccion;
            case 8:
                return c_estado;
            case 9:
                return  c_comentario;
            case 10:
                return c_rutafoto;
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
                    c_compania= o.toString();
                    break;
                case 1:
                    n_correlativo = o.toString();
                    break;
                case 2:
                    n_linea = o.toString();
                    break;
                case 3:
                    c_inpeccion = o.toString();
                    break;
                case 4:
                    c_tipoinspeccion = o.toString();
                    break;
                case 5:
                    n_porcentajeminimo = o.toString();
                    break;
                case 6:
                    n_porcentajemaximo = o.toString();
                    break;
                case 7:
                    n_pocentajeinspeccion = o.toString();
                    break;
                case 8:
                    c_estado = o.toString();
                    break;
                case 9:
                    c_comentario = o.toString();
                    break;
                case 10:
                    c_rutafoto = o.toString();
                    break;
                case 11:
                    c_ultimousuario = o.toString();
                    break;
                case 12:
                    d_ultimafechamodificacion = o.toString();
                    break;
                default:break;





            }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {

        switch (i){

            case 0 :
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "c_compania";
                break;
            case 1:
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "n_correlativo";
                break;
            case 2:

                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "n_linea";
                break;
            case 3:
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "c_inpeccion";
                break;
            case 4:
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "c_tipoinspeccion";
                break;
            case 5:
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "n_porcentajeminimo";
                break;
            case 6:
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "n_porcentajemaximo";
                break;
            case 7:
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "n_pocentajeinspeccion";
                break;
            case 8:
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "c_estado";
                break;
            case 9:
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "c_comentario";
                break;
            case 10:
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "c_rutafoto";
                break;
            case 11:
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "c_ultimousuario";
                break;
            case 12:
                propertyInfo.type= PropertyInfo.STRING_CLASS;
                propertyInfo.name = "d_ultimafechamodificacion";
                break;
            default:break;
        }

    }
}
