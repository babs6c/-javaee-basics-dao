package com.exos.forms;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import com.exos.beans.Utilisateur;
import com.exos.dao.UtilisateurDao;

public class InscriptionForm {
	
	private UtilisateurDao utilisateurDao;
	private Part part;
	private Map <String,String> erreurs=new HashMap<String,String>();
	private static final int TAILLE_TAMPON=10240;
	private static final String REPERTOIRE_PERMANENT="/Users/macair/fichiersperm/";
	
	public InscriptionForm(UtilisateurDao utilisateurDao)
	{
		this.utilisateurDao=utilisateurDao;
	}
	
	public Map<String, String> getErreurs() {
		return erreurs;
	}
	
	
	/*Fonction principale d'ajout d'un membre
	 Recuperation des parametres de requete ...Validation champs avec capture d'exceptions ..
	 Traitement envoi fichier ...Ajout membre dans BD
	 */
	public Utilisateur addMembre(HttpServletRequest request)
	{
		String nom=request.getParameter("nom");
		String email=request.getParameter("email");
		String pass=request.getParameter("pass");
		String agree=request.getParameter("agree");
		
		Utilisateur utilisateur=new Utilisateur();
		
			try {
				part=request.getPart("photo");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ServletException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		try 
		{
			validationNom(nom);
		}
		catch(Exception e)
		{
			erreurs.put("nom", e.getMessage());
		}
		utilisateur.setNom(nom.trim());
		
		
		try 
		{
			validationEmail(email);
		}
		catch(Exception e)
		{
			erreurs.put("email", e.getMessage());
		}
		utilisateur.setEmail(email.trim());
		
		try 
		{
			validationMotDePasse(pass);
		}
		catch(Exception e)
		{
			erreurs.put("pass", e.getMessage());
		}
		utilisateur.setPass(hasherEnMD5(pass.trim()));
		
		try 
		{
			validationAgree(agree);
		}
		catch(Exception e)
		{
			erreurs.put("agree", e.getMessage());
		}
		
		
		if(erreurs.isEmpty())
		{
			traitementFichier(part,utilisateur);
			try
			{
				utilisateurDao.add(utilisateur);
			}
			catch(Exception e)
			{
				erreurs.put("Whateve","Hehe");
				request.setAttribute("problemes", e.getMessage());
			}
		
		}
		
		
		return utilisateur;
		
	}
	
	private void validationEmail( String email ) throws Exception 
    {
		if ( email.trim().equals(""))
        {
        throw new Exception( "Merci de saisir une adresse mail." );
        }
		else if ( email != null && !email.matches( "([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)" ) ) {
            throw new Exception( "Merci de saisir une adresse mail valide." );
        }
    }

    
    private void validationMotDePasse( String pass ) throws Exception 
    {
        if ( pass != null ) 
        {
        		if ( pass.trim().equals(""))
            {
            throw new Exception( "Merci de saisir votre mot de passe." );
            }
        		
        		else if ( pass.length() < 7 ) {
                throw new Exception( "Le mot de passe doit contenir au moins 7 caractères." );
            }
        }
    }
  
    
    private void validationNom( String nom ) throws Exception 
    {
        if ( nom !=null )
        {
        		if ( nom.trim().equals(""))
            {
            throw new Exception( "Merci de saisir votre nom." );
            }
        }
    }
    
    private void validationAgree(String agree) throws Exception 
    {
        if ( agree == null )
        {
            throw new Exception( "Veuilez accepter les termes d'utilisation pour continuer la procédure d'inscription." );
        }
    }
   
   
    //Traitement du fichier
    private void traitementFichier(Part part,Utilisateur utilisateur)
    {
    	// On vérifie qu'on a bien reçu un fichier
    			String nomFichier = getNomFichier(part);

    			// Si on a bien un fichier
    			if (nomFichier != null && !nomFichier.isEmpty()) 
    			{
    				// Corrige un bug du fonctionnement d'Internet Explorer
    				nomFichier = nomFichier.substring(nomFichier.lastIndexOf('/') + 1).substring(nomFichier.lastIndexOf('\\') + 1);
    				utilisateur.setPhoto(REPERTOIRE_PERMANENT+nomFichier);
    			    // On écrit définitivement le fichier sur le disque
    			    try {
						ecrireFichier(part, nomFichier, REPERTOIRE_PERMANENT);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    	
    }
    
    //Ecris le fichier dans un repertoire permanent du disque
    private void ecrireFichier( Part part, String nomFichier, String chemin ) throws IOException {
        BufferedInputStream entree = null;
        BufferedOutputStream sortie = null;
        try {
            entree = new BufferedInputStream(part.getInputStream(), TAILLE_TAMPON);
            sortie = new BufferedOutputStream(new FileOutputStream(new File(chemin + nomFichier)), TAILLE_TAMPON);

            byte[] tampon = new byte[TAILLE_TAMPON];
            int longueur;
            while ((longueur = entree.read(tampon)) > 0) {
                sortie.write(tampon, 0, longueur);
            }
        } finally {
            try {
                sortie.close();
            } catch (IOException ignore) {
            }
            try {
                entree.close();
            } catch (IOException ignore) {
            }
        }
    }
	
    //Retourne le nom du fichier
	 private static String getNomFichier( Part part ) {
	        for ( String contentDisposition : part.getHeader( "content-disposition" ).split( ";" ) ) {
	            if ( contentDisposition.trim().startsWith( "filename" ) ) {
	                return contentDisposition.substring( contentDisposition.indexOf( '=' ) + 1 ).trim().replace( "\"", "" );
	            }
	        }
	        return null;
	    }  
	
	 //Hash du mot de passe en MD5
	 private static String hasherEnMD5(String password)
	    {
	        byte[] uniqueKey = password.getBytes();
	        byte[] hash      = null;

	        try
	        {
	            hash = MessageDigest.getInstance("MD5").digest(uniqueKey);
	        }
	        catch (NoSuchAlgorithmException e)
	        {
	            throw new Error("No MD5 support in this VM.");
	        }

	        StringBuilder hashString = new StringBuilder();
	        for (int i = 0; i < hash.length; i++)
	        {
	            String hex = Integer.toHexString(hash[i]);
	            if (hex.length() == 1)
	            {
	                hashString.append('0');
	                hashString.append(hex.charAt(hex.length() - 1));
	            }
	            else
	                hashString.append(hex.substring(hex.length() - 2));
	        }
	        return hashString.toString();
	    }

}
