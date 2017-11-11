package es.viewnext.modelo;

import javafx.beans.property.SimpleStringProperty;

public class BaseDatos extends Server {

	public BaseDatos() {
		super();
		this.tipo = new SimpleStringProperty("BBDD");
	}

	public BaseDatos(String name, String ip, String tipo, String servidor, String jdk, String version) {
		super(name, ip, tipo, servidor, jdk, version);
	}

}
