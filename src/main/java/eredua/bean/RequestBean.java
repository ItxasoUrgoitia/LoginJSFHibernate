package eredua.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import businessLogic.BLFacade;
import domain.Passenger;
import domain.Ride;

import java.io.Serializable;
import java.util.List;

@Named("RequestBean")
@ViewScoped
public class RequestBean implements Serializable {
	
    private static final long serialVersionUID = 1L;
    private String PassengerEmail;
    private Ride selectedRide;  
    private int seats;
    private List<Ride> rides;

    private BLFacade facadeBL;
	private String userName;
	
    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public RequestBean() {
        facadeBL = FacadeBean.getBusinessLogic();
    }

    public List<Ride> getRides() { return rides; }

   

    public String getPassengerEmail() {
		return PassengerEmail;
	}

	public void setPassengerEmail(String passengerEmail) {
		PassengerEmail = passengerEmail;
	}

	public Ride getSelectedRide() { return selectedRide; }
    public void setSelectedRide(Ride selectedRide) { 
        this.selectedRide = selectedRide;
    }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public void requestRide() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (PassengerEmail == null || PassengerEmail.isEmpty()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error1", "Enter your email"));
            return;
        }
        if (selectedRide == null) {  
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error2", "Select a ride"));
            return;
        }
        if (seats <= 0) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error3", "Enter number of seats"));
            return;
        }

        boolean ok = facadeBL.createRequest(PassengerEmail, selectedRide.getRideNumber(), seats);
        if (ok) {
            loadRides();
            
            this.selectedRide = null;
            this.seats = 0;
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Request created and accepted"));
        } else {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error4", "Request failed (insufficient funds or seats or invalid data)"));
        }
    }
    
    @PostConstruct
    public void init() {
        System.out.println(">>> SeeRequestsBean.init() ejecutado");
        loadPassengerFromSession();
        
        if (isPassengerLoggedIn()) {
            System.out.println(">>> Passenger logged in: " + PassengerEmail);
            loadRides();
            
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

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public void loadRides() {
        this.rides = facadeBL.getAllRides();
    }
    
    
    public void clearSelection() {
        this.selectedRide = null;
        this.seats = 0;
    }
}