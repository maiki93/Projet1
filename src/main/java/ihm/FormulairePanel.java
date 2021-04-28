package main.java.ihm;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import main.java.Stagiaire;

class FormulairePanel extends GridPane implements EventHandler<ActionEvent> {
	
	private TextField textNom;
	private TextField textPrenom;
	private TextField textDepartement;
	private TextField textFormation;
	private TextField textAnnee;
	
	//  or to retrieve id could try TableView tb = (TableView) scene.lookup("#history");
	private Button btNew;
	private Button btSave;
	private Button btDel;
	
	//private boolean isAdmin; // should not be in a formulaire !
	private boolean isNewStagiaire;
	
	FormulairePanel() {
		super();
		createLayout();
		// Afficher l'entrée sélectionnée dans le tableau
		// ou le premier par défaut, celui qui provient de la recherche
		// Ecris dans TableView
	}
	
	
	private void createLayout() {

		textNom = addEntry("Nom", 0);
		textPrenom = addEntry("Prenom",1);
		textDepartement = addEntry("Departement", 2);
		textFormation = addEntry("Formation",3);
		textAnnee = addEntry("Année",4);
		
		// boutons
		btNew = new Button("Nouv.Stagiaire");
		btNew.setId("btNew");
		btSave = new Button("Sauvegarder");
		btSave.setId("btSave");
		// binds expects an ObservableValue (could be ObservableAdmin ?)
		// here not need yet just for layout
		btSave.managedProperty().bind(visibleProperty());
		btDel = new Button("Supprimer");
		btDel.setId("btDel");
		btDel.managedProperty().bind(visibleProperty());
		// button states in this function
		//setAdmin(isAdmin);
		btNew.setOnAction(this);
		btSave.setOnAction(this);
		btDel.setOnAction(this);
		this.setId("pannelLeft");
		this.addRow(5, btNew, btSave, btDel);
	}

	/** Acts on an ObservableList 
	 *  Modificiation to dao is done in @see TablePannel
	 *  Normal user should not use Save button to modify an user
	 */
	@Override
	public void handle(ActionEvent event) {
		System.out.println("Button clicked");
		Button btnEvent = (Button)event.getSource();
		String idBt = btnEvent.getId();
		
		if( idBt.equals("btNew") ) {
			System.out.println("btNew");
			resetTextFields(); // TODO unselect entry in table
			
			// clearSelection in table, could be a function
			RootPanel root = (RootPanel) getScene().getRoot();
			//TableView<Stagiaire> tblV = ((TablePannel) root.getTablePannel()).getTableView(); ??
			TablePanel tblPan = root.getTablePannel();
			TableView<Stagiaire> tblV = tblPan.getTableView(); 
			tblV.getSelectionModel().clearSelection();
			isNewStagiaire = true;
			// save new stagiaire or valide the modification of 
			// a previously selected one, USER should not be able modify
		} else if( idBt.equals("btSave")) {
			System.out.println("btSave isNewStagiaire "+ isNewStagiaire);
			Stagiaire stagiaire = readTextFields();
			System.out.println("new stagiare "+ stagiaire);
			RootPanel root = (RootPanel) getScene().getRoot();
			
			//SortedList<Stagiaire> sorted = new SortedList<Stagiaire>(root.getObservable());
			//sorted.add(stagiaire, Comparator.);
			root.getObservable().add(stagiaire);
			
			//root.getObservable().sort(null);
			//root.getSortedObservable().add(stagiaire);
			// here more complexe
			
		// supprime  
		} else if( idBt.equals("btDel")) {
			System.out.println("btDel ");
			Stagiaire stagiaire = readTextFields();
			RootPanel root = (RootPanel) getScene().getRoot();
			root.getObservable().remove(stagiaire);
		}
	}
	
	private TextField addEntry(String label, int rowNb) {
		//HBox oneEntry = new HBox(20);
		//oneEntry.setPrefWidth(500.);
		//oneEntry.setAlignment(Pos.CENTER_LEFT);
		Label lbl = new Label(label);
		lbl.setId("lbl"+label); // or lbl+string(label)
		//lbl.setPrefWidth(200);
		//lbl.setTextAlignment(TextAlignment );
		//lbl.setAlignment(Pos.CENTER_LEFT);
		TextField tf = new TextField();
		tf.setId("tf"+label);
		//tf.setAlignment(Pos.CENTER_RIGHT);
		//oneEntry.getChildren().addAll(lbl, tf);
		//this.getChildren().add(oneEntry);
		addRow( rowNb, lbl, tf);
		return tf;
	}
	
	//private void insertStagiaireToObservable(ObservableList<Stagiaire> list, Stagiaire stagiaire) {
	//	list.sorted()
	//}
	
	public void loadAStagiaire(Stagiaire stagiaire) {
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
		textNom.setText("");
		textPrenom.setText("");
		textDepartement.setText("");
		textFormation.setText("");
		textAnnee.setText("");
	}
	 
	public void setFormWithRights(boolean adminAccess) {
		if( adminAccess) {
			this.setVisible(true);
			btNew.setVisible(true);
			btDel.setVisible(true);
		} else {
			this.setVisible(false);
			btNew.setVisible(false);
			btDel.setVisible(false);
		}
	}
}
