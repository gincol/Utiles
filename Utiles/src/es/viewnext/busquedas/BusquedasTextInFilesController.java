package es.viewnext.busquedas;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class BusquedasTextInFilesController {

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
	private TableColumn<Fichero, String> columnaTextoFicheroSearch;
	@FXML
	private TextField textoFichero;
	
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

		this.ficheroSearchTable.setItems(this.getFicheroSearchData());
		this.columnaAuxFicheroSearch.setCellValueFactory(cellData -> cellData.getValue().auxProperty());
		this.columnaTextoFicheroSearch.setCellValueFactory(cellData -> cellData.getValue().textoProperty());
		final ContextMenu contextMenu = new ContextMenu();
		MenuItem dec = new MenuItem("Source");
		contextMenu.getItems().addAll(dec);
		dec.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	Utils.decompile(ficheroSearchTable.getSelectionModel().getSelectedItem());
		    }
		});
		this.ficheroSearchTable.setContextMenu(contextMenu);
		
		this.ficheroSearchTable.setOnMousePressed(new EventHandler<MouseEvent>() {
		    @Override 
		    public void handle(MouseEvent event) {
		        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
		            Utils.decompile(ficheroSearchTable.getSelectionModel().getSelectedItem());
		        }
		    }
		});
	}

	@FXML
	private void handleSearchTextInFile() {
		System.out.println("handleSearchTextInFile");
		this.ficheroSearchData.clear();

		if(this.getLibreriaData().size()>0 && !this.textoFichero.getText().trim().isEmpty()){
			for (Libreria libreriaIn : this.getLibreriaData()) {
				try {
					Files.walk(Paths.get(libreriaIn.getRuta())).forEach(filePath -> {
						if (Files.isRegularFile(filePath)) {
							try {
								ZipInputStream zip = new ZipInputStream(new FileInputStream(filePath.toFile()));
								String linea = "";
								StringTokenizer trozo;
								boolean success;
	
								JarFile jf = new JarFile(filePath.toAbsolutePath().toString());
								ITypeLoader it = new JarTypeLoader(jf);
								InputTypeLoader itl = new InputTypeLoader(it);
	
								DecompilerSettings ds = new DecompilerSettings();
								ds.setTypeLoader(itl);
								ITextOutput ito;
	
								while (true) {
									ZipEntry e = zip.getNextEntry();
									if (e == null) {
										break;
									} else {
										if (!e.isDirectory()) {
											URL url = new URL(
													"jar:file:/" + filePath.toAbsolutePath() + "!/" + e.getName());
											InputStream is;
											if (!e.getName().endsWith(".class")) {
												is = url.openStream();
											} else {
												ito = new PlainTextOutput();
												Decompiler.decompile(e.getName(), ito, ds);
												is = new ByteArrayInputStream(ito.toString().getBytes());
											}
											Scanner sc = new Scanner(is);
											while (sc.hasNextLine()) {
												linea = sc.nextLine();
												success = true;
												if (this.textoFichero.getText().contains("*")) {// se
																								// accepta
																								// el
																								// comodin
																								// "*"
													trozo = new StringTokenizer(this.textoFichero.getText(), "*");
													while (trozo.hasMoreTokens()) {
														if (!linea.trim().contains(trozo.nextToken())) {
															success = false;
														}
													}
													if (success) {
														Fichero fichero = new Fichero();
														fichero.setNombre(e.getName());
														fichero.setRuta(filePath.toAbsolutePath().toString());
														fichero.setAux(filePath.toAbsolutePath().toString() + " --> "
																+ e.getName());
														fichero.setTexto(linea);
														this.ficheroSearchData.add(fichero);
													}
												} else {
													if (linea.trim().contains(this.textoFichero.getText())) {
														Fichero fichero = new Fichero();
														fichero.setNombre(e.getName());
														fichero.setRuta(filePath.toAbsolutePath().toString());
														fichero.setAux(filePath.toAbsolutePath().toString() + " --> "
																+ e.getName());
														fichero.setTexto(linea);
														this.ficheroSearchData.add(fichero);
													}
												}
											}
											sc.close();
											is.close();
										}
									}
								}
							} catch (Exception e) {
								System.out.println("error");
								e.printStackTrace();
							} 
						}
					});
				} catch (IOException e) {
					System.out.println("error");
					e.printStackTrace();
				}
			}
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Información");
			alert.setHeaderText("Nada que buscar\n");
			alert.setContentText("Introduzca el fichero y el texto a buscar.\nA continuación pulse buscar");

			alert.showAndWait();
		}
	}
	@FXML
	private void limpieza(){
		this.limpiaSearchData();
		this.limpiaLibreriaData();
		this.limpiaCampos();
	}
	
	private void listarFilesInJar() {
		System.out.println("handleListingFilesInFile");
		this.ficheroSearchData.clear();
		for (Libreria libreriaIn : this.getLibreriaData()) {
			try {
				Files.walk(Paths.get(libreriaIn.getRuta())).forEach(filePath -> {
					if (Files.isRegularFile(filePath)) {
						try {
							ZipInputStream zip = new ZipInputStream(new FileInputStream(filePath.toFile()));
							while (true) {
								ZipEntry e = zip.getNextEntry();
								if (e == null) {
									break;
								} else {
									if (!e.isDirectory()) {
										Fichero fichero = new Fichero();
										fichero.setNombre(e.getName());
										fichero.setRuta(filePath.toAbsolutePath().toString());
										fichero.setAux(filePath.toAbsolutePath().toString() + " --> " + e.getName());
										this.ficheroSearchData.add(fichero);
									}
								}
							}

						} catch (Exception e) {
							System.out.println("error");
						}
					}
				});
			} catch (IOException e) {
				System.out.println("error");
			}
		}
	}
	
	private void limpiaSearchData() {
		this.ficheroSearchData.clear();
	}
	
	private void limpiaLibreriaData() {
		this.libreriaData.clear();
	}
	
	private void limpiaCampos(){
		this.textoFichero.clear();
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

		if (success) {
			event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
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
			List<File> listaFicheros = new ArrayList<File>();
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
						} 
					}
				}
			}
		}
		this.listarFilesInJar();
		this.cuentaFiles();
		if(this.getLibreriaData().size()>0 && !this.textoFichero.getText().trim().isEmpty()){
			this.handleSearchTextInFile();
		}
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
		
		this.ficheroSearchData.clear();
	}

	@FXML
	private void setOnDragDroppedTrash(DragEvent event) {
		System.out.println("setOnDragDroppedTrash");
		this.libreriaData.remove(this.libreriaInputTable.getSelectionModel().getSelectedItem());

	}

	public ObservableList<Libreria> getLibreriaData() {
		return libreriaData;
	}

	public void setLibreriaData(ObservableList<Libreria> ficheroData) {
		this.libreriaData = ficheroData;
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
