package es.viewnext.login;

import es.viewnext.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
	
	@FXML
    private TextField usuarioField;
    @FXML
    private PasswordField passwordField;
    
    private Main main;
    private boolean okClicked = false;
    private Stage dialogStage;
    
    public LoginController(){
    	super();
    }
    
    @FXML
	private void initialize() {
    	this.usuarioField.setText("admin");
    	this.passwordField.setText("admin");
    }
    
    @FXML
    private void handleOk() {
        if (this.isInputValid()) {
        	this.okClicked = true;
            this.dialogStage.close();
        }
    }
    
    @FXML
    private void handleCancel() {
    	this.okClicked = false;
    	this.dialogStage.close();
    }
    
    private boolean isInputValid() {
        String errorMessage = "";
        if (this.usuarioField.getText() == null || this.usuarioField.getText().trim().length() == 0 || !"admin".equals(this.usuarioField.getText().trim())) {
       		errorMessage += "Usuario no válido!\n";
        }
        if (this.passwordField.getText() == null || this.passwordField.getText().trim().length() == 0 || !"admin".equals(this.passwordField.getText().trim())) {
           	errorMessage += "Password no válido!\n"; 
        }
        
        if (errorMessage.length() == 0) {
            this.getMain().getUser().setUsuario(this.usuarioField.getText().trim());
            return true;
        } else {
        	Alert alert = new Alert(AlertType.INFORMATION);
	    	alert.setTitle("Valores inválidos");
	    	alert.setHeaderText("Por favor corrija los valores");
	    	alert.setContentText(errorMessage);
	    	alert.showAndWait();
            return false;
        }
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
	}
}
