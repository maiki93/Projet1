package main.java.ihm;

import java.io.File;
import java.net.MalformedURLException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.java.Admin;
import main.java.AdminDAO;

public class TopPannel extends GridPane {
	private HBox topBox;
	private HBox btnBox;
	private VBox adminBox;
	private Image img;
	private Label utilisateur;
	private Label role;
	private Button adminBtn;
	private Label nomlabel;
	private TextField nomtxt;
	private Label passwordlabel;
	private TextField passwordtxt;
	private Button connectBtn;
	private Button closebtn;
	private Boolean first = true;

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

		utilisateur = new Label("utilisateur: Invité");
		role = new Label("Permission: Utilisateur");
		utilisateur.setId("utilisateur");
		role.setId("role");

		adminBtn = new Button("Admin");
		adminBtn.setId("adminBtn");
		adminBtn.setPrefSize(150, 50);
		adminBtn.setStyle("-fx-background-color:#873D48");

/** Mege conflicts MIC, MIC tests here		
		adminBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//MainPannel root = (MainPannel) getScene().getRoot();
				FormAdminPannel fAP = 
						(FormAdminPannel) ((MainPannel)getScene().getRoot()).getLeft();
				
				if( fAP.isAdmin())
					fAP.setAdmin(false);
				else
					fAP.setAdmin(true);
				// force refresh, but not good !!
				//getScene().getWindow().setWidth(getScene().getWidth() + 0.001);
				/*
				if( fAP.isVisible() ) 
					fAP.setVisible(false);
				else
					fAP.setVisible(true);
				*/
			}
		});
*/
		topBox.getChildren().addAll(utilisateur, role, adminBtn);

		add(topBox, 1, 0);

		setPadding(new Insets(10));
		setStyle("-fx-background-color:#E8EBE4");

		nomlabel = new Label("Nom: ");
		nomlabel.setId("textConnect");
		nomtxt = new TextField();
		nomtxt.setId("textConnect");
		passwordlabel = new Label("Mot de passe: ");
		passwordlabel.setId("textConnect");
		passwordtxt = new TextField();
		passwordtxt.setId("textConnect");

		connectBtn = new Button("Connexion");
		closebtn = new Button("Annuler");

		btnBox = new HBox(10);
		btnBox.setId("btnBox");
		btnBox.getChildren().addAll(connectBtn, closebtn);

		adminBox = new VBox(10);
		adminBox.setId("adminBox");
		adminBox.getChildren().addAll(nomlabel, nomtxt, passwordlabel, passwordtxt, btnBox);

		adminBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				MainPannel root = (MainPannel) getScene().getRoot();
				if (first == true) {
					root.getTablePannel().getChildren().add(adminBox);
					first = false;
				}
				MotionBlur mb = new MotionBlur();
				mb.setRadius(5.0f);
				mb.setAngle(5.0f);
				root.getTablePannel().getTableView().setEffect(mb);
				adminBox.setVisible(true);
			}
		});
		connectBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String name = nomtxt.getText();
				String password = passwordtxt.getText();
				AdminDAO admindao = new AdminDAO();
				Admin admin = new Admin(name, password);

				if (admindao.trytoFind(admin)) {
					MotionBlur mb = new MotionBlur();
					mb.setRadius(0.0f);
					mb.setAngle(0.0f);
					MainPannel root = (MainPannel) getScene().getRoot();
					root.getTablePannel().getTableView().setEffect(mb);
					adminBox.setVisible(false);
					adminBtn.setVisible(false);
					utilisateur.setText("Utilisateur: " + admin.getNom());
					role.setText("Permission: Administrateur");
				}
			}
		});
		closebtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				MotionBlur mb = new MotionBlur();
				mb.setRadius(0.0f);
				mb.setAngle(0.0f);
				MainPannel root = (MainPannel) getScene().getRoot();
				root.getTablePannel().getTableView().setEffect(mb);
				adminBox.setVisible(false);
			}
		});
	}

}
