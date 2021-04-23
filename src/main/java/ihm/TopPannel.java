package main.java.ihm;

import java.io.File;
import java.net.MalformedURLException;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class TopPannel extends GridPane {
	private HBox topBox;
	private Image img;
	private Label utilisateur;
	private Label role;
	private Button adminBtn;

	public TopPannel() {
		super();

		
		topBox = new HBox(100);
		topBox.setId("topBox");

		File file = new File(System.getProperty("user.dir") + "/src/main/resources/logo.png");
		try {
			String localUrl = file.toURI().toURL().toString();
			img = new Image(localUrl);
			topBox.getChildren().addAll(new ImageView(img));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		utilisateur = new Label("utilisateur : Invité");
		utilisateur.setId("utilisateur");
		role = new Label("Permission: Utilisateur");
		role.setId("role");

		adminBtn = new Button("Admin");
		adminBtn.setId("adminBtn");
		adminBtn.setPrefSize(150, 50);
		adminBtn.setStyle("-fx-background-color:#873D48");

		topBox.getChildren().addAll(utilisateur, role, adminBtn);

		add(topBox, 1, 0);

		setPadding(new Insets(10));
		setStyle("-fx-background-color:#E8EBE4");
		System.out.println(this.getWidth());
	}

}
