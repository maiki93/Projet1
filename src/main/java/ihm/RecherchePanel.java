package main.java.ihm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
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
	private VBox boxbtnpdf;

	private VBox boxCriteriaRecherche;
	private boolean isRechercheGlobal;
	private Map<String, String> mapCritere = new HashMap<>();

	public static final String FONT = "main/resources/Poppins-Regular.ttf";

	public RecherchePanel() {
		super();

		rechercheBtn = new Button("Recherche");
		rechercheBtn.setId("rechercheBtn");
		rechercheBtn.setPrefSize(150, 25);

		rechercheTxt = new TextField();
		rechercheTxt.setId("rechercheTxt");

		rechercheCb = new ChoiceBox<>();
		rechercheCb.getItems().addAll("Tout", "Nom", "Prénom", "Département", "Formation", "Année");
		rechercheCb.getSelectionModel().select("Nom");
		rechercheCb.setId("rechercheCb");

		boxRecherche = new VBox(150);
		boxRecherche.setId("boxRecherche");

		boxRecherche.getChildren().addAll(rechercheTxt, rechercheCb, rechercheBtn);
		add(boxRecherche, 1, 1);

		// selection de la recheche, contains the selected criteria
		boxCriteriaRecherche = new VBox(10);
		add(boxCriteriaRecherche, 1, 2);

		// Info coming from the result of the search ? or comming from the
		// observableList ?

		
		infosLabel = new Label("Infos");
		infosLabel.setId("infosLabel");
		totalEtudiantLabel = new Label("Total d'étudiants: ");
		totalEtudiantLabel.setId("totalEtudiantLabel");
		elementTrouverLabel = new Label("Eléments filtrer: non");
		elementTrouverLabel.setId("elementTrouverLabel");
		typeRechercheLabel = new Label("Type de recherche: Tout");
		typeRechercheLabel.setId("typeRechercheLabel");

		boxinfos = new VBox(10);
		boxinfos.setId("boxinfos");
		boxinfos.getChildren().addAll(infosLabel, totalEtudiantLabel, elementTrouverLabel);
		add(boxinfos, 1, 3);

		exportPDFBtn = new Button("Export PDF");
		exportPDFBtn.setId("exportPDFBtn");
		exportPDFBtn.setPrefSize(150, 25);

		addNewStagiaireBtn = new Button("Nouv. stag.");
		addNewStagiaireBtn.setId("newStagBtn");
		addNewStagiaireBtn.setPrefSize(150, 25);

		boxbtnpdf = new VBox(10);
		boxbtnpdf.setId("boxbtnpdf");
		boxbtnpdf.getChildren().addAll(exportPDFBtn, addNewStagiaireBtn);
		add(boxbtnpdf, 1, 4);

		setId("boxright");
		setPadding(new Insets(5));

		exportPDFBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				// donner le choix du nom

				RootPanel root = (RootPanel) getScene().getRoot();
				File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());
				List<Stagiaire> ListeStagiaire = root.getObservable();

				String fileName = selectedDirectory.toString() + "\\export_" + new Date().getTime() + ".pdf";
				System.out.println(fileName);
				Document document = new Document();
				try {
					PdfWriter.getInstance(document, new FileOutputStream(fileName));
					document.open();
					CMYKColor bColor = new CMYKColor(84, 36, 0, 5);
					BaseFont bf = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
					Font f = new Font(bf, 22, Font.NORMAL, bColor);
					Paragraph para = new Paragraph("Annuaire EQL ", f);
					para.setSpacingAfter(30);
					para.setAlignment(Element.ALIGN_CENTER);
					PdfPTable table = new PdfPTable(5);
					table.addCell("Nom");
					table.addCell("Prénom");
					table.addCell("Departement");
					table.addCell("Formation");
					table.addCell("Année");

					for (Stagiaire stagiaire : ListeStagiaire) {
						table.addCell(stagiaire.getNom());
						table.addCell(stagiaire.getPrenom());
						table.addCell(stagiaire.getDepartement());
						table.addCell(stagiaire.getFormation());
						table.addCell(Integer.toString(stagiaire.getAnnee()));
					}
					document.add(para);
					document.add(table);
					document.close();
				} catch (FileNotFoundException | DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		rechercheBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// set isRechercheGlobal si "Tout" est présent dans la liste de critère
				Stagiaire stagiaireTemplate = createTemplateForSearch();

				// voir UML
				RootPanel root = (RootPanel) getScene().getRoot();
				StagiaireDAO stageDao = root.getStagiaireDao();
				List<Stagiaire> listAll = root.getObservable();
				List<Stagiaire> listFiltree = stageDao.rechercheStagiaire(stagiaireTemplate, isRechercheGlobal);
				System.out.println("listFiltree, size:" + listFiltree.size());
				elementTrouverLabel.setText("Eléments filtrer: "+listFiltree.size());
				totalEtudiantLabel.setText("Eléments total: "+listAll.size());
				// update other panels
				root.setNewRecherche(listFiltree);
				// reset la recherche

				clearCritereRecherche();
			}
		});

		// some could be in RootPanel : OpenFormulairePanel ? need to change text here
		addNewStagiaireBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// Formulaire doit apparaitre
				RootPanel root = (RootPanel) getScene().getRoot();
				if (root.getFormulairePanel().isVisible()) {
					root.getFormulairePanel().setVisible(false);
					addNewStagiaireBtn.setText("Nouv.Stag.");
				} else {
					root.getFormulairePanel().setVisible(true);
					addNewStagiaireBtn.setText("Fermer Form");
				}
			}
		});

		rechercheTxt.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				System.out.println("rechercgeTxt focus " + observable);
				System.out.println("values: " + oldValue + "," + newValue);
				System.out.println("Recherche Text: " + rechercheTxt.getText());
				// we lost the focus
				if (newValue == false) {
					System.out.println("New value");
					String value = rechercheTxt.getText();
					String critere = rechercheCb.getSelectionModel().getSelectedItem();
					System.out.println("critere, value " + critere + "," + value);
					addCritere(value, critere);
					// we gain the focus, nothing to do
				} else {
					System.out.println("Old Value");
				}
			}
		});

	}

	private void addCritere(String value, String critere) {
		mapCritere.put(critere, value);
		Label lblCritere = new Label(critere + ":" + value);
		boxCriteriaRecherche.getChildren().add(lblCritere);

	}

	private Stagiaire createTemplateForSearch() {
		// Read the text fields available
		// String text = rechercheTxt.getText();
		// String textCb = rechercheCb.getSelectionModel().getSelectedItem();
		// System.out.println("createTemplate "+ text + " " + textCb);
		String nom = (mapCritere.get("Nom") != null) ? mapCritere.get("Nom") : "";
		String prenom = (mapCritere.get("Prenom") != null) ? mapCritere.get("Prenom") : "";
		String departement = (mapCritere.get("Departement") != null) ? mapCritere.get("Departement") : "";
		String formation = (mapCritere.get("Formation") != null) ? mapCritere.get("Departement") : "";
		String anneeStr = (mapCritere.get("annee") != null) ? mapCritere.get("Departement") : "";
		int annee;
		// System.out.println("Nom: " + nom);
		// System.out.println("Prenom: " + prenom);

		// test for global
		String demandeGlobale = (mapCritere.get("Tout") != null) ? mapCritere.get("Tout") : "";
		if (!demandeGlobale.isEmpty()) {
			System.out.println("demande globale");
			isRechercheGlobal = true;
			nom = demandeGlobale;
		}

		try {
			annee = Integer.parseInt(anneeStr);
		} catch (NumberFormatException e) {
			System.out.println("Number exception avec année, pas grave");
			annee = 0;
		}
		Stagiaire tplt = new Stagiaire(nom, prenom, departement, formation, annee);
		return tplt;
	}

	private void clearCritereRecherche() {
		rechercheTxt.clear();
		mapCritere.clear();
		boxCriteriaRecherche.getChildren().clear();
		rechercheCb.getSelectionModel().select("Nom");
		isRechercheGlobal = false;
	}

	public Label getTotalEtudiantLabel() {
		return totalEtudiantLabel;
	}

	public void setTotalEtudiantLabel(Label totalEtudiantLabel) {
		this.totalEtudiantLabel = totalEtudiantLabel;
	}

}
