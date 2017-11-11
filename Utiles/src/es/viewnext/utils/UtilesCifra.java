package es.viewnext.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class UtilesCifra {

	public static PublicKey readPublicKey(String filename, String pasword)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException,
			NoSuchProviderException, CertificateException, UnrecoverableKeyException {
		KeyStore keystore = KeyStore.getInstance("PKCS12");
		keystore.load(new FileInputStream(new File(filename)), pasword.toCharArray());
		Enumeration<String> aliases = keystore.aliases();
		String keyAlias = "";
		while (aliases.hasMoreElements()) {
			keyAlias = (String) aliases.nextElement();
		}
		Certificate cert = keystore.getCertificate(keyAlias);
		PublicKey key = cert.getPublicKey();
		return key;
	}
	
	public static PublicKey readPublicKey(String filename)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		FileInputStream fin = new FileInputStream(filename);
		CertificateFactory f;
		X509Certificate certificate = null;
		PublicKey pk = null;
		try {
			f = CertificateFactory.getInstance("X.509");
			certificate = (X509Certificate) f.generateCertificate(fin);
			pk = certificate.getPublicKey();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return pk;
	}
	
	public static PrivateKey readPrivateKey(String filename, String pasword)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException,
			NoSuchProviderException, CertificateException, UnrecoverableKeyException {
		KeyStore keystore = KeyStore.getInstance("PKCS12");
		keystore.load(new FileInputStream(new File(filename)), pasword.toCharArray());
		Enumeration<String> aliases = keystore.aliases();
		String keyAlias = "";
		while (aliases.hasMoreElements()) {
			keyAlias = (String) aliases.nextElement();
		}
		PrivateKey key = (PrivateKey) keystore.getKey(keyAlias, pasword.toCharArray());
		return key;
	}
	
	public static void customAlert(String funcion, String texto){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Se ha producido un error en la función '"+ funcion +"'");
		alert.setContentText("Pulse sobre 'Mostrar detalles' para ver el mensaje");

		Label label = new Label("Descripción:");

		TextArea error = new TextArea(texto);
		error.setEditable(false);
		error.setWrapText(true);
		
		error.setMaxWidth(Double.MAX_VALUE);
		error.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(error, Priority.ALWAYS);
		GridPane.setHgrow(error, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(error, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);
		alert.showAndWait();
	}
}
