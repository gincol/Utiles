package es.viewnext.modelo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Server {
	
	public StringProperty name;
	public StringProperty ip;
	public StringProperty tipo;
	public StringProperty servidor;
	public StringProperty version;
	public StringProperty jdk;
	
	public Server() {
		this(null,null,null,null,null,null);
	}
	
	public Server(String name, String ip, String tipo, String servidor, String jdk, String version) {
		super();
		this.name = new SimpleStringProperty(name);
		this.ip = new SimpleStringProperty(ip);
		this.tipo = new SimpleStringProperty(tipo);
		this.servidor = new SimpleStringProperty(servidor);
		this.jdk = new SimpleStringProperty(jdk);
		this.version = new SimpleStringProperty(version);
	}
	
	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public StringProperty nameProperty(){
		return this.name;
	}
	
	public String getIp() {
		return ip.get();
	}
	public void setIp(String ip) {
		this.ip.set(ip);
	}
	public StringProperty ipProperty(){
		return this.ip;
	}
	
	public String getTipo() {
		return tipo.get();
	}
	public void setTipo(String tipo) {
		this.tipo.set(tipo);
	}
	public StringProperty tipoProperty(){
		return this.tipo;
	}
	
	public String getServidor() {
		return servidor.get();
	}
	public void setServidor(String servidor) {
		this.servidor.set(servidor);
	}
	public StringProperty servidorProperty(){
		return this.servidor;
	}
	
	public String getJdk() {
		return jdk.get();
	}
	public void setJdk(String jdk) {
		this.jdk.set(jdk);
	}
	public StringProperty jdkProperty(){
		return this.jdk;
	}

	public String getVersion() {
		return version.get();
	}
	public void setVersion(String version) {
		this.version.set(version);
	}
	public StringProperty versionProperty(){
		return this.version;
	}
}
