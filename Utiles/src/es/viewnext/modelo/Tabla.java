package es.viewnext.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Tabla {
	
	private StringProperty nombre;
	private IntegerProperty numColumnas;
	private IntegerProperty numRegistros;

	public Tabla() {
		this(null, 0, 0);
	}

	public Tabla(String nombre, int numColumnas, int numRegistros) {
		super();
		this.nombre = new SimpleStringProperty(nombre);
		this.numColumnas = new SimpleIntegerProperty(numColumnas);
		this.numRegistros = new SimpleIntegerProperty(numRegistros);
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

	public Integer getNumColumnas() {
		return numColumnas.get();
	}

	public void setNumColumnas(Integer numColumnas) {
		this.numColumnas.set(numColumnas);
	}

	public IntegerProperty numColumnasProperty() {
		return this.numColumnas;
	}

	public Integer getNumRegistros() {
		return numRegistros.get();
	}

	public void setNumRegistros(Integer numRegistros) {
		this.numRegistros.set(numRegistros);
	}

	public IntegerProperty numRegistrosProperty() {
		return this.numRegistros;
	}
	
}
