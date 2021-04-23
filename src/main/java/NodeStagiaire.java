package main.java;

public class NodeStagiaire {
	
	private Stagiaire stagiaire;
	private long childLeft;
	private long childRight;
	
	public NodeStagiaire(Stagiaire stagiaire, long childLeft, long childRight) {
		super();
		this.stagiaire = stagiaire;
		this.childLeft = childLeft;
		this.childRight = childRight;
	}
	
	public NodeStagiaire() {
		super();
	}
	
	@Override
	public String toString() {
		return "NodeStagiaire [stagiaire=" + stagiaire.toString() + ", childLeft=" + childLeft + ", childRight=" + childRight
				+ "]";
	}

	public Stagiaire getStagiaire() {
		return stagiaire;
	}
	
	public void setStagiaire(Stagiaire stagiaire) {
		this.stagiaire = stagiaire;
	}

	public boolean hasNoChild() {
		//if (!hasChildLeft() && !hasChildRight())
		if( hasChildLeft() || hasChildRight() )  
			return false;
		return true;
	}
	public boolean hasChildLeft() {
		if (childLeft == 0L) 
			return false;
		return true;
	}
	
	public long getChildLeft() {
		return childLeft;
	}
	
	public boolean hasChildRight() {
		if (childRight == 0L) 
			return false;
		return true;
	}
	
	public void setChildLeft(long childLeft) {
		this.childLeft = childLeft;
	}
	
	public long getChildRight() {
		return childRight;
	}
	
	public void setChildRight(long childRight) {
		this.childRight = childRight;
	}
}
