package es.viewnext.modelo;

import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import es.viewnext.utils.LocalDateAdapter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Certificado {

	private ObjectProperty<KeyStoreApp> Keystore;
	private ObjectProperty<Alias> alias;
	private String rutaLocal;
	private String fichero;
	private String url;
	private String subject;
	private ObjectProperty<LocalDate> fechaCaducidad;
	private IntegerProperty numCertsInChain;
	private List<X509Certificate> certificateList;

	public Certificado() {
		this(null, null, null, null, null, null, null, 0, null);
	}

	public Certificado(SimpleObjectProperty<KeyStoreApp> keystore, SimpleObjectProperty<Alias> alias,
			String rutaLocal, String fichero, String url, String subject, SimpleObjectProperty<LocalDate> fechaCaducidad,
			int numCertsInChain, List<X509Certificate> certificateList) {
		super();
		this.Keystore = new SimpleObjectProperty<KeyStoreApp>();
		this.alias = new SimpleObjectProperty<Alias>();
		this.rutaLocal = rutaLocal;
		this.fichero = fichero;
		this.url = url;
		this.subject = subject;
		this.fechaCaducidad = new SimpleObjectProperty<LocalDate>();
		this.numCertsInChain = new SimpleIntegerProperty(numCertsInChain);
		this.certificateList = new ArrayList<X509Certificate>();
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
	
	public Alias getAlias() {
		return alias.get();
	}
	public void setAlias(Alias alias) {
		this.alias.set(alias);
	}
	public ObjectProperty<Alias> aliasProperty() {
		return alias;
	}

	public String getRutaLocal() {
		return rutaLocal;
	}

	public void setRutaLocal(String rutaLocal) {
		this.rutaLocal = rutaLocal;
	}

	public StringProperty rutaLocalProperty() {
		return new SimpleStringProperty(this.rutaLocal);
	}

	public String getFichero() {
		return fichero;
	}

	public void setfichero(String fichero) {
		this.fichero = fichero;
	}

	public StringProperty ficheroProperty() {
		return new SimpleStringProperty(this.fichero);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public StringProperty urlProperty() {
		return new SimpleStringProperty(this.url);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public StringProperty subjectProperty() {
		return new SimpleStringProperty(this.subject);
	}

	@XmlJavaTypeAdapter(LocalDateAdapter.class)
	public LocalDate getFechaCaducidad() {
		return fechaCaducidad.get();
	}

	public void setFechaCaducidad(LocalDate fechaCaducidad) {
		this.fechaCaducidad.set(fechaCaducidad);
	}

	public ObjectProperty<LocalDate> fechaCaducidadProperty() {
		return fechaCaducidad;
	}

	public Integer getNumCertsInChain() {
		return numCertsInChain.get();
	}

	public void setNumCertsInChain(Integer numCertsInChain) {
		this.numCertsInChain.set(numCertsInChain);
	}

	public IntegerProperty numCertsInChainProperty() {
		return this.numCertsInChain;
	}

	public List<X509Certificate> getCertificateList() {
		return new ArrayList<X509Certificate>(certificateList);
	}

	public void setCertificateList(List<X509Certificate> certificateList) {
		this.certificateList = new ArrayList<X509Certificate>(certificateList);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Keystore == null) ? 0 : Keystore.hashCode());
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Certificado other = (Certificado) obj;
		if (Keystore == null) {
			if (other.Keystore != null)
				return false;
		} else if (!Keystore.equals(other.Keystore))
			return false;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		return true;
	}

}
