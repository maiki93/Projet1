package main.java.ihm;

import java.util.ArrayList;
import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import main.java.StagiaireDAO;
import main.java.Stagiaire;

public class TablePannel extends AnchorPane {
	private StagiaireDAO dao = new StagiaireDAO();
	private ObservableList<Stagiaire> observablesStagiaires;
	private TableView<Stagiaire> tableView;

	@SuppressWarnings("unchecked")
	public TablePannel() {
		super();

		dao.readTxtFichier();
		//observablesStagiaires = FXCollections.observableArrayList(dao.getStagiairelist());
		observablesStagiaires = FXCollections.observableArrayList(dao.getStagiaireListFromArbre());

		tableView = new TableView<>(observablesStagiaires);

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

	}

	public TableView<Stagiaire> getTableView() {
		return tableView;
	}

	public void setTableView(TableView<Stagiaire> tableView) {
		this.tableView = tableView;
	}

}
