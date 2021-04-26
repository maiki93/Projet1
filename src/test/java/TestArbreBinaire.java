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
		list.add( new Stagiaire("Bic", "Ca", "75", "AI 109", 2021) );
		list.add( new Stagiaire("Bic", "Ma", "93","AI 109",1967) );
		list.add( new Stagiaire("Dick", "Jagger", "83", "AL 250", 2021) );
		// If MICK MA is MICA, tree is correct, duplicates on the left
		list.add( new Stagiaire("MiCK", "Ma", "93","AI 109", 2021) );
		list.add( new Stagiaire("MiCK", "Mab", "93","AI 109", 2021) );
		//list.add( new Stagiaire("MiCa", "Ma", "93","AI 109", 2021) );
		list.add( new Stagiaire("MiCK", "Mae", "93","AI 109", 2021) );
		list.add( new Stagiaire("MiCk", "Mat", "93","AI 119", 2021) );
		//list.add( new Stagiaire("Rick", "Jagger", "83", "AL 250", 1967) );
		
		Stagiaire Ma = new Stagiaire( "MiCK", "Ma", "93","AI 109", 2021);
		Stagiaire Mat = new Stagiaire( "MiCK", "Mat", "93","AI 109", 2021);
		System.out.println("compare MA et Mat: " + Ma.compareTo(Mat) );
		
		System.out.println();
		ArbreBinaire ab = new ArbreBinaire( 10, 10, 10, 10);
		ab.createBinFile(list);
		// Lit fichier
		//System.out.println("\n=== Lecture ==");
		//ab = new ArbreBinaire();
		//ab.printOrdreAlphabetique();
		
		
		System.out.println("\n=== ReLecture Alphabetique==");
		ab = new ArbreBinaire();
		List<Stagiaire> listStag = null;
		// just traverse the tree already sorted normally
		listStag = ab.getStagiaireOrdreAlphabetique();
		affiche(listStag);
//		System.out.println("Size of the list: " + listStag.size());
//		System.out.println("Stagiaire 0: "+ listStag.get(0).toString());
//		System.out.println("Stagiaire 600: "+ listStag.get(3).toString());
//		System.out.println("Stagiaire dernier: "+ listStag.get( listStag.size()-1).toString());
		
		System.out.println("\n=== Search with the Tree ==");
		ab = new ArbreBinaire();
		// algorithme iteratif of search in the tree
		//List<Stagiaire> l2 = ab.searchStagiaireParNom("MICK");
		List<Stagiaire> l2 = ab.searchStagiaireParNom("MICK"); //("Bic");
		affiche(l2);
	}
	
	public static void affiche(List<Stagiaire> list) {
		System.out.println("affiche taille: " + list.size());
		for(Stagiaire stage : list)
			System.out.println(stage.toString());
	}
}
