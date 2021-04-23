package main.java;

public class Stagiaire implements Comparable<Stagiaire> {
	private String nom;
	private String prenom;
	private String departement;
	private String formation;
	private int annee;

	public Stagiaire(String nom, String prenom, String departement, String formation, int annee) {
		super();
		setNom(nom);
		setPrenom(prenom);
		setDepartement(departement);
		setFormation(formation);
		setAnnee(annee);
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom.toUpperCase();
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getDepartement() {
		return departement;
	}

	public void setDepartement(String departement) {
		this.departement = departement;
	}

	public String getFormation() {
		return formation;
	}

	public void setFormation(String formation) {
		this.formation = formation;
	}

	public int getAnnee() {
		return annee;
	}

	public void setAnnee(int annee) {
		this.annee = annee;
	}

	@Override
	public String toString() {
		return nom + ";" + prenom + ";" + departement + ";" + formation + ";" + annee;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + annee;
		result = prime * result + ((departement == null) ? 0 : departement.hashCode());
		result = prime * result + ((formation == null) ? 0 : formation.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((prenom == null) ? 0 : prenom.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Stagiaire other = (Stagiaire) obj;
		if (annee != other.annee) {
			return false;
		}
		if (departement == null) {
			if (other.departement != null) {
				return false;
			}
		} else if (!departement.equals(other.departement)) {
			return false;
		}
		if (formation == null) {
			if (other.formation != null) {
				return false;
			}
		} else if (!formation.equals(other.formation)) {
			return false;
		}
		if (nom == null) {
			if (other.nom != null) {
				return false;
			}
		} else if (!nom.equals(other.nom)) {
			return false;
		}
		if (prenom == null) {
			if (other.prenom != null) {
				return false;
			}
		} else if (!prenom.equals(other.prenom)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Stagiaire o) {
		// code genrated by eclipse
		// TODO Auto-generated method stub
		// return 0;
		//return o.getNom().toUpperCase().compareTo( this.getNom().toUpperCase());
		return this.getNom().toUpperCase().compareTo( o.getNom().toUpperCase() );
	}
}
