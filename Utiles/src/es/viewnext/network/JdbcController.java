package es.viewnext.network;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.viewnext.Main;
import es.viewnext.modelo.Catalogo;
import es.viewnext.modelo.Columna;
import es.viewnext.modelo.Esquema;
import es.viewnext.modelo.Funcion;
import es.viewnext.modelo.Procedure;
import es.viewnext.modelo.Tabla;
import es.viewnext.principal.RootController;
import es.viewnext.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class JdbcController {
	private Main main;
	private RootController rootController;

	@FXML
	private TextField urlTextField;
	@FXML
	private TextField driverTextField;
	@FXML
	private TextField queryTestTextField;
	@FXML
	private TextArea queryArea;
	@FXML
	private TextArea querySalidaArea;
	@FXML
	private TextField userTextField;
	@FXML
	private TextField passwordTextField;
	@FXML
	private ComboBox<String> driversCombo;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private TextArea bddInfoArea;
	@FXML
	private TextArea erroresArea;
	
	@FXML
	private TableView<Tabla> tablaView;
	@FXML
	private TableColumn<Tabla, String> columnaNombreTablaView;
//	@FXML
//	private TableColumn<Tabla, Number> columnaNColumnasTablaView;
//	@FXML
//	private TableColumn<Tabla, Number> columnaNRegistrosTablaView;
	@FXML
	private TableView<Catalogo> catalogoView;
	@FXML
	private TableColumn<Catalogo, String> columnaNombreCatalogoView;
	@FXML
	private TableView<Esquema> esquemaView;
	@FXML
	private TableColumn<Esquema, String> columnaNombreEsquemaView;
	@FXML
	private TableView<Columna> columnaView;
	@FXML
	private TableColumn<Columna, String> columnaNombreColumnaView;	
	@FXML
	private TableColumn<Columna, String> columnaTipoColumnaView;
	@FXML
	private TextField filterField;
	@FXML
	private TableView<Procedure> procedureView;
	@FXML
	private TableColumn<Procedure, String> columnaNombreProcedureView;
	@FXML
	private TableView<Funcion> funcionView;
	@FXML
	private TableColumn<Funcion, String> columnaNombreFuncionView;
	
	ContextMenu contextTableMenu;
	MenuItem verColumnas;
	
	ContextMenu contextEsquemaMenu;
	MenuItem verTablas;
	
	ContextMenu contextCatalogoMenu;
	MenuItem verEsquemas;
	
	private Map<Integer, String> urlList;
	private Map<Integer, String> driverList;
	private Map<Integer, String> queryList;
	private ObservableList<Tabla> tablaData = FXCollections.observableArrayList();
	private ObservableList<Esquema> esquemaData = FXCollections.observableArrayList();
	private ObservableList<Catalogo> catalogoData = FXCollections.observableArrayList();
	private ObservableList<Columna> columnaData = FXCollections.observableArrayList();
	private ObservableList<Procedure> procedureData = FXCollections.observableArrayList();
	private ObservableList<Funcion> funcionData = FXCollections.observableArrayList();

	@FXML
	private void initialize() {
		List<String> unique = new ArrayList<String>();
		unique.add("Seleccione un driver");
		unique.add("Oracle");
		unique.add("DB2");
		unique.add("DB2-AS400");
		unique.add("MySql");
		this.driversCombo.getItems().addAll(unique);
		this.driversCombo.getSelectionModel().select(0);
		
		this.urlList = new HashMap<Integer, String>();
		urlList.put(1,"jdbc:oracle:<drivertype>:@localhost:1521:<SID>");
		urlList.put(2,"jdbc:db2://localhost:<port>;<parameters>");
		urlList.put(3,"jdbc:db2://<host>:<port>;<parameters>");
		urlList.put(4,"jdbc:mysql://localhost:3306/<database>");
		
		this.urlTextField.setText(this.urlList.get(0));
		
		this.driverList = new HashMap<Integer, String>();
		this.driverList.put(1, "oracle.jdbc.driver.OracleDriver");
		this.driverList.put(2, "com.ibm.db2.jcc.DB2Driver");
		this.driverList.put(3, "com.ibm.as400.access.AS400JDBCDriver");
		this.driverList.put(4, "com.mysql.jdbc.Driver");
		
		this.queryList = new HashMap<Integer, String>();
		this.queryList.put(1, "SELECT 1 FROM DUAL");
		this.queryList.put(2, "SELECT 1 FROM SYSIBM.SYSDUMMY1");
		this.queryList.put(3, "SELECT 1 FROM SYSIBM.SYSDUMMY1");
		this.queryList.put(4, "SELECT 1");
		
		this.tablaView.setItems(this.getTablaData());
		this.tablaView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.columnaNombreTablaView.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
//		this.columnaNColumnasTablaView.setCellValueFactory(cellData -> cellData.getValue().numColumnasProperty());
//		this.columnaNRegistrosTablaView.setCellValueFactory(cellData -> cellData.getValue().numRegistrosProperty());
		this.contextTableMenu = new ContextMenu();
		this.verColumnas = new MenuItem("Ver columnas");
		this.verColumnas.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				getColumnas(tablaView.getSelectionModel().getSelectedItem());
			}
		});
		this.contextTableMenu.getItems().addAll(this.verColumnas);
		this.tablaView.setContextMenu(contextTableMenu);
		this.tablaView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					getColumnas(tablaView.getSelectionModel().getSelectedItem());
				}
			}
		});
		//CATALOGO
		this.catalogoView.setItems(this.getCatalogoData());
		this.catalogoView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.columnaNombreCatalogoView.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
		this.contextCatalogoMenu = new ContextMenu();
		this.verEsquemas = new MenuItem("Ver esquemas");
		this.verEsquemas.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				getEsquemas(catalogoView.getSelectionModel().getSelectedItem());
			}
		});
		this.contextCatalogoMenu.getItems().addAll(this.verEsquemas);
		this.catalogoView.setContextMenu(contextCatalogoMenu);
		this.catalogoView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					getEsquemas(catalogoView.getSelectionModel().getSelectedItem());
				}
			}
		});
		
		//COLUMNAS
		this.columnaView.setItems(this.getColumnaData());
		this.columnaView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.columnaNombreColumnaView.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
		this.columnaTipoColumnaView.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
		
		//PROCEDURES
		this.procedureView.setItems(this.getProcedureData());
		this.procedureView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.columnaNombreProcedureView.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
		
		//FUNCIONES
		this.funcionView.setItems(this.getFuncionData());
		this.funcionView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.columnaNombreFuncionView.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());

		
		//ESQUEMA
		this.contextEsquemaMenu = new ContextMenu();
		this.verTablas = new MenuItem("Ver tablas");
		this.verTablas.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				getTablas(esquemaView.getSelectionModel().getSelectedItem());
			}
		});
		this.contextEsquemaMenu.getItems().addAll(this.verTablas);
		this.esquemaView.setContextMenu(contextEsquemaMenu);
		this.esquemaView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					getTablas(esquemaView.getSelectionModel().getSelectedItem());
				}
			}
		});
		//Ordenamos la lista por esquema
		FXCollections.sort(this.esquemaData, new Comparator<Esquema>(){
			@Override
			public int compare(Esquema e1, Esquema e2){
				//para que tenga en cuenta los acentos
				Collator myCollator = Collator.getInstance(new Locale("es", "ES"));
				return myCollator.compare(e1.getNombre(), e2.getNombre());
			}
		});
		//Anadimos un filtro
		FilteredList<Esquema> filteredData = new FilteredList<Esquema>(this.esquemaData, p -> true);
		this.filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(esquema -> {
				// If filter text is empty, display all jugadores.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				if (esquema.getNombre().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches first name.
				}
				return false; // Does not match.
			});
		});
		this.esquemaView.setItems(filteredData);
		this.esquemaView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.columnaNombreEsquemaView.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
	}

	@FXML
	private void handleUrl(){
		this.handleClearNoDriver();
		this.urlTextField.setText(this.urlList.get(this.driversCombo.getSelectionModel().getSelectedIndex()));
		this.driverTextField.setText(this.driverList.get(this.driversCombo.getSelectionModel().getSelectedIndex()));
		this.queryTestTextField.setText(this.queryList.get(this.driversCombo.getSelectionModel().getSelectedIndex()));
	}
	
	/**
	 * Limpia de atributos
	 */
	@FXML
	private void handleClear() {
		this.filterField.clear();
		this.urlTextField.clear();
		this.userTextField.clear();
		this.passwordTextField.clear();
		this.querySalidaArea.clear();
		this.queryArea.clear();
		this.bddInfoArea.clear();
		this.driversCombo.getSelectionModel().clearSelection();
		this.queryTestTextField.clear();
		this.driverTextField.clear();
		this.tablaData.clear();
		this.esquemaData.clear();
		this.erroresArea.clear();
		this.catalogoData.clear();
		this.catalogoView.getItems().removeAll(getCatalogoData());
		this.esquemaView.getItems().removeAll(getEsquemaData());
		this.tablaView.getItems().removeAll(getTablaData());
		this.columnaView.getItems().removeAll(getColumnaData());
		this.procedureView.getItems().removeAll(getProcedureData());
		this.funcionView.getItems().removeAll(getFuncionData());
	}
	
	private void handleClearNoDriver() {
		this.filterField.clear();
		this.userTextField.clear();
		this.passwordTextField.clear();
		this.querySalidaArea.clear();
		this.queryArea.clear();
		this.bddInfoArea.clear();
		this.tablaData.clear();
		this.esquemaData.clear();
		this.erroresArea.clear();
		this.catalogoData.clear();
		this.catalogoView.getItems().removeAll(getCatalogoData());
		this.esquemaView.getItems().removeAll(getEsquemaData());
		this.tablaView.getItems().removeAll(getTablaData());
		this.columnaView.getItems().removeAll(getColumnaData());
		this.procedureView.getItems().removeAll(getProcedureData());
		this.funcionView.getItems().removeAll(getFuncionData());
	}
	
	@FXML
	private void handleClearQuery() {
		this.queryArea.clear();
		this.querySalidaArea.clear();
	}
	
	@FXML
	private void handleDriverSelected(){
		if(this.driversCombo.getSelectionModel().isSelected(0)){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Faltan datos");
			alert.setHeaderText("Driver no seleccionado");
			alert.setContentText("Antes de rellenar la url de conexión se ha de seleccionar un driver");
			alert.showAndWait();
		}
	}

	@FXML
	private void handleJdbcConnect(){
		System.out.println("handleJdbcConnect");
		this.bddInfoArea.clear();
		this.catalogoData.clear();
		this.tablaData.clear();
		this.esquemaData.clear();
		this.querySalidaArea.clear();
		StringBuilder mensaje = new StringBuilder();
		StringBuilder mensaje2 = new StringBuilder();
		if(!this.driversCombo.getSelectionModel().isSelected(0) &&
				this.urlTextField.getText().trim().length()>0 &&
				this.userTextField.getText().trim().length()>0){
			long inicio = 0;
			long fin = 0;
			long total = 0;
			
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				inicio = System.currentTimeMillis();
				con = this.getConnection();
				fin = System.currentTimeMillis();
				total = fin - inicio;
				mensaje.append("\nCreacion conexion, tiempo empleado: " + total + " ms.");
				
				ps = con.prepareStatement(this.queryTestTextField.getText());
				inicio = System.currentTimeMillis();
				rs = ps.executeQuery();
				this.querySalidaArea.appendText("Resultado de la query: \n");
				this.queryArea.appendText(this.queryTestTextField.getText());
				while (rs.next()) {
					this.querySalidaArea.appendText(rs.getString(1));					
				}
				fin = System.currentTimeMillis();
				total = fin - inicio;
				mensaje.append("\nQuery, tiempo empleado: " + total + " ms.");
				
				//Obtenemos info de la BBDD
				this.getMetaData(con);
			} catch (InstantiationException e) {
				e.printStackTrace();
				mensaje2.append("\n"+Utils.getErrorText(e));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				mensaje2.append("\n"+Utils.getErrorText(e));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				mensaje2.append("\n"+Utils.getErrorText(e));
			} catch (SQLException e) {
				e.printStackTrace();
				mensaje2.append("\n"+Utils.getErrorText(e));
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (ps != null)
						ps.close();
					if (con != null) {
						inicio = System.currentTimeMillis();
						con.close();
						fin = System.currentTimeMillis();
						total = fin - inicio;
						mensaje.append("\nCierre conexion, tiempo empleado: " + total + " ms.");
					}
				} catch (SQLException e) {
					mensaje2.append("\n"+Utils.getErrorText(e));
				}
			}
		} else {
			mensaje.append("\nFaltan datos");
		}
		
		this.bddInfoArea.appendText(mensaje.toString());
		this.erroresArea.setText(mensaje2.toString());
	}
	
	@FXML
	private void handleQuery(){
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder mensaje2 = new StringBuilder();
		ResultSetMetaData metadata = null;
		try {
			con = this.getConnection();
			ps = con.prepareStatement(this.queryArea.getText().trim());
			rs = ps.executeQuery();
			metadata = rs.getMetaData();
			int columnCount = metadata.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				this.querySalidaArea.appendText(metadata.getColumnName(i) + ", ");      
		    }
			this.querySalidaArea.appendText("\n");
			while(rs.next()){
				String row = "";
		        for (int i = 1; i <= columnCount; i++) {
		            row += rs.getString(i) + ", ";          
		        }
		        this.querySalidaArea.appendText(row + "\n");
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
			mensaje2.append("\n"+Utils.getErrorText(e));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			mensaje2.append("\n"+Utils.getErrorText(e));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			mensaje2.append("\n"+Utils.getErrorText(e));
		} catch (SQLException e) {
			e.printStackTrace();
			mensaje2.append("\n"+Utils.getErrorText(e));
		} finally {
			this.cierraRecursos(con, ps, rs);
		}
		this.erroresArea.setText(mensaje2.toString());
	}
	

	private void getMetaData(Connection con) throws SQLException{
		this.columnaView.getItems().clear();
		this.catalogoView.getItems().clear();
		this.esquemaView.getItems().clear();
		this.erroresArea.clear();
		
		DatabaseMetaData dmd = con.getMetaData();
		StringBuilder sb = new StringBuilder();
		sb.append("\nProduct name: "+dmd.getDatabaseProductName());
		sb.append("\nProduct version: "+dmd.getDatabaseProductVersion());
		sb.append("\nDB Major version: "+dmd.getDatabaseMajorVersion());
		sb.append("\nDB Minor version: "+dmd.getDatabaseMinorVersion());
		sb.append("\nDriver name: "+dmd.getDriverName());
		sb.append("\nDriver version: "+dmd.getDriverVersion());
		sb.append("\nDriver major version: "+dmd.getDriverMajorVersion());
		sb.append("\nDriver minor version: "+dmd.getDriverMinorVersion());
		sb.append("\nDefault tx isolation: "+dmd.getDefaultTransactionIsolation());
		sb.append("\nJDBC Major version: "+dmd.getJDBCMajorVersion());
		sb.append("\nJDBC Minor version: "+dmd.getJDBCMinorVersion());
		this.bddInfoArea.setText(sb.toString());
		
		//CATALOGOS y ESQUEMAS
		ResultSet rs = dmd.getCatalogs();
		while (rs.next()) {
			Catalogo c = new Catalogo();
			c.setNombre(rs.getString(1));
			this.catalogoData.add(c);
		}
		
		//Si es oracle no hay catalogo
		if(this.catalogoData.size()==0){
			rs = dmd.getSchemas(null, null);
			while (rs.next()) {
				Esquema esquema = new Esquema();
				esquema.setNombre(rs.getString(1));
				this.esquemaData.add(esquema);
			}
		}

	}
	
	private void getEsquemas(Catalogo catalogo){
//		this.esquemaView.getItems().clear();
		this.esquemaData.clear();
		this.tablaView.getItems().clear();
		this.columnaView.getItems().clear();
		this.procedureView.getItems().clear();
		this.funcionView.getItems().clear();
		StringBuilder mensaje2 = new StringBuilder();
		Connection con = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			DatabaseMetaData dmd = con.getMetaData();
			if(catalogo!=null){
				rs = dmd.getSchemas(catalogo.getNombre(), null);
			} else {
				rs = dmd.getSchemas(null, null);
			}
			
			while (rs.next()) {
				Esquema esquema = new Esquema();
				esquema.setNombre(rs.getString(1));
				this.esquemaData.add(esquema);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
			mensaje2.append("\n"+Utils.getErrorText(e));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			mensaje2.append("\n"+Utils.getErrorText(e));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			mensaje2.append("\n"+Utils.getErrorText(e));
		} catch (SQLException e) {
			e.printStackTrace();
			mensaje2.append("\n"+Utils.getErrorText(e));
		} finally {
			this.cierraRecursos(con, null, rs);
		}
		this.erroresArea.setText(mensaje2.toString());
	}
	
	@SuppressWarnings("resource")
	private void getTablas(Esquema esquema){
		this.tablaView.getItems().clear();
		this.columnaView.getItems().clear();
		this.procedureView.getItems().clear();
		this.funcionView.getItems().clear();
		StringBuilder mensaje = new StringBuilder();
		Connection con = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			DatabaseMetaData dmd = con.getMetaData();
			String [] tiposTabla = new String[1];
			tiposTabla[0] = "TABLE";
			rs = dmd.getTables(null, esquema.getNombre(), "%", tiposTabla);
//			String nombreTabla = "";
			while (rs.next()) {
//				nombreTabla = rs.getString("TABLE_NAME");
//				Tabla tabla = this.getDetalleTabla(con, esquema.getNombre(), nombreTabla);
				Tabla tabla = new Tabla();
				tabla.setNombre(rs.getString("TABLE_NAME"));
				this.tablaData.add(tabla);
			}
			
			//PROCEDURES
			if(this.catalogoData.size()==0){
				rs = dmd.getProcedures(null, esquema.getNombre(), "%");
			} else {
				rs = dmd.getProcedures(null, esquema.getNombre(), "%");
			}
			while (rs.next()) {
				Procedure p = new Procedure();
				p.setNombre(rs.getString("PROCEDURE_NAME"));
				this.procedureData.add(p);
			}
			//FUNCIONES
			rs = dmd.getFunctions(null, esquema.getNombre(), "%");
			while (rs.next()) {
				Funcion f = new Funcion();
				f.setNombre(rs.getString("FUNCTION_NAME"));
				this.funcionData.add(f);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
			mensaje.append("\n"+Utils.getErrorText(e));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			mensaje.append("\n"+Utils.getErrorText(e));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			mensaje.append("\n"+Utils.getErrorText(e));
		} catch (SQLException e) {
			e.printStackTrace();
			mensaje.append("\n"+Utils.getErrorText(e));
		} finally {
			this.cierraRecursos(con, null, rs);
		}
		this.erroresArea.setText(mensaje.toString());
	}
	
//	private Tabla getDetalleTabla(Connection con, String esquema, String nombreTabla){
//		Tabla tabla = new Tabla();
//		tabla.setNombre(nombreTabla);
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		ResultSetMetaData metadata = null;
//		try {
//			con = this.getConnection();
//			ps = con.prepareStatement("select * from " + esquema + "." + nombreTabla);
//			rs = ps.executeQuery();
//			metadata = rs.getMetaData();
//			tabla.setNumColumnas(metadata.getColumnCount());
//			
//			ps = con.prepareStatement("select count(*) from " + esquema + "." + nombreTabla);
//			rs = ps.executeQuery();
//			while(rs.next()){
//				tabla.setNumRegistros(rs.getInt(1));
//			}
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return tabla;
//	}
	
	private void getColumnas(Tabla tabla){
		this.columnaView.getItems().clear();
		StringBuilder mensaje = new StringBuilder();
		Connection con = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			DatabaseMetaData dmd = con.getMetaData();
			if(this.getCatalogoData().size()==0){
				rs = dmd.getColumns(null,this.esquemaView.getSelectionModel().getSelectedItem().getNombre(), tabla.getNombre(), "%");
			} else {
				rs = dmd.getColumns(this.catalogoView.getSelectionModel().getSelectedItem().getNombre(), 
						this.esquemaView.getSelectionModel().getSelectedItem().getNombre(), tabla.getNombre(), "%");	
			}
			
			while (rs.next()) {
				Columna c = new Columna(rs.getString("COLUMN_NAME"), rs.getString("TYPE_NAME") + " (" + rs.getInt("COLUMN_SIZE") +")");
				this.columnaView.getItems().add(c);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
			mensaje.append("\n"+Utils.getErrorText(e));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			mensaje.append("\n"+Utils.getErrorText(e));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			mensaje.append("\n"+Utils.getErrorText(e));
		} catch (SQLException e) {
			e.printStackTrace();
			mensaje.append("\n"+Utils.getErrorText(e));
		} finally {
			this.cierraRecursos(con, null, rs);
		}
		this.erroresArea.setText(mensaje.toString());
	}
	
	private Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		Connection con = null;
		Class.forName(this.driverTextField.getText()).newInstance();
		con = DriverManager.getConnection(this.urlTextField.getText(), this.userTextField.getText(), this.passwordTextField.getText());
		return con;
	}
	
	private void cierraRecursos(Connection con, PreparedStatement ps, ResultSet rs){
		StringBuilder mensaje = new StringBuilder();
		try {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			mensaje.append("\n"+Utils.getErrorText(e));
		}
		this.erroresArea.setText(mensaje.toString());
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

	public ObservableList<Tabla> getTablaData() {
		return tablaData;
	}

	public void setTablaData(ObservableList<Tabla> tablaData) {
		this.tablaData = tablaData;
	}
	
	public ObservableList<Esquema> getEsquemaData() {
		return esquemaData;
	}

	public void setEsquemaData(ObservableList<Esquema> esquemaData) {
		this.esquemaData = esquemaData;
	}

	public ObservableList<Catalogo> getCatalogoData() {
		return catalogoData;
	}

	public void setCatalogoData(ObservableList<Catalogo> catalogoData) {
		this.catalogoData = catalogoData;
	}
	public ObservableList<Columna> getColumnaData() {
		return columnaData;
	}

	public void setColumnaData(ObservableList<Columna> columnaData) {
		this.columnaData = columnaData;
	}
	public ObservableList<Procedure> getProcedureData() {
		return procedureData;
	}

	public void setProcedureData(ObservableList<Procedure> procedureData) {
		this.procedureData = procedureData;
	}

	public ObservableList<Funcion> getFuncionData() {
		return funcionData;
	}

	public void setFuncionData(ObservableList<Funcion> funcionData) {
		this.funcionData = funcionData;
	}
	
}
