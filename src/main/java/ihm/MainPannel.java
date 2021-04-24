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
