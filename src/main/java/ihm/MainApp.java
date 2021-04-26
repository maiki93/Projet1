package main.java.ihm;

import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import main.java.Admin;
import main.java.AdminDAO;
import main.java.ArbreBinaire;
import main.java.NodeStagiaire;
import main.java.Stagiaire;
import main.java.StagiaireDAO;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		MainPannel root = new MainPannel();
		stage.setTitle("Annuaire Eql");
		Scene scene = new Scene(root);
		System.out.println(getClass());
		scene.getStylesheets().add("/main/java/ihm/style.css");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}

}






