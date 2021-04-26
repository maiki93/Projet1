package main.java.ihm;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import main.java.Stagiaire;
import main.java.StagiaireDAO;

public class RecherchePanel extends GridPane {

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

	public RecherchePanel() {
		super();
		
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

		// Info coming from the result of the search ? or comming from the observableList
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
		
		addNewStagiaireBtn = new Button("Nouv. stag.");
		addNewStagiaireBtn.setId("newStagBtn");
		addNewStagiaireBtn.setPrefSize(150, 50);
		addNewStagiaireBtn.setStyle("-fx-background-color:#2589BD");
		add(addNewStagiaireBtn,1,4);

		setVgap(20);
		setPadding(new Insets(5));
		setStyle("-fx-background-color:#E8EBE4");
		
		rechercheBtn.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				boolean isGlobal = false;
				Stagiaire stagiaireTemplate = createTemplateForSearch();
				// is recherche specific ? or global, depends on the ComboxBox "Tout"
				if(rechercheCb.getSelectionModel().getSelectedItem() == "Tout" ) {
					//nom = text; 
					isGlobal = true;
				}
				
				// voir UML
				RootPanel root = (RootPanel)getScene().getRoot();
				StagiaireDAO stageDao = root.getStagiaireDao();
				List<Stagiaire> listFiltree = stageDao.rechercheStagiaire(stagiaireTemplate, isGlobal);
				System.out.println("listFiltree, size:" + listFiltree.size());
				// update Main Panel
				root.setNewRecherche( listFiltree);
				// reset la recherche
				
			}
			
		});
		
		// some could be in RootPanel : OpenFormulairePanel ? need to change text here
		addNewStagiaireBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// Formulaire doit apparaitre
				RootPanel root = (RootPanel) getScene().getRoot();
				if( root.getFormulairePanel().isVisible()) {
					root.getFormulairePanel().setVisible(false);
					addNewStagiaireBtn.setText("Nouv.Stag.");
				} else {
					root.getFormulairePanel().setVisible(true);
					addNewStagiaireBtn.setText("Fermer Form");
				}
			}
		});

	}
	
	private Stagiaire createTemplateForSearch() {
		// Read the text fields available
		String text = rechercheTxt.getText();
		String textCb = rechercheCb.getSelectionModel().getSelectedItem();
		System.out.println("createTemplate "+ text + " " + textCb);
		String nom = "";
		String prenom="";
		String departement="";
		String formation="";
		int annee=0;
		if(textCb == "Tout" ) {
			nom = text;
		}
		Stagiaire tplt = new Stagiaire(nom, prenom, departement, formation, annee );
		return tplt;
	}

}
