package main.java.ihm;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
	public static void main(String[] args) {
		
	

		launch(args);

	}

	@Override
	public void start(Stage stage) throws Exception {
		RootPanel root = new RootPanel();
		stage.setTitle("Annuaire Eql");
		Scene scene = new Scene(root);
		// TODO
		// scene.getStylesheets().add(System.getProperty("user.dir")+"/src/main/java/ihm/style.css");
		scene.getStylesheets().add("main/java/ihm/style.css");
		System.out.println(getClass());
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}

}

