package main.java.ihm;

import java.io.File;
import java.net.MalformedURLException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
//import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import main.java.Admin;
import main.java.AdminDAO;

public class TopPanel extends HBox {
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
	private Button documentationBtn;
	private Boolean first = true;
	private Boolean docopen = true;
	private WebView browser;
	private WebEngine webEngine;

	public TopPanel() {
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

		utilisateur = new Label("Utilisateur: Invité");
		utilisateur.setId("utilisateur");
		utilisateur.setFont(new Font("Delicious",16));
		role = new Label("Permission: Utilisateur");
		role.setId("role");
		role.setFont(new Font("Delicious",16));


		adminBtn = new Button("Admin");
		adminBtn.setFont(Font.font("Lato",14));
		adminBtn.setPrefSize(150, 25);
		adminBtn.setId("adminBtn");


		documentationBtn = new Button("Documentation");
		adminBtn.setFont(new Font("Lato",14));
		documentationBtn.setPrefSize(150, 25);
		documentationBtn.setId("documentationBtn");


		//adminBox
		nomlabel = new Label("Nom: ");
		nomlabel.setId("textConnect");
		nomtxt = new TextField();
		passwordlabel = new Label("Mot de passe: ");
		passwordlabel.setId("textConnect");
		passwordtxt = new TextField();
		

		connectBtn = new Button("Connexion");
		connectBtn.setId("btnConnect");
		closebtn = new Button("Annuler");
		closebtn.setId("btnConnect");

		btnBox = new HBox(10);
		btnBox.setId("btnBox");
		btnBox.getChildren().addAll(connectBtn, closebtn);

		adminBox = new VBox(10);
		adminBox.setId("adminBox");
		adminBox.getChildren().addAll(nomlabel, nomtxt, passwordlabel, passwordtxt, btnBox);
		
		setPadding(new Insets(10));
		setId("topPannel");
		getChildren().addAll(topBox, utilisateur, role, adminBtn, documentationBtn);
		
		browser = new WebView();
		webEngine = browser.getEngine();
		webEngine.load("https://gracious-morse-4690f0.netlify.app/#consultation");

		adminBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				RootPanel root = (RootPanel) getScene().getRoot();

				// si on est déjà admin on redevient user
				if (root.hasAdminRights()) {
					root.setAdminRights(false);
					utilisateur.setText("Utilisateur: Invité");
					role.setText("Permission: Utilisateur");
					adminBtn.setText("Admin");
					return;
				}
				if (first == true) {
					root.getTablePannel().getChildren().add(adminBox);
					first = false;
				}
				MotionBlur mb = new MotionBlur();
				mb.setRadius(5.0f);
				mb.setAngle(5.0f);
				root.getTablePannel().getTableView().setEffect(mb);
				// root.setEffect(mb); this box is also blured...
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
					RootPanel root = (RootPanel) getScene().getRoot();
					root.getTablePannel().getTableView().setEffect(mb);

					utilisateur.setText("Utilisateur: " + admin.getNom());
					role.setText("Permission: Administrateur");
					adminBtn.setText("User");
					// if true open
					root.setAdminRights(true);
					// close the box if correct, better to remove it ? done by first
					adminBox.setVisible(false);
					// root.getTablePannel().getChildren().remove(adminBox);
					// change stroke color around the FormAdmin if admin ?
				}
			}
		});
		closebtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				MotionBlur mb = new MotionBlur();
				mb.setRadius(0.0f);
				mb.setAngle(0.0f);
				RootPanel root = (RootPanel) getScene().getRoot();
				root.getTablePannel().getTableView().setEffect(mb);
				adminBox.setVisible(false);
			}
		});
		documentationBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				RootPanel root = (RootPanel) getScene().getRoot();
				if (docopen) {
					System.out.println(docopen);
					docopen = false;
					
					// mettre la webview
					root.setCenter(browser);
				} else {
					docopen = true;
					root.setCenter(root.getTablePannel());

				}
			}
		});

	}

}
