package main.java.ihm;

import javafx.scene.layout.BorderPane;

public class MainPannel extends BorderPane {

	private FormPannel formPannel = new FormPannel();
	private TablePannel tablePannel = new TablePannel();
	private TopPannel topPannel = new TopPannel();

	public MainPannel() {
		super();
		setTop(topPannel);
		setRight(formPannel);
		setCenter(tablePannel);

	}

}
