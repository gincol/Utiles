package es.viewnext.modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Esquema {
	
	private StringProperty nombre;
	
	public Esquema() {
		this(null);
	}

	public Esquema(String nombre) {
		super();
		this.nombre = new SimpleStringProperty(nombre);
	}
	
	public String getNombre() {
		return nombre.get();
	}

	public void setNombre(String nombre) {
		this.nombre.set(nombre);
	}
	
	public StringProperty nombreProperty() {
		return this.nombre;
	}
	
}
