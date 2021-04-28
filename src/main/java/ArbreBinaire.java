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
			if (currentNode.getStagiaire().getNom().toUpperCase().compareTo(key.toUpperCase()) == 0)
				listFound.add(currentNode.getStagiaire());
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
			System.out.println("ERROR nameMedian: " + nameMedian + ", median=" + median);
			System.out.println("name median+1: " + nameMedianPlusOne);
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
		System.out.println("Found correct median : "+ (int)(incrementMedian) );
		return incrementMedian;
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
		//System.out.println("readOneNode: " + positionEnregistrement + ",file pointer: " + raf.getFilePointer() );

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
		byte[] field = champ.getBytes();
		// Copy the first caracters, others are initialzed to zero by default
		for (int i = 0; i < field.length; i++) // CharSet("Cpp1252");
			bytes[i] = field[i];

		return bytes;
	}

	public NodeStagiaire addStagiaire(Stagiaire stagiaire) throws IOException {
		//List<Stagiaire> listeStagiaire = getStagiaireOrdreAlphabetique();
		NodeStagiaire root = readOneNode(0);
		NodeStagiaire ns = findParentStagiaire(stagiaire, root);
		return ns;
	}

	public NodeStagiaire findParentStagiaire(Stagiaire stagiaire, NodeStagiaire root) throws IOException {
		System.out.println("Find parent stagiaire pour ecrire");
		System.out.println(root.getStagiaire().compareTo(stagiaire));
		System.out.println(root.getStagiaire());
		System.out.println("Enfant gauche" + root.getChildLeft());
		System.out.println("Enfant droite" + root.getChildRight());
		// cas egale a gauche
		// attention au recalcul de la taille des champs

		if (root.getStagiaire().compareTo(stagiaire) <= 0 && root.getChildRight() != 0) {
			this.ParentPosition = root.getChildRight();
			findParentStagiaire(stagiaire, readOneNode(root.getChildRight()));
		} else if (root.getStagiaire().compareTo(stagiaire) > 0 && root.getChildLeft() != 0) {
			this.ParentPosition = root.getChildLeft();
			findParentStagiaire(stagiaire, readOneNode(root.getChildLeft()));
		} else if (root.getStagiaire().compareTo(stagiaire) > 0 && root.getChildLeft() == 0) {
			long pos = raf.length();
			System.out.println("*** Arbre de gauche pour : " + stagiaire.getNom());
			System.out.println("Taille enregistrement: " + this.tailleEnregistrement);
			System.out.println("Taille header: " + this.tailleHeader);
			System.out.println("Taille du fichier: " + pos);
			System.out.println("Parent de ce stagiaire: " + root.getStagiaire().getNom());
			System.out.println("Position du pointeur: "+raf.getFilePointer());
			System.out.println("Parent position: " + this.ParentPosition);
			System.out.println("Enfant gauche" + root.getChildLeft());
			System.out.println("Enfant droite" + root.getChildRight());

			long positionFile = (pos - tailleHeader) / tailleEnregistrement;
			NodeStagiaire stagiaireParentNode = new NodeStagiaire(root.getStagiaire(), positionFile,
					root.getChildRight());
			System.out.println("Position file: " + positionFile);
			reWriteOneNode(stagiaireParentNode, raf.getFilePointer() - this.tailleEnregistrement);
			NodeStagiaire stagiaireChildNode = new NodeStagiaire(stagiaire, 0L, 0L);
			System.out.println("----------------------------- gauche");
			reWriteOneNode(stagiaireChildNode, pos);
			return root;
		} else if (root.getStagiaire().compareTo(stagiaire) <= 0 && root.getChildRight() == 0) {
			System.out.println("** Arbre de droite pour : " + stagiaire.getNom());
			System.out.println("taille enregistrement: " + this.tailleEnregistrement);
			System.out.println("taille header: " + this.tailleHeader);
			System.out.println("parent du stagiaire: " + root.getStagiaire().getNom());
			System.out.println("rapport: " + root.getStagiaire().getNom());
			System.out.println("Positionnement du pointeur: " + raf.getFilePointer());
			System.out.println("parent position: " + this.ParentPosition);
			System.out.println("Enfant gauche" + root.getChildLeft());
			System.out.println("Enfant droite" + root.getChildRight());

			long pos = raf.length();
			System.out.println("taille du fichier: " + pos);
			long positionFile = (pos - tailleHeader) / tailleEnregistrement;
			NodeStagiaire stagiaireParentNode = new NodeStagiaire(root.getStagiaire(), root.getChildLeft(),
					positionFile);
			System.out.println("position file: " + positionFile);
			reWriteOneNode(stagiaireParentNode, raf.getFilePointer() - this.tailleEnregistrement);
			NodeStagiaire stagiaireChildNode = new NodeStagiaire(stagiaire, 0L, 0L);
			System.out.println();
			System.out.println("----------------------------- droite");
			reWriteOneNode(stagiaireChildNode, pos);
			return root;
		}else {
			System.out.println("aucun traitement ------------------------");
		}
		return null;
	}
	
	///////////////////////////////////////////////
	// Exact Stagiaire to remove, should not have duplicates
	public boolean removeStagiaire( Stagiaire stagiaire) throws IOException {
		System.out.println("Stagiaire to remove: "+ stagiaire);
		
		NodeStagiaire memory = null;
		//long posGP = 0;
		//List<Long> positionFileHistory = new ArrayList<>();
		List< Map.Entry<Long, Boolean>> positionFileHistory = new ArrayList<>();
		NodeStagiaire root = readOneNode( 0 );
		//positionFileHistory.add((long)tailleHeader);
		//positionFileHistory.add(Pair.of( (long)tailleHeader, false) );
		
		binaryTreeDelete(root , memory, positionFileHistory, stagiaire);
		return true;
	}
	
	private boolean /*NodeStagiaire*/ binaryTreeDelete(NodeStagiaire nodeCurrent ,NodeStagiaire nodeParent, List<Map.Entry<Long, Boolean>> posHistory, Stagiaire stagiaireToRemove) throws IOException {
		
		NodeStagiaire nodeChild = null;
		// Should always be the position in file of the last current node after the first part
		long posFileCurrent = raf.getFilePointer(); /* -tailleEnregistrement -taille to be  the start of the record*/
		
		if( stagiaireToRemove.compareTo( nodeCurrent.getStagiaire()) < 0) {
			// should test it exists , error if the entry does not exist
			//posHistory.add( raf.getFilePointer()); 
			// save before the reading, we'll have the start of the record
			posHistory.add(Pair.of( raf.getFilePointer()-tailleEnregistrement, false) );
			nodeChild = readOneNode( nodeCurrent.getChildLeft() );
			// memorize 
			binaryTreeDelete( nodeChild , nodeCurrent, posHistory, stagiaireToRemove );
			return false;
		}
		// key > nodeCurrent.key, go to the right
		if( stagiaireToRemove.compareTo(nodeCurrent.getStagiaire()) > 0) {
			// should test it exists
			//posHistory.add( raf.getFilePointer() ); // 
			posHistory.add( Pair.of( raf.getFilePointer()-tailleEnregistrement, true) ); // true is right
			nodeChild = readOneNode( nodeCurrent.getChildRight() );
			
			/*nodeRetour =*/ binaryTreeDelete( nodeChild, nodeCurrent, posHistory, stagiaireToRemove );
			return false;
		}
		///////////// deletetion of key
		System.out.println("deletetion of key");
		
		System.out.println("On est au node current: "+ nodeCurrent);
		//System.out.println("avec node child(left or right?) " + nodeChild);
		System.out.println("Parent:  "+ nodeParent);
		System.out.println("position history: " + posHistory);
		System.out.println("getFilePointeur " + raf.getFilePointer());
		
		// 2 childs, 2 choices find maximum in the left subtree
		if( nodeCurrent.hasChildLeft() && nodeCurrent.hasChildRight()) {
			System.out.println("2 children");
			//nodeParent = not used anymore
			
			// search the node with max value on the left, 
			// default the first one on the left
			nodeChild = readOneNode( nodeCurrent.getChildLeft() );
			long posNodeMax = findMaxInSubtree( nodeChild /*, nodeMax*/ );
			// nodeChild contains now the next stagiaire to be deleted by recursivity
			stagiaireToRemove = nodeChild.getStagiaire();
			
			// node with max value takes the place of the deleted one (current)
			// with value max, and children of the previous
			nodeChild.setChildLeft(nodeCurrent.getChildLeft());
			nodeChild.setChildRight(nodeCurrent.getChildRight());
			reWriteOneNode(nodeChild, posFileCurrent - tailleEnregistrement );
			
			// and now delete the node with max values
			posHistory = new ArrayList<>();
			//posHistory.add( Pair.of( raf.getFilePointer()-tailleEnregistrement, true) ); // true is right
			posHistory.add( Pair.of(posFileCurrent - tailleEnregistrement, false));
			// nodeCurrent.getChild "BIC"
			//nodeParent =
			// current with new parameter becomes the parent
			nodeCurrent = nodeChild;
			nodeChild = readOneNode(nodeCurrent.getChildLeft());
			binaryTreeDelete( nodeChild, nodeCurrent, posHistory, stagiaireToRemove);
			//removeStagiaire( nodeMax.getStagiaire() );
			
		} else if( nodeCurrent.hasChildLeft() ) {
			System.out.println("current has 1 child left");
			long positionToWrite = tailleHeader;
			long childPosition = nodeCurrent.getChildLeft();
			
			if( !posHistory.isEmpty() ) { 
				// we have to indicate the new children position of the parentNode
				changeChildrenOfParentNode(nodeParent, posHistory, childPosition);
				positionToWrite = posHistory.get(posHistory.size()-1).getKey();
				reWriteOneNode(nodeParent,positionToWrite);
			// case of root, must rewrite the child at the first position to keep root as the first record and keep accessible
			} else {
				// Must rewrite at the record 0 the firstChild
				nodeParent = readOneNode( nodeCurrent.getChildLeft());
				reWriteOneNode( nodeParent, positionToWrite );
			}
			
		} else if( nodeCurrent.hasChildRight() ) {
			System.out.println("1child right");
			long positionToWrite = tailleHeader;
			long childPosition = nodeCurrent.getChildRight();
			
			if( !posHistory.isEmpty() ) { 
				// we have to indicate the children position of the parentNode
				changeChildrenOfParentNode(nodeParent, posHistory, childPosition);
				positionToWrite = posHistory.get(posHistory.size()-1).getKey();
				reWriteOneNode(nodeParent; positionToWrite);
			// case of root, must rewrite the child at the first position to keep root as the first record and keep accessible
			} else {
				// Must rewrite at the record 0 the firstChild
				nodeParent = readOneNode( nodeCurrent.getChildRight());
				reWriteOneNode( nodeParent, positionToWrite );
			}
			
		// no child, case root ??
		} else {
			System.out.println("0 child");
			// remonter au parent et lui effacer son lien gauche ou droite
			if( ! posHistory.get(posHistory.size()-1).getValue() )
				nodeParent.setChildLeft(0L);
			else
				nodeParent.setChildLeft(0L);
			reWriteOneNode(nodeParent,posHistory.get(posHistory.size()-1).getKey());
			//replaceNodeInParent( nodeParent, posHistory.get(posHistory.size()-1) );
		}
		
		System.out.println("Retour binary_tree: " + nodeCurrent );
		//return nodeCurrent;
		return true;
	}
	/*
	//private void replaceNodeInParent( NodeStagiaire node, long positionFile ) throws IOException {
	private void replaceNodeInParent( NodeStagiaire node, Map.Entry<Long, Boolean> positionFile ) throws IOException {
		System.out.println("entry replaceNodeInParent pos: "+ positionFile);
		System.out.println("node to write :" + node);
		
		// child can be modified before
		long pos = positionFile.getKey();
		//boolean child = positionFile.getValue();
		//raf.seek(positionFile);
		// test if at the good position
		long positionEnregistrement = (pos - tailleHeader)/tailleEnregistrement;
		System.out.println("position enregistrement "+ positionEnregistrement);
		//NodeStagiaire test = readOneNode(positionEnregistrement);
		//System.out.println("stage test: " + test);
		
		// modify the child info
		//if( !child ) // child false it is a left-handed child 
		//	node.setChildLeft(0L);
		//else
		//	node.setChildRight(0L);
		
		System.out.println("node to rewrite: " + node);
		System.out.println("at posFile " + pos);
		reWriteOneNode(node, pos);	
	}*/

	public void changeChildrenOfParentNode(NodeStagiaire nodeParent, List<Map.Entry<Long, Boolean>> posHistory,
			long childPosition) {
		
		if( ! posHistory.isEmpty()) { // no history we are on the root node of the tree
			if( ! posHistory.get(posHistory.size()-1).getValue() ) // false is left
				nodeParent.setChildLeft( childPosition );
			else // true is right
				nodeParent.setChildRight( childPosition );
		}
	}
	
	// node with  maximum value in the subtree on the left 
	// return a Node or a Enrgestrement Number or File position ?
	// always extreme left of the subtree
	private long findMaxInSubtree(NodeStagiaire currentNode /*, NodeStagiaire nodeMax*/) throws IOException {

		long positionMax = raf.getFilePointer() - tailleEnregistrement;
		System.out.println("currentNode first left " + currentNode);		
		//nodeMax = new NodeStagiaire( currentNode.getStagiaire(), currentNode.getChildLeft(), currentNode.getChildRight());
		
		while( currentNode.hasChildRight() ) {
			positionMax = raf.getFilePointer();
			currentNode = readOneNode( currentNode.getChildLeft() );
		} 
		//while( currentNode.hasChildLeft() );
		//System.out.println("findMax : " + nodeMax);
		System.out.println("Node current " + currentNode);
		System.out.println("positionMax :" + positionMax); // not needed
		return positionMax;
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

}
