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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import main.java.Stagiaire;
import main.java.StagiaireDAO;

public class RecherchePanel extends GridPane implements EventHandler<ActionEvent> {

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
	public int idBoxCritere;

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

		boxRecherche.getChildren().addAll(rechercheCb, rechercheTxt, rechercheBtn);
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

		addNewStagiaireBtn = new Button("Nouv.Stag.");
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
				RootPanel root = (RootPanel) getScene().getRoot();
				File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());

				if (selectedDirectory == null) {
					// No Directory selected
				} else {
					System.out.println(selectedDirectory.getAbsolutePath());
					List<Stagiaire> ListeStagiaire = root.getObservable();
					root.getFormulairePanel().setVisible(false);

					// https://stackoverflow.com/questions/28268767/pressing-cancel-after-showing-filechooser-causes-nullpointerexception
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
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		rechercheBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				rechercheBtn.requestFocus();
				arg0.consume();

				// set isRechercheGlobal si "Tout" est présent dans la liste de critère
				Stagiaire stagiaireTemplate = createTemplateForSearch();
				// voir UML
				RootPanel root = (RootPanel) getScene().getRoot();
				StagiaireDAO stageDao = root.getStagiaireDao();
				List<Stagiaire> listFiltree = stageDao.rechercheStagiaire(stagiaireTemplate, isRechercheGlobal);
				System.out.println("listFiltree, size:" + listFiltree.size());
				elementTrouverLabel.setText("Eléments filtrer: " + listFiltree.size());
				// update other panels
				root.setNewRecherche(listFiltree);
				// reset la recherche
				System.out.println("____________test-__________________");
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
		Button supprCritereBtn;
		idBoxCritere++;
		HBox hb = new HBox(60);
		hb.getStyleClass().add("hboxCritere");
		supprCritereBtn = new Button("X");
		supprCritereBtn.setId(critere);
		supprCritereBtn.setPrefSize(5, 5);
		supprCritereBtn.setOnAction(this);
		//HBox hb = new HBox(50);
		//supprCritereBtn = new Button("X");
		//supprCritereBtn.setId("X");
		hb.setId("boxcritere" + idBoxCritere);
		mapCritere.put(critere, value);
		Label lblCritere = new Label(critere + " : " + value);
		lblCritere.setId("crit");
		hb.getChildren().add(lblCritere);
		hb.getChildren().add(supprCritereBtn);
		boxCriteriaRecherche.getChildren().add(hb);
	}

	private Stagiaire createTemplateForSearch() {
		String nom = (mapCritere.get("Nom") != null) ? mapCritere.get("Nom") : "";
		String prenom = (mapCritere.get("Prénom") != null) ? mapCritere.get("Prénom") : "";
		String departement = (mapCritere.get("Département") != null) ? mapCritere.get("Département") : "";
		String formation = (mapCritere.get("Formation") != null) ? mapCritere.get("Formation") : "";
		System.out.println("formation : " + formation);
		String anneeStr = (mapCritere.get("Année") != null) ? mapCritere.get("Année") : "";
		int annee;
		// test for global
		String demandeGlobale = (mapCritere.get("Tout") != null) ? mapCritere.get("Tout") : "";
		if (!demandeGlobale.isEmpty()) {
			System.out.println("demande globale : " + demandeGlobale);
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
		idBoxCritere = 0;
	}

	public Label getTotalEtudiantLabel() {
		return totalEtudiantLabel;
	}

	public void setTotalEtudiantLabel(Label totalEtudiantLabel) {
		this.totalEtudiantLabel = totalEtudiantLabel;
	}

	@Override
	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
		Button btnEvent = (Button)event.getSource();
		String idBt = btnEvent.getId();
		System.out.println("Bouton DEL crit id: " + idBt);
		mapCritere.remove(idBt);
		List<Node> child = boxCriteriaRecherche.getChildren();
		HBox hboxToDelete = null;
		if (child.size() > 0) {
			for (Node node : child) {
				if( node instanceof HBox ) {
					HBox hb = (HBox) node;
					for(Node node2 : hb.getChildren()) {
						if( node2 instanceof Button ) {
							String id = node2.getId();
							System.out.println("getID" + id + node2.getClass());
							if (id.compareTo(idBt) == 0) {
								hboxToDelete = (HBox) node;
								//return;
							}
						}
					}
				}
			}	
		}
		boxCriteriaRecherche.getChildren().remove(hboxToDelete);

	}
}
