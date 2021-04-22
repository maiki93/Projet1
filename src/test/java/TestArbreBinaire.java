package test.java;

import java.util.ArrayList;
import java.util.List;

import main.java.Stagiaire;
import main.java.ArbreBinaire;
import main.java.NodeStagiaire;

public class TestArbreBinaire {

	public static void main(String[] args) {
		
		ArrayList<Stagiaire> list = new ArrayList<Stagiaire>();
		list.add( new Stagiaire("Aaul", "Ca", "75", "AI 109", 2021) );
		list.add( new Stagiaire("Bert", "Ca", "75", "AI 109", 2021) );
		list.add( new Stagiaire("Bic", "Ma", "93","AI 109",1967) );
		list.add( new Stagiaire("Dick", "Jagger", "83", "AL 250", 2021) );
		list.add( new Stagiaire("Mic", "Ma", "93","AI 109", 2021) );
		list.add( new Stagiaire("Rick", "Jagger", "83", "AL 250", 1967) );
		
		ArbreBinaire ab = new ArbreBinaire( 10, 10, 10, 10);
		ab.createBinFile(list);
		// Lit fichier
		System.out.println("\n=== Lecture ==");
		ab = new ArbreBinaire();
		ab.printOrdreAlphabetique();
		
		
		System.out.println("\n=== ReLecture ==");
		//ab = new ArbreBinaire();
		List<Stagiaire> listStag = null;
		listStag = ab.getStagiaireOrdreAlphabetique();
		System.out.println("Size of the list: " + listStag.size());
		System.out.println("Stagiaire 0: "+ listStag.get(0).toString());
		System.out.println("Stagiaire 600: "+ listStag.get(3).toString());
		System.out.println("Stagiaire dernier: "+ listStag.get( listStag.size()-1).toString());
		
	}
}
