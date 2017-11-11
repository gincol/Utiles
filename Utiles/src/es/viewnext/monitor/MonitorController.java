package es.viewnext.monitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Comparator;

import es.viewnext.Main;
import es.viewnext.modelo.Server;
import es.viewnext.principal.RootController;
import es.viewnext.utils.Utils;
import es.viewnext.utils.UtilsData;
import es.viewnext.utils.UtilsWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MonitorController {

	private Main main;
	private RootController rootController;

	private ObservableList<Server> serverData = FXCollections.observableArrayList();

	@FXML
	private TableView<Server> listaServer;
	@FXML
	private TableColumn<Server, String> columnaServerName;
	@FXML
	private TableColumn<Server, String> columnaServerIp;
	@FXML
	private TableColumn<Server, String> columnaServerServidor;
	@FXML
	private TableColumn<Server, String> columnaServerVersion;
	@FXML
	private TableColumn<Server, String> columnaServerJdk;
	@FXML
	private TextField filterNameField;
	@FXML
	private TextArea areaResult;
	@FXML
	CheckBox port22;
	@FXML
	CheckBox port80;
	@FXML
	CheckBox port443;
	@FXML
	TextField otroPuerto;
	
	@FXML
	private void initialize() {
		this.columnaServerName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		this.columnaServerIp.setCellValueFactory(cellData -> cellData.getValue().ipProperty());
		this.columnaServerServidor.setCellValueFactory(cellData -> cellData.getValue().servidorProperty());
		this.columnaServerVersion.setCellValueFactory(cellData -> cellData.getValue().versionProperty());
		this.columnaServerJdk.setCellValueFactory(cellData -> cellData.getValue().jdkProperty());
		this.serverData = UtilsData.loadServersDataFromFile();

		FilteredList<Server> filteredNameData = new FilteredList<Server>(this.serverData, p -> true);
		this.filterNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredNameData.setPredicate(server -> {
				// If filter text is empty, display all servers.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				// Compare name of every server with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				if (server.getName().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches name.
				}
				return false; // Does not match.
			});
		});
		
		//Orden inicial
		FXCollections.sort(this.serverData, new Comparator<Server>() {
			@Override
			public int compare(Server s1, Server s2) {
				return s1.getName().compareTo(s2.getName());
			}
		});
		// 3. Wrap the FilteredList in a SortedList. 
        SortedList<Server> sortedNameData = new SortedList<Server>(filteredNameData);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedNameData.comparatorProperty().bind(this.listaServer.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        this.listaServer.setItems(sortedNameData);
        this.listaServer.getSelectionModel().select(0);
        this.port22.setSelected(true);
	}

	@FXML
	private void anadirAppServer() {

	}

	@FXML
	private void editarAppServer() {

	}

	@FXML
	private void eliminarAppServer() {

	}
	
	@FXML
	private void pruebaConexion() {
		this.areaResult.clear();
		Server server = this.listaServer.getSelectionModel().getSelectedItem();
		int sel = 0;
		int[] ports = new int[4];
		if(port22.isSelected()){
			ports[sel] = 22; sel++;
		}
		if(port80.isSelected()){
			ports[sel] = 80; sel++;
		}
		if(port443.isSelected()){
			ports[sel] = 443; sel++;
		}
		if(otroPuerto.getText().trim().length()>0 && otroPuerto.getText().trim().matches("\\d*")){
			ports[sel] = Utils.stringToInteger(otroPuerto.getText().trim());
		}
		this.socketTest(server.getIp(), ports);
	}
	
	public void socketTest(String servidor, int [] puerto) {
		StringWriter errors = new StringWriter();
		StringBuilder texto = new StringBuilder();
		boolean excepcion = false;
		long inicio = 0;
		long fin = 0;
		long total = 0;
		for (int i : puerto) {
			if(i!=0){
				try {
					texto.append("Conectividad con puerto " + i + "\n");
					inicio = System.currentTimeMillis();
					Socket sock = new Socket(servidor, i);
					fin = System.currentTimeMillis();
					texto.append("\tCreacion socket, tiempo empleado " + total + " ms.");
					inicio = System.currentTimeMillis();
					String DirIP = sock.getInetAddress().toString();
					fin = System.currentTimeMillis();
					total = fin - inicio;
					texto.append("\n\tConsulta socket, server:puerto: " + DirIP + ":" + i + ", tiempo empleado " + total + " ms.");
					inicio = System.currentTimeMillis();
					sock.close();
					fin = System.currentTimeMillis();
					total = fin - inicio;
					texto.append("\n\tCierre socket, tiempo empleado: " + total + " ms.\n\n");
				
				} catch (Exception e) {
			//			excepcion = true;
			//			e.printStackTrace(new PrintWriter(errors));
					texto.append("\tError de conectividad en puerto  " + i + "\n");
					texto.append("\t" + e.getMessage() + "\n\n");
				}
			}
		}
		this.areaResult.setText(texto.toString());
		if (excepcion) {
			UtilsWindow.customAlert("Error de conectividad", errors.toString());
		}
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

	public ObservableList<Server> getServerData() {
		return serverData;
	}

	public void setServerData(ObservableList<Server> serverData) {
		this.serverData = serverData;
	}

}
