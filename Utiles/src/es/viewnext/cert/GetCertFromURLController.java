package es.viewnext.cert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import es.viewnext.Main;
import es.viewnext.modelo.Certificado;
import es.viewnext.modelo.KeyStoreApp;
import es.viewnext.principal.RootController;
import es.viewnext.utils.DateUtil;
import es.viewnext.utils.Utils;
import es.viewnext.utils.UtilsCertificates;
import es.viewnext.utils.UtilsWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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

public class GetCertFromURLController {

	private Main main;
	private RootController rootController;
	private ObservableList<Certificado> certificadoData = FXCollections.observableArrayList();

	@FXML
	private TableView<Certificado> certificadoTable;
	@FXML
	private TableColumn<Certificado, String> columnaRutaLocalCertificado;
	@FXML
	private TableColumn<Certificado, String> columnaSubjectCertificado;
	@FXML
	private TableColumn<Certificado, String> columnaUrlCertificado;
	@FXML
	private TableColumn<Certificado, String> columnaFechaCaducidadCertificado;
	@FXML
	private TableColumn<Certificado, Number> numCertsInChainCertificado;
	@FXML
	private TextField hostTextField;
	@FXML
	private TextField portTextField;
	@FXML
	private ImageView trashView;
	
	ContextMenu contextMenu;
	MenuItem exportarADisco;
	MenuItem verDetalleCertificado;
	MenuItem verResumenCertificado;
	MenuItem addCertificadoAKeystore;

	@FXML
	private void initialize() {
		this.certificadoTable.setItems(this.getCertificadoData());
		this.certificadoTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.columnaRutaLocalCertificado.setCellValueFactory(cellData -> cellData.getValue().rutaLocalProperty());
		this.columnaSubjectCertificado.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
		this.columnaUrlCertificado.setCellValueFactory(cellData -> cellData.getValue().urlProperty());
		this.columnaFechaCaducidadCertificado
				.setCellValueFactory(cellData -> DateUtil.formatSP(cellData.getValue().getFechaCaducidad()));
		this.numCertsInChainCertificado.setCellValueFactory(cellData -> cellData.getValue().numCertsInChainProperty());

		this.contextMenu = new ContextMenu();
		this.exportarADisco = new MenuItem("Exportar certificado a disco");
		this.verDetalleCertificado = new MenuItem("Ver detalle cetificado");
		this.verResumenCertificado = new MenuItem("Ver resumen certificado");
		this.addCertificadoAKeystore = new MenuItem("Añadir certificado a KeyStore");
		this.exportarADisco.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				guardarCert(certificadoTable.getSelectionModel().getSelectedItem());
			}
		});
		this.verDetalleCertificado.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				UtilsCertificates.verDetalleCertificado(certificadoTable.getSelectionModel().getSelectedItem());
			}
		});
		this.verResumenCertificado.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				verResumenCert(certificadoTable.getSelectionModel().getSelectedItem());
			}
		});
		this.addCertificadoAKeystore.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addCertificadoToKeystore(certificadoTable.getSelectionModel().getSelectedItem());
			}
		});
		
		this.certificadoTable.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					UtilsCertificates.verDetalleCertificado(certificadoTable.getSelectionModel().getSelectedItem());
				}
			}
		});
		this.certificadoTable.setContextMenu(contextMenu);
	}

	@FXML
	private void handleGetCertFromURL() {
		System.out.println("handleGetCertFromURL");
		String host = this.hostTextField.getText();
		int port = Utils.stringToInt(this.portTextField.getText());
		try {
			Certificado certificado = UtilsCertificates.getCertificateFromServer(host, port);
			certificado.setUrl(this.hostTextField.getText());
			this.certificadoData.add(certificado);
			if(this.certificadoData.size()>0 && this.contextMenu.getItems().size()==0){
				this.contextMenu.getItems().addAll(this.exportarADisco, this.verDetalleCertificado, this.verResumenCertificado, this.addCertificadoAKeystore);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addCertificadoToKeystore(Certificado certificado) {
		System.out.println("addCertificadoAKeystore");
		UtilsCertificates.addCertificadoToSelectKeystore(main, certificado);
	}
	
	@FXML
	private void limpieza(){
		this.hostTextField.clear();
		this.portTextField.clear();
		this.certificadoData.clear();
		if(this.certificadoData.size()==0){
			this.contextMenu.getItems().removeAll(this.exportarADisco, this.verDetalleCertificado, this.verResumenCertificado, this.addCertificadoAKeystore);
		}
	}
	
	private void guardarCert(Certificado certificado) {
		System.out.println("guardarCert");

		String fichero = UtilsWindow.guardar(this.getMain(), "Cert files", "cer", certificado.getUrl());
		File file = new File(fichero);
		if (file != null) {
			file = new File(file.getPath());
			try {
				FileOutputStream fos = new FileOutputStream(file.getPath());
				fos.write(certificado.getCertificateList().get(0).getEncoded());
				fos.flush();
				fos.close();
				Utils.refreshTableView(this.certificadoTable, 0);
			} catch (IOException e) {
				System.out.println("Error writing certificate to " + file.getName() + " : " + e.getMessage());
			} catch (CertificateEncodingException e) {
				e.printStackTrace();
			} catch (CertificateException e) {
				e.printStackTrace();
			}
		}
	}

	// Ficheros Origen para el borrado
	@FXML
	private void setOnDragDetectedFiles(MouseEvent event) {
		System.out.println("setOnDragDetectedFiles");

		/* allow any transfer mode */
		Dragboard db = certificadoTable.startDragAndDrop(TransferMode.ANY);

		/* put a string on dragboard */
		ClipboardContent content = new ClipboardContent();
		content.putString(Utils.integerToString(certificadoTable.getSelectionModel().getSelectedIndex()));
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
		List<Certificado> listDelete = new ArrayList<Certificado>();
		for(Certificado certificado : this.certificadoTable.getSelectionModel().getSelectedItems()){
			listDelete.add(certificado);
		}
		this.certificadoData.removeAll(listDelete);
		if(this.certificadoData.size()==0){
			this.contextMenu.getItems().removeAll(this.exportarADisco, this.verDetalleCertificado, this.verResumenCertificado, this.addCertificadoAKeystore);
		}
	}

	@FXML
	private void setOnDragDroppedTrash(DragEvent event) {
		System.out.println("setOnDragDroppedTrash");
		
	}

	private void verResumenCert(Certificado certificado) {
		System.out.println("verResumenCert");
		String resumen = UtilsCertificates.verResumenCertificado(certificado);
		UtilsWindow.openAlert("Certificado", "Certificado de la URL", certificado.getUrl(), resumen);
	}

	public ObservableList<Certificado> getCertificadoData() {
		return certificadoData;
	}

	public void setCertificadoData(ObservableList<Certificado> certificadoData) {
		this.certificadoData = certificadoData;
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
