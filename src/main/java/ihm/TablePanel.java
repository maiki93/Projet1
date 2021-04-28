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
		
		//tableView.getSortOr

		tableView.getColumns().addAll(colNom, colPrenom, colDepartement, colFormation, colAnnee);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		getChildren().add(tableView);

		setPrefSize(1000, 500);
		AnchorPane.setTopAnchor(tableView, 5.);
		AnchorPane.setLeftAnchor(tableView, 5.);
		AnchorPane.setRightAnchor(tableView, 5.);
		AnchorPane.setBottomAnchor(tableView, 5.);
		
		TableViewSelectionModel<Stagiaire> selectModel = tableView.getSelectionModel();
		selectModel.setSelectionMode(SelectionMode.MULTIPLE);
		
		tableView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<Stagiaire>() {
			@Override
			public void changed(ObservableValue<? extends Stagiaire> observable, Stagiaire oldValue, Stagiaire newValue) {
				System.out.println("On item selected new value "+ newValue + " " + oldValue);
				System.out.println("ObservableValue" + observable);
				// often error here, not clear why...
				if(newValue != null ) {
					RootPanel root = (RootPanel)getScene().getRoot();
					root.getFormulairePanel().loadAStagiaire(newValue);
					// test, in jafafxdemo
					// formulaire.setFocusedItem(tableView.getSelectionModel().getSelectedIndex(), newValue);
				} // for test 
				else {
					System.out.println("new value is null");
				}
			}});
		
		// Listener on Observable, when it is modified we can update the dao
		// both seem working, sorted or direct Observable
		//sortedObservablesStagiaires.addListener(new ListChangeListener<Stagiaire>() {
		observablesStagiaires.addListener(new ListChangeListener<Stagiaire>() {
			@Override
			public void onChanged(Change<? extends Stagiaire> chgStagiaire) {
				System.out.println("Observable list has been modified");
				RootPanel root = (RootPanel) getScene().getRoot();
				StagiaireDAO dao = root.getStagiaireDao();
				
				while(chgStagiaire.next()) {
					if( chgStagiaire.wasUpdated()) {
						System.out.println("ChgStagiare was updated()");
						
					} else if( chgStagiaire.wasReplaced()) {
						System.out.println("ChgStagiare was replaced()");
						
					} else if( chgStagiaire.wasAdded() ) {
						System.out.println("ChgStagiare was added()");
						List<? extends Stagiaire> lstag = chgStagiaire.getAddedSubList();
						dao.addAll( (List<Stagiaire>) lstag);
						root.getRecherchePanel().getTotalEtudiantLabel().setText("Elément Total: "+Integer.toString(root.getObservable().size()));
						
					} else if( chgStagiaire.wasRemoved() ) {
						System.out.println("ChgStagiare was removed()");
						List<? extends Stagiaire> lstag = chgStagiaire.getRemoved();
						dao.removeAll( (List<Stagiaire>)lstag );
						root.getRecherchePanel().getTotalEtudiantLabel().setText("Elément Total: "+Integer.toString(root.getObservable().size()));

					}
				}
			}
		});
	}
	
	public TableView<Stagiaire> getTableView() {
		return tableView;
	}

}
