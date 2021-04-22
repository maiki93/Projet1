package main.java.ihm;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class FormPannel extends GridPane {

	private Button adminBtn;
	private Button rechercheBtn;
	private TextField rechercheTxt;
	private ChoiceBox<String> rechercheCb;
	private Label infosLabel;
	private Label totalEtudiantLabel;
	private Label elementTrouverLabel;
	private Label typeRechercheLabel;
	private Button exportPDFBtn;
	private VBox boxRecherche;
	private VBox boxinfos;

	public FormPannel() {
		super();
		adminBtn = new Button("Admin");
		adminBtn.setPrefSize(150, 50);
		adminBtn.setStyle("-fx-background-color:#873D48");

		rechercheBtn = new Button("Recherche");
		rechercheBtn.setPrefSize(150, 50);
		rechercheBtn.setStyle("-fx-background-color:#2589BD");

		rechercheTxt = new TextField();

		rechercheCb = new ChoiceBox<>();
		rechercheCb.getItems().addAll("Tout", "Nom", "Prénom", "Département", "Formation", "Année");
		rechercheCb.getSelectionModel().select("Tout");
		rechercheCb.setStyle("-fx-alignment: CENTER");

		boxRecherche = new VBox(100);
		boxRecherche.getChildren().addAll(adminBtn, rechercheBtn, rechercheTxt, rechercheCb);
		add(boxRecherche, 1, 0);

		infosLabel = new Label("Infos");
		totalEtudiantLabel = new Label("Total d'étudiants: 1300");
		elementTrouverLabel = new Label("Eléments filtrer: 1300");
		typeRechercheLabel = new Label("Type de recherche: Tout");

		boxinfos = new VBox(100);
		boxinfos.getChildren().addAll(infosLabel, totalEtudiantLabel, elementTrouverLabel);
		add(boxinfos, 1, 2);

		

		exportPDFBtn = new Button("Export PDF");
		exportPDFBtn.setPrefSize(150, 50);
		exportPDFBtn.setStyle("-fx-background-color:#2589BD");
		add(exportPDFBtn, 1, 3);
		
		setVgap(10);
		setPadding(new Insets(5));
		setStyle("-fx-background-color:#E8EBE4");

	}

}
