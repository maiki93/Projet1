package main.java.ihm;

import javafx.scene.layout.BorderPane;

public class MainPannel extends BorderPane {

	private FormPannel formPannel = new FormPannel();
	private TablePannel tablePannel = new TablePannel();

	public MainPannel() {
		super();
		setRight(formPannel);
		setCenter(tablePannel);

	}

}
