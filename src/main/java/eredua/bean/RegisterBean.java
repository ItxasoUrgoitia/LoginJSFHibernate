package eredua.bean;

import businessLogic.BLFacade;
import domain.Driver;
import domain.Passenger;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named("register")
@RequestScoped
public class RegisterBean {
	 private String email;
	    private String pasahitza;
	    private String userType;
	    private String message;
	    
	    private BLFacade facadeBL;

	    public RegisterBean() {
	        facadeBL = FacadeBean.getBusinessLogic();
	    }

	    public String getEmail() { return email; }
	    public void setEmail(String email) { this.email = email; }

	    public String getPasahitza() { return pasahitza; }
	    public void setPasahitza(String pasahitza) { this.pasahitza = pasahitza; }

	    public String getUserType() { return userType; }
	    public void setUserType(String userType) { this.userType = userType; }

	    public String getMessage() { return message; }
	    public void setMessage(String message) { this.message = message; }

	    public String registerUser() {
	        message = null;

	        if (email == null || email.isEmpty()) {
	            message = "Enter email";
	            return null;
	        }
	        if (pasahitza == null || pasahitza.isEmpty()) {
	            message = "Enter password";
	            return null;
	        }
	        if (userType == null || userType.isEmpty()) {
	            message = "Select user type";
	            return null;
	        }

	        try {
	            if ("Driver".equals(userType)) {
	                Driver driver = new Driver();
	                driver.setEmail(email);
	                driver.setPasahitza(pasahitza);
	                facadeBL.createDriver(driver);
	            } else if ("Passenger".equals(userType)) {
	                Passenger passenger = new Passenger();
	                passenger.setEmail(email);
	                passenger.setPasahitza(pasahitza);
	                facadeBL.createPassenger(passenger);
	            }
	            message = "User registered successfully!";
	            return "Login.xhtml?faces-redirect=true"; 
	        } catch (Exception e) {
	            message = "Error registering user. " + e.getMessage();
	            return null;
	        }
	    }
}
