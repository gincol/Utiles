package es.viewnext.encoding;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import es.viewnext.Main;
import es.viewnext.modelo.Fichero;
import es.viewnext.principal.RootController;
import es.viewnext.utils.Utils;
import es.viewnext.utils.UtilsEncoding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class VariosEncodingController {

	private Main main;
	private RootController rootController;
	private ObservableList<Fichero> ficheroData = FXCollections.observableArrayList();
	
	@FXML
	private TableView<Fichero> ficheroTable;
	@FXML
	private TableColumn<Fichero, String> columnaRutaFichero;
	@FXML
	private TableColumn<Fichero, String> columnaHashFichero;
	@FXML
	private ImageView trashView;
	@FXML
	private TextArea leeArea;
	@FXML
	private TextArea hashArea;
	@FXML
	private TextField entradaDecode64Txt;
	@FXML
	private TextField salidaDecode64Txt;	
	@FXML
	private TextField entradaEncode64Txt;
	@FXML
	private TextField salidaEncode64Txt;
	@FXML
	private ToggleGroup radiosGroup;
	@FXML
	private RadioButton radioMD5;
	@FXML
	private RadioButton radioSHA1;
	@FXML
	private RadioButton radioSHA256;
	
	@FXML
	private void initialize() {
		this.columnaRutaFichero.setCellValueFactory(cellData -> cellData.getValue().rutaProperty());
		this.columnaHashFichero.setCellValueFactory(cellData -> cellData.getValue().hashProperty());
		this.ficheroTable.setItems(this.getFicheroData());
		this.ficheroTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.radiosGroup = new ToggleGroup();
		this.radioMD5.setUserData("MD5");
		this.radioSHA1.setUserData("SHA-1");
		this.radioSHA256.setUserData("SHA-256");
		this.radioMD5.setToggleGroup(this.radiosGroup);
		this.radioSHA1.setToggleGroup(this.radiosGroup);
		this.radioSHA256.setToggleGroup(this.radiosGroup);
		
		this.radiosGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
		    public void changed(ObservableValue<? extends Toggle> ov,
		        Toggle old_toggle, Toggle new_toggle) {
		            if (radiosGroup.getSelectedToggle() != null) {
		            	refreshFiles();		
		            	handleHashText();
		            }                
		        }
		});
	}

	@FXML
	private void handleLeeFiles(){
		System.out.println("handleLeeFiles");
		for (Fichero fichero : this.getFicheroData()) {
			File f = new File(fichero.getRuta());
			if(f.isFile()){
                try {
                    fichero.setHash(Utils.getFileChecksum(this.radiosGroup.getSelectedToggle().getUserData().toString(), fichero.getRuta()));
                } catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void refreshFiles(){
		System.out.println("handleChangeFiles");
		List<Fichero> listaFichero = new ArrayList<>();
		for (Fichero fichero : this.getFicheroData()) {
			File f = new File(fichero.getRuta());
			if(f.isFile()){
                try {
                    fichero.setHash(Utils.getFileChecksum(this.radiosGroup.getSelectedToggle().getUserData().toString(), fichero.getRuta()));
                    listaFichero.add(fichero);
                } catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		this.ficheroData.clear();
		this.ficheroData.addAll(listaFichero);
	}
	
	@FXML
	private void handleHashText(){
		System.out.println("handleEncodingFiles");
		this.hashArea.clear();
		if(this.leeArea.getText().trim().length()>0){
			try {
				this.hashArea.setText(Utils.getHash(this.radiosGroup.getSelectedToggle().getUserData().toString(),this.leeArea.getText()));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleEncode64Files(){
		if(this.entradaEncode64Txt.getText().trim().length()>0){
			String encoded = Base64.getEncoder().encodeToString(this.entradaEncode64Txt.getText().trim().getBytes());
			this.salidaEncode64Txt.setText(encoded);
		}
	}
	
	@FXML
	private void handleDecode64Files(){
		if(this.entradaDecode64Txt.getText().trim().length()>0){
			byte[] bytesEncoded = Base64.getDecoder().decode(this.entradaDecode64Txt.getText().trim());
			this.salidaDecode64Txt.setText(new String(bytesEncoded));
		}
	}
	
	// Ficheros destino para la recuperacion
	@FXML
	private void setOnDragOverFiles(DragEvent event) {
		System.out.println("setOnDragOverFiles");
		Dragboard db = event.getDragboard();
		
		boolean success = false;
		if (db.hasFiles()) {
			List<File> file = db.getFiles();
			if(file.get(0).isFile()) success = true;
		}
		
		if (success) {
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		} else {
//        	Alert alert = new Alert(AlertType.INFORMATION);
//	    	alert.setTitle("File");
//	    	alert.setHeaderText("Selección inválida");
//	    	alert.setContentText("En esta opción sólo se permiten ficheros");
//	    	alert.showAndWait();
		}
		event.consume();
	}

	@FXML
	private void setOnDragDroppedFiles(DragEvent event) {
		System.out.println("setOnDragDroppedFiles");
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasFiles()) {
			success = true;
			for (File file : db.getFiles()) {
				Fichero fichero = new Fichero();
				fichero.setNombre(file.getName());
				fichero.setRuta(file.getAbsolutePath());
				this.ficheroData.add(fichero);
			}
			this.handleLeeFiles();
		}

		event.setDropCompleted(success);
		event.consume();
	}
	
	// Ficheros Origen para el borrado
	@FXML
	private void setOnDragDetectedFiles(MouseEvent event) {
		System.out.println("setOnDragDetectedFiles");
        
        /* allow any transfer mode */
        Dragboard db = ficheroTable.startDragAndDrop(TransferMode.ANY);
        
        /* put a string on dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(Utils.integerToString(ficheroTable.getSelectionModel().getSelectedIndex()));
        db.setContent(content);
        
        event.consume();
	}

	// PAPELERA TARGET
	/**
	 * Cuando entramos en la papelera
	 * 
	 * @param event
	 */
	@FXML
	private void setOnDragOverTrash(DragEvent event) {
		System.out.println("setOnDragOverTrash");
		event.acceptTransferModes(TransferMode.MOVE);
	}

	/**
	 * Para que se vea que estamos sobre la papelera
	 * 
	 * @param event
	 */
	@FXML
	private void setOnDragEnteredTrash(DragEvent event) {
		System.out.println("setOnDragEnteredTrash");
		this.trashView.setBlendMode(BlendMode.DIFFERENCE);
	}

	/**
	 * Para que se vea que salimos de la papelera
	 * 
	 * @param event
	 */
	@FXML
	private void setOnDragExitedTrash(DragEvent event) {
		System.out.println("setOnDragExitedTrash");
		this.trashView.setBlendMode(null);
		if(!this.leeArea.getText().isEmpty()){
			this.handleLeeFiles();
		}
		if(!this.leeArea.getText().isEmpty()){
			this.handleLeeFiles();
		}
	}

	@FXML
	private void setOnDragDroppedTrash(DragEvent event) {
		System.out.println("setOnDragDroppedTrash");
		this.ficheroData.remove(this.ficheroTable.getSelectionModel().getSelectedItem());

	}

	public ObservableList<Fichero> getFicheroData() {
		return ficheroData;
	}

	public void setFicheroData(ObservableList<Fichero> ficheroData) {
		this.ficheroData = ficheroData;
	}

	public TableView<Fichero> getFicheroTable() {
		return ficheroTable;
	}

	public void setFicheroTable(TableView<Fichero> ficheroTable) {
		this.ficheroTable = ficheroTable;
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
