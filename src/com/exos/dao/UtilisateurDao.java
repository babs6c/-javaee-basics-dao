package com.exos.dao;

import java.util.List;

import com.exos.beans.Utilisateur;

public interface UtilisateurDao {

	List<Utilisateur> lister() throws DAOException;
	
	void add(Utilisateur utilisateur) throws DAOException;
	
	Utilisateur trouver(String email, String pass) throws DAOException;
	
	Utilisateur trouver(String email) throws DAOException;
}
