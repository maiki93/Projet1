package main.java;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListFilter;

/**
 * full name ArbreDeRechercheBinaireSurDisque
 * 
 * @author michael
 */
public class ArbreBinaire {

	private static final String WORKDIR = System.getProperty("user.dir");
	private static String nomFichier = "/src/main/resources/arbre.bin";
	private RandomAccessFile raf;
	private int metaTailleNom;
	private int metaTaillePrenom;
	private int metaTailleDepartement;
	private int metaTailleFormation;
	private final static int tailleHeader = 4 * 4; // always 4 integers
	private final static int tailleFix = 4 + 2 * 8; // plus fixed 1 integer(annee) + 2 long for childs
	private int tailleEnregistrement; // sum of all fields, computed inside class

	private int profondeurArbre; // number of level
	private long nextFreePosition = 0; // variable incremented at each write of a node
	private long ParentPosition;

	static public boolean fichierExists() {
		File fichierPrecedent = new File(WORKDIR + nomFichier);
		if (fichierPrecedent.exists())
			return true;
		return false;
	}

	/**
	 * Constructeur à appeler pour la création/recréation de l'arbre sur le disque.
	 */
	public ArbreBinaire(int metaTailleNom, int metaTaillePrenom, int metaTailleDepartement, int metaTailleFormation) {
		this();
		this.metaTailleNom = metaTailleNom;
		this.metaTaillePrenom = metaTaillePrenom;
		this.metaTailleDepartement = metaTailleDepartement;
		this.metaTailleFormation = metaTailleFormation;
		// sum of meta data
		this.tailleEnregistrement = metaTailleNom + metaTaillePrenom + metaTailleDepartement + metaTailleFormation;
		// plus fixed info 1 integer + 2 long for childs
		this.tailleEnregistrement += tailleFix;
		System.out.println("taille Enregistrement: " + tailleEnregistrement);
	}

	/**
	 * constructeur à appeler pour la lecture, ou la modification d'un arbre
	 * existant
	 */
	public ArbreBinaire() {
		this.profondeurArbre = 0;
		// si le fichier est déjà présent les metadonnees sont lues dans le header
		// elles seront récrasées en cas d'appel au constructeur avec arguments
		boolean headerAvailable = false;
		if (ArbreBinaire.fichierExists())
			headerAvailable = true;

		try {
			raf = new RandomAccessFile(WORKDIR + nomFichier, "rw"); // fileNot..Exception
			if (headerAvailable) {
				readHeader();
				// sum of meta data
				this.tailleEnregistrement = metaTailleNom + metaTaillePrenom + metaTailleDepartement
						+ metaTailleFormation;
				// plus fixed info 1 integer + 2 long for childs
				this.tailleEnregistrement += 4 + 2 * 8;
				System.out.println("taille Enregistrement const default: " + tailleEnregistrement);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ecrit les stagiaires dans une structure d'arbre en ordre par niveau (level
	 * order) Les noeuds sont écrit par niveaux, puis les noeuds du niveau suivant
	 * 
	 * @param listStagiaireTriee
	 */
	public void createBinFile(List<Stagiaire> listStagiaireTriee) {
		try {
			writeHeader();
			writeAllNodesToFile(listStagiaireTriee);

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<Stagiaire> getStagiaireOrdreAlphabetique() {
		List<Stagiaire> listStagiaires = new ArrayList<Stagiaire>();
		try {
			// the root node is always the first record
			recursiveInorderTraversalFillList(0, listStagiaires);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listStagiaires;
	}

	// function de test surtout
	public void printStagiaireOrdreAlphabetique() {
		try {
			// the root node is always the first record
			recursiveInorderTraversal(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void recursiveInorderTraversalFillList(long addressNode, List<Stagiaire> listToFill) throws IOException {

		NodeStagiaire parent = readOneNode(addressNode);
		if (parent.hasChildLeft())
			recursiveInorderTraversalFillList(parent.getChildLeft(), listToFill);
		//// this part need to be more functional
		// System.out.println( parent.getStagiaire().toString() );
		listToFill.add(parent.getStagiaire());
		////
		if (parent.hasChildRight())
			recursiveInorderTraversalFillList(parent.getChildRight(), listToFill);
	}

	private void recursiveInorderTraversal(long addressNode) throws IOException {

		NodeStagiaire parent = readOneNode(addressNode);
		if (parent.hasChildLeft())
			recursiveInorderTraversal(parent.getChildLeft());
		//// this part need to be more functional
		System.out.println(parent.getStagiaire().toString());
		if (parent.hasChildRight())
			recursiveInorderTraversal(parent.getChildRight());
	}

	private void writeAllNodesToFile(List<Stagiaire> listStagiaireTriee) throws IOException {
		// initialize for the first execution in the while loop for level 0
		List<List<Stagiaire>> listOfListOfStagiaire = new ArrayList<List<Stagiaire>>();
		listOfListOfStagiaire.add(listStagiaireTriee);
		int lvl = 0;

		while (true) {
			lvl++;
			// System.out.println("\n==Level "+ lvl++);
			listOfListOfStagiaire = writeNodesAtLevel(listOfListOfStagiaire);
			// System.out.println("listOfListOfStagiaire "+ listOfListOfStagiaire);
			// exit the loop if there is no more nodes to write
			if (listOfListOfStagiaire.size() == 0) {
				System.out.println("empty ListOfList nothing more treat1 :");
				break;
			}
		}
		this.profondeurArbre = lvl;
		System.out.println("Arbre possède " + lvl + " niveaux");
	}

	public List<Stagiaire> searchStagiaireParNom(String name) {

		List<Stagiaire> list = null;
		try {
			list = iterativeSearchByKey(name, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	// Case equal go the left child, order of duplicates is inversed in putting to the left
	public List<Stagiaire> iterativeSearchByKey(String key, long adressNode) throws IOException {

		List<Stagiaire> listFound = new ArrayList<Stagiaire>();
		NodeStagiaire currentNode = null;

		do {
			// duplicates allowed, still continue to the leaves
			currentNode = readOneNode(adressNode);
			System.out.println(currentNode);
			if (currentNode.getStagiaire().getNom().toUpperCase().compareTo(key.toUpperCase()) == 0) {
				listFound.add(currentNode.getStagiaire());
				System.out.println("trouver : "+ key);
				System.out.println(currentNode.getStagiaire().getNom().toUpperCase().compareTo(key.toUpperCase()) == 0);				
			}
			// <=> key <= current.nom, go to left 
			if (currentNode.getStagiaire().getNom().toUpperCase().compareTo(key.toUpperCase()) >= 0) {
				adressNode = currentNode.getChildLeft();
			// key> nom should go to right
			} else {
				adressNode = currentNode.getChildRight();
			}
		} while (adressNode != 0); // currentNode is a leave

		return listFound;
	}

	/**
	 * Write all nodes of a level
	 * 
	 * @param listOfListOfStagiaireTriee
	 * @param bStillToWrite
	 * @return
	 * @throws IOException
	 */
	private List<List<Stagiaire>> writeNodesAtLevel(List<List<Stagiaire>> listOfListOfStagiaireTriee)
			throws IOException {
		// System.out.println("size of List of List: " +
		// listOfListOfStagiaireTriee.size());
		// System.out.println("ListOfList "+ listOfListOfStagiaireTriee);
		// loop over all the nodes of this level
		int median = 0;
		boolean bChildLeft = false;
		boolean bChildRight = false;

		List<List<Stagiaire>> newList = new ArrayList<List<Stagiaire>>();
		// loop over the nodes of this level
		for (List<Stagiaire> listOfStagiaire : listOfListOfStagiaireTriee) {
			bChildLeft = false;
			bChildRight = false;
			median = listOfStagiaire.size() / 2;
			
			// Deal with duplicates, no duplicates should be on the right
			// not a problem if median is the last one : 
			// - 1. It is maybe at the left of a duplicate
			if( median < listOfStagiaire.size()-1 ) 
				median = checkErrorMedian( listOfStagiaire, median);
			
			// add 0,1 or 2 new sublists around the node if there are childs to write to
			// the next level
			if (median > 0) {// there is at least one element on the left
				newList.add(listOfStagiaire.subList(0, median));
				bChildLeft = true;
			}
			if (median < listOfStagiaire.size() - 1) { // at least one element on the right
				newList.add(listOfStagiaire.subList(median + 1, listOfStagiaire.size()));
				bChildRight = true;
			}
			writeOneNode(listOfStagiaire.get(median), bChildLeft, bChildRight);
		}
		return newList;
	}
	
	// Check for bug in the construction, some duplicates are put at the right of the median
	// all duplicates should be at the left !!
	private int checkErrorMedian(List<Stagiaire> listOfStagiaire, int median) throws IOException {
		
		String nameMedian = listOfStagiaire.get(median).getNom();
		String nameMedianPlusOne = listOfStagiaire.get(median+1).getNom();
		
		// try to correct
		int newMedian = median;
		// Should be only equal, never >
		if( nameMedian.compareTo(nameMedianPlusOne) >= 0) {
			//System.out.println("ERROR nameMedian: " + nameMedian + ", median=" + median);
			//System.out.println("name median+1: " + nameMedianPlusOne);
			//throw new IOException("ERROR in median : " + nameMedian + " " + nameMedianPlusOne );
			return newMedian = correctMedian(listOfStagiaire, median);
		}
		// no need to change the median
		return newMedian;
	}
	
	private int correctMedian(List<Stagiaire> listOfStagiaire, int median) throws IOException {
		// median must be incremented to get all duplicates on its left
		// or arrive at the boundary, it is fine
		int incrementMedian = median + 1; 
		String medianName = listOfStagiaire.get(median).getNom();
		String medianNamePlusOne = listOfStagiaire.get(median+1).getNom();
		// condition si size of the list
		while( (medianName.compareTo(medianNamePlusOne) == 0) &&
			   ( incrementMedian < listOfStagiaire.size()-1 ) )  {
			incrementMedian++;
			medianNamePlusOne = listOfStagiaire.get(incrementMedian).getNom();
		}
		//System.out.println("Found correct median : "+ (int)(incrementMedian) );
		return incrementMedian;
	}

	public NodeStagiaire addStagiaire(Stagiaire stagiaire) throws IOException {
		NodeStagiaire root = readOneNode(0);
		NodeStagiaire ns = findParentStagiaire(stagiaire, root);
		return ns;
	}

	public NodeStagiaire findParentStagiaire(Stagiaire stagiaire, NodeStagiaire root) throws IOException {
		/*
		System.out.println("Find parent stagiaire pour ajouter");
		System.out.println(root.getStagiaire().compareTo(stagiaire));
		System.out.println(root.getStagiaire());
		System.out.println("Enfant gauche" + root.getChildLeft());
		System.out.println("Enfant droite" + root.getChildRight());
		*/
		// cas egale a gauche
		// attention au recalcul de la taille des champs

		if (root.getStagiaire().compareTo(stagiaire) < 0 && root.getChildRight() != 0) {
			this.ParentPosition = root.getChildRight();
			findParentStagiaire(stagiaire, readOneNode(root.getChildRight()));
		} else if (root.getStagiaire().compareTo(stagiaire) >= 0 && root.getChildLeft() != 0) {
			this.ParentPosition = root.getChildLeft();
			findParentStagiaire(stagiaire, readOneNode(root.getChildLeft()));
		} else if (root.getStagiaire().compareTo(stagiaire) >=	 0 && root.getChildLeft() == 0) {
			long pos = raf.length();
			/*
			System.out.println("*** Arbre de gauche pour : " + stagiaire.getNom());
			System.out.println("Taille enregistrement: " + this.tailleEnregistrement);
			System.out.println("Taille header: " + this.tailleHeader);
			System.out.println("Taille du fichier: " + pos);
			System.out.println("Parent de ce stagiaire: " + root.getStagiaire().getNom());
			System.out.println("Position du pointeur: "+raf.getFilePointer());
			System.out.println("Parent position: " + this.ParentPosition);
			System.out.println("Enfant gauche" + root.getChildLeft());
			System.out.println("Enfant droite" + root.getChildRight());
			*/

			long positionFile = (pos - tailleHeader) / tailleEnregistrement;
			NodeStagiaire stagiaireParentNode = new NodeStagiaire(root.getStagiaire(), positionFile,
					root.getChildRight());
			//System.out.println("Position file: " + positionFile);
			reWriteOneNode(stagiaireParentNode, raf.getFilePointer() - this.tailleEnregistrement);
			NodeStagiaire stagiaireChildNode = new NodeStagiaire(stagiaire, 0L, 0L);
			//System.out.println("----------------------------- gauche");
			reWriteOneNode(stagiaireChildNode, pos);
			return root;
		} else if (root.getStagiaire().compareTo(stagiaire) < 0 && root.getChildRight() == 0) {
			/*
			System.out.println("** Arbre de droite pour : " + stagiaire.getNom());
			System.out.println("taille enregistrement: " + this.tailleEnregistrement);
			System.out.println("taille header: " + this.tailleHeader);
			System.out.println("parent du stagiaire: " + root.getStagiaire().getNom());
			System.out.println("rapport: " + root.getStagiaire().getNom());
			System.out.println("Positionnement du pointeur: " + raf.getFilePointer());
			System.out.println("parent position: " + this.ParentPosition);
			System.out.println("Enfant gauche" + root.getChildLeft());
			System.out.println("Enfant droite" + root.getChildRight());
			*/

			long pos = raf.length();
			//System.out.println("taille du fichier: " + pos);
			long positionFile = (pos - tailleHeader) / tailleEnregistrement;
			NodeStagiaire stagiaireParentNode = new NodeStagiaire(root.getStagiaire(), root.getChildLeft(),
					positionFile);
			//System.out.println("position file: " + positionFile);
			reWriteOneNode(stagiaireParentNode, raf.getFilePointer() - this.tailleEnregistrement);
			NodeStagiaire stagiaireChildNode = new NodeStagiaire(stagiaire, 0L, 0L);
			System.out.println();
			//System.out.println("----------------------------- droite");
			reWriteOneNode(stagiaireChildNode, pos);
			return root;
		}/*else {
			System.out.println("aucun traitement ------------------------");
		}*/
		return null;
	}
	
	///////////////////////////////////////////////
	// Exact Stagiaire to remove, should not have duplicates
	public boolean removeStagiaire( Stagiaire stagiaire) throws IOException {
		System.out.println("Stagiaire to remove: "+ stagiaire);
		
		boolean retour =false;
		NodeStagiaire dummy = null;
		// not really needed a List, convenient for debug
		List< Map.Entry<Long, Boolean>> positionFileHistory = new ArrayList<>();
		NodeStagiaire root = readOneNode( 0 );
		try {
			retour = binaryTreeDelete(root , dummy, positionFileHistory, stagiaire);
		} catch(IOException e) {
			e.printStackTrace();
			System.err.println("ERROR dans la suppression de "+ stagiaire);
			return false;
		}
		System.out.println("retour from binaryTreeDelete: "+ retour);
		return retour;
	}
	
	private boolean binaryTreeDelete(NodeStagiaire nodeCurrent ,NodeStagiaire nodeParent, List<Map.Entry<Long, Boolean>> posHistory, Stagiaire stagiaireToRemove) throws IOException {
		
		boolean retour = false;
		NodeStagiaire nodeChild = null;
		// First part only way to reconstruct the history : it should be positioned at the end of the current
		// Second part : should always be the position in file of the current node in the second part of the function
		long posFileCurrent = raf.getFilePointer(); /* -tailleEnregistrement to be at the start of the record*/
		
		if( stagiaireToRemove.compareTo( nodeCurrent.getStagiaire()) < 0) {
			// should test it exists , error if the entry does not exist
			// save before the reading, we'll have the start of the record
			posHistory.add(Pair.of( raf.getFilePointer()-tailleEnregistrement, false) );
			// should test it exists ?
			nodeChild = readOneNode( nodeCurrent.getChildLeft() );
			retour =  binaryTreeDelete( nodeChild , nodeCurrent, posHistory, stagiaireToRemove );
			return retour;
		}
		// key > nodeCurrent.key, go to the right
		if( stagiaireToRemove.compareTo(nodeCurrent.getStagiaire()) > 0) {
			posHistory.add( Pair.of( raf.getFilePointer()-tailleEnregistrement, true) ); // true is right
			nodeChild = readOneNode( nodeCurrent.getChildRight() );
			retour = binaryTreeDelete( nodeChild, nodeCurrent, posHistory, stagiaireToRemove );
			return retour;
		}
		///////////// deletetion of key
		//System.out.println("On est au node current: "+ nodeCurrent);
		//System.out.println("Parent:  "+ nodeParent);
		//System.out.println("position history: " + posHistory);
		
		// 2 childs, 2 choices find maximum in the left subtree
		if( nodeCurrent.hasChildLeft() && nodeCurrent.hasChildRight()) {
			//System.out.println("2 children");
			//nodeParent = not used anymore
			NodeStagiaire maxChild = null;
			
			// search the node with max value on the left, 
			// default the first one on the left
			nodeChild = readOneNode( nodeCurrent.getChildLeft() );
			// nodeChild is not updated as parameter ?? need to return
			maxChild = findMaxInSubtree( nodeChild );
			// maxChild contains the next stagiaire to be deleted by recursivity, save it
			stagiaireToRemove = maxChild.getStagiaire();
			// node with max value will take the place of the deleted one (current), update of its children
			// maxChild sera le prochain nodeParent
			maxChild.setChildLeft(nodeCurrent.getChildLeft());
			maxChild.setChildRight(nodeCurrent.getChildRight());
			reWriteOneNode(maxChild, posFileCurrent - tailleEnregistrement );
			
			// and now delete the node with max values in the subtree, recreate history : always a left child
			posHistory = new ArrayList<>();
			posHistory.add( Pair.of(posFileCurrent - tailleEnregistrement, false)); //false is left
			
			// RAF should be positioned at the end of the parent for entering in the recursive function
			nodeChild = readOneNode(maxChild.getChildLeft());
			retour = binaryTreeDelete( nodeChild, maxChild, posHistory, stagiaireToRemove);
			
		} else if( nodeCurrent.hasChildLeft() ) {
			//System.out.println("current has 1 child left");
			long positionToWrite = tailleHeader;
			long childPosition = nodeCurrent.getChildLeft();
			
			if( !posHistory.isEmpty() ) { 
				// we have to indicate the new children position of the parentNode
				changeChildrenOfParentNode(nodeParent, posHistory, childPosition);
				positionToWrite = posHistory.get(posHistory.size()-1).getKey();
				reWriteOneNode(nodeParent,positionToWrite);
			// case of root, must rewrite the child at the first position 
			// to keep a new root as the first record and and the tree accessible
			} else {
				nodeParent = readOneNode( nodeCurrent.getChildLeft());
				reWriteOneNode( nodeParent, positionToWrite );
			}
			retour = true;
			
		} else if( nodeCurrent.hasChildRight() ) {
			//System.out.println("1 child right");
			long positionToWrite = tailleHeader;
			long childPosition = nodeCurrent.getChildRight();
			
			if( !posHistory.isEmpty() ) { 
				// we have to indicate the children position of the parentNode
				changeChildrenOfParentNode(nodeParent, posHistory, childPosition);
				positionToWrite = posHistory.get(posHistory.size()-1).getKey();
				reWriteOneNode(nodeParent, positionToWrite);
			// case of root, must rewrite the child at the first position to keep root as the first record and keep accessible
			} else {
				nodeParent = readOneNode( nodeCurrent.getChildRight());
				reWriteOneNode( nodeParent, positionToWrite );
			}
			retour = true;
			
		// no child, case root to take into account ??
		} else {
			//System.out.println("0 child");
			System.out.println("posHistory " + posHistory);
			long positionToWrite = tailleHeader;
			// remonter au parent et lui effacer son lien gauche ou droite
			changeChildrenOfParentNode( nodeParent, posHistory, 0);
			positionToWrite = posHistory.get(posHistory.size()-1).getKey();
			reWriteOneNode( nodeParent, positionToWrite);
			retour = true;
		}
		//System.out.println("Retour binary_tree: " + nodeCurrent );
		return retour;
	}
	
	public void changeChildrenOfParentNode(NodeStagiaire nodeParent, List<Map.Entry<Long, Boolean>> posHistory,
			long childPosition) {
		// should not be the root node, checked before
		//if( ! posHistory.isEmpty()) { // no history we are on the root node of the tree
		if( ! posHistory.get(posHistory.size()-1).getValue() ) // false is left
			nodeParent.setChildLeft( childPosition );
		else // true is right
			nodeParent.setChildRight( childPosition );
	}
	
	// node with  maximum value in the subtree 
	private NodeStagiaire findMaxInSubtree(NodeStagiaire currentNode) throws IOException {
		// max always on the right
		while( currentNode.hasChildRight() ) {
			currentNode = readOneNode( currentNode.getChildRight() );
		} 
		return currentNode;
	}

	private void reWriteOneNode(NodeStagiaire nodeStagiaire, long positionwrite) throws IOException {
		raf.seek(positionwrite);
		raf.write(formatStringToBytes(nodeStagiaire.getStagiaire().getNom(), metaTailleNom));
		raf.write(formatStringToBytes(nodeStagiaire.getStagiaire().getPrenom(), metaTaillePrenom));
		raf.write(formatStringToBytes(nodeStagiaire.getStagiaire().getDepartement(), metaTailleDepartement));
		raf.write(formatStringToBytes(nodeStagiaire.getStagiaire().getFormation(), metaTailleFormation));
		raf.writeInt(nodeStagiaire.getStagiaire().getAnnee());
		raf.writeLong(nodeStagiaire.getChildLeft());
		raf.writeLong(nodeStagiaire.getChildRight());
	}
	
	// could be implemented in NodeStagiaire ,much cleaner
	private void writeOneNode(Stagiaire stagiaire, boolean childLeft, boolean childRight) throws IOException {
		// System.out.println("stagiaire: " + stagiaire);
		// System.out.println("file pointer : " + raf.getFilePointer() );
		// System.out.println("nextFreePosition: " + nextFreePosition);
		// System.out.println("childLeft: " + childLeft + ",childRight: " + childRight);
		raf.write(formatStringToBytes(stagiaire.getNom(), metaTailleNom));
		raf.write(formatStringToBytes(stagiaire.getPrenom(), metaTaillePrenom));
		raf.write(formatStringToBytes(stagiaire.getDepartement(), metaTailleDepartement));
		raf.write(formatStringToBytes(stagiaire.getFormation(), metaTailleFormation));
		raf.writeInt(stagiaire.getAnnee());
		/////////////// Child's position
		if (childLeft)
			raf.writeLong(++nextFreePosition);
		else
			raf.writeLong(0L);
		///////////////
		if (childRight)
			raf.writeLong(++nextFreePosition);
		else
			raf.writeLong(0L);
	}

	private NodeStagiaire readOneNode(long positionEnregistrement) throws IOException {
		// position in bytes in the file
		long positionFile = positionEnregistrement * tailleEnregistrement + tailleHeader;
		raf.seek(positionFile);
		byte[] bNom = new byte[metaTailleNom];
		byte[] bPrenom = new byte[metaTaillePrenom];
		byte[] bDepartement = new byte[metaTailleDepartement];
		byte[] bFormation = new byte[metaTailleFormation];
		raf.read(bNom);
		raf.read(bPrenom);
		raf.read(bDepartement);
		raf.read(bFormation);
		int annee = raf.readInt();
		long child1 = raf.readLong();
		long child2 = raf.readLong();

		Stagiaire stage = new Stagiaire(new String(bNom).trim(), new String(bPrenom).trim(),
				new String(bDepartement).trim(), new String(bFormation).trim(), annee);

		return new NodeStagiaire(stage, child1, child2);
	}

	private void writeHeader() throws IOException {
		raf.seek(0);
		raf.writeInt(this.metaTailleNom);
		raf.writeInt(this.metaTaillePrenom);
		raf.writeInt(this.metaTailleDepartement);
		raf.writeInt(this.metaTailleFormation);
	}

	private void readHeader() throws IOException {
		this.metaTailleNom = raf.readInt();
		this.metaTaillePrenom = raf.readInt();
		this.metaTailleDepartement = raf.readInt();
		this.metaTailleFormation = raf.readInt();
	}

	private byte[] formatStringToBytes(String champ, int tailleMax) {
		byte[] bytes = new byte[tailleMax];
		byte[] field = champ.getBytes(); // .CharSet("Cpp1252") si probleme d'encodage
		// Copy the first caracters, others are initialzed to zero by default
		for (int i = 0; i < field.length; i++) // CharSet("Cpp1252");
			bytes[i] = field[i];
		return bytes;
	}

}
