package test.java;

//import java.util.ArrayList;

import main.java.Stagiaire;
import main.java.StagiaireDAO;

import java.util.List;

import main.java.ArbreBinaire;

public class TestStagiaireDAO {

	public static void main(String[] args) {
		
		StagiaireDAO stageDao = new StagiaireDAO();
		stageDao.readTxtFichier();
		
		ArbreBinaire ab = new ArbreBinaire( stageDao.getTailleNom(), stageDao.getTaillePrenom(),
				stageDao.getTailleDepartement(), stageDao.getTailleFormation());
		// écrit
		ab.createBinFile( stageDao.getStagiairelist()); 
		// Lit fichier
		System.out.println("\n=== Lecture ==");
		//ArbreBinaire ab; 
		ab = new ArbreBinaire(); //(40,40,40,40);
		//ab.testReadBinFile();
		ab.printOrdreAlphabetique();
		
		List<Stagiaire> listStag = null;
		listStag = ab.getStagiaireOrdreAlphabetique();
		System.out.println("Size of the list: " + listStag.size());
		System.out.println("Stagiaire 0: "+ listStag.get(0).toString());
		System.out.println("Stagiaire 600: "+ listStag.get(600).toString());
		System.out.println("Stagiaire dernier: "+ listStag.get( listStag.size()-1).toString());
	}
}
