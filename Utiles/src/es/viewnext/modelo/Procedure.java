package es.viewnext.modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Procedure {
	
	private StringProperty nombre;

	public Procedure() {
		this(null);
	}

	public Procedure(String nombre) {
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
