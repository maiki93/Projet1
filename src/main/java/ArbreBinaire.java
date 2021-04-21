package main.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ArbreBinaire {
	
	private static final String WORKDIR = System.getProperty("user.dir");
	private static String nomFichier = "arbre.bin"; 
	private RandomAccessFile raf;
	private int metaTailleNom;
	private int metaTaillePrenom;
	private int metaTailleDepartement;
	private int metaTailleFormation; 
	private int tailleEnregistrement; // sum of all fields

	private long nextFreePosition = 0; // variable incremented at each write of a node
	
	public ArbreBinaire(int metaTailleNom, int metaTaillePrenom, 
			int metaTailleDepartement, int metaTailleFormation) {
		this();
		this.metaTailleNom = metaTailleNom;
		this.metaTaillePrenom = metaTaillePrenom;
		this.metaTailleDepartement = metaTailleDepartement;
		this.metaTailleFormation = metaTailleFormation;
	}
	
	public ArbreBinaire() {
		try {
			raf = new RandomAccessFile(WORKDIR+nomFichier,"rw");
		} catch (FileNotFoundException e) {
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
	
	// test function for development
	public void testReadBinFile() {
		try {
			readHeader();
			System.out.println("Header: " + metaTailleNom + " " + metaTaillePrenom + " "
					+ metaTailleDepartement + " " + metaTailleFormation);
			readOneNode(0);
			
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
	
	private void writeAllNodesToFile(List<Stagiaire> listStagiaireTriee) throws IOException {
		// initialze for the first execution in while : Level 0
		List<List<Stagiaire>> listOfListOfStagiaire= new ArrayList<List<Stagiaire>>();
		listOfListOfStagiaire.add( listStagiaireTriee);
		
		int lvl = 0;
		boolean bStillToWrite = true;
		
		while( bStillToWrite ) {
			System.out.println("\n==Level "+ lvl++);
			listOfListOfStagiaire = writeNodesAtLevel( listOfListOfStagiaire, bStillToWrite );
			
			// exit the loop if there is no more nodes to write
			System.out.println("listOfListOfStagiaire "+ listOfListOfStagiaire);
			if( listOfListOfStagiaire.size() == 0) {
				System.out.println("empty ListOfList nothing more treat1 :");
				bStillToWrite = false;
			}
		}
	}
	
	/** Write all nodes of a level
	 * @param listOfListOfStagiaireTriee
	 * @param bStillToWrite
	 * @return
	 * @throws IOException
	 */
	private List<List<Stagiaire>> writeNodesAtLevel( List<List<Stagiaire>> listOfListOfStagiaireTriee, boolean bStillToWrite) throws IOException {
		System.out.println("size of List of List: " + listOfListOfStagiaireTriee.size());
		System.out.println("ListOfList "+ listOfListOfStagiaireTriee);
		// loop over all the nodes of this level
		int median=0;
		boolean bChildLeft = false;
		boolean bChildRight = false;
		 
		List<List<Stagiaire>> newList = new ArrayList<List<Stagiaire>>();
		// loop over the nodes of this level
		for( List<Stagiaire> listOfStagiaire : listOfListOfStagiaireTriee) {
			System.out.println("In for loop listOfStagiaire.size(): " + listOfStagiaire.size());
			bChildLeft = false;
			bChildRight = false;
			median = listOfStagiaire.size()/2;
			System.out.println("median " + median);
			// add 0,1 or 2 new sublists aroound the node if there are childs to write to the next level
			if ( median > 0 ) {// there is at least one element on the left
				newList.add( listOfStagiaire.subList(0, median) );
				bChildLeft = true;
			}
			if ( median < listOfStagiaire.size()-1 ) { // at least one element on the right
				newList.add( listOfStagiaire.subList(median+1, listOfStagiaire.size()) );
				bChildRight = true;
			}
			System.out.println("childs: "+ bChildLeft + " " + bChildRight);
			writeOneNode( listOfStagiaire.get(median), bChildLeft, bChildRight );
		}
		return newList;
	}
	
	private void writeOneNode( Stagiaire stagiaire, boolean childLeft, boolean childRight) throws IOException {
		System.out.println("stagiaire: " + stagiaire);
		
		System.out.println("file pointer : " + raf.getFilePointer() );
		System.out.println("nextFreePosition: " +  nextFreePosition);
		
		byte[] bytesNom = new byte[metaTailleNom];
		byte[] bField = stagiaire.getNom().getBytes(); // CharSet("Cpp1252");
 		// Copy the first caracters, others are initialzed to zero by default
		for(int i = 0; i < bField.length; i++ )
			bytesNom[i] = bField[i];
		
		//if( bField.length != ligneSplittee.length() )
		//	throw new IOException("Erreur de mon encodage en Cp1252 tailleMax" 
		//			+ tailleMax + " " + bField.length  + "!=" + ligneSplittee.length() );

		byte[] bytesPrenom = new byte[metaTaillePrenom];
		byte[] bField2 = stagiaire.getPrenom().getBytes();
		for(int i = 0; i < bField2.length; i++ )
			bytesPrenom[i] = bField2[i];
		
		byte[] bytesDepartement = new byte[metaTailleDepartement];
		byte[] bField3 = stagiaire.getDepartement().getBytes();
		for(int i = 0; i < bField3.length; i++ )
			bytesPrenom[i] = bField3[i];
		
		byte[] bytesFormation = new byte[metaTailleFormation];
		byte[] bField4 = stagiaire.getFormation().getBytes();
		for(int i = 0; i < bField4.length; i++ )
			bytesFormation[i] = bField4[i];
		
		raf.write( bytesNom );
		raf.write( bytesPrenom);
		raf.write( bytesDepartement );
		raf.write( bytesFormation );
		raf.writeInt( stagiaire.getAnnee() );
		///////////////
		if( childLeft)
			raf.writeLong( ++nextFreePosition );
		else 
			raf.writeLong( 0 );
		///////////////
		if( childRight)
			raf.writeLong( ++nextFreePosition );
		else
			raf.writeLong( 0);
	}
	
	private void readOneNode( long position) throws IOException {
		
		byte[] bNom = new byte[ metaTailleNom];
		byte[] bPrenom = new byte[ metaTailleNom];
		byte[] bDepartement = new byte[ metaTailleDepartement];
		byte[] bFormation = new byte[ metaTailleFormation];
		int annee;
		raf.read( bNom );
		raf.read( bPrenom );
		raf.read( bDepartement );
		raf.read( bFormation );
		annee = raf.readInt();
		
		long child1 = raf.readLong();
		long child2 = raf.readLong();
		
		Stagiaire stage = new Stagiaire( 
								new String(bNom).trim(),
								new String(bPrenom).trim(),
								new String(bDepartement).trim(),
								new String(bFormation).trim(),
								annee);
		System.out.println("stagiaire "+ stage);
		System.out.println("childs "+ child1 + " " + child2);
	}
	
	private void writeHeader() throws IOException {
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
