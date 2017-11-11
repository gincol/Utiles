package es.viewnext.modelo;

import javafx.beans.property.SimpleStringProperty;

public class AppServer extends Server {

	public AppServer() {
		super();
		this.tipo = new SimpleStringProperty("AppServer");
	}

	public AppServer(String name, String ip, String tipo, String servidor, String jdk, String version) {
		super(name, ip, tipo, servidor, jdk, version);
	}

}
