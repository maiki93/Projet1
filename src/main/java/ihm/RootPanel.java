package main.java.ihm;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import main.java.AdminDAO;
import main.java.Stagiaire;
import main.java.StagiaireDAO;

public class RootPanel extends BorderPane {

	private StagiaireDAO dao = new StagiaireDAO();
	private ObservableList<Stagiaire> observablesStagiaires;
	
	private RecherchePanel rechPanel = new RecherchePanel();
	private TablePanel tablePannel; // = new TablePannel();
	private TopPanel topPanel = new TopPanel();
	private FormulairePanel formPanel = new FormulairePanel();
	
	// could be in Top ? or need to access from getScene.getRoot
	private AdminDAO adminDao = new AdminDAO();

	public RootPanel() {
		super();
		// defaut at start-up,  get the full list of stagiaires
		observablesStagiaires = FXCollections.observableArrayList(dao.getStagiaireListFromArbre());
		System.out.println("Size of observablesStagiaires: " + observablesStagiaires.size());
		tablePannel = new TablePanel(observablesStagiaires);
		
		setTop(topPanel);
		setRight(rechPanel);
		setCenter(tablePannel);
		
		setLeft(formPanel);
		// to make it invisible and the parent to reuse the place
		//formPanel.setVisible( false );
		formPanel.setFormWithRights(false);
		formPanel.managedProperty().bind(formPanel.visibleProperty());
	}
	
	// resultat d'une nouvelle recherche (panel de droite) => ObservableList => tableView
	public void setNewRecherche(List<Stagiaire> listFiltree) {
		System.out.println("In Main Panel, setNewRecherche");
		// Tableau mis à jour mais clear() calls "was removed(), was added()..."
		//observablesStagiaires.clear();
		//observablesStagiaires.addAll(listFiltree);
		//System.out.println("Size of observablesStagiaires: " + observablesStagiaires.size());
		//System.out.println("Size of dao ");
		////////////////////////////////////////
		
		observablesStagiaires = FXCollections.observableArrayList(listFiltree);
		tablePannel = new TablePanel(observablesStagiaires);
		setCenter(tablePannel);
	}
	
	// MIC I would put Observable and Dao at a base level Frame easy to retrieve ?
	public ObservableList<Stagiaire> getObservable() {
		//return ( (TablePannel)getCenter() ).getObservable();
		return observablesStagiaires;
	}
	
	public StagiaireDAO getStagiaireDao() {
		return dao;
	}
	
	public boolean hasAdminRights() {
		return  adminDao.isAdmin();
	}
	
	// For admin open the formulaire, for user close it
	public void setAdminRights(boolean adminRights) {
		adminDao.setAdminRights(adminRights);
		//formPanel.setVisible(adminRights);
		// set button in accordance to rights, visibility also, setFormWithRights 
		formPanel.setFormWithRights(adminRights);
		// button in recherche formulaire to update
		// formReche
		
	}
	
	public FormulairePanel getFormulairePanel() {
		return formPanel; // getLeft/Center
	}

	public RecherchePanel getRecherchePanel() {
		return rechPanel;
	}
	/*
	public void setFormPannel(RecherchePanel formPannel) {
		this.formPannel = formPannel;
	}*/

	public TablePanel getTablePannel() {
		return tablePannel;
	}

	/*
	public void setTablePannel(TablePanel tablePannel) {
		this.tablePannel = tablePannel;
	}
	*/
	/*
	public TopPanel getTopPanel() {
		return topPanel;
	}
	*/
	/*
	public void setTopPanel(TopPanel topPanel) {
		this.topPanel = topPanel;
	}*/

}
