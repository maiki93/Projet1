package main.java;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

	private Admin admin;
	private List<Admin> adminList = new ArrayList<Admin>();
	private String name;
	private String password;
	public AdminDAO(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}
	
	// think about ObservablebooleanValue
	private boolean isAdmin = false;

	public AdminDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void readTxtFichier() {
		BufferedReader br;
		String[] adminLinearray = new String[5];
		int i = 0;

		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/admin.txt"),
					StandardCharsets.UTF_8));
			while (br.ready()) {
				String strcompare = br.readLine();
				if (strcompare.compareTo("*") == 0) {
					String nom = adminLinearray[0];
					String motDePasse = adminLinearray[1];
					admin = new Admin(nom, motDePasse);
					adminList.add(admin);
					i = 0;
				} else {
					adminLinearray[i] = strcompare;
					i++;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean trytoFind(Admin admin2) {
		readTxtFichier();
		for (Admin admin : getAdminList()) {
			if (admin.getNom().compareTo(admin2.getNom()) == 0
					&& admin.getMotDePasse().compareTo(admin2.getMotDePasse()) == 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isAdmin() {
		return isAdmin;
	}
	
	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public List<Admin> getAdminList() {
		return adminList;
	}

	public void setAdminList(List<Admin> adminList) {
		this.adminList = adminList;
	}
}
