package es.viewnext.cert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import es.viewnext.Main;
import es.viewnext.modelo.Alias;
import es.viewnext.modelo.Certificado;
import es.viewnext.modelo.KeyStoreApp;
import es.viewnext.principal.RootController;
import es.viewnext.utils.DateUtil;
import es.viewnext.utils.Utils;
import es.viewnext.utils.UtilsCertificates;
import es.viewnext.utils.UtilsFiles;
import es.viewnext.utils.UtilsWindow;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class KeyStoreController {

	private Main main;
	private RootController rootController;
	private ObservableList<KeyStoreApp> keystoreData = FXCollections.observableArrayList();
	private ObservableList<Alias> aliasData = FXCollections.observableArrayList();
	private ObservableList<Certificado> certificadosData = FXCollections.observableArrayList();

	@FXML
	private TableView<KeyStoreApp> keyStoreInputTable;
	@FXML
	private TableColumn<KeyStoreApp, String> columnaRutaKeyStoreInput;
	@FXML
	private TableColumn<KeyStoreApp, String> columnaNombreKeyStoreInput;
	@FXML
	private TableColumn<KeyStoreApp, Number> numAliasInKeyStore;

	@FXML
	private TableView<Alias> aliasTable;
	@FXML
	private TableColumn<Alias, String> columnaKeyStoreAlias;
	@FXML
	private TableColumn<Alias, String> columnaNombreAlias;

	@FXML
	private TableView<Certificado> certificadosTable;

	@FXML
	private TableColumn<Certificado, String> columnaKeystoreCertificado;
	@FXML
	private TableColumn<Certificado, String> columnaAliasCertificado;
	@FXML
	private TableColumn<Certificado, String> columnaSubjectCertificado;
	@FXML
	private TableColumn<Certificado, String> columnaFechaCaducidadCertificado;
	@FXML
	private TableColumn<Certificado, Number> numCertsInChainCertificado;

	@FXML
	private ImageView trashView;
	@FXML
	private TextField keystoreFilteredField;
	@FXML
	private TextField aliasFilterField;

	ContextMenu keystoreContextMenu;
	MenuItem nuevoKeyStore;
	MenuItem refrescarKeyStore;
	MenuItem addCertKeyStore;
	ContextMenu aliasContextMenu;
	MenuItem deleteAlias;
	ContextMenu certificadoContextMenu;
	MenuItem verDetalleCertificado;
	MenuItem verResumenCertificado;
	MenuItem exportarCertificado;

	@FXML
	private void initialize() {
		this.keyStoreInputTable.setItems(this.getKeystoreData());
		this.keyStoreInputTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.columnaRutaKeyStoreInput.setCellValueFactory(cellData -> cellData.getValue().rutaProperty());
		this.columnaNombreKeyStoreInput.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
		this.numAliasInKeyStore.setCellValueFactory(cellData -> cellData.getValue().numAliasProperty());

		this.aliasTable.setItems(this.getAliasData());
		this.aliasTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.columnaKeyStoreAlias
				.setCellValueFactory(cellData -> cellData.getValue().getKeyStoreApp().nombreProperty());
		this.columnaNombreAlias.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());

		// Dos filtros sobre la misma tabla a partir de dos campos diferentes
		FilteredList<Alias> filteredAliasData = new FilteredList<Alias>(this.aliasData, p -> true);
		filteredAliasData.predicateProperty()
				.bind(Bindings.createObjectBinding(
						() -> alias -> alias.getNombre().toLowerCase().contains(this.aliasFilterField.getText())
								&& alias.getKeyStoreApp().getNombre().contains(this.keystoreFilteredField.getText()),
						this.aliasFilterField.textProperty(), this.keystoreFilteredField.textProperty()

		));
		this.aliasTable.setItems(filteredAliasData);

		this.certificadosTable.setItems(this.getCertificadosData());
		this.certificadosTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.columnaKeystoreCertificado
				.setCellValueFactory(cellData -> cellData.getValue().getKeyStoreApp().nombreProperty());
		this.columnaAliasCertificado.setCellValueFactory(cellData -> cellData.getValue().getAlias().nombreProperty());
		this.columnaSubjectCertificado.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
		this.columnaFechaCaducidadCertificado
				.setCellValueFactory(cellData -> DateUtil.formatSP(cellData.getValue().getFechaCaducidad()));
		this.numCertsInChainCertificado.setCellValueFactory(cellData -> cellData.getValue().numCertsInChainProperty());

		this.keystoreContextMenu = new ContextMenu();
		this.nuevoKeyStore = new MenuItem("Nuevo Keystore");
		this.refrescarKeyStore = new MenuItem("Refescar KeyStore");
		this.addCertKeyStore = new MenuItem("Añadir Certificado");
		this.nuevoKeyStore.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				nuevoKeyStore();
			}
		});
		this.refrescarKeyStore.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				handleRefresh();
			}
		});
		this.addCertKeyStore.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addCertificado(keyStoreInputTable.getSelectionModel().getSelectedItem());
			}
		});
		this.keystoreContextMenu.getItems().add(this.nuevoKeyStore);
		this.keyStoreInputTable.setContextMenu(this.keystoreContextMenu);

		this.aliasContextMenu = new ContextMenu();
		this.deleteAlias = new MenuItem("Borrar alias del Keystore");
		this.deleteAlias.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				deleteAlias(aliasTable.getSelectionModel().getSelectedItem());
			}
		});
		this.aliasTable.setContextMenu(this.aliasContextMenu);

		this.certificadoContextMenu = new ContextMenu();
		this.verDetalleCertificado = new MenuItem("Ver detalle cetificado");
		this.verResumenCertificado = new MenuItem("Ver resumen certificado");
		this.exportarCertificado = new MenuItem("Exportar certificado");
		this.verDetalleCertificado.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				UtilsCertificates.verDetalleCertificado(certificadosTable.getSelectionModel().getSelectedItem());
			}
		});
		this.verResumenCertificado.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				verResumenCert(certificadosTable.getSelectionModel().getSelectedItem());
			}
		});
		this.exportarCertificado.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				exportarCertificado(certificadosTable.getSelectionModel().getSelectedItem());
			}
		});

		this.certificadosTable.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					UtilsCertificates.verDetalleCertificado(certificadosTable.getSelectionModel().getSelectedItem());
				}
			}
		});
		this.certificadosTable.setContextMenu(certificadoContextMenu);

		this.aliasTable.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					handleViewCertificados();
				}
			}
		});

	}

	@FXML
	private void handleViewAlias() {
		System.out.println("handleViewAlias");
		this.aliasData.clear();
		this.aliasData.setAll(UtilsCertificates.getAliasListFromKeystore(this.getKeystoreData()));
		if (this.aliasData.size() > 0 && this.aliasContextMenu.getItems().size() == 0) {
			this.aliasContextMenu.getItems().addAll(this.deleteAlias);
		}
	}

	private void handleRefresh() {
		System.out.println("handleRefresh");
		this.handleViewAlias();
	}

	@FXML
	private void handleViewCertificados() {
		System.out.println("handleViewCertificados");
		Alias alias = this.aliasTable.getSelectionModel().getSelectedItem();

		if (alias != null) {
			try {
				boolean encontrado = false;
				int posicion = 0;
				int i = 0;
				for (Certificado cert : this.getCertificadosData()) {
					if (cert.getAlias().getNombre().equals(alias.getNombre())
							&& cert.getKeyStoreApp().getNombre().equals(alias.getKeyStoreApp().getNombre())) {
						encontrado = true;
						posicion = i;
					}
					i++;
				}
				if (!encontrado) {
					Certificado certificado = UtilsCertificates.getCertificateFromAliasKeystore(alias.getKeyStoreApp(),	alias);
					this.certificadosData.add(certificado);
				} else {
					this.certificadosTable.getSelectionModel().select(posicion);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (this.certificadosData.size() > 0 && this.certificadoContextMenu.getItems().size()==0) {
				this.certificadoContextMenu.getItems().addAll(this.verDetalleCertificado, this.verResumenCertificado,
						this.exportarCertificado);
			}
		}
	}

	@FXML
	private void limpia() {
		this.keystoreData.clear();
		this.aliasData.clear();
		this.certificadosData.clear();
		this.keystoreFilteredField.clear();
		this.aliasFilterField.clear();
		if (this.keystoreData.size() == 0) {
			this.keystoreContextMenu.getItems().removeAll(this.refrescarKeyStore, this.addCertKeyStore);
		}
		if (this.aliasData.size() == 0) {
			this.aliasContextMenu.getItems().removeAll(this.deleteAlias);
		}
		if (this.certificadosData.size() == 0) {
			this.certificadoContextMenu.getItems().removeAll(this.verDetalleCertificado, this.verResumenCertificado,
					this.exportarCertificado);
		}
	}

	private void limpiaCertificados(KeyStoreApp ksa){
		List<Certificado> listDelete = new ArrayList<Certificado>();
		for (Certificado certificado : this.getCertificadosData()) {
			if(certificado.getKeyStoreApp().getNombre().equals(ksa.getNombre())){
				listDelete.add(certificado);
			}
		}
		this.certificadosData.removeAll(listDelete);
	}
	// Ficheros destino para la recuperacion
	@FXML
	private void setOnDragOverFiles(DragEvent event) {
		System.out.println("setOnDragOverFiles");
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasFiles()) {
			List<File> file = db.getFiles();
			success = true;
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
			success = this.loadKeystores(db.getFiles());
		}
		event.setDropCompleted(success);
		event.consume();
	}

	private boolean loadKeystores(List<File> listFiles) {
		boolean success = true;
		String password = null;
		for (File file : listFiles) {
			try {
				password = UtilsWindow.openInputDialog("changeit", "Contraseña",
						"Contraseña del almacen '" + file.getName() + "'",
						"por favor, introduzca la contraseña del almacén");
				if(password != null){
					FileInputStream is = new FileInputStream(file.getPath());
					KeyStore keystore;
					if(UtilsFiles.getFileExtension(file).equals("p12")){
						keystore = KeyStore.getInstance("PKCS12");
					} else {
						keystore = KeyStore.getInstance(KeyStore.getDefaultType());
					}
					
					keystore.load(is, password.toCharArray());
					is.close();
					if (keystore != null && !keystore.getType().isEmpty()) {
						KeyStoreApp ksa = new KeyStoreApp();
						ksa.setNombre(file.getName());
						ksa.setRuta(file.getPath());
						ksa.setPassword(password);
						ksa.setNumAlias(keystore.size());
						this.keystoreData.add(ksa);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				success = false;
			} catch (KeyStoreException e) {
				e.printStackTrace();
				success = false;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				success = false;
			} catch (CertificateException e) {
				e.printStackTrace();
				success = false;
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error");
				alert.setHeaderText("No se puede leer el almacen '" + file.getName() + "'");
				alert.setContentText(e.getMessage());
				alert.showAndWait();
				success = false;
			}
		}

		if (this.keystoreData.size() > 0 && this.keystoreContextMenu.getItems().size()==1) {
			this.keystoreContextMenu.getItems().addAll(this.refrescarKeyStore, this.addCertKeyStore);
		}
		this.handleViewAlias();
		return success;
	}

	// Ficheros Origen para el borrado
	@FXML
	private void setOnDragDetectedFiles(MouseEvent event) {
		System.out.println("setOnDragDetectedFiles");

		/* allow any transfer mode */
		Dragboard db = keyStoreInputTable.startDragAndDrop(TransferMode.ANY);

		/* put a string on dragboard */
		ClipboardContent content = new ClipboardContent();
		content.putString(Utils.integerToString(keyStoreInputTable.getSelectionModel().getSelectedIndex()));
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
		List<KeyStoreApp> listDelete = new ArrayList<KeyStoreApp>();
		for (KeyStoreApp ksa : this.keyStoreInputTable.getSelectionModel().getSelectedItems()) {
			this.limpiaCertificados(ksa);
			listDelete.add(ksa);
		}
		this.keystoreData.removeAll(listDelete);
		if(this.keystoreData.size()==0){
			this.keystoreContextMenu.getItems().removeAll(this.refrescarKeyStore, this.addCertKeyStore);
		}
		this.handleViewAlias();
	}

	@FXML
	private void setOnDragDroppedTrash(DragEvent event) {
		System.out.println("setOnDragDroppedTrash");
	}

	private void nuevoKeyStore() {
		System.out.println("nuevoKeyStore");
		KeyStoreApp ksa = UtilsCertificates.createKeyStore(this.getMain());
		this.keystoreData.add(ksa);
		if(this.keystoreData.size()>0 && this.keystoreContextMenu.getItems().size() == 1){
			this.keystoreContextMenu.getItems().addAll(this.refrescarKeyStore, this.addCertKeyStore);
		}
		this.handleViewAlias();
	}

	private void verResumenCert(Certificado certificado) {
		System.out.println("verResumenCert");
		String resumen = UtilsCertificates.verResumenCertificado(certificado);
		UtilsWindow.openAlert("Certificado", "Certificado del Subject ", certificado.getSubject(), resumen);
	}

	private void exportarCertificado(Certificado certificado) {
		System.out.println("exportarCertificado");
		UtilsCertificates.exportarCertificado(this.getMain(), certificado);
	}

	private void addCertificado(KeyStoreApp ksa) {
		System.out.println("addCertificado");
		Certificado certificado = UtilsCertificates.addCertificadoToKeystore(this.getMain(), ksa);
		if (certificado != null) {
			this.aliasData.add(certificado.getAlias());
			ksa.setNumAlias(ksa.getNumAlias() + 1);
			if (this.aliasData.size() > 0 && this.aliasContextMenu.getItems().size()==0) {
				this.aliasContextMenu.getItems().addAll(this.deleteAlias);
			}
		}
	}

	private void deleteAlias(Alias alias) {
		System.out.println("deleteAlias");
		int indice = this.aliasTable.getSelectionModel().getSelectedIndex();
		this.aliasData.remove(indice);
		UtilsCertificates.deleteAliasFromKeystore(alias);
		if (this.aliasData.size() == 0) {
			this.aliasContextMenu.getItems().removeAll(this.deleteAlias);
		}
		KeyStoreApp ksa = alias.getKeyStoreApp();
		if (ksa.getNumAlias() != 0) {
			ksa.setNumAlias(ksa.getNumAlias() - 1);
		}
		this.limpiaCertificados(ksa);
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

	public ObservableList<KeyStoreApp> getKeystoreData() {
		return keystoreData;
	}

	public void setKeystoreData(ObservableList<KeyStoreApp> keystoreData) {
		this.keystoreData = keystoreData;
	}

	public ObservableList<Alias> getAliasData() {
		return aliasData;
	}

	public void setAliasData(ObservableList<Alias> aliasData) {
		this.aliasData = aliasData;
	}

	public ObservableList<Certificado> getCertificadosData() {
		return certificadosData;
	}

	public void setCertificadosData(ObservableList<Certificado> certificadosData) {
		this.certificadosData = certificadosData;
	}
}
