package es.viewnext.modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Columna {

	private StringProperty nombre;
	private StringProperty tipo;

	public Columna() {
		this(null, null);
	}

	public Columna(String nombre, String tipo) {
		super();
		this.nombre = new SimpleStringProperty(nombre);
		this.tipo = new SimpleStringProperty(tipo);
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
	
	public String getTipo() {
		return tipo.get();
	}

	public void setTipo(String tipo) {
		this.tipo.set(tipo);
	}

	public StringProperty tipoProperty() {
		return this.tipo;
	}
	
}
