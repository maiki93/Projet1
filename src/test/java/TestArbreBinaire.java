package test.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.java.Stagiaire;
import main.java.ArbreBinaire;
import main.java.NodeStagiaire;

public class TestArbreBinaire {

	public static void main(String[] args) throws IOException {
		
		ArrayList<Stagiaire> list = new ArrayList<Stagiaire>();
		list.add( new Stagiaire("Aaul", "Ca", "75", "AI 109", 2021) );
		list.add( new Stagiaire("Bic", "Ca", "75", "AI 109", 2021) );
		//list.add( new Stagiaire("Bic", "Ma", "93","AI 109",1967) );
		list.add( new Stagiaire("Dick", "Jagger", "83", "AL 250", 2021) );
		// If MICK MA is MICA, tree is correct, duplicates on the left
		list.add( new Stagiaire("MiCK", "Ma", "93","AI 109", 2021) );
		//list.add( new Stagiaire("MiCK", "Mab", "93","AI 109", 2021) );
		//list.add( new Stagiaire("MiCa", "Ma", "93","AI 109", 2021) );
		//list.add( new Stagiaire("MiCK", "Mae", "93","AI 109", 2021) );
		//list.add( new Stagiaire("MiCk", "Mat", "93","AI 119", 2021) );
		
		
		list.add( new Stagiaire("Rick", "Jagger", "83", "AL 250", 1967) );
		list.add( new Stagiaire("Rick2", "Jagger", "83", "AL 250", 1967) );
		list.add( new Stagiaire("Rick3", "Jagger", "83", "AL 250", 1967) );
		
		//Stagiaire Ma = new Stagiaire( "MiCK", "Ma", "93","AI 109", 2021);
		//Stagiaire Mat = new Stagiaire( "MiCK", "Mat", "93","AI 109", 2021);
		//System.out.println("compare MA et Mat: " + Ma.compareTo(Mat) );
		
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
		
		/*
		System.out.println("\n=== Search with the Tree ==");
		ab = new ArbreBinaire();
		// algorithme iteratif of search in the tree
		//List<Stagiaire> l2 = ab.searchStagiaireParNom("MICK");
		List<Stagiaire> l2 = ab.searchStagiaireParNom("MICK"); //("Bic");
		affiche(l2);
		
		
		System.out.println("\n==== Insertion =====");
		//Stagiaire s1 = new Stagiaire("AAAA","B","C","D",2021);
		Stagiaire s1 = new Stagiaire("AR","B","C","D",2021);
		ab.addStagiaire(s1);
		
		List<Stagiaire> l3 = ab.searchStagiaireParNom("AAAA");
		affiche(l3);
		listStag = ab.getStagiaireOrdreAlphabetique();
		affiche(listStag);
		 */
		
	/*
		System.out.println("\n==== Remove One leave =====");
		//Stagiaire s1 = new Stagiaire("AAAA","B","C","D",2021);
		// not exactly the same
		//Stagiaire s2 = new Stagiaire("AAUL","B","C","D",2021);
		/// exactly the same
		Stagiaire s2 = new Stagiaire("Aaul", "Ca", "75", "AI 109", 2021);
		//Stagiaire s2 = new Stagiaire("MiCK", "Ma", "93","AI 109", 2021);
		ab.removeStagiaire(s2);
		listStag = ab.getStagiaireOrdreAlphabetique();
		affiche(listStag);
	*/
/*		
		System.out.println("\n==== Remove with One child =====");
		Stagiaire s3 = new Stagiaire("Bic", "Ca", "75", "AI 109", 2021);
		//Stagiaire s3 = new Stagiaire("MiCK", "Ma", "93","AI 109", 2021);
		//Stagiaire s3 = new Stagiaire("MiCK", "Mab", "93","AI 109", 2021) );
		ab.removeStagiaire(s3);
		listStag = ab.getStagiaireOrdreAlphabetique();
		affiche(listStag);
*/	

		System.out.println("\n==== Remove with Two child =====");
		//Stagiaire s4 = new Stagiaire("Dick", "Jagger", "83", "AL 250", 2021);
		//Stagiaire s3 = new Stagiaire("MiCK", "Ma", "93","AI 109", 2021);
		//Stagiaire s3 = new Stagiaire("MiCK", "Mab", "93","AI 109", 2021) );
		Stagiaire s4 = new Stagiaire("Bic", "Ca", "75", "AI 109", 2021);
		ab.removeStagiaire(s4);
		listStag = ab.getStagiaireOrdreAlphabetique();
		affiche(listStag);

		
		System.out.println("\n==== Remove Root node =====");
		//Stagiaire s5 = new Stagiaire("MiCk", "Mattt", "93","AI 119", 2021);
		
		//Stagiaire s5 = new Stagiaire("Bic", "Ca", "75", "AI 109", 2021);
		//Stagiaire s5 = new Stagiaire("Dick", "Jagger", "83", "AL 250", 2021);
		Stagiaire s5 = new Stagiaire("MiCK", "Ma", "93","AI 109", 2021);
		ab.removeStagiaire(s5);
		listStag = ab.getStagiaireOrdreAlphabetique();
		affiche(listStag);		
	}
	
	public static void affiche(List<Stagiaire> list) {
		System.out.println("affiche taille: " + list.size());
		for(Stagiaire stage : list)
			System.out.println(stage.toString());
	}
}
