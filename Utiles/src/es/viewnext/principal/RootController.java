package es.viewnext.principal;

import java.io.IOException;

import es.viewnext.Main;
import es.viewnext.busquedas.BusquedasController;
import es.viewnext.busquedas.BusquedasTextInFilesController;
import es.viewnext.cert.GetCertFromURLController;
import es.viewnext.cert.KeyStoreController;
import es.viewnext.cert.CifrarDescifrarController;
import es.viewnext.encoding.EncodingFilesController;
import es.viewnext.encoding.EncodingFilesConversionController;
import es.viewnext.encoding.EncodingFolderController;
import es.viewnext.encoding.VariosEncodingController;
import es.viewnext.monitor.MonitorController;
import es.viewnext.network.JdbcController;
import es.viewnext.network.WebServiceController;
import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RootController {

	private Main main;
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private AnchorPane centerAnchor;
	@FXML
	private AnchorPane bottonAnchor;
	@FXML
	private AnchorPane leftAnchor;
	@FXML
	private AnchorPane rightAnchor;
	@FXML
	private AnchorPane contentImagen;
	
	@FXML
	private void initialize(){
//		try {
//			FXMLLoader loader = new FXMLLoader();
//			loader.setLocation(Main.class.getResource("principal/ContentControllerLayout.fxml"));
//			AnchorPane content = (AnchorPane) loader.load();
//			
//			this.contentController = loader.getController();
//			this.contentController.setMain(this.getMain());
//			this.contentController.setRootController(this);
//			
//			this.borderPane.setCenter(content);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	@FXML
	private void handleInicio() {
		this.muestraImagen();
	}
	
	@FXML
	private void handleExit() {
		System.exit(0);
	}
	
	@FXML
	private void handleBusquedaTexto(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("busquedas/BusquedasTextInFilesControllerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			BusquedasTextInFilesController bc = loader.getController();
			bc.setMain(this.getMain());
			bc.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleBusquedaFichero(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("busquedas/BusquedasControllerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			BusquedasController bc = loader.getController();
			bc.setMain(this.getMain());
			bc.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleEncodingFiles(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("encoding/EncodingFilesControllerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			EncodingFilesController efc = loader.getController();
			efc.setMain(this.getMain());
			efc.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleEncodingFilesInFolder(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("encoding/EncodingFolderControllerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			EncodingFolderController efifc = loader.getController();
			efifc.setMain(this.getMain());
			efifc.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleEncodingConversionFiles(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("encoding/EncodingFilesConversionControllerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			EncodingFilesConversionController efcc = loader.getController();
			efcc.setMain(this.getMain());
			efcc.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void handleVariosEncoding(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("encoding/VariosEncodingControllerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			VariosEncodingController vec = loader.getController();
			vec.setMain(this.getMain());
			vec.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleCertFromURL(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("cert/GetCertFromURLControlerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			GetCertFromURLController gcfuc = loader.getController();
			gcfuc.setMain(this.getMain());
			gcfuc.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleKeystore(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("cert/KeyStoreControlerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			KeyStoreController ksc = loader.getController();
			ksc.setMain(this.getMain());
			ksc.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleWS(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("network/WebServiceControllerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			WebServiceController ksc = loader.getController();
			ksc.setMain(this.getMain());
			ksc.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleJdbc(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("network/JdbcControllerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			JdbcController jdbc = loader.getController();
			jdbc.setMain(this.getMain());
			jdbc.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleXifra(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("cert/CifrarDescifrarControllerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			CifrarDescifrarController xifra = loader.getController();
			xifra.setMain(this.getMain());
			xifra.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleMonitorizacion(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("monitor/MonitorControllerLayout.fxml"));
			AnchorPane content = (AnchorPane) loader.load();
			
			MonitorController mc = loader.getController();
			mc.setMain(this.getMain());
			mc.setRootController(this);

			this.borderPane.setCenter(content);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleAcercaDe() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("principal/AcercaDeLayout.fxml"));
			AnchorPane acercaDeLayout = (AnchorPane) loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Acerca de");
			dialogStage.getIcons().add(new Image("file:resources/imagenes/viewnext.png"));
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(this.main.getPrimaryStage());
			Scene scene = new Scene(acercaDeLayout);
			dialogStage.resizableProperty().setValue(Boolean.FALSE);
			dialogStage.setScene(scene);
			
			FadeTransition ft = new FadeTransition(Duration.millis(2000), acercaDeLayout);
			ft.setFromValue(0.0);
			ft.setToValue(1.0);
			ft.play();
			
			scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			    @Override
			    public void handle(MouseEvent mouseEvent) {
			        dialogStage.close();
			    }
			});
			
			dialogStage.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void muestraImagen(){
		this.borderPane.setCenter(this.centerAnchor);
	}
	
	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
	}
	
}
