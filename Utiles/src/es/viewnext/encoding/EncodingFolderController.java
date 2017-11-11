package es.viewnext.encoding;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import es.viewnext.Main;
import es.viewnext.modelo.Fichero;
import es.viewnext.modelo.Folder;
import es.viewnext.principal.RootController;
import es.viewnext.utils.Utils;
import es.viewnext.utils.UtilsEncoding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class EncodingFolderController {

	private Main main;
	private RootController rootController;
	private ObservableList<Folder> folderData = FXCollections.observableArrayList();
	private ObservableList<Fichero> ficheroEncodingData = FXCollections.observableArrayList();

	@FXML
	private TableView<Folder> ficheroInputTable;
	@FXML
	private TableColumn<Folder, String> columnaRutaFicheroInput;
	@FXML
	private ImageView trashView;
	
	@FXML
	private TableView<Fichero> ficheroEncodingTable;
	@FXML
	private TableColumn<Fichero, String> columnaRutaFicheroEncoding;
	@FXML
	private TableColumn<Fichero, String> columnaEncodingFicheroEncoding;
	
	@FXML
	private void initialize() {
		this.ficheroInputTable.setItems(this.getFolderData());
		this.columnaRutaFicheroInput.setCellValueFactory(cellData -> cellData.getValue().rutaProperty());
		
		this.ficheroEncodingTable.setItems(this.getFicheroEncodingData());
		this.columnaRutaFicheroEncoding.setCellValueFactory(cellData -> cellData.getValue().rutaProperty());
		this.columnaEncodingFicheroEncoding.setCellValueFactory(cellData -> cellData.getValue().encodingProperty());
	}

	@FXML
	private void handleEncodingFilesInFolder(){
		System.out.println("handleEncodingFilesInFolder");
		this.ficheroEncodingData.clear();
		
		for (Folder folder : this.getFolderData()) {
			try {
				Files.walk(Paths.get(folder.getRuta())).forEach(filePath -> {
				    if (Files.isRegularFile(filePath)) {
				    	Fichero fichero = new Fichero();
				    	fichero.setNombre(filePath.getFileName().toString());
				    	fichero.setRuta(filePath.toAbsolutePath().toString());
				    	fichero.setEncoding(UtilsEncoding.detectEncoding(filePath.toAbsolutePath().toString()));
				    	this.ficheroEncodingData.add(fichero);
				    }
				});
			} catch (IOException e) {
				System.out.println("error");
			}
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
			if(file.get(0).isDirectory()) success = true;
		}
		
		if (success) {
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		} else {
//        	Alert alert = new Alert(AlertType.INFORMATION);
//	    	alert.setTitle("Folder");
//	    	alert.setHeaderText("Selección inválida");
//	    	alert.setContentText("En esta opción sólo se permiten directorios");
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
				Folder folder = new Folder();
				folder.setNombre(file.getName());
				folder.setRuta(file.getAbsolutePath());
				this.folderData.add(folder);
			}
		}

		event.setDropCompleted(success);
		event.consume();
	}
	
	// Ficheros Origen para el borrado
	@FXML
	private void setOnDragDetectedFiles(MouseEvent event) {
		System.out.println("setOnDragDetectedFiles");
        
        /* allow any transfer mode */
        Dragboard db = ficheroInputTable.startDragAndDrop(TransferMode.ANY);
        
        /* put a string on dragboard */
        ClipboardContent content = new ClipboardContent();
        content.putString(Utils.integerToString(ficheroInputTable.getSelectionModel().getSelectedIndex()));
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
		//TODO limpiar
		this.ficheroEncodingData.clear();
	}

	@FXML
	private void setOnDragDroppedTrash(DragEvent event) {
		System.out.println("setOnDragDroppedTrash");
		this.folderData.remove(this.ficheroInputTable.getSelectionModel().getSelectedItem());

	}

	public ObservableList<Folder> getFolderData() {
		return folderData;
	}

	public void setFolderData(ObservableList<Folder> folderData) {
		this.folderData = folderData;
	}

	public ObservableList<Fichero> getFicheroEncodingData() {
		return ficheroEncodingData;
	}

	public void setFicheroEncodingData(ObservableList<Fichero> ficheroEncodingData) {
		this.ficheroEncodingData = ficheroEncodingData;
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
