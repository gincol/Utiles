package es.viewnext.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import es.viewnext.Main;
import es.viewnext.modelo.Alias;
import es.viewnext.modelo.Certificado;
import es.viewnext.modelo.KeyStoreApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.util.Pair;

public class UtilsWindow {

	/**
	 * Abre un alert con la información que se le envía. dicha información se muestra en un textarea
	 * @param titulo el título de la ventana
	 * @param tema el tema sobre el que se trata
	 * @param detalle el lugar detallado del cual se muestra su texto
	 * @param fuente el texto que se muestra en el textarea
	 */
	public static void openAlert(String titulo, String tema, String detalle, String fuente){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(titulo);
		alert.setHeaderText(tema + " '" + detalle + "'");
		
		TextArea textArea = new TextArea(fuente);
		
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);
		
		alert.getDialogPane().setExpanded(true);
		
		alert.showAndWait();
	}
	
	/**
	 * Diálogo con la petición de un único dato
	 * @param defaultValue valor por defecto a mostrar en el input
	 * @param cabecera título de la ventana
	 * @param comentario aclaración sobre lo que se pide
	 * @param textoPeticion texto con la petición del dato
	 * @return devuelve un string con el dato pedido
	 */
	public static String openInputDialog(String defaultValue, String cabecera, String comentario, String textoPeticion){
		TextInputDialog dialog = new TextInputDialog(defaultValue);
		dialog.setTitle(cabecera);
		dialog.setHeaderText(comentario);
		dialog.setContentText(textoPeticion);

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    System.out.println("Your name: " + result.get());
		} else {
			return null;
		}

		// The Java 8 way to get the response value (with lambda expression).
//		result.ifPresent(name -> System.out.println("Your name: " + name));
		return result.get();
	}
	
	/**
	 * Ventana para seleccionar un certificado y añadirlo a un almacén.
	 * @param main referencia a la ventana principal de la aplicación
	 * @param ksa Objeto KeyStoreApp
	 * @return Devuelve un objeto certificado
	 */
	public static Certificado selectNewCertificadoDialog(Main main, KeyStoreApp ksa){
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Nuevo certificado");
		dialog.setHeaderText("Añadir nuevo certificado al almacén");
		
		// Set the button types.
		ButtonType aceptar = new ButtonType("Aceptar", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(aceptar, ButtonType.CANCEL);
		
		// Create the nombre and descripción labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TextField nombre = new TextField();
		nombre.setPromptText("Alias");
		
		TextField ruta = new TextField();
		ruta.setPromptText("Ruta Certificcado");
		
		Button button = new Button();
		button.setText("Seleccionar Certificado");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override public void handle(ActionEvent e) {
				HashMap has = new HashMap<>();
		    	has.put("cer", "Cert files");
		    	has.put("crt", "Crt files");
		    	has.put("pfx", "pfx files");
		    	has.put("cert", "cert files");
		    	ruta.setText(seleccionarMultiple(main, has));
		    }
		});
		
		grid.add(new Label("Alias:"), 0, 0);
		grid.add(nombre, 1, 0);
		grid.add(new Label("Ruta:"), 0, 1);
		grid.add(ruta, 1, 1);
		grid.add(button, 2, 1);

		// Enable/Disable aceptar button depending on whether a nombre was entered.
		Node aceptarButton = dialog.getDialogPane().lookupButton(aceptar);
		aceptarButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		nombre.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!ruta.getText().trim().isEmpty()){
				aceptarButton.setDisable(newValue.trim().isEmpty());
			}
		});
		ruta.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!nombre.getText().trim().isEmpty()){
				aceptarButton.setDisable(newValue.trim().isEmpty());
			}
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the nombre field by default.
		Platform.runLater(() -> nombre.requestFocus());

		// Convert the result to a nombre-desc-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == aceptar) {
		        return new Pair<>(nombre.getText(), ruta.getText());
		    }
		    return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();
		Certificado certificado = new Certificado();
		result.ifPresent(nombreRuta -> {
		    System.out.println("nombre=" + nombreRuta.getKey() + ", descripción=" + nombreRuta.getValue());
		    Alias alias = new Alias();
		    alias.setNombre(nombreRuta.getKey());
		    alias.setKeyStoreApp(ksa);
		    alias.setNumCert(1);
		    certificado.setAlias(alias);
		    int numSlash = nombreRuta.getValue().split("//").length;
		    certificado.setfichero(nombreRuta.getValue().split("//")[numSlash-1]);
		    certificado.setRutaLocal(nombreRuta.getValue());
		});
		
		if(certificado.getRutaLocal()==null){
			return null;
		}
		return certificado;
	}
	/**
	 * Metodo que selecciona un keystore existente o crea uno nuevo
	 * @param main referencia a la ventana principal de la aplicación 
	 * @param nuevo si es true se creará un keystore nuevo, si false se seleccionará uno ya existente
	 * @return retorna un objeto Alias
	 */
	public static Alias selectKeystoreDialog(Main main, boolean nuevo){
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Añadir certificado");
		dialog.setHeaderText("Añadir un certificado a un almacén existente");
		
		// Set the button types.
		ButtonType aceptar = new ButtonType("Aceptar", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(aceptar, ButtonType.CANCEL);
		
		// Create the nombre and descripción labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TextField nombre = new TextField();
		nombre.setPromptText("Alias KeyStore");
		
		TextField ruta = new TextField();
		ruta.setPromptText("Ruta KeyStore");
		
		TextField password = new TextField();
		password.setPromptText("Password KeyStore");
		
		Button button = new Button();
		button.setText("Seleccionar KeyStore");
		button.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	if(nuevo){
		    		ruta.setText(guardar(main, "KeyStore files","jks","keystore"));
		    	} else {
		    		ruta.setText(seleccionar(main, "KeyStore files","jks"));
		    	}
		    }
		});
		
		grid.add(new Label("Alias:"), 0, 0);
		grid.add(nombre, 1, 0);
		grid.add(new Label("Ruta:"), 0, 1);
		grid.add(ruta, 1, 1);
		grid.add(button, 2, 1);
		grid.add(new Label("Password:"), 0, 2);
		grid.add(password, 1, 2);

		// Enable/Disable aceptar button depending on whether a nombre was entered.
		Node aceptarButton = dialog.getDialogPane().lookupButton(aceptar);
		aceptarButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		nombre.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!ruta.getText().trim().isEmpty() && !password.getText().trim().isEmpty()){
				aceptarButton.setDisable(newValue.trim().isEmpty());
			}
		});
		ruta.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!nombre.getText().trim().isEmpty() && !password.getText().trim().isEmpty()){
				aceptarButton.setDisable(newValue.trim().isEmpty());
			}
		});
		password.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!nombre.getText().trim().isEmpty() && !ruta.getText().trim().isEmpty()){
				aceptarButton.setDisable(newValue.trim().isEmpty());
			}
		});
		dialog.getDialogPane().setContent(grid);

		// Request focus on the nombre field by default.
		Platform.runLater(() -> nombre.requestFocus());

		// Convert the result to a nombre-desc-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == aceptar) {
		        return new Pair<>(nombre.getText(), ruta.getText());
		    }
		    return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();
		Certificado certificado = new Certificado();
		KeyStoreApp ksa = new KeyStoreApp();
		Alias alias = new Alias();
		result.ifPresent(nombreRuta -> {
		    System.out.println("nombre=" + nombreRuta.getKey() + ", descripción=" + nombreRuta.getValue());
		    ksa.setRuta(nombreRuta.getValue());
		    ksa.setPassword(password.getText());
		    alias.setNombre(nombreRuta.getKey());
		    alias.setKeyStoreApp(ksa);
		    alias.setNumCert(1);
		    certificado.setAlias(alias);
		    int numSlash = nombreRuta.getValue().split("//").length;
		    certificado.setfichero(nombreRuta.getValue().split("//")[numSlash-1]);
		    certificado.setRutaLocal(nombreRuta.getValue());
		});
		
		if(certificado.getRutaLocal()==null){
			return null;
		}
		return alias;
	}
	/**
	 * Método que abre un objeto {@link FileChooser} para seleccionar un fichero de disco
	 * @param main referencia a la ventana principal de la aplicación
	 * @param descripcion detalle sobre el tipo de fichero que se busca
	 * @param extension extensión del fichero que se busca
	 * @return devuelve un string con la ruta del fichero buscado
	 */
	public static String seleccionar(Main main, String descripcion, String extension){
		String ruta = "";
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(descripcion + "(*."+extension+")", "*."+extension);
        fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		fileChooser.setInitialFileName("."+extension);
		File file = fileChooser.showOpenDialog(main.getPrimaryStage());
		if(file!=null){
			ruta = file.getPath();
		}
		return ruta;
	}
	
	/**
	 * Método que abre un objeto {@link FileChooser} para seleccionar un fichero de disco
	 * @param main referencia a la ventana principal de la aplicación
	 * @param descripcion detalle sobre el tipo de fichero que se busca
	 * @param extension extensión del fichero que se busca
	 * @return devuelve un string con la ruta del fichero buscado
	 */
	@SuppressWarnings("rawtypes")
	public static String seleccionarMultiple(Main main, HashMap hmLista){
		String ruta = "";
		FileChooser fileChooser = new FileChooser();
		String extension = "";
		Iterator itLista = hmLista.entrySet().iterator();
		while (itLista.hasNext()) {
			Map.Entry pair = (Map.Entry)itLista.next();
	        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(pair.getValue() + "(*."+pair.getKey()+")", "*."+pair.getKey());
	        fileChooser.getExtensionFilters().add(extFilter);
	        extension = (String) pair.getKey();
	        itLista.remove(); 
		}

		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		fileChooser.setInitialFileName("."+extension);
		File file = fileChooser.showOpenDialog(main.getPrimaryStage());
		if(file!=null){
			ruta = file.getPath();
		}
		return ruta;
	}
	
	/**
	 * Método que abre un objeto {@link FileChooser} para guardar un fichero a disco
	 * @param main referencia a la ventana principal de la aplicación
	 * @param descripcion detalle sobre el tipo de fichero que se va a guardar
	 * @param extension extensión del fichero que se guardará
	 * @param nombreInicial nombre inicial propuesto del fichero a guardar
	 * @return devuelve un sting con la ruta del fichero guardado
	 */
	public static String guardar(Main main, String descripcion, String extension, String nombreInicial){
		String ruta = "";
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(descripcion + "(*."+extension+")", "*."+extension);
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		fileChooser.setInitialFileName(nombreInicial + "."+extension);
		File file = fileChooser.showSaveDialog(main.getPrimaryStage());
		if(file!=null){
			ruta = file.getPath();
		}
		return ruta;
	}
	
	/**
	 * Ventana customizada para mostrar cualquier tipo de información de ERROR
	 * @param funcion donde se ha producido el error
	 * @param texto el log del error reportado
	 */
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
