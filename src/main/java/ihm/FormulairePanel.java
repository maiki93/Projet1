package main.java.ihm;

import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.java.Stagiaire;

class FormulairePanel extends VBox implements EventHandler<ActionEvent> {
	
	private TextField textNom;
	private TextField textPrenom;
	private TextField textDepartement;
	private TextField textFormation;
	private TextField textAnnee;
	//  or to retrieve id could try TableView tb = (TableView) scene.lookup("#history");
	private Button btNew;
	private Button btSave;
	private Button btDel;
	//private int selectedItemInTableNb = -1; // dangerous with listener, better to check in function 
	private boolean isNewStagiaire;
	
	FormulairePanel() {
		super();
		createLayout();
	}
	
	private void createLayout() {

		GridPane gP1 = new GridPane();
		gP1.setPrefWidth(200);
		gP1.setId("gP1");
		//HBox entryNom = new HBox(20);
		Label lblNom = new Label("Nom");
		lblNom.setId("lblNom"); // or lbl+string(label)
		//lbl.setPrefWidth(200);
		//lbl.setTextAlignment(TextAlignment );
		//lbl.setAlignment(Pos.CENTER_LEFT);
		textNom = new TextField();
		textNom.setId("tfNom");
		textNom.setPrefWidth(100);
		//entryNom.getChildren().addAll(lblNom,textNom);
		gP1.addRow(0, lblNom, textNom);
		
		//HBox entryPrenom = new HBox(20);
		Label lblPrenom = new Label("Prenom");
		lblNom.setId("lblPrenom"); // or lbl+string(label)
		textPrenom = new TextField();
		textPrenom.setId("tfPrenom");
		textPrenom.setPrefWidth(100);
		gP1.addRow(1, lblPrenom, textPrenom);
		
		Label lblDepartement = new Label("Département");
		lblNom.setId("lblDepartement"); // or lbl+string(label)
		textDepartement = new TextField();
		textDepartement.setId("tfDepartement");
		textDepartement.setPrefWidth(100);
		gP1.addRow(2, lblDepartement, textDepartement);
		
		HBox gP2 = new HBox();
		gP2.setId("gP2");
		
		Label lblFormation = new Label("Formation");
		lblFormation.setId("lblFormation"); // or lbl+string(label)
		textFormation = new TextField();
		textFormation.setId("tfFormation");
		//textFormation.setMaxWidth(50);
		
		Label lblAnnee = new Label("Année");
		lblAnnee.setId("lblAnnee"); // or lbl+string(label)
		textAnnee = new TextField();
		textAnnee.setId("tfAnnee");
		//textAnnee.setMaxWidth(30);
		gP2.getChildren().addAll(lblFormation, textFormation, lblAnnee, textAnnee );
		
		this.getChildren().add(gP1);
		this.getChildren().add(gP2);
		// boutons
		HBox buttonBox = new HBox(20);
		btNew = new Button("Nouveau");
		btNew.setId("btNew");
		btNew.getStyleClass().add("button1");
		
		
		btSave = new Button("Sauvegarder");
		btSave.setId("btSave");
		// binds expects an ObservableValue (could be ObservableAdmin ?) here not need yet just for layout
		//btSave.managedProperty().bind(visibleProperty());
		btDel = new Button("Supprimer");
		btDel.setId("btDel");
		btDel.managedProperty().bind(visibleProperty());
		buttonBox.getChildren().addAll(btNew, btSave, btDel);
		this.getChildren().add(buttonBox);
		// button states in this function
		//setAdmin(isAdmin);
		btNew.setOnAction(this);
		btSave.setOnAction(this);
		btDel.setOnAction(this);
		this.setId("paneLeft");
	}

	/** Acts on an ObservableList 
	 *  Modificiation to dao is done in @see TablePannel
	 *  Normal user should not use Save button to modify an user
	 */
	@Override
	public void handle(ActionEvent event) {
		//System.out.println("Button clicked");
		Button btnEvent = (Button)event.getSource();
		String idBt = btnEvent.getId();
		RootPanel root = (RootPanel) getScene().getRoot();
		TablePanel tblPan = root.getTablePannel();
		TableView<Stagiaire> tblV = tblPan.getTableView();
		int selectedItemInTableNb =  tblV.getSelectionModel().getSelectedIndex();
		System.out.println("selected " + selectedItemInTableNb );
		
		if( idBt.equals("btNew") ) {
			System.out.println("btNew");
			resetTextFields(); // TODO unselect entry in table
			tblV.getSelectionModel().clearSelection();
		// save new stagiaire or valide the modification of 
		// a previously selected one, USER should not be able modify
		} else if( idBt.equals("btSave")) {
			System.out.println("btSave isNewStagiaire "+ isNewStagiaire);
			Stagiaire stagiaire = readTextFields();
			//System.out.println("stagiare in textfields"+ stagiaire);
			root = (RootPanel) getScene().getRoot();

			// nouvelle entrée, nothing selected in table
			if( selectedItemInTableNb == -1) {
				System.out.println("SelectedItem == -1, nouvelle série a enregistrer");
				//resetTextFields();
				root.getObservable().add(stagiaire);
			// one was selected, it is a modification
			} else {
				System.out.println("selectedItemNb "+ selectedItemInTableNb);
				// default true, we make a modification (only admin)
				boolean confirmation = true; 
				Stagiaire stagiaireSelected = tblV.getSelectionModel().getSelectedItem();
				if( (stagiaireSelected != null)  && (stagiaire.compareTo(stagiaireSelected ) == 0)) {
					System.out.println("\n==IT IS A doublon\n");
					// Ask for confirmation
					confirmation = askConfirmationDoublon();
					if( confirmation == true) {
						root.getObservable().add( stagiaire );
					}
					return;
				}
				// check admin because it is a modification
				if( !root.hasAdminRights() ) {
					alertRights();
					return;
				}
				// so it is a modification
				int index = root.getObservable().indexOf(stagiaireSelected); // index was sometimes modified(table and observable)
				root.getObservable().set(index, stagiaire);
				//resetTextFields(); was causing bug if selction was changedfrom here
			}
		// supprime  
		} else if( idBt.equals("btDel")) {
			System.out.println("btDel ");
			Stagiaire stagiaire = readTextFields();
			root = (RootPanel) getScene().getRoot();
			root.getObservable().remove(stagiaire);
		}
	}
	
	private TextField addEntry(String label, int width) {
		HBox oneEntry = new HBox(20);
		oneEntry.setId("Entry");
		//oneEntry.setPrefWidth(500.);
		//oneEntry.setAlignment(Pos.CENTER_LEFT);
		Label lbl = new Label(label);
		lbl.setId("lbl"+label); // or lbl+string(label)
		//lbl.setPrefWidth(200);
		//lbl.setTextAlignment(TextAlignment );
		//lbl.setAlignment(Pos.CENTER_LEFT);
		TextField tf = new TextField();
		tf.setId("tf"+label);
		tf.setPrefWidth(20);
		//tf.setAlignment(Pos.CENTER_RIGHT);
		//oneEntry.getChildren().addAll(lbl, tf);
		//this.getChildren().add(oneEntry);
		oneEntry.getChildren().addAll(lbl, tf);
		this.getChildren().add(oneEntry);
		return tf;
	}

	public void loadAStagiaire(Stagiaire stagiaire, int indexTable) {
		//this.selectedItemInTableNb = indexTable;
		textNom.setText( stagiaire.getNom());
		textPrenom.setText( stagiaire.getPrenom());
		textDepartement.setText( stagiaire.getDepartement());
		textFormation.setText( stagiaire.getFormation() );
		textAnnee.setText( Integer.toString(stagiaire.getAnnee()));
	}
	
	// Need validation
	public Stagiaire readTextFields() {
		String nom = textNom.getText();
		String prenom = textPrenom.getText();
		String dep = textDepartement.getText();
		String formation = textFormation.getText();
		int annee = 0;
		try {
			annee = Integer.parseInt( textAnnee.getText());
		} catch(NumberFormatException e) {
			System.err.println("Error in parsing année text field");
		}
		Stagiaire newStagiaire = new Stagiaire( nom, prenom, dep , formation, annee);
		return newStagiaire;
	}
	
	public void resetTextFields() {
		System.out.println("Enrtry resetTextFields");
		textNom.setText("");
		textPrenom.setText("");
		textDepartement.setText("");
		textFormation.setText("");
		textAnnee.setText("");
		/* observable acts with a delay as asynchronous function, danngerous to modiffy the selection
		RootPanel root = (RootPanel) getScene().getRoot();
		TablePanel tblPan = root.getTablePannel();
		TableView<Stagiaire> tblV = tblPan.getTableView(); 
		tblV.getSelectionModel().clearSelection();
		selectedItemInTableNb = -1; */
	}
	 
	public void setFormWithRights(boolean adminAccess) {
		if( adminAccess) {
			btDel.setVisible(true);
		} else {
			btDel.setVisible(false);
		}
	}
	
	public boolean askConfirmationDoublon() {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation de la création d'un doublon");
		String s = "Confirmez pour enregistrer un doublon";
		alert.setContentText(s);
		 
		Optional<ButtonType> result = alert.showAndWait();
		if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
			return true;
		    //textFld.setText("");
		    //actionStatus.setText("An example of Alert Dialogs. Enter some text and save.");
		    //textFld.requestFocus();
		}
		return false;
	}
	
	public void alertRights() {
		
		Alert alert = new Alert(AlertType.ERROR);
	    alert.setTitle("Droits insuffisants");
	    String s = "Les droits administrateurs sont nécessaires pour modifier un stagiaire";
	    alert.setContentText(s);
	    alert.showAndWait();
	}
	
}
