
jQuery(document).ready(function()
{ 
	
	$('#signup').validate({  
		//Regles de validation
	    rules: {
	    	  nom:"required",
	      email: { 
	    	  	required :true,
			email :true,
	       	remote: {
	            url: "EmailAvailability",
	            type: "post",
	            data:
	                  {
	                      email: function()
	                      {
	                          return $('#signup :input[name="email"]').val();
	                      }
	                  }
	        			},                
				   },
		  pass:{ 
	    	  	required :true,
			minlength :7,
		  		},
		  photo:"required",
		  agree:"required",
	    },
	    //Messages d'erreur
	     messages:{
	    	 	nom:"Merci de saisir votre nom",
	        email:{
	        	 required: "Merci de saisir une adresse mail",
            	 email: "Merci de saisir une adresse mail valide",
	         remote: "Inscription déja effectuée avec cet email",
	         	},
	         pass:{
		      required: "Merci de saisir votre mot de passe",
	          minlength: "Le mot de passe doit contenir au moins 7 caractères",
		         	},
		  	photo:"Merci de choisir une photo",
		  	agree:"Veuilez accepter les termes d'utilisation pour continuer la procédure d'inscription",
	     		},
	     	//Placement du message d'erreur
	     	errorPlacement: function(error, element) {
	    			if (element.attr("name") == "agree" )
	    				{
	    				 error.appendTo("#erreur_agree");
	    				}		
	    			else
	    				{
	    				error.insertAfter(element);
	    				}
	    			}
	});
});