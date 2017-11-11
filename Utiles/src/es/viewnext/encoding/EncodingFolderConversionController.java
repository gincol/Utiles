package es.viewnext.encoding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import es.viewnext.Main;
import es.viewnext.modelo.Fichero;
import es.viewnext.principal.RootController;
import es.viewnext.utils.Utils;
import es.viewnext.utils.UtilsEncoding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class EncodingFolderConversionController {

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
	private TextArea encodingOrigenArea;
	@FXML
	private TextArea encodingDestinoArea;
	@FXML
	private TextArea encodingArea;
	
	private ToggleGroup grupOrigen;
	private ToggleGroup grupDestino;
	@FXML
	private RadioButton rbOrigenUTF8;
	@FXML
	private RadioButton rbOrigenISO;
	@FXML
	private RadioButton rbOrigenCP;
	@FXML
	private RadioButton rbDestinoUTF8;
	@FXML
	private RadioButton rbDestinoISO;
	@FXML
	private RadioButton rbDestinoCP;
	
	@FXML
	private void initialize() {
		this.columnaRutaFichero.setCellValueFactory(cellData -> cellData.getValue().rutaProperty());
		this.ficheroTable.setItems(this.getFicheroData());
		this.grupOrigen = new ToggleGroup();
		rbOrigenUTF8.setToggleGroup(this.grupOrigen);
		rbOrigenISO.setToggleGroup(this.grupOrigen);
		rbOrigenCP.setToggleGroup(this.grupOrigen);
		
		this.grupDestino = new ToggleGroup();
		rbDestinoUTF8.setToggleGroup(this.grupDestino);
		rbDestinoISO.setToggleGroup(this.grupDestino);
		rbDestinoCP.setToggleGroup(this.grupDestino);
	}

	@FXML
	private void handleConvertFiles(){
		System.out.println("handleLeeFiles");
		this.encodingOrigenArea.clear();
		this.encodingDestinoArea.clear();
		Charset charsetOrigen = Charset.forName("ISO-8859-1");
		if(rbOrigenCP.isSelected()) charsetOrigen = Charset.forName("Cp1252");
		if(rbOrigenUTF8.isSelected()) charsetOrigen = Charset.forName("UTF-8"); 
		
		Charset charsetDestino = Charset.forName("ISO-8859-1");
		if(rbDestinoCP.isSelected()) charsetDestino = Charset.forName("Cp1252");
		if(rbDestinoUTF8.isSelected()) charsetDestino = Charset.forName("UTF-8"); 
		
		StringBuffer salidaOrigen = new StringBuffer();
		StringBuffer salidaDestino = new StringBuffer();
		StringBuffer nombreOut = new StringBuffer();
		for (Fichero fichero : this.getFicheroData()) {
			File f = new File(fichero.getRuta());
			if(f.isFile()){
				salidaOrigen.append("Fichero: " + fichero.getNombre() + "\n");
				salidaDestino.append("Fichero: " + fichero.getNombre() + "\n");
					
				Charset charset = UtilsEncoding.detectCharset(f, charsetOrigen);
				if (charset != null) {
	                try {
	                	
	                	//Fichero destino
	                	String [] trozos = fichero.getNombre().split("\\.");
	        			nombreOut.append(trozos[0]);
	        			if(trozos.length>1) {
	        				nombreOut.append("."+charsetDestino.name()+"."+trozos[1]);
	        			} 
	        			File fout = new File(fichero.getRuta().replace(fichero.getNombre(), nombreOut));
	        			Path newFile = Paths.get(fout.getPath());
	                    BufferedWriter writer = Files.newBufferedWriter(newFile, charsetDestino);
	                    
	                    InputStreamReader readerOriginal = new InputStreamReader(new FileInputStream(f), charsetOrigen);
	                    int c = 0;
	                    char tmp;
	                    while ((c = readerOriginal.read()) != -1) {
	                    	salidaOrigen.append((char)c);
	                    	if(this.rbOrigenUTF8.isSelected()){
	                    		tmp = UtilsEncoding.convertEncodingFile2((char)c, charsetOrigen, charsetDestino);
	                    		salidaDestino.append(tmp);
	                    		if(Character.isLetterOrDigit(tmp)){
	                    			writer.append(tmp);
	                    		}
	                    	}
	                    }
	                    
	                    salidaOrigen.append("\n");
	                    readerOriginal.close();
	                    if(this.rbOrigenISO.isSelected()){
	                    	String textConvertido = UtilsEncoding.convertEncodingFile(fichero.getRuta(), charsetOrigen, charsetDestino);
	                    	salidaDestino.append(textConvertido);
	                    	writer.append(textConvertido);
	                    }
	                    salidaDestino.append("\n");
	                    writer.flush();
	                } catch (FileNotFoundException fnfe) {
	                	salidaOrigen.append("File Not found");
	                	salidaDestino.append("File Not found");
	                }catch(IOException ioe){
	                	salidaOrigen.append("Can't read file");
	                	salidaDestino.append("Can't read file");
	                }
	                
	            }else{
	            	salidaOrigen.append("Unrecognized charset.");
	            	salidaDestino.append("Unrecognized charset.");
	            }
					
				this.encodingOrigenArea.setText(salidaOrigen.toString());
				this.encodingDestinoArea.setText(salidaDestino.toString());
		
			} else {
				salidaOrigen.append("Carpeta: " + fichero.getNombre() + "\n");
				salidaOrigen.append("Es una carpeta. No se puede tratar\n");
				salidaDestino.append("Carpeta: " + fichero.getNombre() + "\n");
				salidaDestino.append("Es una carpeta. No se puede tratar\n");
				
				this.encodingOrigenArea.setText(salidaOrigen.toString());
				this.encodingDestinoArea.setText(salidaDestino.toString());
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
				this.encodingOrigenArea.clear();
				this.encodingDestinoArea.clear();
				salida.append("Fichero: " + fichero.getNombre() + "\n");
				String charsert = UtilsEncoding.detectEncoding(fichero.getRuta());
				salida.append("Encoding: " + charsert + "\n");
				this.encodingArea.setText(salida.toString());
				salida.append("\n");
				if(charsert.startsWith("ISO")){
					this.rbOrigenISO.setSelected(true);
					this.rbDestinoUTF8.setSelected(true);
				} else if(charsert.startsWith("UTF")){
					this.rbOrigenUTF8.setSelected(true);
					this.rbDestinoISO.setSelected(true);
				} else {
					this.rbOrigenCP.setSelected(true);
					this.rbDestinoUTF8.setSelected(true);
				}
			} else {
				salida.append("Carpeta: " + fichero.getNombre() + "\n");
				salida.append("Es una carpeta. No se puede tratar\n");
				salida.append("Vaya a la opción de 'Encoding Files In Folder\n");
				this.encodingArea.setText(salida.toString());
				salida.append("\n");
			}
		}
	}
	
	// Ficheros destino para la recuperacion
	@FXML
	private void setOnDragOverFiles(DragEvent event) {
		System.out.println("setOnDragOverFiles");
		Dragboard db = event.getDragboard();
		if (db.hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		} else {
			event.consume();
		}
	}

	@FXML
	private void setOnDragDroppedFiles(DragEvent event) {
		System.out.println("setOnDragDroppedFiles");
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasFiles()) {
			success = true;
			if(this.ficheroData!=null && this.ficheroData.size()>0){
				this.ficheroData.clear();
			}
			if(db.getFiles().size()>0){
				File file = db.getFiles().get(0);
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
		this.encodingOrigenArea.clear();
		this.encodingDestinoArea.clear();
		this.encodingArea.clear();
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
