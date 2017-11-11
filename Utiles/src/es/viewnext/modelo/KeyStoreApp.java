package es.viewnext.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class KeyStoreApp {
	private StringProperty nombre;
	private StringProperty ruta;
	private StringProperty password;
	private IntegerProperty numAlias;
	
	public KeyStoreApp(){
		this(null, null, null, 0);
	}

	public KeyStoreApp(String nombre, String ruta, String password, int numAlias) {
		super();
		this.nombre = new SimpleStringProperty(nombre);
		this.ruta = new SimpleStringProperty(ruta);
		this.password = new SimpleStringProperty(password);
		this.numAlias = new SimpleIntegerProperty(numAlias);
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

	public String getRuta() {
		return ruta.get();
	}

	public void setRuta(String ruta) {
		this.ruta.set(ruta);
	}
	
	public StringProperty rutaProperty() {
		return this.ruta;
	}
	
	public String getPassword() {
		return password.get();
	}

	public void setPassword(String password) {
		this.password.set(password);
	}
	
	public StringProperty passwordProperty() {
		return this.password;
	}
	
	public Integer getNumAlias() {
		return numAlias.get();
	}

	public void setNumAlias(Integer numAlias) {
		this.numAlias.set(numAlias);
	}
	
	public IntegerProperty numAliasProperty(){
		return this.numAlias;
	}
	
}
