package main.java;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StagiaireDAO {
	private Stagiaire stagiaire;
	private List<Stagiaire> stagiairelist = new ArrayList<Stagiaire>();
	private int tailleNom;
	private int taillePrenom;
	private int tailleDepartement;
	private int tailleFormation;

	public void readTxtFichier() {
		BufferedReader br;
		String[] stagiaireLinearray = new String[5];
		int i = 0;

		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/stagiaires.txt"),
					"CP1252"));
			while (br.ready()) {
				String strcompare = br.readLine();
				if (strcompare.compareTo("*") == 0) {
					String nom = stagiaireLinearray[0];
					String prenom = stagiaireLinearray[1];
					String departement = stagiaireLinearray[2];
					String formation = stagiaireLinearray[3];
					int annee = Integer.parseInt(stagiaireLinearray[4]);
					stagiaire = new Stagiaire(nom, prenom, departement, formation, annee);
					stagiairelist.add(stagiaire);
					i = 0;
				} else {
					stagiaireLinearray[i] = strcompare;
					i++;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			for (Stagiaire stagiaire : stagiairelist) {
				compteTailleChamps(stagiaire);
			}
		}

	}

	// Calcul de la taille des champs
	private void compteTailleChamps(Stagiaire filou) {
		String[] str = filou.toString().split(";");
		for (int i = 0; i < filou.toString().length() - str.length; i++) {

			if (getTailleNom() < str[0].length()) {
				setTailleNom(str[0].length());
			}
			if (getTaillePrenom() < str[1].length()) {
				setTaillePrenom(str[1].length());
			}
			if (getTailleDepartement() < str[2].length()) {
				setTailleDepartement(str[2].length());
			}
			if (getTailleFormation() < str[3].length()) {
				setTailleFormation(str[3].length());
			}
		}

	}

	public int getTailleNom() {
		return tailleNom;
	}

	public void setTailleNom(int tailleNom) {
		this.tailleNom = tailleNom;
	}

	public int getTaillePrenom() {
		return taillePrenom;
	}

	public void setTaillePrenom(int taillePrenom) {
		this.taillePrenom = taillePrenom;
	}

	public int getTailleDepartement() {
		return tailleDepartement;
	}

	public void setTailleDepartement(int tailleDepartement) {
		this.tailleDepartement = tailleDepartement;
	}

	public int getTailleFormation() {
		return tailleFormation;
	}

	public void setTailleFormation(int tailleFormation) {
		this.tailleFormation = tailleFormation;
	}
}
