package main.java.ihm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage)throws Exception{
		MainPannel root = new MainPannel();
		stage.setTitle("Annuaire Eql");
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
		
		
	}
	

}
