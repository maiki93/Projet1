package main.java;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

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

	public List<Stagiaire> iterativeSearchByKey(String key, long adressNode) throws IOException {

		List<Stagiaire> listFound = new ArrayList<Stagiaire>();
		NodeStagiaire currentNode = readOneNode(adressNode);

		do {
			// duplicates allowed, still continue to the leaves
			currentNode = readOneNode(adressNode);
			if (currentNode.getStagiaire().getNom().compareTo(key) == 0)
				listFound.add(currentNode.getStagiaire());
			if (currentNode.getStagiaire().getNom().toUpperCase().compareTo(key.toUpperCase()) < 0) {
				adressNode = currentNode.getChildRight();
			} else {
				adressNode = currentNode.getChildLeft();
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
			// add 0,1 or 2 new sublists aroound the node if there are childs to write to
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

	// could be implemented in NodeStagiaire ,m uch cleaner
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
		// System.out.println("readOneNode: " + positionEnregistrement + ",file pointer
		// : " + raf.getFilePointer() );

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
		List<Stagiaire> listeStagiaire = getStagiaireOrdreAlphabetique();
		listeStagiaire.size();// nombre d'enregistrement
		NodeStagiaire root = readOneNode(0);
		NodeStagiaire ns = findParentStagiaire(stagiaire, root);
		System.out.println();
		System.out.println("add Stagiaire noeud parent:" + ns);
		return ns;
	}

	public NodeStagiaire findParentStagiaire(Stagiaire stagiaire, NodeStagiaire root) throws IOException {

		if (root.getChildLeft() != 0 && root.getChildRight() != 0) {
			if (root.getStagiaire().compareTo(stagiaire) < 0) {
				this.ParentPosition = root.getChildRight();
				findParentStagiaire(stagiaire, readOneNode(root.getChildRight()));
			}
			if (root.getStagiaire().compareTo(stagiaire) > 0) {
				this.ParentPosition = root.getChildLeft();
				findParentStagiaire(stagiaire, readOneNode(root.getChildLeft()));
			}
		}

		if (root.getChildLeft() == 0 && root.getStagiaire().compareTo(stagiaire) > 0) {
			long pos = raf.length();
			System.out.println("arbre de gauche pour : " + stagiaire.getNom());
			System.out.println("rapport: " + root.getStagiaire().getNom());
			System.out.println(this.tailleEnregistrement);
			System.out.println(raf.getFilePointer());
			System.out.println("taille header: " + this.tailleHeader);
			System.out.println("taille du fichier: " + pos);
			System.out.println("parent position: " + this.ParentPosition);
			long positionFile = (pos - tailleHeader) / tailleEnregistrement;
			NodeStagiaire stagiaireParentNode = new NodeStagiaire(root.getStagiaire(), positionFile,
					root.getChildRight());
			System.out.println("position file: " + positionFile);
			reWriteOneNode(stagiaireParentNode, raf.getFilePointer() - this.tailleEnregistrement);
			NodeStagiaire stagiaireChildNode = new NodeStagiaire(stagiaire, 0L, 0L);
			reWriteOneNode(stagiaireChildNode, pos);

			return root;
		} else if (root.getChildRight() == 0 && root.getStagiaire().compareTo(stagiaire) < 0) {
			System.out.println("arbre de droite pour : " + stagiaire.getNom());
			System.out.println("parent du stagiaire: " + root.getStagiaire().getNom());
			long pos = raf.length();
			System.out.println("arbre de gauche pour : " + stagiaire.getNom());
			System.out.println("rapport: " + root.getStagiaire().getNom());
			System.out.println(this.tailleEnregistrement);
			System.out.println(raf.getFilePointer());
			System.out.println("taille header: " + this.tailleHeader);
			System.out.println("taille du fichier: " + pos);
			System.out.println("parent position: " + this.ParentPosition);
			long positionFile = (pos - tailleHeader) / tailleEnregistrement;
			NodeStagiaire stagiaireParentNode = new NodeStagiaire(root.getStagiaire(), root.getChildLeft(),
					positionFile);
			System.out.println("position file: " + positionFile);
			reWriteOneNode(stagiaireParentNode, raf.getFilePointer() - this.tailleEnregistrement);
			NodeStagiaire stagiaireChildNode = new NodeStagiaire(stagiaire, 0L, 0L);
			reWriteOneNode(stagiaireChildNode, pos);

			return root;
		}
		return null;
	}

	private void reWriteOneNode(NodeStagiaire nodeStagiaire, long positionwrite) throws IOException {
		raf.seek(positionwrite);
		raf.write(formatStringToBytes(nodeStagiaire.getStagiaire().getNom(), metaTailleNom));
		raf.write(formatStringToBytes(nodeStagiaire.getStagiaire().getPrenom(), metaTaillePrenom));
		raf.write(formatStringToBytes(nodeStagiaire.getStagiaire().getDepartement(), metaTailleDepartement));
		raf.write(formatStringToBytes(nodeStagiaire.getStagiaire().getFormation(), metaTailleFormation));
		raf.writeInt(nodeStagiaire.getStagiaire().getAnnee());
		raf.writeLong(nodeStagiaire.getChildLeft());
		raf.writeLong(this.nextFreePosition);
	}

}
