package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
/**
 * full name ArbreDeRechercheBinaireSurDisque
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
	private final static int tailleHeader = 4*4; // always 4 integers
	private int tailleEnregistrement; // sum of all fields, computed inside class
	
	private int profondeurArbre; // number of level
	private long nextFreePosition = 0; // variable incremented at each write of a node
	
	/** Constructeur à appeler pour la création/recréation de l'arbre sur le disque. */
	public ArbreBinaire(int metaTailleNom, int metaTaillePrenom, 
			int metaTailleDepartement, int metaTailleFormation) {
		this();
		this.metaTailleNom = metaTailleNom;
		this.metaTaillePrenom = metaTaillePrenom;
		this.metaTailleDepartement = metaTailleDepartement;
		this.metaTailleFormation = metaTailleFormation;
		// sum of meta data
		this.tailleEnregistrement =  metaTailleNom + metaTaillePrenom+ metaTailleDepartement + metaTailleFormation;
		// plus fixed info 1 integer + 2 long for childs
		this.tailleEnregistrement += 4 + 2*8;
		System.out.println("taille Enregistrement: "+ tailleEnregistrement);
	}
	
	/** constructeur à appeler pour la lecture, ou la modification d'un arbre existant */
	public ArbreBinaire() {
		this.profondeurArbre = 0;
		// si le fichier est déjà présent les metadonnees sont lues dans le header
		// elles seront récrasées en cas d'appel au constructeur avec arguments 
		boolean headerAvailable = false;		
		File fichierPrecedent = new File(WORKDIR+nomFichier);
		if (  fichierPrecedent.exists() )
			headerAvailable = true;
		
		try {
			raf = new RandomAccessFile(WORKDIR+nomFichier,"rw"); // fileNot..Exception
			if( headerAvailable) {
				readHeader();
				// sum of meta data
				this.tailleEnregistrement =  metaTailleNom + metaTaillePrenom+ metaTailleDepartement + metaTailleFormation;
				// plus fixed info 1 integer + 2 long for childs
				this.tailleEnregistrement += 4 + 2*8;
				System.out.println("taille Enregistrement const default: "+ tailleEnregistrement);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ecrit les stagiaires dans une structure d'arbre en ordre par niveau (level order)
	 * Les noeuds sont écrit par niveaux, puis les noeuds du niveau suivant
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
	
	public void printOrderAlphabetique() { 
		try {
			// the root node is always the first record
			iterativeInorderTraversal( 0 );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void iterativeInorderTraversal( long addressNode ) throws IOException {
		
		NodeStagiaire parent = readOneNode( addressNode );
		if( parent.hasChildLeft() )
			iterativeInorderTraversal( parent.getChildLeft() );
		//// this part need to be more functional
		System.out.println( parent.getStagiaire().toString() );
		////
		if( parent.hasChildRight() )
			iterativeInorderTraversal( parent.getChildRight() );
	}
	
	private void writeAllNodesToFile(List<Stagiaire> listStagiaireTriee) throws IOException {
		// initialize for the first execution in the while loop for level 0
		List<List<Stagiaire>> listOfListOfStagiaire= new ArrayList<List<Stagiaire>>();
		listOfListOfStagiaire.add( listStagiaireTriee);
		int lvl = 0;
		
		while( true ) {
			//System.out.println("\n==Level "+ lvl++);
			listOfListOfStagiaire = writeNodesAtLevel( listOfListOfStagiaire );
			//System.out.println("listOfListOfStagiaire "+ listOfListOfStagiaire);
			// exit the loop if there is no more nodes to write
			if( listOfListOfStagiaire.size() == 0) {
				System.out.println("empty ListOfList nothing more treat1 :");
				break;
			}
		}
		this.profondeurArbre = lvl;
		System.out.println("Arbre possède " + lvl + " niveaux");
	}
	
	/** Write all nodes of a level
	 * @param listOfListOfStagiaireTriee
	 * @param bStillToWrite
	 * @return
	 * @throws IOException
	 */
	private List<List<Stagiaire>> writeNodesAtLevel( List<List<Stagiaire>> listOfListOfStagiaireTriee ) throws IOException {
		//System.out.println("size of List of List: " + listOfListOfStagiaireTriee.size());
		//System.out.println("ListOfList "+ listOfListOfStagiaireTriee);
		// loop over all the nodes of this level
		int median=0;
		boolean bChildLeft = false;
		boolean bChildRight = false;
		 
		List<List<Stagiaire>> newList = new ArrayList<List<Stagiaire>>();
		// loop over the nodes of this level
		for( List<Stagiaire> listOfStagiaire : listOfListOfStagiaireTriee) {
			bChildLeft = false;
			bChildRight = false;
			median = listOfStagiaire.size()/2;
			// add 0,1 or 2 new sublists aroound the node if there are childs to write to the next level
			if ( median > 0 ) {// there is at least one element on the left
				newList.add( listOfStagiaire.subList(0, median) );
				bChildLeft = true;
			}
			if ( median < listOfStagiaire.size()-1 ) { // at least one element on the right
				newList.add( listOfStagiaire.subList(median+1, listOfStagiaire.size()) );
				bChildRight = true;
			}
			writeOneNode( listOfStagiaire.get(median), bChildLeft, bChildRight );
		}
		return newList;
	}
	
	private void writeOneNode( Stagiaire stagiaire, boolean childLeft, boolean childRight) throws IOException {
		//System.out.println("stagiaire: " + stagiaire);
		//System.out.println("file pointer : " + raf.getFilePointer() );
		//System.out.println("nextFreePosition: " +  nextFreePosition);
		//System.out.println("childLeft: " + childLeft + ",childRight: " + childRight);
		raf.write( formatStringToBytes( stagiaire.getNom(), metaTailleNom));
		raf.write( formatStringToBytes( stagiaire.getPrenom(), metaTaillePrenom));
		raf.write( formatStringToBytes( stagiaire.getDepartement(), metaTailleDepartement) );
		raf.write( formatStringToBytes( stagiaire.getFormation(), metaTailleFormation) );
		raf.writeInt( stagiaire.getAnnee() );
		/////////////// Child's position
		if( childLeft)
			raf.writeLong( ++nextFreePosition );
		else 
			raf.writeLong( 0L );
		///////////////
		if( childRight)
			raf.writeLong( ++nextFreePosition );
		else
			raf.writeLong( 0L );
	}
	
	private byte[] formatStringToBytes(String champ, int tailleMax) {
		byte[] bytes = new byte[tailleMax];
		byte[] field = champ.getBytes();
		// Copy the first caracters, others are initialzed to zero by default
		for(int i = 0; i < field.length; i++ ) // CharSet("Cpp1252");
			bytes[i] = field[i];
		
		return bytes;
	}
	
	private NodeStagiaire readOneNode( long positionEnregistrement ) throws IOException {
		// position in bytes in the file
		long positionFile = positionEnregistrement * tailleEnregistrement + tailleHeader;
		raf.seek(positionFile);
		//System.out.println("readOneNode: " + positionEnregistrement + ",file pointer : " + raf.getFilePointer() );
		
		byte[] bNom = new byte[ metaTailleNom];
		byte[] bPrenom = new byte[ metaTaillePrenom];
		byte[] bDepartement = new byte[ metaTailleDepartement];
		byte[] bFormation = new byte[ metaTailleFormation];
		raf.read( bNom );
		raf.read( bPrenom );
		raf.read( bDepartement );
		raf.read( bFormation );
		int annee = raf.readInt();
		long child1 = raf.readLong();
		long child2 = raf.readLong();
		
		Stagiaire stage = new Stagiaire( 
								new String(bNom).trim(),
								new String(bPrenom).trim(),
								new String(bDepartement).trim(),
								new String(bFormation).trim(),
								annee);
		
		return new NodeStagiaire(stage, child1, child2);
	}
		
	private void writeHeader() throws IOException {
		raf.seek(0);
		raf.writeInt( this.metaTailleNom );
		raf.writeInt( this.metaTaillePrenom);
		raf.writeInt( this.metaTailleDepartement);
		raf.writeInt( this.metaTailleFormation);
	}
	
	private void readHeader() throws IOException {
		this.metaTailleNom = raf.readInt();
		this.metaTaillePrenom = raf.readInt();
		this.metaTailleDepartement = raf.readInt();
		this.metaTailleFormation = raf.readInt();
	}
}
