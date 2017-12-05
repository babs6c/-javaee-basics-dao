package com.exos.forms;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.exos.beans.Utilisateur;
import com.exos.dao.UtilisateurDao;

public class ConnexionForm {
	
	private UtilisateurDao utilisateurDao;
	private Map<String,String> erreurs=new HashMap<String,String>();	
	
	public ConnexionForm(UtilisateurDao utilisateurDao)
	{
		this.utilisateurDao=utilisateurDao;
	}

	public Map<String, String> getErreurs() {
		return erreurs;
	}
	
	public Utilisateur connexion(HttpServletRequest request)
	
	{
		Utilisateur utilisateur=null;
		String email=request.getParameter("email");
		String pass=request.getParameter("pass");
		
		try 
		{
			validationEmail(email);
		}
		catch(Exception e)
		{
			erreurs.put("email", e.getMessage());
		}
		
		try 
		{
			validationMotDePasseConnexion(pass);
		}
		catch(Exception e)
		{
			erreurs.put("pass", e.getMessage());
		}

		
		if(erreurs.isEmpty())
		{
			try
			{
				utilisateur=utilisateurDao.trouver(email, hasherEnMD5(pass));
			}
			catch(Exception e)
			{
				request.setAttribute("problemes", e.getMessage());
			}
			
			if(utilisateur==null)
			{
						erreurs.put("credentials","Verifiez vos saisies !");
						erreurs.put("emailvalue", email);	
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

	 private void validationMotDePasseConnexion( String pass ) throws Exception 
	    {
	        if ( pass != null ) 
	        {	
	        		if ( pass.trim().equals(""))
	            {
	            throw new Exception( "Merci de saisir votre mot de passe." );
	            }
	        }
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
