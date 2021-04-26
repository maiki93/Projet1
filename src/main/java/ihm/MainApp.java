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

		Stagiaire stagiaire = new Stagiaire("alonzo", "paul", "94", "eql ai109", 2020);
		// StagiaireDAO dao = new StagiaireDAO();
		ArbreBinaire ab = new ArbreBinaire();
//		try {
//			System.out.println("test de text: " + ab.addStagiaire(stagiaire));
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	
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






