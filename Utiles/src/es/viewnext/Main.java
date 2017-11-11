package es.viewnext;

import java.io.IOException;

import es.viewnext.login.LoginController;
import es.viewnext.modelo.User;
import es.viewnext.principal.RootController;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

	private Stage primaryStage;
	private LoginController loginController;
	private RootController rootController;
	private BorderPane rootLayout;
	private User user;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		System.out.println("Se inicia la aplicación");
//		setUserAgentStylesheet(STYLESHEET_CASPIAN);
		try {
			this.user = new User();
			this.setPrimaryStage(primaryStage);
			this.getPrimaryStage().getIcons().add(new Image("file:resources/imagenes/viewnext.png"));

			if (this.initLoginLayot()) {
				this.initRootLayout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean initLoginLayot() {
		boolean ok = false;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("login/LoginControllerLayout.fxml"));
			AnchorPane loginLayout = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Login");
			dialogStage.getIcons().add(new Image("file:resources/imagenes/viewnext.png"));
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.resizableProperty().setValue(Boolean.FALSE);
			dialogStage.initOwner(this.primaryStage);
			Scene scene = new Scene(loginLayout);
			dialogStage.setScene(scene);

			FadeTransition ft = new FadeTransition(Duration.millis(2000), loginLayout);
			ft.setFromValue(0.0);
			ft.setToValue(1.0);
			ft.play();

			this.loginController = loader.getController();
			this.loginController.setDialogStage(dialogStage);
			this.loginController.setMain(this);

			// Evitamos que cierren mediante la X
			// dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			// @Override
			// public void handle(WindowEvent event) {
			// event.consume();
			// }
			// });

			dialogStage.showAndWait();

			ok = this.loginController.isOkClicked();
			if (ok) {

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ok;
	}

	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("principal/RootControllerLayout.fxml"));
			this.rootLayout = (BorderPane) loader.load();

			Scene scene = new Scene(this.rootLayout);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Utiles");
			FadeTransition ft = new FadeTransition(Duration.millis(2000), this.rootLayout);
			ft.setFromValue(0.0);
			ft.setToValue(1.0);
			ft.play();

			this.rootController = loader.getController();
			this.rootController.setMain(this);

			this.primaryStage.show();
			// Evitamos que cierren mediante la X
			// this.primaryStage.setOnCloseRequest(new
			// EventHandler<WindowEvent>() {
			// @Override
			// public void handle(WindowEvent event) {
			// event.consume();
			// }
			// });

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
