package main.java.ihm;

import java.util.Comparator;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
//import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import main.java.StagiaireDAO;
import main.java.Stagiaire;
import main.java.StagiaireComparator;

public class TablePanel extends AnchorPane {
	
	private TableView<Stagiaire> tableView;
	
	SortedList<Stagiaire> sortedObservablesStagiaires;

	// Recreer à chaque fois, peut etre un peu bourrin comme méthode. 
	// A voir plus tard si on peut se limiter à la table, voir reassigner l'observable. 
	@SuppressWarnings("unchecked")
	public TablePanel(ObservableList<Stagiaire> observablesStagiaires) {
		super();
		// Data are sorted in a wrapper around the observable
		Comparator<? super Stagiaire> stagiaireComparator = new StagiaireComparator();
		sortedObservablesStagiaires = new SortedList<Stagiaire>(observablesStagiaires, stagiaireComparator);

		tableView = new TableView<>(sortedObservablesStagiaires);
		
		TableColumn<Stagiaire, String> colNom = new TableColumn<>("Nom");
		colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

		TableColumn<Stagiaire, String> colPrenom = new TableColumn<>("Prenom");
		colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
		colPrenom.setStyle("-fx-alignment: CENTER");

		TableColumn<Stagiaire, String> colDepartement = new TableColumn<>("Departement");
		colDepartement.setCellValueFactory(new PropertyValueFactory<>("departement"));

		TableColumn<Stagiaire, String> colFormation = new TableColumn<>("Formation");
		colFormation.setCellValueFactory(new PropertyValueFactory<>("formation"));

		TableColumn<Stagiaire, Integer> colAnnee = new TableColumn<>("Année");
		colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));

		tableView.getColumns().addAll(colNom, colPrenom, colDepartement, colFormation, colAnnee);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		getChildren().add(tableView);

		setPrefSize(1000, 500);
		AnchorPane.setTopAnchor(tableView, 5.);
		AnchorPane.setLeftAnchor(tableView, 5.);
		AnchorPane.setRightAnchor(tableView, 5.);
		AnchorPane.setBottomAnchor(tableView, 5.);
		
		TableViewSelectionModel<Stagiaire> selectModel = tableView.getSelectionModel();
		// not used
		//selectModel.setSelectionMode(SelectionMode.MULTIPLE);
		
		tableView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<Stagiaire>() {
			@Override
			public void changed(ObservableValue<? extends Stagiaire> observable, Stagiaire oldValue, Stagiaire newValue) {
				System.out.println("On item selected new value "+ newValue + " " + oldValue);
				// often error here, not clear why...
				if(newValue != null ) {
					RootPanel root = (RootPanel)getScene().getRoot();
					int selectedItemNb = tableView.getSelectionModel().getSelectedIndex();
					// Indicate also the item selected
					root.getFormulairePanel().loadAStagiaire(newValue, selectedItemNb);
				} // for test 
				else {
					System.out.println("new value is null");
				}
			}});
		
		// Listener on Observable, when it is modified we can update the dao
		// both seem working, sorted or direct Observable
		sortedObservablesStagiaires.addListener(new ListChangeListener<Stagiaire>() {
		//observablesStagiaires.addListener(new ListChangeListener<Stagiaire>() {
			@Override
			public void onChanged(Change<? extends Stagiaire> chgStagiaire) {
				System.out.println("Observable list has been modified");
				RootPanel root = (RootPanel) getScene().getRoot();
				StagiaireDAO dao = root.getStagiaireDao();
				Stagiaire modifiedStagiaire = null;
				
				while(chgStagiaire.next()) {
					if( chgStagiaire.wasUpdated()) {
						System.out.println("ChgStagiare was updated()================");
						//break;

					} else if( chgStagiaire.wasReplaced()) {
						System.out.println("ChgStagiare was replaced()=====================");
						System.out.println("chgStagiaire: " + chgStagiaire);
						List<? extends Stagiaire> lstag = chgStagiaire.getAddedSubList();
						List<? extends Stagiaire> lstag2 = chgStagiaire.getRemoved();
						
						int posView = ((chgStagiaire.getFrom()-1) >=0) ? (chgStagiaire.getFrom()-1) : 0;
						tableView.getSelectionModel().select(chgStagiaire.getFrom());
						tableView.scrollTo(posView);
						tableView.layout();
						dao.replaceStagiaire( lstag.get(0), lstag2.get(0) ); 
						//break;

					} else if( chgStagiaire.wasAdded() ) {
						System.out.println("ChgStagiare was added()");
						System.out.println("chgStagiaire: " + chgStagiaire);
						List<? extends Stagiaire> lstag = chgStagiaire.getAddedSubList();
						modifiedStagiaire = lstag.get(0);

						int posView = ((chgStagiaire.getFrom()-1) >=0) ? (chgStagiaire.getFrom()-1) : 0;
						tableView.getSelectionModel().select(chgStagiaire.getFrom());
						tableView.scrollTo(posView);
						tableView.layout();
						dao.addAll( (List<Stagiaire>) lstag);
						root.getRecherchePanel().getTotalEtudiantLabel().setText("Elément Total: "+Integer.toString(root.getObservable().size()));
						root.getFormulairePanel().resetTextFields();

					} else if( chgStagiaire.wasRemoved() ) {
						System.out.println("ChgStagiaire was removed()");
						root.getFormulairePanel().resetTextFields();
						tableView.getSelectionModel().clearSelection();
						List<? extends Stagiaire> lstag = chgStagiaire.getRemoved();
						dao.removeAll( (List<Stagiaire>)lstag );
						root.getRecherchePanel().getTotalEtudiantLabel().setText("Elément Total: "+Integer.toString(root.getObservable().size()));
						//break;

					}/* else {
						System.err.println("========== Nothing IT IS BAD ! ============");
					}*/
				}
			}
		});
	}
	
	public TableView<Stagiaire> getTableView() {
		return tableView;
	}

}
