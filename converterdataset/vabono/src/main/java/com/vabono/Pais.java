package com.vabono;

import java.util.List;

public class Pais {
	

	public String nombre ; 
	public List<Anio> anios;
		
	public Pais(String nombre, List<Anio> anios) {
		super();
		this.nombre = nombre;
		this.anios = anios;
	}
	
	public List<Anio> getAnios() {
		return anios;
	}

	public void setAnios(List<Anio> anios) {
		this.anios = anios;
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
