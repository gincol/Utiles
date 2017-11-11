package es.viewnext.encoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import es.viewnext.Main;
import es.viewnext.modelo.Fichero;
import es.viewnext.principal.RootController;
import es.viewnext.utils.Utils;
import es.viewnext.utils.UtilsEncoding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class EncodingFilesController {

	private Main main;
	private RootController rootController;
	private ObservableList<Fichero> ficheroData = FXCollections.observableArrayList();
	
	@FXML
	private TableView<Fichero> ficheroTable;
	@FXML
	private TableColumn<Fichero, String> columnaRutaFichero;
	@FXML
	private ImageView trashView;
	@FXML
	private TextArea leeArea;
	@FXML
	private TextArea encodingArea;
	@FXML
	private TextArea hexArea;
	@FXML
	CheckBox cbUTF8;
	@FXML
	CheckBox cbISO;
	@FXML
	CheckBox cbCP;
	
	@FXML
	private void initialize() {
		this.columnaRutaFichero.setCellValueFactory(cellData -> cellData.getValue().rutaProperty());
		this.ficheroTable.setItems(this.getFicheroData());
		this.ficheroTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	@FXML
	private void handleLeeFiles(){
		System.out.println("handleLeeFiles");
		this.leeArea.clear();
		List<String> listaEncodings = new ArrayList<String>();
		if(this.cbUTF8.isSelected()) listaEncodings.add("UTF-8");
		if(this.cbISO.isSelected()) listaEncodings.add("ISO-8859-1");
		if(this.cbCP.isSelected()) listaEncodings.add("Cp1252");
		StringBuffer salida = new StringBuffer();
		for (Fichero fichero : this.getFicheroData()) {
			File f = new File(fichero.getRuta());
			if(f.isFile()){
				salida.append("Fichero: " + fichero.getNombre() + "\n");
				for (String encoding : listaEncodings) {
					salida.append(encoding + ":\n");
					Charset charset = UtilsEncoding.detectCharset(f, Charset.forName(encoding));
					if (charset != null) {
		                try {
		                    InputStreamReader reader = new InputStreamReader(new FileInputStream(f), charset);
		                    int c = 0;
		                    while ((c = reader.read()) != -1) {
		                    	salida.append((char)c);
		                    }
		                    salida.append("\n");
		                    reader.close();
		                } catch (FileNotFoundException fnfe) {
		                    fnfe.printStackTrace();
		                }catch(IOException ioe){
		                    ioe.printStackTrace();
		                }
		                
		            }else{
		            	salida.append("Unrecognized charset.");
		            }
					this.leeArea.setText(salida.toString());
					salida.append("\n\n");
				}
			} else {
				salida.append("Carpeta: " + fichero.getNombre() + "\n");
				salida.append("Es una carpeta. No se puede tratar\n");
				salida.append("Vaya a la opción de 'Encoding Files In Folder\n");
				this.leeArea.setText(salida.toString());
				salida.append("\n\n");
			}
		}
	}
	
	@FXML
	private void handleEncodingFiles(){
		System.out.println("handleEncodingFiles");
		this.encodingArea.clear();
		StringBuffer salida = new StringBuffer();
		for (Fichero fichero : this.getFicheroData()) {
			File f = new File(fichero.getRuta());
			if(f.isFile()){
				salida.append("Fichero: " + fichero.getNombre() + "\n");
				salida.append("Encoding: " + UtilsEncoding.detectEncoding(fichero.getRuta()) + "\n");
				this.encodingArea.setText(salida.toString());
				salida.append("\n");
			} else {
				salida.append("Carpeta: " + fichero.getNombre() + "\n");
				salida.append("Es una carpeta. No se puede tratar\n");
				salida.append("Vaya a la opción de 'Encoding Files In Folder\n");
				this.encodingArea.setText(salida.toString());
				salida.append("\n");
			}
		}
	}
	
	@FXML
	private void handelHexFile(){
		System.out.println("handelHexFile");
		this.hexArea.clear();
		StringBuffer salida = new StringBuffer();
		for (Fichero fichero : this.getFicheroData()) {
			File f = new File(fichero.getRuta());
			if(f.isFile()){
				salida.append("Fichero: " + fichero.getNombre() + "\n");
				salida.append("Hex: \n");
				salida.append(UtilsEncoding.convertToHex(f) + "\n");
				this.hexArea.setText(salida.toString());
				salida.append("\n");
			} else {
				salida.append("Carpeta: " + fichero.getNombre() + "\n");
				salida.append("Es una carpeta. No se puede tratar\n");
				salida.append("Vaya a la opción de 'Encoding Files In Folder\n");
				this.hexArea.setText(salida.toString());
				salida.append("\n");
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
			this.handleEncodingFiles();
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
		if(!this.encodingArea.getText().isEmpty()){
			this.handleEncodingFiles();
		}
		if(!this.hexArea.getText().isEmpty()){
			this.handelHexFile();
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
