package es.viewnext.cert;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

import es.viewnext.Main;
import es.viewnext.principal.RootController;
import es.viewnext.utils.UtilesCifra;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;

public class CifrarDescifrarController {
	private Main main;
	private RootController rootController;

	@FXML
	private TextField textIn;
	@FXML
	private TextArea textOut;
	@FXML
	private TextField pathCert;
	@FXML
	private TextField textPassword;
	
	@FXML
	private TextArea textInDes;
	@FXML
	private TextArea textOutDes;
	@FXML
	private TextField pathCertDes;
	@FXML
	private TextField textPasswordDes;
	
	File file;
	File fileDes;
	File fileSel;
	final Clipboard clipboard = Clipboard.getSystemClipboard();
	final ClipboardContent content = new ClipboardContent();
	
	@FXML
	private void initialize() {
		System.out.println();
	}
	
	@FXML
	private void generateText(){
		SecureRandom random = new SecureRandom();
		textIn.setText(new BigInteger(130, random).toString(32));
	}
	
	@FXML
	private void getCertificate(){
		FileChooser fileChooser = new FileChooser();
		if(this.fileSel != null){
			File existDirectory = this.fileSel.getParentFile();
            fileChooser.setInitialDirectory(existDirectory);
		} else {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		}
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("KeyStore (*.p12), PublicKey (*.crt)","*.p12", "*.crt");
		fileChooser.getExtensionFilters().add(extFilter);
		this.file = fileChooser.showOpenDialog(main.getPrimaryStage());
		if (this.file != null) {
			this.pathCert.setText(this.file.getPath().trim());
			this.fileSel = this.file;
		}
		
	}
	
	@FXML
	private void xifrar() throws Exception {
		
		if(this.textIn.getText().trim().length()>0 && this.pathCert.getText().trim().length()>0){
			StringWriter errors = new StringWriter();	
			try{
				X509EncodedKeySpec x509EncodedKeySpec = null;
				if(this.pathCert.getText().endsWith(".p12")){
					if(this.textPassword.getText().trim().length()>0){
						x509EncodedKeySpec = new X509EncodedKeySpec(UtilesCifra.readPublicKey(this.pathCert.getText().trim(), this.textPassword.getText().trim()).getEncoded());
					} else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Error");
						alert.setHeaderText("Manquen dades");
						alert.setContentText("Es necessari informar el pasword del KeyStore");
						alert.showAndWait();
						return;
					}
				} else {
					x509EncodedKeySpec = new X509EncodedKeySpec(UtilesCifra.readPublicKey(this.pathCert.getText().trim()).getEncoded());
				}
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec));
				this.textOut.setText(DatatypeConverter.printBase64Binary(cipher.doFinal(this.textIn.getText().getBytes("UTF-8"))));
			} catch(Exception e){
				e.printStackTrace(new PrintWriter(errors));
				UtilesCifra.customAlert("Errors al desxifrar text", errors.toString());
			}
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Error");
			alert.setHeaderText("Manquen dades");
			alert.setContentText("Ompli les dades, si us plau");

			alert.showAndWait();
		}
	}
	@FXML
	private void copy(){
		String copyText = this.textOut.getText().trim();
		this.content.putString(copyText);
		this.clipboard.setContent(content);
	}
	
	@FXML
	private void netejar(){
		this.textIn.clear();
		this.textOut.clear();
		this.pathCert.clear();
		this.textPassword.clear();
		this.file = null;
		this.fileSel = null;
	}
	
	//Descifrar
	@FXML
	private void getCertificateDes(){
		FileChooser fileChooser = new FileChooser();
		if(this.fileSel != null){
			File existDirectory = this.fileSel.getParentFile();
            fileChooser.setInitialDirectory(existDirectory);
		} else {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		}
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("KeyStore (*.p12)","*.p12");
		fileChooser.getExtensionFilters().add(extFilter);
		this.fileDes = fileChooser.showOpenDialog(main.getPrimaryStage());
		if (this.fileDes != null) {
			this.pathCertDes.setText(this.fileDes.getPath().trim());
			this.fileSel = this.fileDes;
		}
		
	}
	
	@FXML
	private void desxifrar() throws Exception {
		StringWriter errors = new StringWriter();
		if(this.textInDes.getText().trim().length()>0 && this.pathCertDes.getText().trim().length()>0){
			if(this.textPasswordDes.getText().trim().length()>0){
				try{
					PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(UtilesCifra.readPrivateKey(this.pathCertDes.getText().trim(), this.textPasswordDes.getText().trim()).getEncoded());
					Cipher cipher = Cipher.getInstance("RSA");
					cipher.init(Cipher.DECRYPT_MODE, KeyFactory.getInstance("RSA").generatePrivate(pkcs8EncodedKeySpec));
					this.textOutDes.setText(new String(cipher.doFinal(DatatypeConverter.parseBase64Binary(this.textInDes.getText().trim())), "UTF-8"));
				} catch(Exception e){
					e.printStackTrace(new PrintWriter(errors));
					UtilesCifra.customAlert("Errors al desxifrar text", errors.toString());
				}
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error");
				alert.setHeaderText("Manquen dades");
				alert.setContentText("Es necessari informar el pasword del KeyStore");
	
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Error");
			alert.setHeaderText("Manquen dades");
			alert.setContentText("Ompli les dades, si us plau");

			alert.showAndWait();
		}
	}
	
	@FXML
	private void paste(){
		this.textInDes.setText(this.content.getString());
	}
	
	@FXML
	private void netejarDes(){
		this.textInDes.clear();
		this.textOutDes.clear();
		this.pathCertDes.clear();
		this.textPasswordDes.clear();
		this.fileDes = null;
		this.fileSel = null;
	}
	
	public Main getMain() {
		return main;
	}
	public void setMain(Main main) {
		this.main = main;
	}
	public RootController getRootController() {
		return rootController;
	}
	public void setRootController(RootController rootController) {
		this.rootController = rootController;
	}
	
}
