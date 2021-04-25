package main.java.ihm;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class FormPannel extends GridPane {

	private Button addNewStagiaireBtn;
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
		// test, include at the bottom because of GridPane
		addNewStagiaireBtn = new Button("Nouv. stag.");
		addNewStagiaireBtn.setId("newStagBtn");
		addNewStagiaireBtn.setPrefSize(150, 50);
		addNewStagiaireBtn.setStyle("-fx-background-color:#2589BD");
		
		rechercheBtn = new Button("Recherche");
		rechercheBtn.setId("rechercheBtn");
		rechercheBtn.setPrefSize(150, 50);
		rechercheBtn.setStyle("-fx-background-color:#2589BD");

		rechercheTxt = new TextField();
		rechercheTxt.setId("rechercheTxt");

		rechercheCb = new ChoiceBox<>();
		rechercheCb.setId("rechercheCb");
		rechercheCb.getItems().addAll("Tout", "Nom", "Prénom", "Département", "Formation", "Année");
		rechercheCb.getSelectionModel().select("Tout");
		rechercheCb.setStyle("-fx-alignment: CENTER");

		boxRecherche = new VBox(100);
		boxRecherche.setId("boxRecherche");
		boxRecherche.getChildren().addAll( rechercheBtn, rechercheTxt, rechercheCb);
		add(boxRecherche, 1, 1);

		infosLabel = new Label("Infos");
		infosLabel.setId("infosLabel");
		totalEtudiantLabel = new Label("Total d'étudiants: 1300");
		totalEtudiantLabel.setId("totalEtudiantLabel");
		elementTrouverLabel = new Label("Eléments filtrer: 1300");
		elementTrouverLabel.setId("elementTrouverLabel");
		typeRechercheLabel = new Label("Type de recherche: Tout");
		typeRechercheLabel.setId("typeRechercheLabel");

		boxinfos = new VBox(100);
		boxinfos.setId("boxinfos");
		boxinfos.getChildren().addAll(infosLabel, totalEtudiantLabel, elementTrouverLabel);
		add(boxinfos, 1, 2);

		exportPDFBtn = new Button("Export PDF");
		exportPDFBtn.setId("exportPDFBtn");
		exportPDFBtn.setPrefSize(150, 50);
		exportPDFBtn.setStyle("-fx-background-color:#2589BD");
		add(exportPDFBtn, 1, 3);
		
		add(addNewStagiaireBtn,1,4);

		setVgap(20);
		setPadding(new Insets(5));
		setStyle("-fx-background-color:#E8EBE4");
		
		addNewStagiaireBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// Formulaire doit apparaitre
				MainPannel root = (MainPannel) getScene().getRoot();
				if( root.getFormAdmin().isVisible()) {
					root.getFormAdmin().setVisible(false);
					addNewStagiaireBtn.setText("Nouv.Stag.");
				} else {
					root.getFormAdmin().setVisible(true);
					addNewStagiaireBtn.setText("Fermer Form");
				}
			}
		});

	}

}
