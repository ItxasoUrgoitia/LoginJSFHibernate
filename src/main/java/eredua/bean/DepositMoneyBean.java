package eredua.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import businessLogic.BLFacade;

import domain.Passenger;

@Named("DepositBean")
@RequestScoped
public class DepositMoneyBean {

    private String PassengerEmail; 
    private Float amount; 
    private String userName;

    private BLFacade facadeBL;
    
    public String getPassengerEmail() {
		return PassengerEmail;
	}

	public void setPassengerEmail(String PassengerEmail) {
		this.PassengerEmail = PassengerEmail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public DepositMoneyBean() {
        facadeBL = FacadeBean.getBusinessLogic();
    }

    

    public Float getAmount() { return amount; }
    public void setAmount(Float amount) { this.amount = amount; }
    
    @PostConstruct
    public void init() {
        System.out.println(">>> SeeRequestsBean.init() ejecutado");
        loadPassengerFromSession();
        
        if (isPassengerLoggedIn()) {
            System.out.println(">>> Passenger logged in: " + PassengerEmail);
            
        } else {
            System.out.println(">>> NOT a Passenger or not logged in");
        }
    }
    
    private void loadPassengerFromSession() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            
            System.out.println(">>> Checking session...");
            
            if (session != null) {
                String userEmail = (String) session.getAttribute("userEmail");
                String userType = (String) session.getAttribute("userType");
                
                System.out.println(">>> Session userEmail: " + userEmail);
                System.out.println(">>> Session userType: " + userType);
                
                
                java.util.Enumeration<String> sessionAttr = session.getAttributeNames();
                System.out.println(">>> All session attributes:");
                while (sessionAttr.hasMoreElements()) {
                    String attr = sessionAttr.nextElement();
                    System.out.println("  - " + attr + ": " + session.getAttribute(attr));
                }
                
                if (userEmail != null && "Passenger".equals(userType)) {
                    this.PassengerEmail = userEmail;
                    
                    
                    try {
                    	Passenger Passenger = facadeBL.getPassenger(userEmail);
                        if (Passenger != null) {
                            this.userName = Passenger.getName();
                        }
                    } catch (Exception e) {
                        System.out.println(">>> Could not get Passenger details: " + e.getMessage());
                    }
                } else {
                    System.out.println(">>> NOT a Passenger in session");
                }
            } else {
                System.out.println(">>> No active session found");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(">>> Error in loadPassengerFromSession: " + e.getMessage());
        }
    }
    
    public boolean isPassengerLoggedIn() {
        boolean loggedIn = (PassengerEmail != null && !PassengerEmail.isEmpty());
        System.out.println(">>> isPassengerLoggedIn() returning: " + loggedIn + " (email: " + PassengerEmail + ")");
        return loggedIn;
    }
    
    
    public void depositMoney() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (amount == null || amount <= 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Enter a positive amount"));
            return;
        }

        try {
            boolean ondo = facadeBL.depositMoney(PassengerEmail, amount);
            if (!ondo) {
            	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred"));
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred"));
            e.printStackTrace();
        }
    }
}
