package eredua.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.primefaces.event.SelectEvent;

import businessLogic.BLFacade;
import domain.Driver;
import domain.Passenger;
import domain.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.inject.Named;

@Named("login")
@RequestScoped
public class LoginBean implements Serializable {
	private String email;
	private String pasahitza;
	private BLFacade facadeBL;
	private String message;
	
	/*public LoginBean() {
		motak.add(new ErabiltzailearenMota(1,"ikaslea"));
		motak.add(new ErabiltzailearenMota(2,"irakaslea"));
	}*/
	
	/*public List<ErabiltzailearenMota> getMotak() {
		return motak;
	}*/
	
	public LoginBean() {
		facadeBL=FacadeBean.getBusinessLogic();
	}
	
	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasahitza() {
		return pasahitza;
	}

	public void setPasahitza(String pasahitza) {
		this.pasahitza = pasahitza;
	}
	

	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	public String egiaztatu() {
	    message = null;
	    if (email == null || email.isEmpty()) {
	        message = "Enter email";
	        return null;
	    }
	    if (pasahitza == null || pasahitza.isEmpty()) {
	        message = "Enter password";
	        return null;
	    }

	    User u = facadeBL.isRegistered(email, pasahitza);
	    if (u == null) {
	        message = "Email or password incorrect";
	        return null;
	    }

	    // Reconocer tipo de usuario
	    if (u instanceof Driver) {
	        System.out.println("Logged in as Driver");
	        return "CreateRide.xhtml?faces-redirect=true";
	    } else if (u instanceof Passenger) {
	        System.out.println("Logged in as Passenger");
	        return "QueryRides.xhtml?faces-redirect=true";
	    }

	    return null;
	}

	
	

}