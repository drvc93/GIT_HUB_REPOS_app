package com.lys.mobile.data;

import java.io.Serializable;

public class InspeccionGData implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String c_compania;
	public String n_correlativo; 
	public String n_linea;
    public String c_comentario;  
	public String c_rutafoto;  
	public String c_ultimousuario;
	public String d_ultimafechamodificacion;
	public String c_tiporevisiong;
	public String c_flagadictipo;
	//public String c_flaggensolxdet;
	
	public InspeccionGData(String c_compania,String n_correlativo,String n_linea,
			String c_comentario,String c_rutafoto,String c_ultimousuario,
			String d_ultimafechamodificacion,String c_tiporevisiong,
			String c_flagadictipo) {
		//,String c_flaggensolxdet
		this.setCompania(c_compania);
		this.setCorrelativo(n_correlativo);
		this.setLinea(n_linea);
		this.setComentario(c_comentario);
		this.setRutaFoto(c_rutafoto);
		this.setUltimoUsuario(c_ultimousuario);
		this.setUltimaFechaMod(d_ultimafechamodificacion);
		this.setTipoRevisionG(c_tiporevisiong);
		this.setFlagAdicTipo(c_flagadictipo);
		//this.setFlagGenSolxDet(c_flaggensolxdet);
	}
	
	public String getCompania() {
		return c_compania;
	}

	public void setCompania(String c_compania) {
		this.c_compania = c_compania;
	}

	public String getCorrelativo() {
		return n_correlativo;
	}

	public void setCorrelativo(String n_correlativo) {
		this.n_correlativo = n_correlativo;
	}
	
	public String getLinea() {
		return n_linea;
	}

	public void setLinea(String n_linea) {
		this.n_linea = n_linea;
	}
	
	public String getComentario() {
		return c_comentario;
	}

	public void setComentario(String c_comentario) {
		this.c_comentario = c_comentario;
	}
	
	public String getRutaFoto() {
		return c_rutafoto;
	}

	public void setRutaFoto(String c_rutafoto) {
		this.c_rutafoto = c_rutafoto;
	}
	
	public String getUltimoUsuario() {
		return c_ultimousuario;
	}

	public void setUltimoUsuario(String c_ultimousuario) {
		this.c_ultimousuario = c_ultimousuario;
	}
	
	public String getUltimaFechaMod() {
		return d_ultimafechamodificacion;
	}

	public void setUltimaFechaMod(String d_ultimafechamodificacion) {
		this.d_ultimafechamodificacion = d_ultimafechamodificacion;
	}
	
	public String getTipoRevisionG() {
		return c_tiporevisiong;
	}
	
	public void setTipoRevisionG(String c_tiporevisiong) {
		this.c_tiporevisiong = c_tiporevisiong;
	}
	
	public String getFlagAdicTipo() {
		return c_flagadictipo;
	}
	
	public void setFlagAdicTipo(String c_flagadictipo) {
		this.c_flagadictipo = c_flagadictipo;
	}
	
	/*public String getFlagGenSolxDet() {
		return c_flaggensolxdet;
	}
	
	public void setFlagGenSolxDet(String c_flaggensolxdet) {
		this.c_flaggensolxdet = c_flaggensolxdet;
	}*/
	
}
