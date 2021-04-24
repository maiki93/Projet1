package main.java.ihm;

import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import main.java.Stagiaire;
import main.java.StagiaireDAO;

public class MainPannel extends BorderPane {

	private FormPannel formPannel = new FormPannel();
	private TablePannel tablePannel = new TablePannel();
	private TopPannel topPannel = new TopPannel();
	private FormAdminPannel formAdminPannel = new FormAdminPannel(true);

	public MainPannel() {
		super();
		setTop(topPannel);
		setRight(formPannel);
		setCenter(tablePannel);
		
		setLeft(formAdminPannel);
		// to make it invisible and the parent and reuse the place
		// formAdminPannel.setVisible( false );
		//formAdminPannel.setManaged(false); // to take out from the layout
		// formAdminPannel.managedProperty().bind(formAdminPannel.visibleProperty());
	}
	
	// MIC I would put Observable and Dao at a base level Frame easy to retrieve ?
	public ObservableList<Stagiaire> getObservable() {
		return ( (TablePannel)getCenter() ).getObservable();
	}
	
	public StagiaireDAO getDao() {
		return  ( (TablePannel)getCenter() ).getDao();
	}
	
	public FormAdminPannel getFormAdmin() {
		return (FormAdminPannel)getLeft();
	}
}
