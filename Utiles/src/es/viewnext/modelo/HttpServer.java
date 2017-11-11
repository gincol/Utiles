package es.viewnext.modelo;

import javafx.beans.property.SimpleStringProperty;

public class HttpServer extends Server {

	public HttpServer() {
		super();
		this.tipo = new SimpleStringProperty("HttpServer");
	}

	public HttpServer(String name, String ip, String tipo, String servidor, String jdk, String version) {
		super(name, ip, tipo, servidor, jdk, version);
	}

}
