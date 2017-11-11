package es.viewnext.modelo;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Folder {
	private String nombre;
	private String ruta;
	private List<Fichero> listFile;

	public Folder() {
		this(null, null, null);
	}

	public Folder(String nombre, String ruta, List<Fichero> listFile) {
		super();
		this.nombre = nombre;
		this.ruta = ruta;
		this.listFile = listFile;
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

	public List<Fichero> getListFile() {
		return new ArrayList<Fichero>(listFile);
	}

	public void setListFile(List<Fichero> listFile) {
		this.listFile = new ArrayList<Fichero>(listFile);
	}

}
