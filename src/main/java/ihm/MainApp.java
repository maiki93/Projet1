package main.java.ihm;

import java.util.List;

import javafx.application.Application;
import main.java.Admin;
import main.java.AdminDAO;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
	public static void main(String[] args) {
		AdminDAO daoadmin = new AdminDAO();
		daoadmin.readTxtFichier();
		List<Admin> adminList=daoadmin.getAdminList();
		for (Admin admin : adminList) {
			System.out.println(admin.getNom()+admin.getMotDePasse());
		}
		Admin adminRoot = new Admin("root", "roote");
		daoadmin.trytoFind(adminRoot);
		
		
		
		
		launch(args);
	}
	
	@Override
	public void start(Stage stage)throws Exception{
		MainPannel root = new MainPannel();
		stage.setTitle("Annuaire Eql");
		Scene scene = new Scene(root);
		scene.getStylesheets().add("main/java/ihm/style.css");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}
	

}
