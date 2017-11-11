package es.viewnext.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Libreria {
	private String nombre;
	private String ruta;
	private String encoding;
	private String texto;
	private String aux;
	private int numFiles;

	public Libreria() {
		this(null, null, null, null, null, 0);
	}

	public Libreria(String nombre, String ruta, String encoding, String texto, String aux, int numFiles) {
		super();
		this.nombre = nombre;
		this.ruta = ruta;
		this.encoding = encoding;
		this.texto = texto;
		this.aux = aux;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public StringProperty nombreProperty() {
		return new SimpleStringProperty(this.nombre);
	}

	public String getRuta() {
		return ruta;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

	public StringProperty rutaProperty() {
		return new SimpleStringProperty(this.ruta);
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public StringProperty encodingProperty() {
		return new SimpleStringProperty(this.encoding);
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public StringProperty textoProperty() {
		return new SimpleStringProperty(this.texto);
	}
	
	public String getAux() {
		return aux;
	}

	public void setAux(String aux) {
		this.aux = aux;
	}

	public StringProperty auxProperty() {
		return new SimpleStringProperty(this.aux);
	}
	
	public int getNumFiles() {
		return numFiles;
	}

	public void setNumFiles(int numFiles) {
		this.numFiles = numFiles;
	}

	public IntegerProperty numFilesProperty() {
		return new SimpleIntegerProperty(this.numFiles);
	}
}
