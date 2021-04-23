package main.java;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StagiaireDAO {
	private Stagiaire stagiaire;
	private List<Stagiaire> stagiairelist = new ArrayList<Stagiaire>();
	private int tailleNom;
	private int taillePrenom;
	private int tailleDepartement;
	private int tailleFormation;
	
	////////// fonctionallités de recherche
	
	
	/////////
	
	/** Criteres de comparaison pour les filtres sont tres large.
	 *  Cherche une substring en fait, ignore minuscule/majuscule. Sauf pour annee...
	 *  
	 *  Accès à l'arbre demandé à chaque fois, pourrait mettre en cache dans certaines conditions
	 */
	public List<Stagiaire> rechercheStagiaire( Stagiaire stagiaireTemplate, boolean global) {
		
		System.out.println("stagiaire template "+ stagiaireTemplate.toString());
		System.out.println("global: "+ global);
		List<Stagiaire> listFiltree = null;
		
		ArbreBinaire ab = new ArbreBinaire();
		listFiltree = ab.getStagiaireOrdreAlphabetique();
		
		if (global == false ) {
			listFiltree = filtreParNom( listFiltree, stagiaireTemplate.getNom() );
			listFiltree = filtreParPrenom( listFiltree, stagiaireTemplate.getPrenom() );
			listFiltree = filtreParDepartement( listFiltree, stagiaireTemplate.getDepartement() );
			listFiltree = filtreParFormation( listFiltree, stagiaireTemplate.getFormation() );
			listFiltree = filtreParAnnee( listFiltree, stagiaireTemplate.getAnnee() );
			
		// filtre global, at least one field should return true
		} else {
			/*
			List<Stagiaire> tmpOneStagiaire = new ArrayList<>();
			String critere = stagiaireTemplate.getNom();
			
			for( Stagiaire stage : listFiltree) {
				tmpOneStagiaire.add(stage);
				if( (filtreParNom( tmpOneStagiaire, critere) 
			}
			listFiltree = filtreParNom( listFiltree, critere );
			listFiltree = filtreParPrenom( listFiltree, critere );
			listFiltree = filtreParDepartement( listFiltree, critere );
			listFiltree = filtreParFormation( listFiltree, critere );
			try {
				listFiltree = filtreParAnnee( listFiltree, Integer.parseInt(critere) );
			} catch(NumberFormatException e) {
				System.out.println("cannot parse l'annee en entier, vraiment pas méchant, voir normal execution");
			}
			*/
		}
			
		return listFiltree;
	}
		
	public List<Stagiaire> filtreParNom( List<Stagiaire> listeEntree, String nom) {
		
		if (nom.isBlank())
			return listeEntree;
		
		List<Stagiaire> listeSortie = new ArrayList<Stagiaire>();
		for( Stagiaire stagiaire : listeEntree)
			//if( stagiaire.getNom().equalsIgnoreCase(nom))
			if( stagiaire.getNom().toUpperCase().indexOf( nom.toUpperCase())!= -1)
				listeSortie.add( stagiaire );
		
		return listeSortie;
	}

	public List<Stagiaire> filtreParPrenom( List<Stagiaire> listeEntree, String prenom) {
		
		if (prenom.isBlank())
			return listeEntree;
		
		List<Stagiaire> listeSortie = new ArrayList<Stagiaire>();
		for( Stagiaire stagiaire : listeEntree)
			//if( stagiaire.getPrenom().equalsIgnoreCase(prenom))
			if( stagiaire.getPrenom().toUpperCase().indexOf( prenom.toUpperCase())!= -1)
				listeSortie.add( stagiaire );
		
		return listeSortie;
	}
	
	public List<Stagiaire> filtreParDepartement( List<Stagiaire> listeEntree, String departement) {
		
		if( departement.isBlank())
			return listeEntree;
		
		List<Stagiaire> listeSortie = new ArrayList<Stagiaire>();
		for( Stagiaire stagiaire : listeEntree)
			//if( stagiaire.getDepartement().equalsIgnoreCase(departement))
			if( stagiaire.getDepartement().toUpperCase().indexOf( departement.toUpperCase())!= -1)
				listeSortie.add( stagiaire );
		
		return listeSortie;
	}
	
	public List<Stagiaire> filtreParFormation( List<Stagiaire> listeEntree, String formation) {
		
		if( formation.isBlank() )
			return listeEntree;
					
		List<Stagiaire> listeSortie = new ArrayList<Stagiaire>();
		for( Stagiaire stagiaire : listeEntree)
			//if( stagiaire.getFormation().equalsIgnoreCase(formation))
			if( stagiaire.getFormation().toUpperCase().indexOf( formation.toUpperCase())!= -1)
				listeSortie.add( stagiaire );
		
		return listeSortie;
	}
	
	public List<Stagiaire> filtreParAnnee( List<Stagiaire> listeEntree, int annee) {
		
		if( annee == 0)
			return listeEntree;
		
		List<Stagiaire> listeSortie = new ArrayList<Stagiaire>();
		for( Stagiaire stagiaire : listeEntree)
			if( stagiaire.getAnnee() == annee)  
				listeSortie.add( stagiaire );
		
		return listeSortie;
	}
	
	
	public void readTxtFichier() {
		BufferedReader br;
		String[] stagiaireLinearray = new String[5];
		int i = 0;

		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/stagiaires.txt"),
					StandardCharsets.UTF_8));
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
			setStagiairelist(quickSort(getStagiairelist(), 0, getStagiairelist().size()));
		}

	}

	private void compteTailleChamps(Stagiaire stagiaire) {
		String[] str = stagiaire.toString().split(";");
		for (int i = 0; i < stagiaire.toString().length() - str.length; i++) {

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

	static List<Stagiaire> quickSort(List<Stagiaire> tabATrier, int indiceDebut, int indiceFin) {
		int positionPivot = 0;
		if (((indiceFin - 1) - indiceDebut) > 0) {
			positionPivot = partition(tabATrier, indiceDebut, indiceFin);
		} else {
			return (tabATrier);
		}
		quickSort(tabATrier, indiceDebut, positionPivot);
		return quickSort(tabATrier, positionPivot + 1, indiceFin);
	}

	static int partition(List<Stagiaire> tabATrier, int indiceDebut, int indiceFin) {
		int j = indiceDebut;
		int dernier = indiceFin - 1;
		for (int i = indiceDebut; i < dernier; i++) {
			if (tabATrier.get(i).getNom().compareTo(tabATrier.get(dernier).getNom()) <= 0) {
				permute(tabATrier, i, j);
				j++;
			}
		}
		permute(tabATrier, dernier, j);
		return j;
	}

	static void permute(List<Stagiaire> tab, int ind1, int ind2) {
		Stagiaire buffer = tab.get(ind1);
		tab.set(ind1, tab.get(ind2));
		tab.set(ind2, buffer);
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

	public Stagiaire getStagiaire() {
		return stagiaire;
	}

	public void setStagiaire(Stagiaire stagiaire) {
		this.stagiaire = stagiaire;
	}

	public List<Stagiaire> getStagiairelist() {
		return stagiairelist;
	}
	
	public List<Stagiaire> getStagiaireListFromArbre() {
		ArbreBinaire ab = new ArbreBinaire();
		return ab.getStagiaireOrdreAlphabetique();
	}

	public void setStagiairelist(List<Stagiaire> stagiairelist) {
		this.stagiairelist = stagiairelist;
	}
}
