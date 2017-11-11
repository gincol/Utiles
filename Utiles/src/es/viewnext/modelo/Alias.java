package es.viewnext.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Alias {
	private ObjectProperty<KeyStoreApp> Keystore;
	private StringProperty nombre;
	private IntegerProperty numCert;
	
	public Alias() {
		this(null,null,0);
	}

	public Alias(SimpleObjectProperty<KeyStoreApp> keystore, String nombre, int numCert) {
		super();
		this.Keystore = new SimpleObjectProperty<KeyStoreApp>();
		this.nombre = new SimpleStringProperty(nombre);
		this.numCert = new SimpleIntegerProperty(numCert);
	}
	
	public KeyStoreApp getKeyStoreApp() {
		return Keystore.get();
	}
	public void setKeyStoreApp(KeyStoreApp Keystore) {
		this.Keystore.set(Keystore);
	}
	public ObjectProperty<KeyStoreApp> keyStoreAppProperty() {
		return Keystore;
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
	
	public Integer getNumCert() {
		return numCert.get();
	}

	public void setNumCert(Integer numCert) {
		this.numCert.set(numCert);
	}
	
	public IntegerProperty numCertProperty(){
		return this.numCert;
	}
}
