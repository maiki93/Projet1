package main.java.ihm;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import main.java.AdminDAO;
import main.java.Stagiaire;
import main.java.StagiaireDAO;

public class MainPannel extends BorderPane {

	private StagiaireDAO dao = new StagiaireDAO();
	private ObservableList<Stagiaire> observablesStagiaires;
	
	private FormPannel formPannel = new FormPannel();
	private TablePannel tablePannel; // = new TablePannel();
	private TopPannel topPannel = new TopPannel();
	private FormAdminPannel formAdminPannel = new FormAdminPannel(false);
	// could be in Top ? or need to access from getScene.getRoot
	private AdminDAO adminDao = new AdminDAO();

	public MainPannel() {
		super();
		// defaut at start-up,  get the full list of stagiaires
		observablesStagiaires = FXCollections.observableArrayList(dao.getStagiaireListFromArbre());
		System.out.println("Size of observablesStagiaires: " + observablesStagiaires.size());
		tablePannel = new TablePannel(observablesStagiaires);
		
		setTop(topPannel);
		setRight(formPannel);
		setCenter(tablePannel);
		
		setLeft(formAdminPannel);
		// to make it invisible and the parent to reuse the place
		formAdminPannel.setVisible( false );
		formAdminPannel.managedProperty().bind(formAdminPannel.visibleProperty());
		
	}
	
	// resultat d'une nouvelle recherche (panel de droite) => ObservableList => tableView
	public void setNewRecherche(List<Stagiaire> listFiltree) {
		System.out.println("In Main Panel, setNewRecherche");
		observablesStagiaires.clear();
		observablesStagiaires.addAll(listFiltree);
		//ObservableList<Stagiaire> observablesStagiaires2 = FXCollections.observableArrayList(listFiltree);
		//System.out.println("Size of observablesStagiaires: " + observablesStagiaires2.size());
		
		//tablePannel = new TablePannel(observablesStagiaires);
		//setCenter(tablePannel);
		//tablePannel.getTableView().refresh();
	}
	
	// MIC I would put Observable and Dao at a base level Frame easy to retrieve ?
	public ObservableList<Stagiaire> getObservable() {
		//return ( (TablePannel)getCenter() ).getObservable();
		return observablesStagiaires;
	}
	
	public StagiaireDAO getStagiaireDao() {
		return  dao;
	}
	
	public boolean hasAdminRights() {
		return  adminDao.isAdmin();
	}
	
	public FormAdminPannel getFormAdmin() {
		return (FormAdminPannel)getLeft();
	}

	public FormPannel getFormPannel() {
		return formPannel;
	}

	public void setFormPannel(FormPannel formPannel) {
		this.formPannel = formPannel;
	}

	public TablePannel getTablePannel() {
		return tablePannel;
	}

	public void setTablePannel(TablePannel tablePannel) {
		this.tablePannel = tablePannel;
	}

	public TopPannel getTopPannel() {
		return topPannel;
	}

	public void setTopPannel(TopPannel topPannel) {
		this.topPannel = topPannel;
	}

}
