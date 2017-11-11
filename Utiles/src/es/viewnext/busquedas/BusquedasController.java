package es.viewnext.busquedas;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.JarTypeLoader;
import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.ITextOutput;
import com.strobel.decompiler.PlainTextOutput;

import es.viewnext.Main;
import es.viewnext.modelo.Fichero;
import es.viewnext.modelo.Libreria;
import es.viewnext.principal.RootController;
import es.viewnext.utils.Utils;
import es.viewnext.utils.UtilsFiles;
import es.viewnext.utils.UtilsWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class BusquedasController {

	private Main main;
	private RootController rootController;
	private ObservableList<Libreria> libreriaData = FXCollections.observableArrayList();
	private ObservableList<Fichero> ficheroSearchData = FXCollections.observableArrayList();

	@FXML
	private TableView<Libreria> libreriaInputTable;
	@FXML
	private TableColumn<Libreria, String> columnaRutaLibreriaInput;
	@FXML
	private TableColumn<Libreria, Number> columnaNumFilesLibreriaInput;
	@FXML
	private ImageView trashView;

	@FXML
	private TableView<Fichero> ficheroSearchTable;
	@FXML
	private TableColumn<Fichero, String> columnaAuxFicheroSearch;
	@FXML
	private TextField filterField;

	@FXML
	private TextField numLibreriasField;
	@FXML
	private TextField numClasesField;
	@FXML
	private TextField numPropertiesField;
	@FXML
	private TextField numXmlField;
	@FXML
	private TextField numOtrosField;
	@FXML
	private TextField numTotalField;

	@FXML
	private void initialize() {
		this.libreriaInputTable.setItems(this.getLibreriaData());
		this.libreriaInputTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.columnaRutaLibreriaInput.setCellValueFactory(cellData -> cellData.getValue().rutaProperty());
		this.columnaNumFilesLibreriaInput.setCellValueFactory(cellData -> cellData.getValue().numFilesProperty());

		// Anadimos un filtro
		FilteredList<Fichero> filteredData = new FilteredList<Fichero>(this.ficheroSearchData, p -> true);
		this.filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(fichero -> {
				// If filter text is empty, display all jugadores.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				// Compare first name and last name of every person with filter
				// text.
				String lowerCaseFilter = newValue.toLowerCase().replace(".", "/");
				if (fichero.getAux().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches first name.
				}
				return false; // Does not match.
			});
		});
		this.ficheroSearchTable.setItems(filteredData);
		this.columnaAuxFicheroSearch.setCellValueFactory(cellData -> cellData.getValue().auxProperty());

		final ContextMenu contextMenu = new ContextMenu();
		MenuItem dec = new MenuItem("Source");
		contextMenu.getItems().addAll(dec);
		dec.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(ficheroSearchTable.getSelectionModel().getSelectedItem().getNombre().endsWith(".jar") ||
						ficheroSearchTable.getSelectionModel().getSelectedItem().getNombre().endsWith(".war") ||
						ficheroSearchTable.getSelectionModel().getSelectedItem().getNombre().endsWith(".ear")){
					UtilsWindow.customAlert("decompile", "Fichero no válido para decompilar");
				} else {
					Utils.decompile(ficheroSearchTable.getSelectionModel().getSelectedItem());
				}
			}
		});
		this.ficheroSearchTable.setContextMenu(contextMenu);
		this.ficheroSearchTable.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					if(ficheroSearchTable.getSelectionModel().getSelectedItem().getNombre().endsWith(".jar") ||
							ficheroSearchTable.getSelectionModel().getSelectedItem().getNombre().endsWith(".war") ||
							ficheroSearchTable.getSelectionModel().getSelectedItem().getNombre().endsWith(".ear")){
						UtilsWindow.customAlert("decompile", "Fichero no válido para decompilar");
					} else {
						Utils.decompile(ficheroSearchTable.getSelectionModel().getSelectedItem());
					}
				}
			}
		});
	}
	
	@FXML
	private void limpieza(){
		this.filterField.clear();
		this.limpiaSearchData();
		this.limpiaLibreriaData();
		this.limpiaCampos();
	}

	// Ficheros destino para la recuperacion
	@FXML
	private void setOnDragOverFiles(DragEvent event) {
		System.out.println("setOnDragOverFiles");
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasFiles()) {
			List<File> fileList = db.getFiles();
			for (File file : fileList) {
				if (file.getName().endsWith(".jar")) {
					success = true;
				} else if(file.getName().endsWith(".war") || file.getName().endsWith(".ear")) {
					if(!UtilsFiles.recorreWar(file.getPath()).isEmpty()){
						success = true;
					}
				} else if(file.isDirectory()) {
					if(!UtilsFiles.recorreFolder(file.getPath()).isEmpty()){
						success = true;
					}
				}
			}
		}
		System.out.println(success);
		if (success) {
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		}
		event.consume();
	}

	@FXML
	private void setOnDragDroppedFiles(DragEvent event) {
		System.out.println("setOnDragDroppedFiles");
		this.limpiaCampos();
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasFiles()) {
			success = true;
			List<File> listaFicheros = new ArrayList<File>();
			List<File> listaFicherosWar = new ArrayList<File>();
			Libreria libreria;
			for (File file : db.getFiles()) {
				if (file.isFile()) {
					libreria = new Libreria();
					libreria.setNombre(file.getName());
					libreria.setRuta(file.getAbsolutePath());
					libreria.setNumFiles(UtilsFiles.getNumFilesFolder(file.getPath()));
					this.libreriaData.add(libreria);
				} else if(file.isDirectory()){
					listaFicheros = UtilsFiles.recorreFolder(file.getPath());
					for (File fichero : listaFicheros) {
						if (fichero.getName().endsWith(".jar")) {
							libreria = new Libreria();
							libreria.setNombre(fichero.getName());
							libreria.setRuta(fichero.getAbsolutePath());
							libreria.setNumFiles(UtilsFiles.getNumFilesFolder(fichero.getPath()));
							this.libreriaData.add(libreria);
						} else if(fichero.getName().endsWith(".war")){
//							try {
							listaFicherosWar = UtilsFiles.recorreWar(fichero.getParentFile().getPath() + File.separator +  fichero.getName());
							for (File ficheroWar : listaFicherosWar) {
								if (ficheroWar.getName().endsWith(".jar")) {
									libreria = new Libreria();
									libreria.setNombre(fichero.getName());
									libreria.setRuta(fichero.getAbsolutePath());
									libreria.setNumFiles(UtilsFiles.getNumFilesFolder(fichero.getParentFile().getPath() + File.separator +  fichero.getName()));
									this.libreriaData.add(libreria);
								}
							}
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
						}
					}
				}
			}
		}
		this.listarFilesInJar();
		this.cuentaFiles();
		event.setDropCompleted(success);
		event.consume();
	}

	
	// Ficheros Origen para el borrado
	@FXML
	private void setOnDragDetectedFiles(MouseEvent event) {
		System.out.println("setOnDragDetectedFiles");

		/* allow any transfer mode */
		Dragboard db = libreriaInputTable.startDragAndDrop(TransferMode.ANY);

		/* put a string on dragboard */
		ClipboardContent content = new ClipboardContent();
		content.putString(Utils.integerToString(libreriaInputTable.getSelectionModel().getSelectedIndex()));
		db.setContent(content);

		event.consume();
	}
	
	private void listarFilesInJar() {
		System.out.println("handleListingFilesInFile");
		this.ficheroSearchData.clear();
		for (Libreria ficheroIn : this.getLibreriaData()) {
			try {
				Files.walk(Paths.get(ficheroIn.getRuta())).forEach(filePath -> {
					if (Files.isRegularFile(filePath)) {
						System.out.println("filePath inicio: " +ficheroIn.getRuta() + ", " + filePath);
						try {
							ZipInputStream zip = new ZipInputStream(new FileInputStream(filePath.toFile()));
							while (true) {
								ZipEntry e = zip.getNextEntry();
								if (e == null) {
									break;
								} else {
									if (!e.isDirectory()) {
										System.out.println("filePath no dir: " +e.getName());
										Fichero fichero = new Fichero();
										fichero.setNombre(e.getName());
										fichero.setRuta(filePath.toAbsolutePath().toString());
										fichero.setAux(filePath.toAbsolutePath().toString() + " --> " + e.getName());
										this.ficheroSearchData.add(fichero);
									} 
								}
							}
							//TODO si no es zip

						} catch (Exception e) {
							System.out.println("error");
						}
					}
				});
			} catch (IOException e) {
				System.out.println("error");
			}
		}
		this.cuentaFiles();
	}
	private void limpiaSearchData() {
		this.ficheroSearchData.clear();
	}
	private void limpiaLibreriaData() {
		this.libreriaData.clear();
	}
	private void limpiaCampos(){
		this.numLibreriasField.clear();
		this.numClasesField.clear();
		this.numPropertiesField.clear();
		this.numXmlField.clear();
		this.numOtrosField.clear();
	}

	private void cuentaFiles(){
		int contadorClases = 0;
		int contadorProperties = 0;
		int contadorXml = 0;
		int contadorOtros = 0;
		for (Fichero fichero : ficheroSearchData) {
			if(fichero.getNombre().endsWith(".class") || fichero.getNombre().endsWith(".java")){
				contadorClases++;
			} else if(fichero.getNombre().endsWith(".properties")){
				contadorProperties++;
			} else if(fichero.getNombre().endsWith(".xml")){
				contadorXml++;
			} else {
				contadorOtros++;
			}
		}
		this.numLibreriasField.setText(Utils.intToString(this.libreriaData.size()));
		this.numClasesField.setText(Utils.intToString(contadorClases));
		this.numPropertiesField.setText(Utils.intToString(contadorProperties));
		this.numXmlField.setText(Utils.intToString(contadorXml));
		this.numOtrosField.setText(Utils.intToString(contadorOtros));
		this.numTotalField.setText(Utils.intToString(contadorClases+contadorProperties+contadorXml+contadorOtros));
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
		this.limpiaSearchData();
		if(this.libreriaData.size()>0){
			this.listarFilesInJar();
		}
		this.cuentaFiles();
	}
	
	@FXML
	private void setOnDragDroppedTrash(DragEvent event) {
		System.out.println("setOnDragDroppedTrash");
		this.libreriaData.remove(this.libreriaInputTable.getSelectionModel().getSelectedItem());
	}
	
	public ObservableList<Libreria> getLibreriaData() {
		return libreriaData;
	}

	public void setLibreriaData(ObservableList<Libreria> libreriaData) {
		this.libreriaData = libreriaData;
	}

	public ObservableList<Fichero> getFicheroSearchData() {
		return ficheroSearchData;
	}

	public void setFicheroSearchData(ObservableList<Fichero> ficheroSearchData) {
		this.ficheroSearchData = ficheroSearchData;
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
