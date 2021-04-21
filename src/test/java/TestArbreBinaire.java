package test.java;

import java.util.ArrayList;

import main.java.Stagiaire;
import main.java.ArbreBinaire;

public class TestArbreBinaire {

	public static void main(String[] args) {
		
		ArrayList<Stagiaire> list = new ArrayList<Stagiaire>();
		list.add( new Stagiaire("Aaul", "Ca", 75, "AI 109", 2021) );
		list.add( new Stagiaire("Bert", "Ca", 75, "AI 109", 2021) );
		list.add( new Stagiaire("Bic", "Ma", 93,"AI 109", 2021) );
		list.add( new Stagiaire("Dick", "Jagger", 83, "AL 250", 1967)  );
		list.add( new Stagiaire("Mic", "Ma", 93,"AI 109", 2021) );
		list.add( new Stagiaire("Rick", "Jagger", 83, "AL 250", 1967)  );
		ArbreBinaire ab = new ArbreBinaire( 10, 10);
		ab.createBinFile(list);
		// Lit fichier
		//System.out.println("\n=== Lecture ==");
		//ab = new ArbreBinaire();
		//ab.testReadBinFile();
	}
}
