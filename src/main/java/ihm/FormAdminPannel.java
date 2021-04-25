package main.java.ihm;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import main.java.Stagiaire;

class FormAdminPannel extends GridPane implements EventHandler<ActionEvent> {
	
	private TextField textNom;
	private TextField textPrenom;
	private TextField textDepartement;
	private TextField textFormation;
	private TextField textAnnee;
	
	//  or could try TableView tb = (TableView) scene.lookup("#history");
	private Button btNew;
	private Button btSave;
	private Button btDel;
	
	
	private boolean isAdmin;
	private boolean isNewStagiaire;
	
	
	FormAdminPannel(boolean isAdmin) {
		super();
		this.isAdmin = isAdmin;
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
		btSave.managedProperty().bind(visibleProperty());
		btDel = new Button("Supprimer");
		btDel.setId("btDel");
		btDel.managedProperty().bind(visibleProperty());
		//if( !isAdmin)
		//btDel.setVisible(false);
		setAdmin(isAdmin);
		
		btNew.setOnAction(this);
		btSave.setOnAction(this);
		btDel.setOnAction(this);
		
		//HBox buttonBox = new HBox();
		//buttonBox.setId("buttonsAdminForm");
		//if( isAdmin)
		//buttonBox.getChildren().addAll(btNew, btSave, btDel);
		//if( !isAdmin ) {
		//	btNew.setVisible( false );
		//	btDel.setVisible( false );
		//}
		this.addRow(5, btNew, btSave, btDel);
	}

	/** Acts on an ObservableList 
	 *  Modificiation to dao is done in @see TablePannel
	 */
	@Override
	public void handle(ActionEvent event) {
		System.out.println("Button clicked");
		Button btnEvent = (Button)event.getSource();
		String idBt = btnEvent.getId();
		
		if( idBt.equals("btNew") ) {
			System.out.println("btNew");
			resetTextFields(); // TODO unselect entry in table
			isNewStagiaire = true;
			// save new stagiaire or valide the modification of 
			// a previously selected one
		} else if( idBt.equals("btSave")) {
			System.out.println("btSave isNewStagiaire "+ isNewStagiaire);
			Stagiaire stagiaire = readTextFields();
			// here more complexe
			
		// supprime  
		} else if( idBt.equals("btDel")) {
			System.out.println("btDel ");
			Stagiaire stagiaire = readTextFields();
			MainPannel root = (MainPannel) getScene().getRoot();
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
			//e.printStackTrace();
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
	
	public boolean isAdmin() {
		return isAdmin;
	}
	
	public void setAdmin(boolean adminAccess) {
		isAdmin = adminAccess;
		if( isAdmin) {
			btNew.setVisible(true);
			btDel.setVisible(true);
		} else {
			btNew.setVisible(false);
			btDel.setVisible(false);
		}
	}
}
