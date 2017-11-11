package es.viewnext.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import es.viewnext.modelo.Infraestructuras;
import es.viewnext.modelo.Server;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class UtilsData {

	public static ObservableList<Server> loadServersDataFromFile() {
		System.out.println("loadServersDataFromFile");
		File fileInfraestructura = getFilePath(true);
		ObservableList<Server> listaServers = FXCollections.observableArrayList();
		try {
			JAXBContext context = JAXBContext.newInstance(Infraestructuras.class);
			Unmarshaller um = context.createUnmarshaller();
			UtilsFiles.setVisible(fileInfraestructura.toPath());
			Infraestructuras wrapper = (Infraestructuras) um.unmarshal(fileInfraestructura);
			
			if(wrapper.getServerList() != null && wrapper.getServerList().size()>0){
				listaServers.addAll(wrapper.getServerList());
			}
			UtilsFiles.setHidden(fileInfraestructura.toPath());
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Error");
			alert.setHeaderText("No se puede lee el fichero:\n" + fileInfraestructura.getPath());
			alert.setContentText("");

			alert.showAndWait();
		}
		return listaServers;
	}
	
	public static File getFilePath(boolean create) {
		File file = null;
		String filePath = "";
		filePath = getHomeDirectoryString() + File.separator + Constants.FILE_INFRAESTRUCTURA;
		if (!Files.exists(Paths.get(filePath)) && create) {
			UtilsFiles.createFile(filePath);
		}
		file = new File(filePath);
		return file;
	}
	
	public static String getHomeDirectoryString(){
		return getHomeDirectoryPath().toString() + File.separator;
	}
	
	public static Path getHomeDirectoryPath(){
		String userDirectoryString = System.getProperty("user.home");
		Path dir = Paths.get(userDirectoryString +  File.separator + Constants.HOME_DIRECTORY + File.separator);
		return dir;
	}
}
